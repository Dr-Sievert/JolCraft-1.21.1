package net.sievert.jolcraft.data.recipe.param.input.custom.entity.selector;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
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

public record EntityIngredient(List<Target> targets)
        implements SelfValidating<EntityIngredient>, RegistryIntrospectable {

    public record Target(Either<Holder<EntityType<?>>, TagKey<EntityType<?>>> target) implements SelfValidating<Target> {

        private static final Codec<Holder<EntityType<?>>> ENTITY_HOLDER_CODEC =
                RegistryFixedCodec.create(Registries.ENTITY_TYPE);

        private record Raw(Optional<Holder<EntityType<?>>> entity, Optional<TagKey<EntityType<?>>> tag) {}

        private static final Codec<Raw> RAW_CODEC =
                Codec.either(
                        ENTITY_HOLDER_CODEC,
                        com.mojang.serialization.codecs.RecordCodecBuilder.<Raw>create(inst -> inst.group(
                                ENTITY_HOLDER_CODEC.optionalFieldOf(JolCraftParameterIds.ENTITY).forGetter(Raw::entity),
                                TagKey.codec(Registries.ENTITY_TYPE).optionalFieldOf(JolCraftParameterIds.TAG).forGetter(Raw::tag)
                        ).apply(inst, Raw::new))
                ).xmap(
                        either -> either.map(
                                entity -> new Raw(Optional.of(entity), Optional.empty()),
                                raw -> raw
                        ),
                        raw -> {
                            if (raw.entity().isPresent() && raw.tag().isEmpty()) {
                                return Either.left(raw.entity().orElseThrow());
                            }
                            return Either.right(raw);
                        }
                );

        public static final Codec<Target> CODEC =
                ParamCodecContract.create(RAW_CODEC, Target::fromRaw, Target::toRaw);

        private static final StreamCodec<RegistryFriendlyByteBuf, Holder<EntityType<?>>> ENTITY_HOLDER_STREAM =
                ByteBufCodecs.holderRegistry(Registries.ENTITY_TYPE).cast();

        private static final int KIND_ENTITY = 0;
        private static final int KIND_TAG = 1;

        public static final StreamCodec<RegistryFriendlyByteBuf, Target> STREAM_CODEC =
                StreamCodec.of(
                        (buf, v) -> {
                            Optional<Holder<EntityType<?>>> left = v.target.left();
                            if (left.isPresent()) {
                                buf.writeByte(KIND_ENTITY);
                                ENTITY_HOLDER_STREAM.encode(buf, left.get());
                                return;
                            }

                            Optional<TagKey<EntityType<?>>> right = v.target.right();
                            if (right.isPresent()) {
                                buf.writeByte(KIND_TAG);
                                buf.writeResourceLocation(right.get().location());
                                return;
                            }

                            throw new IllegalArgumentException("invalid entity ingredient target");
                        },
                        buf -> {
                            int kind = buf.readUnsignedByte();
                            return switch (kind) {
                                case KIND_ENTITY -> new Target(Either.left(ENTITY_HOLDER_STREAM.decode(buf)));
                                case KIND_TAG -> new Target(Either.right(
                                        TagKey.create(Registries.ENTITY_TYPE, buf.readResourceLocation())
                                ));
                                default -> throw new IllegalArgumentException("unknown entity ingredient target kind: " + kind);
                            };
                        }
                );

        public Target {
            if (target == null) throw new IllegalArgumentException("target cannot be null");
        }

        private static @NotNull DataResult<Target> fromRaw(@NotNull Raw raw) {
            boolean hasEntity = raw.entity().isPresent();
            boolean hasTag = raw.tag().isPresent();

            if (hasEntity == hasTag) {
                return DataResult.error(() ->
                        "EntityIngredient.Target requires exactly one of '" + JolCraftParameterIds.ENTITY + "' or '" + JolCraftParameterIds.TAG + "'"
                );
            }

            return DataResult.success(
                    hasEntity
                            ? new Target(Either.left(raw.entity().orElseThrow()))
                            : new Target(Either.right(raw.tag().orElseThrow()))
            );
        }

        private static @NotNull Raw toRaw(@NotNull Target t) {
            return new Raw(t.target.left(), t.target.right());
        }

        @Override
        public @NotNull DataResult<Target> validate() {
            boolean hasEntity = target.left().isPresent();
            boolean hasTag = target.right().isPresent();

            if (hasEntity == hasTag) {
                return SelfValidating.invalid(
                        "EntityIngredient.Target requires exactly one of '" + JolCraftParameterIds.ENTITY + "' or '" + JolCraftParameterIds.TAG + "'"
                );
            }

            return SelfValidating.ok(this);
        }

        public boolean matches(@NotNull Entity entity) {
            EntityType<?> type = entity.getType();
            return target.map(h -> h.value() == type, type::is);
        }

        @SuppressWarnings("deprecation")
        public static @NotNull Target of(@NotNull EntityType<?> type) {
            return new Target(Either.left(type.builtInRegistryHolder()));
        }

        public static @NotNull Target of(@NotNull TagKey<EntityType<?>> tag) {
            return new Target(Either.right(tag));
        }
    }

    public static @NotNull EntityIngredient of(@NotNull EntityType<?> type) {
        return ofTargets(Target.of(type));
    }

    public static @NotNull EntityIngredient of(@NotNull TagKey<EntityType<?>> tag) {
        return ofTargets(Target.of(tag));
    }

    public static @NotNull EntityIngredient ofTargets(Target... targets) {
        if (targets == null || targets.length == 0) return new EntityIngredient(List.of());

        ArrayList<Target> out = new ArrayList<>(targets.length);
        for (Target t : targets) {
            if (t != null) out.add(t);
        }
        return new EntityIngredient(out);
    }

    public static @NotNull EntityIngredient ofTargets(List<Target> targets) {
        return new EntityIngredient(targets);
    }

    private static final Codec<EntityIngredient> RAW_CODEC =
            Codec.either(Target.CODEC, Target.CODEC.listOf())
                    .xmap(
                            either -> either.map(EntityIngredient::ofTargets, EntityIngredient::ofTargets),
                            ing -> ing.targets().size() == 1
                                    ? Either.left(ing.targets().getFirst())
                                    : Either.right(ing.targets())
                    );

    public static final Codec<EntityIngredient> CODEC = ParamCodecs.validated(RAW_CODEC);

    private static final int MAX_TARGETS_STREAM = 2048;

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityIngredient> STREAM_CODEC =
            StreamCodec.of(
                    (buf, ing) -> {
                        buf.writeVarInt(ing.targets().size());
                        for (Target t : ing.targets()) {
                            Target.STREAM_CODEC.encode(buf, t);
                        }
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        if (size < 0) throw new IllegalArgumentException("negative target size: " + size);
                        if (size == 0) return new EntityIngredient(List.of());
                        if (size > MAX_TARGETS_STREAM) {
                            throw new IllegalArgumentException("target size exceeds max " + MAX_TARGETS_STREAM + " (got " + size + ")");
                        }

                        ArrayList<Target> list = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            list.add(Target.STREAM_CODEC.decode(buf));
                        }
                        return new EntityIngredient(list);
                    }
            );

    public EntityIngredient {
        targets = sanitizeTargets(targets);
    }

    public boolean isEmpty() {
        return targets.isEmpty();
    }

    @Override
    public @NotNull DataResult<EntityIngredient> validate() {
        if (targets.isEmpty()) {
            return SelfValidating.invalid("missing or empty field '" + JolCraftStrings.plural(JolCraftDictionary.INGREDIENT) + "'");
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

    public boolean matches(@NotNull Entity entity) {
        if (targets.isEmpty()) return false;

        for (Target t : targets) {
            if (t.matches(entity)) return true;
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
            Either<Holder<EntityType<?>>, TagKey<EntityType<?>>> e = t.target();
            Optional<Holder<EntityType<?>>> left = e.left();
            if (left.isPresent()) {
                holders++;
                if (holders == 1 && tags == 0) singleHolder = left.get();
                else singleHolder = null;
                continue;
            }

            Optional<TagKey<EntityType<?>>> right = e.right();
            if (right.isPresent()) {
                tags++;
                if (tags == 1 && holders == 0) singleTag = right.get();
                else singleTag = null;
            }
        }

        if (holders == 1 && tags == 0 && singleHolder != null) {
            return RegistryIntrospection.single(Registries.ENTITY_TYPE, singleHolder);
        }
        if (holders == 0 && tags == 1 && singleTag != null) {
            return RegistryIntrospection.singleTag(Registries.ENTITY_TYPE, singleTag);
        }
        if (holders == 0 && tags > 0) {
            return RegistryIntrospection.anyTag(Registries.ENTITY_TYPE);
        }
        return RegistryIntrospection.mixed(Registries.ENTITY_TYPE, holders, tags > 0);
    }

    private static @NotNull List<Target> sanitizeTargets(List<Target> in) {
        if (in == null || in.isEmpty()) return List.of();

        ArrayList<Target> safe = new ArrayList<>(in.size());
        for (Target t : in) {
            if (t != null) safe.add(t);
        }
        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }
}