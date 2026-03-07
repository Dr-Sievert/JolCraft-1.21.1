package net.sievert.jolcraft.data.recipe.param.input.custom.entity.requirement;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public record EntityRequirements(List<Atomic> requirements)
        implements SelfValidating<EntityRequirements>, RegistryIntrospectionSource {

    // ---------------------------------------------------------------------
    // Atomic union
    // ---------------------------------------------------------------------

    public sealed interface Atomic extends RegistryIntrospectionSource permits BabyAtomic, EffectAtomic, AttributeAtomic, EquipmentAtomic, InvalidAtomic {

        Codec<Atomic> CODEC = atomicCodec();
        StreamCodec<RegistryFriendlyByteBuf, Atomic> STREAM_CODEC = streamCodec();

        boolean matches(@NotNull WorldContext ctx, Entity entity);

        default @NotNull List<RegistryIntrospection> introspections() {
            return List.of();
        }

        private static Codec<Atomic> atomicCodec() {
            return new Codec<>() {
                @Override
                public <T> DataResult<Pair<Atomic, T>> decode(DynamicOps<T> ops, T input) {
                    return ops.getMap(input).flatMap(map -> {

                        boolean hasBaby = map.get(JolCraftParameterIds.BABY) != null;
                        boolean hasEffect = map.get(JolCraftParameterIds.EFFECT) != null;
                        boolean hasAttribute = map.get(JolCraftParameterIds.ATTRIBUTE) != null;
                        boolean hasEquipment = map.get(JolCraftParameterIds.EQUIPMENT) != null;

                        int count = 0;
                        if (hasBaby) count++;
                        if (hasEffect) count++;
                        if (hasAttribute) count++;
                        if (hasEquipment) count++;

                        if (count != 1) {
                            return DataResult.error(() -> "Atomic requirement must contain exactly one of: "
                                    + JolCraftParameterIds.BABY + ", "
                                    + JolCraftParameterIds.EFFECT + ", "
                                    + JolCraftParameterIds.ATTRIBUTE + ", "
                                    + JolCraftParameterIds.EQUIPMENT);
                        }

                        if (hasBaby) {
                            return BabyRequirement.CODEC.parse(ops, map.get(JolCraftParameterIds.BABY))
                                    .map(req -> Pair.of(new BabyAtomic(req), input));
                        }
                        if (hasEffect) {
                            return EffectRequirement.CODEC.parse(ops, map.get(JolCraftParameterIds.EFFECT))
                                    .map(req -> Pair.of(new EffectAtomic(req), input));
                        }
                        if (hasAttribute) {
                            return AttributeRequirement.CODEC.parse(ops, map.get(JolCraftParameterIds.ATTRIBUTE))
                                    .map(req -> Pair.of(new AttributeAtomic(req), input));
                        }

                        return EquipmentRequirement.CODEC.parse(ops, map.get(JolCraftParameterIds.EQUIPMENT))
                                .map(req -> Pair.of(new EquipmentAtomic(req), input));
                    });
                }

                @Override
                public <T> DataResult<T> encode(Atomic input, DynamicOps<T> ops, T prefix) {
                    if (input == null) {
                        return ops.mapBuilder().build(prefix);
                    }

                    return switch (input) {
                        case BabyAtomic a -> BabyRequirement.CODEC.encodeStart(ops, a.req)
                                .flatMap(v -> ops.mapBuilder().add(JolCraftParameterIds.BABY, v).build(prefix));
                        case EffectAtomic a -> EffectRequirement.CODEC.encodeStart(ops, a.req)
                                .flatMap(v -> ops.mapBuilder().add(JolCraftParameterIds.EFFECT, v).build(prefix));
                        case AttributeAtomic a -> AttributeRequirement.CODEC.encodeStart(ops, a.req)
                                .flatMap(v -> ops.mapBuilder().add(JolCraftParameterIds.ATTRIBUTE, v).build(prefix));
                        case EquipmentAtomic a -> EquipmentRequirement.CODEC.encodeStart(ops, a.req)
                                .flatMap(v -> ops.mapBuilder().add(JolCraftParameterIds.EQUIPMENT, v).build(prefix));
                        case InvalidAtomic ignored -> ops.mapBuilder().build(prefix);
                    };
                }
            };
        }

        private static StreamCodec<RegistryFriendlyByteBuf, Atomic> streamCodec() {
            return StreamCodec.of(
                    (buf, atomic) -> {
                        switch (atomic) {
                            case BabyAtomic a -> {
                                buf.writeVarInt(0);
                                BabyRequirement.STREAM_CODEC.encode(buf, a.req);
                            }
                            case EffectAtomic a -> {
                                buf.writeVarInt(1);
                                EffectRequirement.STREAM_CODEC.encode(buf, a.req);
                            }
                            case AttributeAtomic a -> {
                                buf.writeVarInt(2);
                                AttributeRequirement.STREAM_CODEC.encode(buf, a.req);
                            }
                            case EquipmentAtomic a -> {
                                buf.writeVarInt(3);
                                EquipmentRequirement.STREAM_CODEC.encode(buf, a.req);
                            }
                            case InvalidAtomic a -> buf.writeVarInt(a.id);
                        }
                    },
                    buf -> {
                        int id = buf.readVarInt();
                        return switch (id) {
                            case 0 -> new BabyAtomic(BabyRequirement.STREAM_CODEC.decode(buf));
                            case 1 -> new EffectAtomic(EffectRequirement.STREAM_CODEC.decode(buf));
                            case 2 -> new AttributeAtomic(AttributeRequirement.STREAM_CODEC.decode(buf));
                            case 3 -> new EquipmentAtomic(EquipmentRequirement.STREAM_CODEC.decode(buf));
                            default -> new InvalidAtomic(id);
                        };
                    }
            );
        }
    }

    public static final class BabyAtomic implements Atomic {
        private final BabyRequirement req;

        public BabyAtomic(BabyRequirement req) {
            this.req = req;
        }

        @Override
        public boolean matches(@NotNull WorldContext ctx, Entity entity) {
            if (entity == null) return false;
            return req != null && req.matches(entity);
        }
    }

    public static final class EffectAtomic implements Atomic {
        private final EffectRequirement req;

        public EffectAtomic(EffectRequirement req) {
            this.req = req;
        }

        @Override
        public boolean matches(@NotNull WorldContext ctx, Entity entity) {
            if (entity == null) return false;
            return req != null && req.matches(entity);
        }

        @Override
        public @NotNull List<RegistryIntrospection> introspections() {
            return req == null ? List.of() : req.asList();
        }
    }

    public static final class AttributeAtomic implements Atomic {
        private final AttributeRequirement req;

        public AttributeAtomic(AttributeRequirement req) {
            this.req = req;
        }

        @Override
        public boolean matches(@NotNull WorldContext ctx, Entity entity) {
            if (entity == null) return false;
            return req != null && req.matches(entity);
        }

        @Override
        public @NotNull List<RegistryIntrospection> introspections() {
            return req == null ? List.of() : req.asList();
        }
    }

    public static final class EquipmentAtomic implements Atomic {
        private final EquipmentRequirement req;

        public EquipmentAtomic(EquipmentRequirement req) {
            this.req = req;
        }

        @Override
        public boolean matches(@NotNull WorldContext ctx, Entity entity) {
            if (entity == null) return false;
            return req != null && req.matches(ctx, entity);
        }

        @Override
        public @NotNull List<RegistryIntrospection> introspections() {
            if (req == null) return List.of();

            var item = req.item();
            if (item instanceof RegistryIntrospectionSource s) {
                return s.introspections();
            }
            return List.of();
        }
    }

    public static final class InvalidAtomic implements Atomic {
        private final int id;

        public InvalidAtomic(int id) {
            this.id = id;
        }

        @Override
        public boolean matches(@NotNull WorldContext ctx, Entity entity) {
            return false;
        }
    }

    // ---------------------------------------------------------------------
    // EntityRequirements bundle
    // ---------------------------------------------------------------------

    public static final EntityRequirements EMPTY = new EntityRequirements(List.of());

    private static final Codec<EntityRequirements> RAW_CODEC =
            Atomic.CODEC.listOf().xmap(EntityRequirements::new, EntityRequirements::requirements);

    public static final Codec<EntityRequirements> CODEC = ParamCodecs.validated(RAW_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityRequirements> STREAM_CODEC =
            StreamCodec.of(
                    (buf, reqs) -> {
                        List<Atomic> list = reqs.requirements;
                        int size = (list == null) ? 0 : list.size();

                        buf.writeVarInt(size);
                        if (size == 0) return;

                        for (Atomic a : list) {
                            Atomic.STREAM_CODEC.encode(buf, a);
                        }
                    },
                    buf -> {
                        int size = buf.readVarInt();
                        if (size <= 0) {
                            return EntityRequirements.EMPTY;
                        }
                        ArrayList<Atomic> list = new ArrayList<>(size);
                        for (int i = 0; i < size; i++) {
                            list.add(Atomic.STREAM_CODEC.decode(buf));
                        }
                        return new EntityRequirements(list);
                    }
            );

    public EntityRequirements(List<Atomic> requirements) {
        if (requirements == null || requirements.isEmpty()) {
            this.requirements = List.of();
            return;
        }

        ArrayList<Atomic> safe = new ArrayList<>(requirements.size());
        for (Atomic a : requirements) {
            if (a != null) safe.add(a);
        }

        this.requirements = safe.isEmpty() ? List.of() : List.copyOf(safe);
    }

    @Override
    public @NotNull DataResult<EntityRequirements> validate() {
        return SelfValidating.ok(this);
    }

    public boolean matches(@NotNull WorldContext ctx, Entity entity) {
        if (entity == null) return false;

        for (Atomic r : requirements) {
            if (r == null) return false;
            if (!r.matches(ctx, entity)) return false;
        }
        return true;
    }

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        if (requirements == null || requirements.isEmpty()) return List.of();
        return RegistryIntrospectionSource.mergeByRegistry(requirements);
    }
}