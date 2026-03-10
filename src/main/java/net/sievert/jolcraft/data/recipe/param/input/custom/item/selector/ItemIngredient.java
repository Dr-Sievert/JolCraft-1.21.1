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
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ItemIngredient(List<Target> targets)
        implements SelfValidating<ItemIngredient>, RegistryIntrospectable {

    public record Target(Either<Holder<Item>, TagKey<Item>> target) implements SelfValidating<Target> {

        private static final Codec<Holder<Item>> ITEM_HOLDER_CODEC =
                RegistryFixedCodec.create(Registries.ITEM);

        private record Raw(Optional<Holder<Item>> item, Optional<TagKey<Item>> tag) {}

        private static final Codec<Raw> RAW_CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        ITEM_HOLDER_CODEC.optionalFieldOf(JolCraftParameterIds.ITEM).forGetter(Raw::item),
                        TagKey.codec(Registries.ITEM).optionalFieldOf(JolCraftParameterIds.TAG).forGetter(Raw::tag)
                ).apply(inst, Raw::new));

        public static final Codec<Target> CODEC =
                ParamCodecContract.create(RAW_CODEC, Target::fromRaw, Target::toRaw);

        private static final StreamCodec<RegistryFriendlyByteBuf, Holder<Item>> ITEM_HOLDER_STREAM =
                ByteBufCodecs.holderRegistry(Registries.ITEM);

        private static final int KIND_ITEM = 0;
        private static final int KIND_TAG = 1;

        public static final StreamCodec<RegistryFriendlyByteBuf, Target> STREAM_CODEC =
                StreamCodec.of(
                        (buf, v) -> {
                            Optional<Holder<Item>> left = v.target.left();
                            if (left.isPresent()) {
                                buf.writeByte(KIND_ITEM);
                                ITEM_HOLDER_STREAM.encode(buf, left.get());
                                return;
                            }

                            Optional<TagKey<Item>> right = v.target.right();
                            if (right.isPresent()) {
                                buf.writeByte(KIND_TAG);
                                buf.writeResourceLocation(right.get().location());
                                return;
                            }

                            throw new IllegalArgumentException("invalid item ingredient target");
                        },
                        buf -> {
                            int kind = buf.readUnsignedByte();
                            return switch (kind) {
                                case KIND_ITEM -> new Target(Either.left(ITEM_HOLDER_STREAM.decode(buf)));
                                case KIND_TAG -> new Target(Either.right(
                                        TagKey.create(Registries.ITEM, buf.readResourceLocation())
                                ));
                                default -> throw new IllegalArgumentException("unknown item ingredient target kind: " + kind);
                            };
                        }
                );

        public Target {
            if (target == null) {
                throw new IllegalArgumentException("target cannot be null");
            }
        }

        private static @NotNull DataResult<Target> fromRaw(@NotNull Raw raw) {
            boolean hasItem = raw.item().isPresent();
            boolean hasTag = raw.tag().isPresent();
            if (hasItem == hasTag) {
                return DataResult.error(() ->
                        "ItemIngredient.Target requires exactly one of '" + JolCraftParameterIds.ITEM + "' or '" + JolCraftParameterIds.TAG + "'"
                );
            }
            return DataResult.success(
                    hasItem ? new Target(Either.left(raw.item().orElseThrow()))
                            : new Target(Either.right(raw.tag().orElseThrow()))
            );
        }

        private static @NotNull Raw toRaw(@NotNull Target t) {
            return new Raw(t.target.left(), t.target.right());
        }

        @Override
        public @NotNull DataResult<Target> validate() {
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
            if (stack.isEmpty()) return false;
            return target.map(
                    h -> stack.getItem() == h.value(),
                    stack::is
            );
        }

        @SuppressWarnings("deprecation")
        public static Target of(@NotNull ItemLike item) {
            return new Target(Either.left(item.asItem().builtInRegistryHolder()));
        }

        public static Target of(@NotNull TagKey<Item> tag) {
            return new Target(Either.right(tag));
        }
    }

    public static ItemIngredient of(@NotNull ItemLike item) {
        return ofTargets(Target.of(item));
    }

    public static ItemIngredient of(@NotNull TagKey<Item> tag) {
        return ofTargets(Target.of(tag));
    }

    public static ItemIngredient ofTargets(Target... targets) {
        if (targets == null || targets.length == 0) {
            return new ItemIngredient(List.of());
        }
        ArrayList<Target> out = new ArrayList<>(targets.length);
        for (Target t : targets) {
            if (t != null) out.add(t);
        }
        return new ItemIngredient(out);
    }

    public static ItemIngredient ofTargets(List<Target> targets) {
        return new ItemIngredient(targets);
    }

    private static final Codec<ItemIngredient> RAW_CODEC =
            Codec.either(Target.CODEC, Target.CODEC.listOf())
                    .xmap(
                            either -> either.map(ItemIngredient::ofTargets, ItemIngredient::ofTargets),
                            ing -> ing.targets().size() == 1
                                    ? Either.left(ing.targets().getFirst())
                                    : Either.right(ing.targets())
                    );

    public static final Codec<ItemIngredient> CODEC = ParamCodecs.validated(RAW_CODEC);

    private static final int MAX_TARGETS_STREAM = 2048;

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemIngredient> STREAM_CODEC =
            StreamCodec.of(
                    (buf, ing) -> {
                        List<Target> list = ing.targets();
                        buf.writeVarInt(list.size());
                        for (Target t : list) {
                            Target.STREAM_CODEC.encode(buf, t);
                        }
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        if (size < 0) throw new IllegalArgumentException("negative target size: " + size);
                        if (size == 0) return new ItemIngredient(List.of());
                        if (size > MAX_TARGETS_STREAM) throw new IllegalArgumentException("target size exceeds max " + MAX_TARGETS_STREAM + " (got " + size + ")");
                        ArrayList<Target> list = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) list.add(Target.STREAM_CODEC.decode(buf));
                        return new ItemIngredient(list);
                    }
            );

    public ItemIngredient {
        targets = sanitizeTargets(targets);
    }

    public boolean isEmpty() {
        return targets.isEmpty();
    }

    @Override
    public @NotNull DataResult<ItemIngredient> validate() {
        if (targets.isEmpty()) {
            return SelfValidating.invalid(
                    "missing or empty field '" + JolCraftStrings.plural(JolCraftDictionary.INGREDIENT) + "'"
            );
        }
        for (int i = 0; i < targets.size(); i++) {
            DataResult<Target> tv = targets.get(i).validate();
            if (tv.error().isPresent()) {
                String msg = tv.error().map(DataResult.Error::message).orElse("");
                return SelfValidating.invalid("targets[" + i + "] invalid: " + msg);
            }
        }
        return SelfValidating.ok(this);
    }

    public boolean matches(@NotNull ItemStack stack) {
        if (targets.isEmpty()) return false;
        for (Target t : targets) {
            if (t.matches(stack)) return true;
        }
        return false;
    }

    @Override
    public @NotNull RegistryIntrospection introspection() {
        int holders = 0;
        int tags = 0;
        Holder<?> singleHolder = null;
        TagKey<?> singleTag = null;
        for (Target t : targets) {
            Either<Holder<Item>, TagKey<Item>> e = t.target();
            Optional<Holder<Item>> left = e.left();
            if (left.isPresent()) {
                holders++;
                if (holders == 1 && tags == 0) singleHolder = left.get(); else singleHolder = null;
                continue;
            }
            Optional<TagKey<Item>> right = e.right();
            if (right.isPresent()) {
                tags++;
                if (tags == 1 && holders == 0) singleTag = right.get(); else singleTag = null;
            }
        }
        if (holders == 1 && tags == 0 && singleHolder != null) return RegistryIntrospection.single(Registries.ITEM, singleHolder);
        if (holders == 0 && tags == 1 && singleTag != null) return RegistryIntrospection.singleTag(Registries.ITEM, singleTag);
        if (holders == 0 && tags > 0) return RegistryIntrospection.anyTag(Registries.ITEM);
        return RegistryIntrospection.mixed(Registries.ITEM, holders, tags > 0);
    }

    private static List<Target> sanitizeTargets(List<Target> in) {
        if (in == null || in.isEmpty()) return List.of();
        ArrayList<Target> safe = new ArrayList<>(in.size());
        for (Target t : in) if (t != null) safe.add(t);
        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }
}
