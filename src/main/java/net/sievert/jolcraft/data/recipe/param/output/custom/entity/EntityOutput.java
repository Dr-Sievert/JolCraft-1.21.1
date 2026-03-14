package net.sievert.jolcraft.data.recipe.param.output.custom.entity;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.data.recipe.param.base.ParamTypeDef;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record EntityOutput(
        @NotNull EntitySpec result
) implements OutputParam, SelfValidating<EntityOutput>, RegistryIntrospectionSource {

    public static final ResourceLocation TYPE_ID =
            JolCraft.location(JolCraftStrings.underscored(
                    JolCraftParameterIds.ENTITY,
                    JolCraftDictionary.OUTPUT
            ));

    public static final byte DISC = 7;

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

    private record VerboseRaw(EntitySpec result) {}

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
                            .optionalFieldOf(
                                    JolCraftStrings.underscored(JolCraftParameterIds.NAME, JolCraftDictionary.VISIBLE),
                                    false
                            )
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
                    EntitySpec.CODEC
                            .fieldOf(JolCraftParameterIds.RESULT)
                            .forGetter(VerboseRaw::result)
            ).apply(inst, VerboseRaw::new));

    public static final Codec<EntityOutput> CODEC =
            ParamCodecContract.create(
                    Codec.either(CANONICAL_RAW_CODEC, VERBOSE_RAW_CODEC),
                    EntityOutput::fromRaw,
                    EntityOutput::toRaw
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityOutput> STREAM_CODEC =
            StreamCodec.composite(
                    EntitySpec.STREAM_CODEC, EntityOutput::result,
                    EntityOutput::new
            );

    public static final ParamTypeDef<OutputParam> TYPE_DEF =
            new ParamTypeDef<>(TYPE_ID, DISC, CODEC, STREAM_CODEC);

    public EntityOutput {
        Objects.requireNonNull(result, JolCraftParameterIds.RESULT);
    }

    private static @NotNull DataResult<EntityOutput> fromRaw(@NotNull Either<CanonicalRaw, VerboseRaw> raw) {
        if (raw.left().isPresent()) {
            CanonicalRaw canonical = raw.left().orElseThrow();

            return EntitySpec.fromSelection(
                    canonical.entity(),
                    canonical.tag(),
                    canonical.count(),
                    canonical.name().orElse(null),
                    canonical.nameVisible(),
                    canonical.attributes(),
                    canonical.spawn().orElse(null)
            ).map(EntityOutput::new);
        }

        VerboseRaw verbose = raw.right().orElseThrow();
        if (verbose.result() == null) {
            return DataResult.error(() -> JolCraftParameterIds.RESULT + " is required");
        }

        return DataResult.success(new EntityOutput(verbose.result()));
    }

    private static @NotNull Either<CanonicalRaw, VerboseRaw> toRaw(@NotNull EntityOutput out) {
        EntitySpec spec = out.result();
        EntityProducer producer = spec.producer();

        return Either.left(new CanonicalRaw(
                producer.entityOpt(),
                producer.tagOpt(),
                spec.count(),
                Optional.ofNullable(spec.name()),
                spec.nameVisible(),
                spec.attributes(),
                Optional.ofNullable(spec.spawn())
        ));
    }

    @Override
    public @NotNull ResourceLocation typeId() {
        return TYPE_ID;
    }

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        return result.introspections();
    }

    @Override
    public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
        Optional<EntitySpec.RolledEntity> rolledOpt = result.roll(ctx);
        if (rolledOpt.isEmpty()) {
            return List.of();
        }

        EntitySpec.RolledEntity rolled = rolledOpt.get();
        var pos = rolled.spawn() != null ? rolled.spawn().pos() : null;

        Output.EntitySpec spec = new Output.EntitySpec(
                rolled.type(),
                rolled.count(),
                pos,
                rolled.name(),
                rolled.nameVisible(),
                rolled.attributes(),
                rolled.spawn()
        );

        return List.of(new Output.Entities(List.of(spec)));
    }

    @Override
    public @NotNull DataResult<EntityOutput> validate() {
        DataResult<EntitySpec> resultValidation = result.validate();
        var error = resultValidation.error();

        return error.<DataResult<EntityOutput>>map(entitySpecError -> DataResult.error(() ->
                "'" + JolCraftParameterIds.RESULT + "' invalid: " + entitySpecError.message()
        )).orElseGet(() -> SelfValidating.ok(this));
    }
}