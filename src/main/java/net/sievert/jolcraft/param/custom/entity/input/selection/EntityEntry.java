package net.sievert.jolcraft.param.custom.entity.input.selection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.custom.condition.Conditions;

import java.util.Optional;

public record EntityEntry(
        Conditions conditions,
        EntityIngredient ingredient
) implements ParamData<EntityEntry> {

    public EntityEntry {
        conditions = conditions != null ? conditions : Conditions.EMPTY;

        if (ingredient == null) {
            throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.ENTITIES + "'");
        }
    }

    private record Raw(
            Conditions conditions,
            Optional<EntityTarget> entity,
            Optional<EntityIngredient> entities
    ) {
        private Raw {
            conditions = conditions == null ? Conditions.EMPTY : conditions;
            entity = entity == null ? Optional.empty() : entity;
            entities = entities == null ? Optional.empty() : entities;
        }
    }

    public static EntityEntry of(EntityIngredient ingredient) {
        return new EntityEntry(Conditions.EMPTY, ingredient);
    }

    public static EntityEntry of(EntityTarget target) {
        return of(EntityIngredient.of(target));
    }

    private static final Codec<Raw> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Conditions.CODEC.optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(Raw::conditions),
                    EntityTarget.CODEC.optionalFieldOf(JolCraftParameterIds.ENTITY)
                            .forGetter(Raw::entity),
                    EntityIngredient.CODEC.optionalFieldOf(JolCraftParameterIds.ENTITIES)
                            .forGetter(Raw::entities)
            ).apply(inst, Raw::new));

    public static final Codec<EntityEntry> CODEC =
            ParamCodecs.validated(
                    RAW_CODEC.flatXmap(EntityEntry::fromRaw, entry -> ParamValidations.ok(entry.toRaw())),
                    EntityEntry::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityEntry> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.composite(
                    Conditions.STREAM_CODEC,
                    EntityEntry::conditions,
                    EntityIngredient.STREAM_CODEC,
                    EntityEntry::ingredient,
                    EntityEntry::new
            ), EntityEntry::validate);

    private static DataResult<EntityEntry> fromRaw(Raw raw) {
        boolean hasEntity = raw.entity().isPresent();
        boolean hasEntities = raw.entities().isPresent();

        if (hasEntity == hasEntities) {
            return ParamValidations.invalid(
                    "requires exactly one of '" + JolCraftParameterIds.ENTITY
                            + "' or '" + JolCraftParameterIds.ENTITIES + "'"
            );
        }

        EntityIngredient ingredient = hasEntity
                ? EntityIngredient.of(raw.entity().orElseThrow())
                : raw.entities().orElseThrow();

        return new EntityEntry(raw.conditions(), ingredient).validate();
    }

    private Raw toRaw() {
        if (ingredient.isSingleTarget()) {
            return new Raw(
                    conditions,
                    Optional.of(ingredient.singleTarget()),
                    Optional.empty()
            );
        }

        return new Raw(
                conditions,
                Optional.empty(),
                Optional.of(ingredient)
        );
    }

    @Override
    public Codec<EntityEntry> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EntityEntry> streamCodec() {
        return STREAM_CODEC;
    }


    @Override
    public DataResult<EntityEntry> validate() {
        return ParamValidations.all(this,
                () -> ParamValidations.child(this, conditions, JolCraftParameterIds.CONDITIONS),
                () -> ParamValidations.child(this, ingredient, JolCraftParameterIds.ENTITIES)
        );
    }
}