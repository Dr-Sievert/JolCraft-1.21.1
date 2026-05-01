package net.sievert.jolcraft.world.recipe.param.output.custom.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.world.recipe.param.level.WorldContext;
import net.sievert.jolcraft.world.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public record EntitySpec(
        @NotNull EntityProducer producer,
        @NotNull IntRange count,
        @Nullable Component name,
        boolean nameVisible,
        @NotNull EntityAttributes attributes,
        @Nullable EntitySpawnConfig spawn
) implements SelfValidating<EntitySpec>, RegistryIntrospectionSource {

    private record CanonicalRaw(
            Optional<Holder<EntityType<?>>> entity,
            Optional<TagKey<EntityType<?>>> tag,
            IntRange count,
            Optional<Component> name,
            boolean nameVisible,
            EntityAttributes attributes,
            Optional<EntitySpawnConfig> spawn
    ) {
        private CanonicalRaw {
            entity = entity != null ? entity : Optional.empty();
            tag = tag != null ? tag : Optional.empty();
            count = count != null ? count : IntRange.ONE;
            name = name != null ? name : Optional.empty();
            attributes = attributes != null ? attributes : EntityAttributes.EMPTY;
            spawn = spawn != null ? spawn : Optional.empty();
        }
    }

    private record VerboseRaw(
            EntityProducer producer,
            IntRange count,
            Optional<Component> name,
            boolean nameVisible,
            EntityAttributes attributes,
            Optional<EntitySpawnConfig> spawn
    ) {
        private VerboseRaw {
            count = count != null ? count : IntRange.ONE;
            name = name != null ? name : Optional.empty();
            attributes = attributes != null ? attributes : EntityAttributes.EMPTY;
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

                    ComponentSerialization.CODEC
                            .optionalFieldOf(JolCraftParameterIds.NAME)
                            .forGetter(CanonicalRaw::name),

                    Codec.BOOL
                            .optionalFieldOf(JolCraftStrings.underscored(JolCraftParameterIds.NAME, JolCraftDictionary.VISIBLE), false)
                            .forGetter(CanonicalRaw::nameVisible),

                    EntityAttributes.CODEC
                            .optionalFieldOf(JolCraftParameterIds.ATTRIBUTES, EntityAttributes.EMPTY)
                            .forGetter(CanonicalRaw::attributes),

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

                    ComponentSerialization.CODEC
                            .optionalFieldOf(JolCraftParameterIds.NAME)
                            .forGetter(VerboseRaw::name),

                    Codec.BOOL
                            .optionalFieldOf(JolCraftStrings.underscored(JolCraftParameterIds.NAME, JolCraftDictionary.VISIBLE), false)
                            .forGetter(VerboseRaw::nameVisible),

                    EntityAttributes.CODEC
                            .optionalFieldOf(JolCraftParameterIds.ATTRIBUTES, EntityAttributes.EMPTY)
                            .forGetter(VerboseRaw::attributes),

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

    private static final StreamCodec<RegistryFriendlyByteBuf, Optional<Component>> OPTIONAL_COMPONENT_STREAM =
            StreamCodec.of(
                    (buf, opt) -> {
                        buf.writeBoolean(opt.isPresent());
                        opt.ifPresent(component -> ComponentSerialization.STREAM_CODEC.encode(buf, component));
                    },
                    buf -> {
                        if (!buf.readBoolean()) {
                            return Optional.empty();
                        }
                        return Optional.of(ComponentSerialization.STREAM_CODEC.decode(buf));
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
                    OPTIONAL_COMPONENT_STREAM, spec -> Optional.ofNullable(spec.name()),
                    ByteBufCodecs.BOOL, EntitySpec::nameVisible,
                    EntityAttributes.STREAM_CODEC, EntitySpec::attributes,
                    OPTIONAL_SPAWN_STREAM, spec -> Optional.ofNullable(spec.spawn()),
                    (producer, count, name, nameVisible, attributes, spawn) -> new EntitySpec(
                            producer,
                            count,
                            name.orElse(null),
                            nameVisible,
                            attributes,
                            spawn.orElse(null)
                    )
            );

    public static @NotNull DataResult<EntitySpec> fromSelection(
            @NotNull Optional<Holder<EntityType<?>>> entity,
            @NotNull Optional<TagKey<EntityType<?>>> tag,
            @NotNull IntRange count,
            @Nullable Component name,
            boolean nameVisible,
            @Nullable EntityAttributes attributes,
            @Nullable EntitySpawnConfig spawn
    ) {
        return EntityProducer.fromSelection(entity, tag)
                .map(producer -> new EntitySpec(
                        producer,
                        count,
                        name,
                        nameVisible,
                        attributes != null ? attributes : EntityAttributes.EMPTY,
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
                    canonical.name().orElse(null),
                    canonical.nameVisible(),
                    canonical.attributes(),
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
                verbose.name().orElse(null),
                verbose.nameVisible(),
                verbose.attributes(),
                verbose.spawn().orElse(null)
        ));
    }

    private static @NotNull Either<CanonicalRaw, VerboseRaw> toRaw(@NotNull EntitySpec spec) {
        return Either.left(new CanonicalRaw(
                spec.producer().entityOpt(),
                spec.producer().tagOpt(),
                spec.count(),
                Optional.ofNullable(spec.name()),
                spec.nameVisible(),
                spec.attributes(),
                Optional.ofNullable(spec.spawn())
        ));
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        List<RegistryIntrospection> producerIntrospections = producer.introspections();
        List<RegistryIntrospection> attributeIntrospections = attributes.introspections();

        if (attributeIntrospections.isEmpty()) {
            return producerIntrospections;
        }
        if (producerIntrospections.isEmpty()) {
            return attributeIntrospections;
        }

        List<RegistryIntrospection> merged =
                new ArrayList<>(producerIntrospections.size() + attributeIntrospections.size());
        merged.addAll(producerIntrospections);
        merged.addAll(attributeIntrospections);
        return List.copyOf(merged);
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

        DataResult<EntityAttributes> attributesValidation = attributes.validate();
        var attributesError = attributesValidation.error();
        if (attributesError.isPresent()) {
            return DataResult.error(() ->
                    JolCraftParameterIds.ATTRIBUTES + " invalid: " + attributesError.get().message());
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
        if (typeOpt.isEmpty()) {
            return Optional.empty();
        }

        RandomSource random = ctx.random();
        int rolled = count.roll(random);
        if (rolled <= 0) {
            return Optional.empty();
        }

        return Optional.of(new RolledEntity(
                typeOpt.get(),
                rolled,
                name,
                nameVisible,
                attributes,
                spawn
        ));
    }

    public record RolledEntity(
            Holder<EntityType<?>> type,
            int count,
            @Nullable Component name,
            boolean nameVisible,
            @NotNull EntityAttributes attributes,
            @Nullable EntitySpawnConfig spawn
    ) {}
}