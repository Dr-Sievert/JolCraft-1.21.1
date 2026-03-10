package net.sievert.jolcraft.data.recipe.param.output.custom.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public record EntitySpec(
        EntityProducer producer,
        IntRange count,
        @Nullable CompoundTag nbt,
        @Nullable EntitySpawnConfig spawn
) implements SelfValidating<EntitySpec>, RegistryIntrospectionSource {

    private record CanonicalRaw(
            Optional<Holder<EntityType<?>>> entity,
            Optional<TagKey<EntityType<?>>> tag,
            IntRange count,
            Optional<CompoundTag> nbt,
            Optional<EntitySpawnConfig> spawn
    ) {
        private CanonicalRaw {
            entity = entity != null ? entity : Optional.empty();
            tag = tag != null ? tag : Optional.empty();
            count = count != null ? count : IntRange.ONE;
            nbt = nbt != null ? nbt : Optional.empty();
            spawn = spawn != null ? spawn : Optional.empty();
        }
    }

    private record VerboseRaw(
            EntityProducer producer,
            IntRange count,
            Optional<CompoundTag> nbt,
            Optional<EntitySpawnConfig> spawn
    ) {
        private VerboseRaw {
            count = count != null ? count : IntRange.ONE;
            nbt = nbt != null ? nbt : Optional.empty();
            spawn = spawn != null ? spawn : Optional.empty();
        }
    }

    private static final Codec<CanonicalRaw> CANONICAL_RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    EntityProducer.ENTITY_HOLDER_CODEC
                            .optionalFieldOf(EntityProducer.ENTITY)
                            .forGetter(CanonicalRaw::entity),

                    EntityProducer.ENTITY_TAG_CODEC
                            .optionalFieldOf(EntityProducer.TAG)
                            .forGetter(CanonicalRaw::tag),

                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE)
                            .forGetter(CanonicalRaw::count),

                    CompoundTag.CODEC
                            .optionalFieldOf(JolCraftParameterIds.NBT)
                            .forGetter(CanonicalRaw::nbt),

                    EntitySpawnConfig.CODEC
                            .optionalFieldOf(JolCraftParameterIds.SPAWN)
                            .forGetter(CanonicalRaw::spawn)
            ).apply(inst, CanonicalRaw::new));

    private static final Codec<VerboseRaw> VERBOSE_RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    EntityProducer.CODEC
                            .fieldOf(JolCraftParameterIds.PRODUCER)
                            .forGetter(VerboseRaw::producer),

                    IntRange.CODEC
                            .optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE)
                            .forGetter(VerboseRaw::count),

                    CompoundTag.CODEC
                            .optionalFieldOf(JolCraftParameterIds.NBT)
                            .forGetter(VerboseRaw::nbt),

                    EntitySpawnConfig.CODEC
                            .optionalFieldOf(JolCraftParameterIds.SPAWN)
                            .forGetter(VerboseRaw::spawn)
            ).apply(inst, VerboseRaw::new));

    public static final Codec<EntitySpec> CODEC =
            ParamCodecContract.create(
                    Codec.either(CANONICAL_RAW_CODEC, VERBOSE_RAW_CODEC),
                    EntitySpec::fromRaw,
                    EntitySpec::toRaw
            );

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
                        opt.ifPresent(v -> EntitySpawnConfig.STREAM_CODEC.encode(buf, v));
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

    public EntitySpec {
        if (producer == null) {
            throw new IllegalArgumentException("'" + JolCraftParameterIds.PRODUCER + "' is required");
        }
        count = count != null ? count : IntRange.ONE;
        if (nbt != null && nbt.isEmpty()) {
            nbt = null;
        }
    }

    public static @NotNull DataResult<EntitySpec> fromSelection(
            @NotNull Optional<Holder<EntityType<?>>> entity,
            @NotNull Optional<TagKey<EntityType<?>>> tag,
            @NotNull IntRange count,
            @Nullable CompoundTag nbt,
            @Nullable EntitySpawnConfig spawn
    ) {
        return EntityProducer.fromSelection(entity, tag)
                .map(producer -> new EntitySpec(
                        producer,
                        count,
                        nbt != null && nbt.isEmpty() ? null : nbt,
                        spawn
                ));
    }

    private static @NotNull DataResult<EntitySpec> fromRaw(
            @NotNull Either<CanonicalRaw, VerboseRaw> raw
    ) {
        if (raw.left().isPresent()) {
            CanonicalRaw canonical = raw.left().orElseThrow();
            return fromSelection(
                    canonical.entity(),
                    canonical.tag(),
                    canonical.count(),
                    canonical.nbt().orElse(null),
                    canonical.spawn().orElse(null)
            );
        }

        VerboseRaw verbose = raw.right().orElseThrow();
        if (verbose.producer() == null) {
            return DataResult.error(() -> JolCraftParameterIds.PRODUCER + " is required");
        }

        return DataResult.success(new EntitySpec(
                verbose.producer(),
                verbose.count(),
                verbose.nbt().orElse(null),
                verbose.spawn().orElse(null)
        ));
    }

    private static @NotNull Either<CanonicalRaw, VerboseRaw> toRaw(@NotNull EntitySpec spec) {
        return Either.left(new CanonicalRaw(
                spec.producer().entityOpt(),
                spec.producer().tagOpt(),
                spec.count(),
                Optional.ofNullable(spec.nbt()),
                Optional.ofNullable(spec.spawn())
        ));
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return producer.introspections();
    }

    @Override
    public @NotNull DataResult<EntitySpec> validate() {
        DataResult<EntityProducer> producerValidation = producer.validate();
        var producerError = producerValidation.error();
        if (producerError.isPresent()) {
            return DataResult.error(() ->
                    JolCraftParameterIds.PRODUCER + " invalid: " + producerError.get().message());
        }

        DataResult<IntRange> countValidation = IntRange.validateRange(count);
        var countError = countValidation.error();
        if (countError.isPresent()) {
            return DataResult.error(() ->
                    JolCraftParameterIds.COUNT + " invalid: " + countError.get().message());
        }

        if (count.min() < 1) {
            return DataResult.error(() ->
                    JolCraftParameterIds.COUNT + ".min must be >= 1 (got " + count.min() + ")");
        }

        if (spawn != null) {
            DataResult<EntitySpawnConfig> spawnValidation = spawn.validate();
            var spawnError = spawnValidation.error();
            if (spawnError.isPresent()) {
                return DataResult.error(() ->
                        JolCraftParameterIds.SPAWN + " invalid: " + spawnError.get().message());
            }
        }

        return SelfValidating.ok(this);
    }

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