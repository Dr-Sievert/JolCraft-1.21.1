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
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
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
                            .optionalFieldOf(JolCraftParameterIds.NBT)
                            .forGetter(spec -> Optional.ofNullable(spec.nbt())),

                    EntitySpawnConfig.CODEC
                            .optionalFieldOf(JolCraftParameterIds.SPAWN)
                            .forGetter(spec -> Optional.ofNullable(spec.spawn()))
            ).apply(inst, (producer, count, nbt, spawn) ->
                    new EntitySpec(
                            producer,
                            count,
                            nbt.orElse(null),
                            spawn.orElse(null)
                    )
            ));

    public static final Codec<EntitySpec> CODEC =
            ParamCodecs.validated(RAW_CODEC);

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    private static final StreamCodec<RegistryFriendlyByteBuf, Optional<CompoundTag>> OPTIONAL_NBT_STREAM =
            StreamCodec.of(
                    (buf, opt) -> {
                        buf.writeBoolean(opt.isPresent());
                        opt.ifPresent(buf::writeNbt);
                    },
                    buf -> {
                        if (!buf.readBoolean()) {
                            return Optional.empty();
                        }
                        return Optional.ofNullable(buf.readNbt());
                    }
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, Optional<EntitySpawnConfig>> OPTIONAL_SPAWN_STREAM =
            StreamCodec.of(
                    (buf, opt) -> {
                        buf.writeBoolean(opt.isPresent());
                        opt.ifPresent(entitySpawnConfig -> EntitySpawnConfig.STREAM_CODEC.encode(buf, entitySpawnConfig));
                    },
                    buf -> {
                        if (!buf.readBoolean()) {
                            return Optional.empty();
                        }
                        return Optional.of(EntitySpawnConfig.STREAM_CODEC.decode(buf));
                    }
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, EntitySpec> STREAM_CODEC =
            StreamCodec.composite(
                    EntityProducer.STREAM_CODEC, EntitySpec::producer,
                    IntRange.STREAM_CODEC, EntitySpec::count,
                    OPTIONAL_NBT_STREAM, spec -> Optional.ofNullable(spec.nbt()),
                    OPTIONAL_SPAWN_STREAM, spec -> Optional.ofNullable(spec.spawn()),
                    (producer, count, nbt, spawn) -> new EntitySpec(
                            producer,
                            count,
                            nbt.orElse(null),
                            spawn.orElse(null)
                    )
            );

    // ---------------------------------------------------------------------
    // DATA
    // ---------------------------------------------------------------------

    public EntitySpec {
        if (nbt != null && nbt.isEmpty()) {
            nbt = null;
        }
    }

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return producer.introspections();
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<EntitySpec> validate() {

        if (producer == null) {
            return DataResult.error(() -> "'" + JolCraftParameterIds.PRODUCER + "' is required");
        }

        if (count == null) {
            return DataResult.error(() -> "'" + JolCraftParameterIds.COUNT + "' is required");
        }

        DataResult<EntityProducer> pv = producer.validate();
        var perr = pv.error();
        if (perr.isPresent()) {
            return DataResult.error(() ->
                    JolCraftParameterIds.PRODUCER + " invalid: " + perr.get().message());
        }

        DataResult<IntRange> cv = IntRange.validateRange(count);
        var cerr = cv.error();
        if (cerr.isPresent()) {
            return DataResult.error(() ->
                    JolCraftParameterIds.COUNT + " invalid: " + cerr.get().message());
        }

        if (count.min() < 1) {
            return DataResult.error(() ->
                    JolCraftParameterIds.COUNT + ".min must be >= 1 (got " + count.min() + ")");
        }

        if (spawn != null) {
            DataResult<EntitySpawnConfig> sv = spawn.validate();
            var serr = sv.error();
            if (serr.isPresent()) {
                return DataResult.error(() ->
                        JolCraftParameterIds.SPAWN + " invalid: " + serr.get().message());
            }
        }

        return DataResult.success(this);
    }

    // ---------------------------------------------------------------------
    // RUNTIME
    // ---------------------------------------------------------------------

    public Optional<RolledEntity> roll(@NotNull WorldContext ctx) {
        Optional<Holder<EntityType<?>>> typeOpt = producer.select(ctx);
        if (typeOpt.isEmpty()) return Optional.empty();

        RandomSource random = ctx.random();
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