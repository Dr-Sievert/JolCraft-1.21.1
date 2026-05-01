package net.sievert.jolcraft.world.recipe.param.input.custom.item.selector;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ItemIngredient(List<Target> targets)
        implements SelfValidating<ItemIngredient>, RegistryIntrospectable {

    public record Target(Either<Holder<Item>, TagKey<Item>> target) implements SelfValidating<Target> {

        public static final Codec<Holder<Item>> ITEM_HOLDER_CODEC = new Codec<>() {
            @Override
            public <T> DataResult<Pair<Holder<Item>, T>> decode(
                    com.mojang.serialization.DynamicOps<T> ops,
                    T input
            ) {
                return ResourceLocation.CODEC.decode(ops, input).flatMap(pair -> {
                    ResourceLocation id = pair.getFirst();
                    T rest = pair.getSecond();

                    if (!(ops instanceof RegistryOps<T> registryOps)) {
                        return DataResult.error(() ->
                                "item decode requires RegistryOps for '" + Registries.ITEM.location() + "'"
                        );
                    }

                    var lookupOpt = registryOps.lookupProvider.lookup(Registries.ITEM);
                    if (lookupOpt.isEmpty()) {
                        return DataResult.error(() ->
                                "missing registry info for '" + Registries.ITEM.location() + "'"
                        );
                    }

                    ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);
                    var holderOpt = lookupOpt.get().getter().get(key);

                    return holderOpt.<DataResult<Pair<Holder<Item>, T>>>map(itemReference ->
                            DataResult.success(Pair.of(itemReference, rest))).orElseGet(() -> DataResult.error(() -> "unknown item '" + id + "'"));

                });
            }

            @Override
            public <T> DataResult<T> encode(
                    Holder<Item> input,
                    com.mojang.serialization.DynamicOps<T> ops,
                    T prefix
            ) {
                if (input == null) {
                    return DataResult.error(() -> "item holder cannot be null");
                }

                return input.unwrapKey()
                        .map(ResourceKey::location)
                        .map(id -> ResourceLocation.CODEC.encode(id, ops, prefix))
                        .orElseGet(() -> DataResult.error(() -> "unkeyed item holder"));
            }
        };

        private record VerboseRaw(Optional<Holder<Item>> item, Optional<TagKey<Item>> tag) {
            private VerboseRaw {
                item = item != null ? item : Optional.empty();
                tag = tag != null ? tag : Optional.empty();
            }
        }

        private static final Codec<VerboseRaw> VERBOSE_CODEC =
                Codec.withAlternative(
                        com.mojang.serialization.codecs.RecordCodecBuilder.create(inst -> inst.group(
                                ITEM_HOLDER_CODEC.optionalFieldOf(JolCraftParameterIds.ITEM).forGetter(VerboseRaw::item),
                                TagKey.codec(Registries.ITEM).optionalFieldOf(JolCraftParameterIds.TAG).forGetter(VerboseRaw::tag)
                        ).apply(inst, VerboseRaw::new)),
                        ITEM_HOLDER_CODEC,
                        holder -> new VerboseRaw(Optional.of(holder), Optional.empty())
                );

        public static final Codec<Target> CODEC =
                ParamCodecContract.create(VERBOSE_CODEC, Target::fromRaw, Target::toRaw);

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

        private static @NotNull DataResult<Target> fromRaw(@NotNull VerboseRaw raw) {
            boolean hasItem = raw.item().isPresent();
            boolean hasTag = raw.tag().isPresent();

            if (hasItem == hasTag) {
                return DataResult.error(() ->
                        "ItemIngredient.Target requires exactly one of '" + JolCraftParameterIds.ITEM + "' or '" + JolCraftParameterIds.TAG + "'"
                );
            }

            return DataResult.success(
                    hasItem
                            ? new Target(Either.left(raw.item().orElseThrow()))
                            : new Target(Either.right(raw.tag().orElseThrow()))
            );
        }

        private static @NotNull VerboseRaw toRaw(@NotNull Target t) {
            Optional<Holder<Item>> item = t.target.left();
            if (item.isPresent()) {
                return new VerboseRaw(item, Optional.empty());
            }
            return new VerboseRaw(Optional.empty(), t.target.right());
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
        public static @NotNull Target of(@NotNull ItemLike item) {
            return new Target(Either.left(item.asItem().builtInRegistryHolder()));
        }

        public static @NotNull Target of(@NotNull TagKey<Item> tag) {
            return new Target(Either.right(tag));
        }
    }

    public static @NotNull ItemIngredient of(@NotNull ItemLike item) {
        return ofTargets(Target.of(item));
    }

    public static @NotNull ItemIngredient of(@NotNull TagKey<Item> tag) {
        return ofTargets(Target.of(tag));
    }

    public static @NotNull ItemIngredient ofTargets(Target... targets) {
        if (targets == null || targets.length == 0) {
            return new ItemIngredient(List.of());
        }

        ArrayList<Target> out = new ArrayList<>(targets.length);
        for (Target t : targets) {
            if (t != null) out.add(t);
        }
        return new ItemIngredient(out);
    }

    public static @NotNull ItemIngredient ofTargets(List<Target> targets) {
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
                        if (size > MAX_TARGETS_STREAM) {
                            throw new IllegalArgumentException("target size exceeds max " + MAX_TARGETS_STREAM + " (got " + size + ")");
                        }

                        ArrayList<Target> list = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            list.add(Target.STREAM_CODEC.decode(buf));
                        }
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
                String msg = tv.error().get().message();
                return SelfValidating.invalid("ingredient[" + i + "]: " + msg);
            }
        }

        return SelfValidating.ok(this);
    }

    public boolean matches(@NotNull ItemStack stack) {
        if (stack.isEmpty()) return false;
        for (Target t : targets) {
            if (t.matches(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @NotNull RegistryIntrospection introspection() {
        int concrete = 0;
        int tags = 0;
        Holder<Item> singleConcrete = null;
        TagKey<Item> singleTag = null;

        for (Target t : targets) {
            Optional<Holder<Item>> item = t.target().left();
            if (item.isPresent()) {
                concrete++;
                if (concrete == 1 && tags == 0) {
                    singleConcrete = item.get();
                } else {
                    singleConcrete = null;
                }
                continue;
            }

            Optional<TagKey<Item>> tag = t.target().right();
            if (tag.isPresent()) {
                tags++;
                if (tags == 1 && concrete == 0) {
                    singleTag = tag.get();
                } else {
                    singleTag = null;
                }
            }
        }

        if (singleConcrete != null) {
            return RegistryIntrospection.single(Registries.ITEM, singleConcrete);
        }

        if (singleTag != null) {
            return RegistryIntrospection.singleTag(Registries.ITEM, singleTag);
        }

        return RegistryIntrospection.mixed(Registries.ITEM, concrete, tags > 0);
    }

    private static @NotNull List<Target> sanitizeTargets(List<Target> in) {
        if (in == null || in.isEmpty()) {
            return List.of();
        }

        ArrayList<Target> out = new ArrayList<>(in.size());
        for (Target t : in) {
            if (t != null) out.add(t);
        }
        return out.isEmpty() ? List.of() : List.copyOf(out);
    }
}