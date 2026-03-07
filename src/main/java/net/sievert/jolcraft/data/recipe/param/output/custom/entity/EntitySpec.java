package net.sievert.jolcraft.data.recipe.param.output.custom.entity;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record EntitySpec(
        EntityProducer producer,
        IntRange count,
        @Nullable CompoundTag nbt,
        @Nullable EntitySpawnConfig spawn
) implements SelfValidating<EntitySpec>, RegistryIntrospectionSource {

    public static final EntitySpec EMPTY = new EntitySpec(EntityProducer.EMPTY, IntRange.ONE, null, null);

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    private static final Codec<EntitySpec> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    EntityProducer.CODEC
                            .fieldOf(JolCraftParameterIds.PRODUCER)
                            .forGetter(EntitySpec::producer),

                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE)
                            .forGetter(EntitySpec::count),

                    CompoundTag.CODEC
                            .optionalFieldOf(JolCraftParameterIds.NBT, null)
                            .forGetter(EntitySpec::nbt),

                    EntitySpawnConfig.CODEC
                            .optionalFieldOf(JolCraftParameterIds.SPAWN, null)
                            .forGetter(EntitySpec::spawn)
            ).apply(inst, EntitySpec::new));

    public static final Codec<EntitySpec> CODEC = ParamCodecs.validated(RAW_CODEC);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    private static void encodeNullableNbt(RegistryFriendlyByteBuf buf, @Nullable CompoundTag tag) {
        buf.writeBoolean(tag != null);
        if (tag != null) buf.writeNbt(tag);
    }

    private static @NotNull CompoundTag decodeNullableNbt(RegistryFriendlyByteBuf buf) {
        boolean present = buf.readBoolean();
        if (!present) return new CompoundTag();

        CompoundTag tag = buf.readNbt();
        return tag != null ? tag : new CompoundTag();
    }

    private static void encodeNullableSpawn(RegistryFriendlyByteBuf buf, @Nullable EntitySpawnConfig spawn) {
        buf.writeBoolean(spawn != null);
        if (spawn != null) {
            EntitySpawnConfig.STREAM_CODEC.encode(buf, spawn);
        }
    }

    private static @NotNull EntitySpawnConfig decodeNullableSpawn(RegistryFriendlyByteBuf buf) {
        boolean present = buf.readBoolean();
        return present ? EntitySpawnConfig.STREAM_CODEC.decode(buf) : EntitySpawnConfig.EMPTY;
    }

    public static final StreamCodec<RegistryFriendlyByteBuf, EntitySpec> STREAM_CODEC =
            StreamCodec.composite(
                    EntityProducer.STREAM_CODEC, EntitySpec::producer,
                    IntRange.STREAM_CODEC, EntitySpec::count,
                    StreamCodec.of(EntitySpec::encodeNullableNbt, EntitySpec::decodeNullableNbt), EntitySpec::nbt,
                    StreamCodec.of(EntitySpec::encodeNullableSpawn, EntitySpec::decodeNullableSpawn), EntitySpec::spawn,
                    EntitySpec::new
            );

    // ---------------------------------------------------------------------
    // DATA
    // ---------------------------------------------------------------------

    public EntitySpec(EntityProducer producer, IntRange count, @Nullable CompoundTag nbt, @Nullable EntitySpawnConfig spawn) {
        this.producer = producer != null ? producer : EntityProducer.EMPTY;
        this.count = count != null ? count : IntRange.ONE;

        this.nbt = (nbt != null && nbt.isEmpty()) ? null : nbt;

        this.spawn = EntitySpawnConfig.normalize(spawn);
    }

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        EntityProducer p = producer != null ? producer : EntityProducer.EMPTY;
        return p.introspections();
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<EntitySpec> validate() {

        DataResult<EntityProducer> pv = producer.validate();
        var perr = pv.error();
        if (perr.isPresent()) {
            return DataResult.error(() -> JolCraftParameterIds.PRODUCER + " invalid: " + perr.get().message());
        }

        DataResult<IntRange> cv = IntRange.validateRange(count);
        var cerr = cv.error();
        if (cerr.isPresent()) {
            return DataResult.error(() -> JolCraftParameterIds.COUNT + " invalid: " + cerr.get().message());
        }

        if (count.min() < 1) {
            return DataResult.error(() ->
                    JolCraftParameterIds.COUNT + ".min must be >= 1 (got " + count.min() + ")");
        }

        if (spawn != null) {
            DataResult<EntitySpawnConfig> sv = spawn.validate();
            var serr = sv.error();
            if (serr.isPresent()) {
                return DataResult.error(() -> JolCraftParameterIds.SPAWN + " invalid: " + serr.get().message());
            }
        }

        return DataResult.success(this);
    }

    // ---------------------------------------------------------------------
    // RUNTIME
    // ---------------------------------------------------------------------

    public Optional<RolledEntity> roll(@NotNull WorldContext ctx) {
        RandomSource random = ctx.random();
        Optional<Holder<EntityType<?>>> typeOpt = producer.select(ctx);
        if (typeOpt.isEmpty()) return Optional.empty();

        int rolled = count.roll(random);
        if (rolled <= 0) return Optional.empty();

        return Optional.of(new RolledEntity(typeOpt.get(), rolled, nbt, spawn));
    }

    public record RolledEntity(
            Holder<EntityType<?>> type,
            int count,
            @Nullable CompoundTag nbt,
            @Nullable EntitySpawnConfig spawn
    ) {}
}