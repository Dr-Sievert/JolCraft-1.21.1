package net.sievert.jolcraft.param.custom.entity.input.requirement;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.runtime.WorldContext;

import java.util.List;

public record EntityRequirements(List<Atomic> requirements)
        implements ParamData<EntityRequirements> {

    public sealed interface Atomic
            permits BabyAtomic, EffectAtomic, AttributeAtomic, EquipmentAtomic, InvalidAtomic {

        Codec<Atomic> CODEC = ParamCodecs.validated(atomicCodec(), Atomic::validateAtomic);
        StreamCodec<RegistryFriendlyByteBuf, Atomic> STREAM_CODEC =
                ParamCodecs.validatedStream(atomicStreamCodec(), Atomic::validateAtomic);

        boolean matches(WorldContext ctx, Entity entity);

        default DataResult<Atomic> validateAtomic() {
            return switch (this) {
                case BabyAtomic a -> a.req() == null
                        ? ParamValidations.invalid("baby requirement is null")
                        : a.req().validate().map(v -> this);
                case EffectAtomic a -> a.req() == null
                        ? ParamValidations.invalid("effect requirement is null")
                        : a.req().validate().map(v -> this);
                case AttributeAtomic a -> a.req() == null
                        ? ParamValidations.invalid("attribute requirement is null")
                        : a.req().validate().map(v -> this);
                case EquipmentAtomic a -> a.req() == null
                        ? ParamValidations.invalid("equipment requirement is null")
                        : a.req().validate().map(v -> this);
                case InvalidAtomic a -> ParamValidations.invalid("unknown atomic requirement discriminator: " + a.id());
            };
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
                            return ParamValidations.invalid("Atomic requirement must contain exactly one of: "
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
                        return ParamValidations.invalid("atomic requirement is null");
                    }

                    return switch (input) {
                        case BabyAtomic a -> BabyRequirement.CODEC.encodeStart(ops, a.req())
                                .flatMap(v -> ops.mapBuilder().add(JolCraftParameterIds.BABY, v).build(prefix));
                        case EffectAtomic a -> EffectRequirement.CODEC.encodeStart(ops, a.req())
                                .flatMap(v -> ops.mapBuilder().add(JolCraftParameterIds.EFFECT, v).build(prefix));
                        case AttributeAtomic a -> AttributeRequirement.CODEC.encodeStart(ops, a.req())
                                .flatMap(v -> ops.mapBuilder().add(JolCraftParameterIds.ATTRIBUTE, v).build(prefix));
                        case EquipmentAtomic a -> EquipmentRequirement.CODEC.encodeStart(ops, a.req())
                                .flatMap(v -> ops.mapBuilder().add(JolCraftParameterIds.EQUIPMENT, v).build(prefix));
                        case InvalidAtomic a -> ParamValidations.invalid(
                                "unknown atomic requirement discriminator: " + a.id()
                        );
                    };
                }
            };
        }

        private static StreamCodec<RegistryFriendlyByteBuf, Atomic> atomicStreamCodec() {
            return StreamCodec.of(
                    (buf, atomic) -> {
                        switch (atomic) {
                            case BabyAtomic a -> {
                                buf.writeVarInt(0);
                                BabyRequirement.STREAM_CODEC.encode(buf, a.req());
                            }
                            case EffectAtomic a -> {
                                buf.writeVarInt(1);
                                EffectRequirement.STREAM_CODEC.encode(buf, a.req());
                            }
                            case AttributeAtomic a -> {
                                buf.writeVarInt(2);
                                AttributeRequirement.STREAM_CODEC.encode(buf, a.req());
                            }
                            case EquipmentAtomic a -> {
                                buf.writeVarInt(3);
                                EquipmentRequirement.STREAM_CODEC.encode(buf, a.req());
                            }
                            case InvalidAtomic a -> buf.writeVarInt(a.id());
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

    public record BabyAtomic(BabyRequirement req) implements Atomic {
        @Override
        public boolean matches(WorldContext ctx, Entity entity) {
            return req != null && req.matches(entity);
        }
    }

    public record EffectAtomic(EffectRequirement req) implements Atomic {
        @Override
        public boolean matches(WorldContext ctx, Entity entity) {
            return req != null && req.matches(entity);
        }
    }

    public record AttributeAtomic(AttributeRequirement req) implements Atomic {
        @Override
        public boolean matches(WorldContext ctx, Entity entity) {
            return req != null && req.matches(entity);
        }
    }

    public record EquipmentAtomic(EquipmentRequirement req) implements Atomic {
        @Override
        public boolean matches(WorldContext ctx, Entity entity) {
            return req != null && req.matches(ctx, entity);
        }
    }

    public record InvalidAtomic(int id) implements Atomic {
        @Override
        public boolean matches(WorldContext ctx, Entity entity) {
            return false;
        }
    }

    public static final EntityRequirements EMPTY = new EntityRequirements(List.of());

    public static final Codec<EntityRequirements> CODEC =
            ParamCodecs.validated(
                    Atomic.CODEC.listOf()
                            .xmap(EntityRequirements::new, EntityRequirements::requirements),
                    EntityRequirements::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityRequirements> STREAM_CODEC =
            ParamCodecs.validatedStream(
                    Atomic.STREAM_CODEC.apply(ByteBufCodecs.list())
                            .map(EntityRequirements::new, EntityRequirements::requirements),
                    EntityRequirements::validate
            );

    public EntityRequirements {
        requirements = ParamValidations.sanitizeList(requirements);
    }

    public boolean isEmpty() {
        return requirements.isEmpty();
    }

    public boolean matches(WorldContext ctx, Entity entity) {
        if (entity == null) return false;

        for (Atomic requirement : requirements) {
            if (!requirement.matches(ctx, entity)) return false;
        }

        return true;
    }

    @Override
    public DataResult<EntityRequirements> validate() {
        return ParamValidations.children(
                this,
                requirements,
                JolCraftParameterIds.REQUIREMENTS,
                Atomic::validateAtomic
        );
    }

    @Override
    public Codec<EntityRequirements> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EntityRequirements> streamCodec() {
        return STREAM_CODEC;
    }
}