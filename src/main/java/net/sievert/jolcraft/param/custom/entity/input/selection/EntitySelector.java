package net.sievert.jolcraft.param.custom.entity.input.selection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.custom.condition.Conditions;
import net.sievert.jolcraft.param.runtime.WorldContext;

import java.util.List;

public record EntitySelector(
        Conditions conditions,
        List<EntityEntry> entries
) implements ParamData<EntitySelector> {

    public EntitySelector {
        conditions = conditions != null ? conditions : Conditions.EMPTY;
        entries = ParamValidations.sanitizeList(entries);
    }

    public static EntitySelector of(List<EntityTarget> targets) {
        return of(EntityIngredient.of(targets));
    }

    public static EntitySelector of(EntityIngredient ingredient) {
        return new EntitySelector(Conditions.EMPTY, List.of(EntityEntry.of(ingredient)));
    }

    public static EntitySelector of(EntityTarget target) {
        return of(EntityIngredient.of(target));
    }

    public boolean isSimple() {
        return conditions == Conditions.EMPTY
                && entries.size() == 1
                && entries.getFirst().conditions() == Conditions.EMPTY;
    }

    public EntityIngredient simpleIngredient() {
        if (!isSimple()) {
            throw new IllegalStateException("EntitySelector is not simple");
        }
        return entries.getFirst().ingredient();
    }

    public boolean matches(WorldContext ctx, Entity entity) {
        if (entity == null) return false;
        if (!conditions.matches(ctx)) return false;

        for (EntityEntry entry : entries) {
            if (!entry.conditions().matches(ctx)) continue;
            if (entry.ingredient().matches(entity)) return true;
        }

        return false;
    }

    @Override
    public Codec<EntitySelector> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EntitySelector> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public DataResult<EntitySelector> validate() {
        return ParamValidations.all(this,
                () -> ParamValidations.child(this, conditions, JolCraftParameterIds.CONDITIONS),
                () -> ParamValidations.notEmpty(this, entries, JolCraftParameterIds.ENTRIES),
                () -> ParamValidations.children(this, entries, JolCraftParameterIds.ENTRIES)
        );
    }

    private static final Codec<EntitySelector> RAW_CODEC =
            RecordCodecBuilder.create(inst -> inst.group(
                    Conditions.CODEC.optionalFieldOf(JolCraftParameterIds.CONDITIONS, Conditions.EMPTY)
                            .forGetter(EntitySelector::conditions),
                    EntityEntry.CODEC.listOf().fieldOf(JolCraftParameterIds.ENTRIES)
                            .forGetter(EntitySelector::entries)
            ).apply(inst, EntitySelector::new));

    public static final Codec<EntitySelector> CODEC =
            ParamCodecs.validated(RAW_CODEC, EntitySelector::validate);

    public static final StreamCodec<RegistryFriendlyByteBuf, EntitySelector> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.composite(
                    Conditions.STREAM_CODEC,
                    EntitySelector::conditions,
                    EntityEntry.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    EntitySelector::entries,
                    EntitySelector::new
            ), EntitySelector::validate);
}