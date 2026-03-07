package net.sievert.jolcraft.data.recipe.param.input.custom.item.selector;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Simple JolCraft-owned item ingredient (atomic matcher).
 *
 * Mirrors vanilla Ingredient shape (simplified):
 * - One matcher object that may contain multiple alternatives internally (OR).
 * - Alternatives are either an explicit item holder or an item tag.
 *
 * JSON shapes:
 * - { "item": "minecraft:iron_ingot" }
 * - { "tag": "c:ingots/copper" }
 * - [ { "item": ... }, { "tag": ... } ]
 */
public record ItemIngredient(List<Target> targets)
        implements SelfValidating<ItemIngredient>, RegistryIntrospectable {

    public static final ItemIngredient EMPTY = new ItemIngredient(List.of());

    // ---------------------------------------------------------------------
    // TARGET
    // ---------------------------------------------------------------------

    public record Target(@Nullable Either<Holder<Item>, TagKey<Item>> target) implements SelfValidating<Target> {

        public static final Target EMPTY = new Target(null);

        private static final Codec<Holder<Item>> ITEM_HOLDER_CODEC =
                RegistryFixedCodec.create(Registries.ITEM);

        private static final Codec<Raw> RAW_CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        ITEM_HOLDER_CODEC.optionalFieldOf(JolCraftParameterIds.ITEM).forGetter(Raw::item),
                        TagKey.codec(Registries.ITEM).optionalFieldOf(JolCraftParameterIds.TAG).forGetter(Raw::tag)
                ).apply(inst, Raw::new));

        public static final Codec<Target> CODEC =
                ParamCodecs.validated(
                        RAW_CODEC.flatXmap(
                                Raw::toTarget,
                                t -> DataResult.success(Raw.fromTarget(t))
                        )
                );

        private static final StreamCodec<RegistryFriendlyByteBuf, Holder<Item>> ITEM_HOLDER_STREAM =
                ByteBufCodecs.holderRegistry(Registries.ITEM);

        private static final int KIND_ITEM = 0;
        private static final int KIND_TAG = 1;
        private static final int KIND_EMPTY = 2;

        public static final StreamCodec<RegistryFriendlyByteBuf, Target> STREAM_CODEC =
                StreamCodec.of(
                        (buf, v) -> {

                            Either<Holder<Item>, TagKey<Item>> t = v.target;
                            if (t == null) {
                                buf.writeByte(KIND_EMPTY);
                                return;
                            }

                            Optional<Holder<Item>> left = t.left();
                            if (left.isPresent()) {
                                buf.writeByte(KIND_ITEM);
                                ITEM_HOLDER_STREAM.encode(buf, left.get());
                                return;
                            }

                            Optional<TagKey<Item>> right = t.right();
                            if (right.isPresent()) {
                                buf.writeByte(KIND_TAG);
                                buf.writeResourceLocation(right.get().location());
                                return;
                            }

                            buf.writeByte(KIND_EMPTY);
                        },
                        buf -> {
                            int kind = buf.readUnsignedByte();
                            return switch (kind) {
                                case KIND_ITEM -> new Target(Either.left(ITEM_HOLDER_STREAM.decode(buf)));
                                case KIND_TAG -> new Target(Either.right(
                                        TagKey.create(Registries.ITEM, buf.readResourceLocation())
                                ));
                                default -> Target.EMPTY;
                            };
                        }
                );

        @Override
        public @NotNull DataResult<Target> validate() {
            if (target == null) {
                return SelfValidating.invalid(
                        "missing required field '" + JolCraftParameterIds.ITEM + "' or '" + JolCraftParameterIds.TAG + "'"
                );
            }

            boolean hasItem = target.left().isPresent();
            boolean hasTag = target.right().isPresent();

            if (hasItem == hasTag) {
                return SelfValidating.invalid(
                        "ItemIngredient.Target requires exactly one of '" + JolCraftParameterIds.ITEM + "' or '" + JolCraftParameterIds.TAG + "'"
                );
            }

            return SelfValidating.ok(this);
        }

        public boolean matches(@NotNull ItemStack stack) {
            if (target == null || stack.isEmpty()) return false;

            return target.map(
                    h -> h != null && stack.getItem() == h.value(),
                    tag -> tag != null && stack.is(tag)
            );
        }

        @SuppressWarnings("deprecation")
        public static Target of(@NotNull ItemLike item) {
            return new Target(Either.left(item.asItem().builtInRegistryHolder()));
        }

        public static Target of(@NotNull TagKey<Item> tag) {
            return new Target(Either.right(tag));
        }

        private record Raw(Optional<Holder<Item>> item, Optional<TagKey<Item>> tag) {

            DataResult<Target> toTarget() {
                boolean hasItem = item.isPresent();
                boolean hasTag = tag.isPresent();

                if (hasItem == hasTag) {
                    return DataResult.error(() ->
                            "ItemIngredient.Target requires exactly one of '" + JolCraftParameterIds.ITEM + "' or '" + JolCraftParameterIds.TAG + "'"
                    );
                }

                return DataResult.success(
                        hasItem ? new Target(Either.left(item.get())) : new Target(Either.right(tag.get()))
                );
            }

            static Raw fromTarget(Target t) {
                if (t == null || t.target == null) return new Raw(Optional.empty(), Optional.empty());
                return new Raw(t.target.left(), t.target.right());
            }
        }
    }

    // ---------------------------------------------------------------------
    // FACTORIES
    // ---------------------------------------------------------------------

    public static ItemIngredient of(@NotNull ItemLike item) {
        return ofTargets(Target.of(item));
    }

    public static ItemIngredient of(@NotNull TagKey<Item> tag) {
        return ofTargets(Target.of(tag));
    }

    public static ItemIngredient ofTargets(Target... targets) {
        if (targets == null || targets.length == 0) return EMPTY;

        ArrayList<Target> out = new ArrayList<>(Math.min(targets.length, 16));
        for (Target t : targets) {
            if (t != null && t != Target.EMPTY) out.add(t);
        }

        return out.isEmpty() ? EMPTY : new ItemIngredient(sanitizeTargets(out));
    }

    public static ItemIngredient ofTargets(List<Target> targets) {
        if (targets == null || targets.isEmpty()) return EMPTY;

        ArrayList<Target> out = new ArrayList<>(Math.min(targets.size(), 16));
        for (Target t : targets) {
            if (t != null && t != Target.EMPTY) out.add(t);
        }

        return out.isEmpty() ? EMPTY : new ItemIngredient(sanitizeTargets(out));
    }

    // ---------------------------------------------------------------------
    // CODEC / STREAM
    // ---------------------------------------------------------------------

    private static final Codec<ItemIngredient> RAW_CODEC =
            Codec.either(Target.CODEC, Target.CODEC.listOf())
                    .xmap(
                            either -> either.map(
                                    ItemIngredient::ofTargets,
                                    ItemIngredient::ofTargets
                            ),
                            ing -> {
                                List<Target> list = (ing == null || ing.targets == null) ? List.of() : ing.targets;
                                if (list.size() == 1) return Either.left(list.getFirst());
                                return Either.right(list);
                            }
                    );

    public static final Codec<ItemIngredient> CODEC = ParamCodecs.validated(RAW_CODEC);

    private static final int MAX_TARGETS_STREAM = 2048;

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemIngredient> STREAM_CODEC =
            StreamCodec.of(
                    (buf, ing) -> {
                        List<Target> list = (ing.targets == null) ? List.of() : ing.targets;
                        buf.writeVarInt(list.size());
                        for (Target t : list) {
                            Target.STREAM_CODEC.encode(buf, t != null ? t : Target.EMPTY);
                        }
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        if (size <= 0) return ItemIngredient.EMPTY;

                        int capped = Math.min(size, MAX_TARGETS_STREAM);
                        ArrayList<Target> list = new ArrayList<>(Math.min(capped, 64));

                        for (int i = 0; i < capped; i++) list.add(Target.STREAM_CODEC.decode(buf));
                        for (int i = capped; i < size; i++) Target.STREAM_CODEC.decode(buf);

                        List<Target> safe = sanitizeTargets(list);
                        return safe.isEmpty() ? ItemIngredient.EMPTY : new ItemIngredient(safe);
                    }
            );

    // ---------------------------------------------------------------------
    // DATA
    // ---------------------------------------------------------------------

    public ItemIngredient(List<Target> targets) {
        this.targets = (targets == null || targets.isEmpty()) ? List.of() : sanitizeTargets(targets);
    }

    public boolean isEmpty() {
        return targets.isEmpty();
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<ItemIngredient> validate() {
        if (targets.isEmpty()) {
            return SelfValidating.invalid(
                    "missing or empty field '" + JolCraftStrings.plural(JolCraftDictionary.INGREDIENT) + "'"
            );
        }

        for (int i = 0; i < targets.size(); i++) {
            Target t = targets.get(i);
            if (t == null) {
                return SelfValidating.invalid("targets[" + i + "] invalid: null");
            }

            DataResult<Target> tv = t.validate();
            if (tv.error().isPresent()) {
                String msg = tv.error().map(DataResult.Error::message).orElse("");
                return SelfValidating.invalid("targets[" + i + "] invalid: " + msg);
            }
        }

        return SelfValidating.ok(this);
    }

    // ---------------------------------------------------------------------
    // MATCHING
    // ---------------------------------------------------------------------

    public boolean matches(@NotNull ItemStack stack) {
        if (stack.isEmpty() || targets.isEmpty()) return false;

        for (Target t : targets) {
            if (t != null && t.matches(stack)) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull RegistryIntrospection introspection() {
        int holders = 0;
        int tags = 0;

        Holder<?> singleHolder = null;
        TagKey<?> singleTag = null;

        List<Target> list = (targets == null) ? List.of() : targets;
        for (Target t : list) {
            if (t == null) continue;

            Either<Holder<Item>, TagKey<Item>> e = t.target();
            if (e == null) continue;

            Optional<Holder<Item>> left = e.left();
            if (left.isPresent()) {
                holders++;
                if (holders == 1 && tags == 0) {
                    singleHolder = left.get();
                } else {
                    singleHolder = null;
                }
                continue;
            }

            Optional<TagKey<Item>> right = e.right();
            if (right.isPresent()) {
                tags++;
                if (tags == 1 && holders == 0) {
                    singleTag = right.get();
                } else {
                    singleTag = null;
                }
            }
        }

        if (holders == 1 && tags == 0 && singleHolder != null) {
            return RegistryIntrospection.single(Registries.ITEM, singleHolder);
        }
        if (holders == 0 && tags == 1 && singleTag != null) {
            return RegistryIntrospection.singleTag(Registries.ITEM, singleTag);
        }
        if (holders == 0 && tags > 0) {
            return RegistryIntrospection.anyTag(Registries.ITEM);
        }

        return RegistryIntrospection.mixed(Registries.ITEM, holders, tags > 0);
    }

    // ---------------------------------------------------------------------
    // HELPERS
    // ---------------------------------------------------------------------

    private static List<Target> sanitizeTargets(List<Target> in) {
        if (in == null || in.isEmpty()) return List.of();

        ArrayList<Target> safe = new ArrayList<>(in.size());
        for (Target t : in) {
            if (t == null) continue;
            if (t == Target.EMPTY) continue;
            safe.add(t);
        }

        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }
}