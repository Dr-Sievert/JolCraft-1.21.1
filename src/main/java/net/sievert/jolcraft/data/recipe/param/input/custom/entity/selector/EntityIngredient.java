package net.sievert.jolcraft.data.recipe.param.input.custom.entity.selector;

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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JolCraft entity ingredient (OR over targets).
 *
 * Targets:
 * - entity holder
 * - entity tag
 */
public record EntityIngredient(List<Target> targets)
        implements SelfValidating<EntityIngredient>, RegistryIntrospectable {

    public static final EntityIngredient EMPTY = new EntityIngredient(List.of());

    // ---------------------------------------------------------------------
    // TARGET
    // ---------------------------------------------------------------------

    public record Target(@Nullable Either<Holder<EntityType<?>>, TagKey<EntityType<?>>> target)
            implements SelfValidating<Target> {

        public static final Target EMPTY = new Target(null);

        private static final Codec<Holder<EntityType<?>>> ENTITY_HOLDER_CODEC =
                RegistryFixedCodec.create(Registries.ENTITY_TYPE);

        private static final Codec<Raw> RAW_CODEC =
                RecordCodecBuilder.create(inst -> inst.group(
                        ENTITY_HOLDER_CODEC.optionalFieldOf(JolCraftParameterIds.ENTITY).forGetter(Raw::entity),
                        TagKey.codec(Registries.ENTITY_TYPE).optionalFieldOf(JolCraftParameterIds.TAG).forGetter(Raw::tag)
                ).apply(inst, Raw::new));

        public static final Codec<Target> CODEC =
                ParamCodecs.validated(
                        RAW_CODEC.flatXmap(
                                Raw::toTarget,
                                t -> DataResult.success(Raw.fromTarget(t))
                        )
                );

        private static final StreamCodec<RegistryFriendlyByteBuf, Holder<EntityType<?>>> ENTITY_HOLDER_STREAM =
                ByteBufCodecs.holderRegistry(Registries.ENTITY_TYPE);

        private static final int KIND_ENTITY = 0;
        private static final int KIND_TAG = 1;
        private static final int KIND_EMPTY = 2;

        public static final StreamCodec<RegistryFriendlyByteBuf, Target> STREAM_CODEC =
                StreamCodec.of(
                        (buf, v) -> {

                            Either<Holder<EntityType<?>>, TagKey<EntityType<?>>> t = v.target;
                            if (t == null) {
                                buf.writeByte(KIND_EMPTY);
                                return;
                            }

                            Optional<Holder<EntityType<?>>> left = t.left();
                            if (left.isPresent()) {
                                buf.writeByte(KIND_ENTITY);
                                ENTITY_HOLDER_STREAM.encode(buf, left.get());
                                return;
                            }

                            Optional<TagKey<EntityType<?>>> right = t.right();
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
                                case KIND_ENTITY -> new Target(Either.left(ENTITY_HOLDER_STREAM.decode(buf)));
                                case KIND_TAG -> new Target(Either.right(
                                        TagKey.create(Registries.ENTITY_TYPE, buf.readResourceLocation())
                                ));
                                default -> Target.EMPTY;
                            };
                        }
                );

        @Override
        public @NotNull DataResult<Target> validate() {
            if (target == null) {
                return SelfValidating.invalid(
                        "missing required field '" + JolCraftParameterIds.ENTITY + "' or '" + JolCraftParameterIds.TAG + "'"
                );
            }

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
            if (target == null) return false;

            EntityType<?> type = entity.getType();

            return target.map(
                    h -> h != null && h.value() == type,
                    tag -> tag != null && type.is(tag)
            );
        }

        @SuppressWarnings("deprecation")
        public static Target of(@NotNull EntityType<?> type) {
            return new Target(Either.left(type.builtInRegistryHolder()));
        }

        public static Target of(@NotNull TagKey<EntityType<?>> tag) {
            return new Target(Either.right(tag));
        }

        private record Raw(Optional<Holder<EntityType<?>>> entity, Optional<TagKey<EntityType<?>>> tag) {

            DataResult<Target> toTarget() {
                boolean hasEntity = entity.isPresent();
                boolean hasTag = tag.isPresent();

                if (hasEntity == hasTag) {
                    return DataResult.error(() ->
                            "EntityIngredient.Target requires exactly one of '" + JolCraftParameterIds.ENTITY + "' or '" + JolCraftParameterIds.TAG + "'"
                    );
                }

                return DataResult.success(
                        hasEntity
                                ? new Target(Either.left(entity.get()))
                                : new Target(Either.right(tag.get()))
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

    public static EntityIngredient of(@NotNull EntityType<?> type) {
        return ofTargets(Target.of(type));
    }

    public static EntityIngredient of(@NotNull TagKey<EntityType<?>> tag) {
        return ofTargets(Target.of(tag));
    }

    public static EntityIngredient ofTargets(Target... targets) {
        if (targets == null || targets.length == 0) return EMPTY;

        ArrayList<Target> out = new ArrayList<>(Math.min(targets.length, 16));
        for (Target t : targets) {
            if (t != null && t != Target.EMPTY) out.add(t);
        }

        return out.isEmpty() ? EMPTY : new EntityIngredient(sanitizeTargets(out));
    }

    public static EntityIngredient ofTargets(List<Target> targets) {
        if (targets == null || targets.isEmpty()) return EMPTY;

        ArrayList<Target> out = new ArrayList<>(Math.min(targets.size(), 16));
        for (Target t : targets) {
            if (t != null && t != Target.EMPTY) out.add(t);
        }

        return out.isEmpty() ? EMPTY : new EntityIngredient(sanitizeTargets(out));
    }

    // ---------------------------------------------------------------------
    // CODEC / STREAM
    // ---------------------------------------------------------------------

    private static final Codec<EntityIngredient> RAW_CODEC =
            Codec.either(Target.CODEC, Target.CODEC.listOf())
                    .xmap(
                            either -> either.map(
                                    EntityIngredient::ofTargets,
                                    EntityIngredient::ofTargets
                            ),
                            ing -> {
                                List<Target> list = (ing == null || ing.targets == null) ? List.of() : ing.targets;
                                if (list.size() == 1) return Either.left(list.getFirst());
                                return Either.right(list);
                            }
                    );

    public static final Codec<EntityIngredient> CODEC = ParamCodecs.validated(RAW_CODEC);

    private static final int MAX_TARGETS_STREAM = 2048;

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityIngredient> STREAM_CODEC =
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
                        if (size <= 0) return EntityIngredient.EMPTY;

                        int capped = Math.min(size, MAX_TARGETS_STREAM);
                        ArrayList<Target> list = new ArrayList<>(Math.min(capped, 64));

                        for (int i = 0; i < capped; i++) list.add(Target.STREAM_CODEC.decode(buf));
                        for (int i = capped; i < size; i++) Target.STREAM_CODEC.decode(buf);

                        List<Target> safe = sanitizeTargets(list);
                        return safe.isEmpty() ? EntityIngredient.EMPTY : new EntityIngredient(safe);
                    }
            );

    // ---------------------------------------------------------------------
    // DATA
    // ---------------------------------------------------------------------

    public EntityIngredient(List<Target> targets) {
        this.targets = (targets == null || targets.isEmpty()) ? List.of() : sanitizeTargets(targets);
    }

    public boolean isEmpty() {
        return targets.isEmpty();
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<EntityIngredient> validate() {
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

    public boolean matches(@NotNull Entity entity) {
        if (targets.isEmpty()) return false;

        for (Target t : targets) {
            if (t != null && t.matches(entity)) {
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
        List<Target> list = (targets == null) ? List.of() : targets;

        int holders = 0;
        int tags = 0;

        Holder<?> singleHolder = null;
        TagKey<?> singleTag = null;

        for (Target t : list) {
            if (t == null) continue;

            Either<Holder<EntityType<?>>, TagKey<EntityType<?>>> e = t.target();
            if (e == null) continue;

            Optional<Holder<EntityType<?>>> left = e.left();
            if (left.isPresent()) {
                holders++;
                if (holders == 1 && tags == 0) {
                    singleHolder = left.get();
                } else {
                    singleHolder = null;
                }
                continue;
            }

            Optional<TagKey<EntityType<?>>> right = e.right();
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