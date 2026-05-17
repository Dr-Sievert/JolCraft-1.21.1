package net.sievert.jolcraft.param.custom.entity.input;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.custom.condition.Conditions;
import net.sievert.jolcraft.param.custom.entity.input.requirement.EntityRequirements;
import net.sievert.jolcraft.param.custom.entity.input.selection.EntityIngredient;
import net.sievert.jolcraft.param.custom.entity.input.selection.EntitySelector;
import net.sievert.jolcraft.param.custom.entity.input.selection.EntityTarget;
import net.sievert.jolcraft.param.custom.input.InputParam;
import net.sievert.jolcraft.param.custom.quantity.IntRange;
import net.sievert.jolcraft.param.runtime.WorldContext;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.Optional;

public record EntityInput(
        Conditions conditions,
        EntitySelector selector,
        IntRange count,
        EntityRequirements requirements
) implements InputParam<Entity>, ParamData<EntityInput> {

    public static final String KEY =
            JolCraftStrings.underscored(JolCraftDictionary.ENTITY, JolCraftParameterIds.INPUT);

    public static final ResourceLocation TYPE_ID = JolCraft.location(KEY);

    private static final EntityRequirements EMPTY_REQUIREMENTS = EntityRequirements.EMPTY;

    private record Raw(
            Conditions conditions,
            Optional<EntitySelector> selector,
            Optional<EntityTarget> entity,
            Optional<EntityIngredient> entities,
            IntRange count,
            EntityRequirements requirements
    ) {
        private Raw {
            conditions = conditions == null ? Conditions.EMPTY : conditions;
            selector = selector == null ? Optional.empty() : selector;
            entity = entity == null ? Optional.empty() : entity;
            entities = entities == null ? Optional.empty() : entities;
            count = count == null ? IntRange.ONE : count;
            requirements = requirements == null ? EMPTY_REQUIREMENTS : requirements;
        }
    }

    private static final Codec<Raw> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Conditions.CODEC.optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(Raw::conditions),
                    EntitySelector.CODEC.optionalFieldOf(JolCraftParameterIds.SELECTOR)
                            .forGetter(Raw::selector),
                    EntityTarget.CODEC.optionalFieldOf(JolCraftParameterIds.ENTITY)
                            .forGetter(Raw::entity),
                    EntityIngredient.CODEC.optionalFieldOf(JolCraftParameterIds.ENTITIES)
                            .forGetter(Raw::entities),
                    IntRange.POSITIVE_CODEC.optionalFieldOf(JolCraftParameterIds.COUNT, IntRange.ONE)
                            .forGetter(Raw::count),
                    EntityRequirements.CODEC.optionalFieldOf(JolCraftParameterIds.REQUIREMENTS, EMPTY_REQUIREMENTS)
                            .forGetter(Raw::requirements)
            ).apply(inst, Raw::new));

    public static final Codec<EntityInput> CODEC =
            ParamCodecs.validated(
                    RAW_CODEC.flatXmap(EntityInput::fromRaw, input -> ParamValidations.ok(input.toRaw())),
                    EntityInput::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityInput> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.composite(
                    Conditions.STREAM_CODEC,
                    EntityInput::conditions,
                    EntitySelector.STREAM_CODEC,
                    EntityInput::selector,
                    IntRange.STREAM_CODEC,
                    EntityInput::count,
                    EntityRequirements.STREAM_CODEC,
                    EntityInput::requirements,
                    EntityInput::new
            ), EntityInput::validate);

    public EntityInput {
        conditions = conditions == null ? Conditions.EMPTY : conditions;
        count = count == null ? IntRange.ONE : count;
        requirements = requirements == null ? EMPTY_REQUIREMENTS : requirements;

        if (selector == null) {
            throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.SELECTOR + "'");
        }
    }

    private static DataResult<EntityInput> fromRaw(Raw raw) {
        int sources = 0;
        if (raw.selector().isPresent()) sources++;
        if (raw.entity().isPresent()) sources++;
        if (raw.entities().isPresent()) sources++;

        if (sources != 1) {
            return ParamValidations.invalid(
                    "requires exactly one of '" + JolCraftParameterIds.SELECTOR
                            + "', '" + JolCraftParameterIds.ENTITY
                            + "', or '" + JolCraftParameterIds.ENTITIES + "'"
            );
        }

        EntitySelector selector = raw.selector()
                .orElseGet(() -> raw.entities()
                        .map(EntitySelector::of)
                        .orElseGet(() -> EntitySelector.of(raw.entity().orElseThrow())));

        return new EntityInput(raw.conditions(), selector, raw.count(), raw.requirements()).validate();
    }

    private Raw toRaw() {
        EntityIngredient flat = tryFlatten(selector);

        if (flat == null) {
            return new Raw(
                    conditions,
                    Optional.of(selector),
                    Optional.empty(),
                    Optional.empty(),
                    count,
                    requirements
            );
        }

        if (flat.isSingleTarget()) {
            return new Raw(
                    conditions,
                    Optional.empty(),
                    Optional.of(flat.singleTarget()),
                    Optional.empty(),
                    count,
                    requirements
            );
        }

        return new Raw(
                conditions,
                Optional.empty(),
                Optional.empty(),
                Optional.of(flat),
                count,
                requirements
        );
    }

    private static EntityIngredient tryFlatten(EntitySelector selector) {
        return selector.isSimple() ? selector.simpleIngredient() : null;
    }

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public boolean matches(WorldContext ctx, Entity entity) {
        return count.isPositiveRange()
                && conditions.matches(ctx)
                && selector.matches(ctx, entity)
                && requirements.matches(ctx, entity);
    }

    public boolean isEmpty() {
        return selector.entries().isEmpty()
                && requirements.isEmpty()
                && count.equals(IntRange.ONE)
                && conditions.isEmpty();
    }

    @Override
    public Codec<EntityInput> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EntityInput> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public DataResult<EntityInput> validate() {
        return ParamValidations.all(this,
                () -> ParamValidations.child(this, conditions, JolCraftParameterIds.CONDITIONS),
                () -> ParamValidations.child(this, selector, JolCraftParameterIds.SELECTOR),
                () -> ParamValidations.wrap(this, IntRange.validatePositiveRange(count), JolCraftParameterIds.COUNT),
                () -> ParamValidations.child(this, requirements, JolCraftParameterIds.REQUIREMENTS)
        );
    }
}