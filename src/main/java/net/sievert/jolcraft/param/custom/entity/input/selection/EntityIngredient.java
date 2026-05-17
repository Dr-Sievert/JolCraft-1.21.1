package net.sievert.jolcraft.param.custom.entity.input.selection;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.Entity;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamMatching;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.List;

public record EntityIngredient(List<EntityTarget> targets)
        implements ParamData<EntityIngredient>, ParamMatching<Entity> {

    public EntityIngredient {
        targets = targets == null ? List.of() : List.copyOf(targets);
    }

    public static EntityIngredient of(EntityTarget target) {
        return new EntityIngredient(List.of(target));
    }

    public static EntityIngredient of(List<EntityTarget> targets) {
        return new EntityIngredient(targets);
    }

    public boolean isSingleTarget() {
        return targets.size() == 1;
    }

    public EntityTarget singleTarget() {
        if (!isSingleTarget()) {
            throw new IllegalStateException("EntityIngredient does not contain exactly one target");
        }
        return targets.getFirst();
    }

    @Override
    public boolean matches(Entity entity) {
        if (entity == null) return false;

        for (EntityTarget target : targets) {
            if (target.target().value().map(
                    holder -> entity.getType() == holder.value(),
                    entity.getType()::is
            )) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Codec<EntityIngredient> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EntityIngredient> streamCodec() {
        return STREAM_CODEC;
    }

    @Override
    public DataResult<EntityIngredient> validate() {
        return ParamValidations.all(this,
                () -> ParamValidations.notEmpty(this, targets, JolCraftStrings.plural(JolCraftParameterIds.TARGET)),
                () -> ParamValidations.children(this, targets, JolCraftStrings.plural(JolCraftParameterIds.TARGET))
        );
    }

    public static final Codec<EntityIngredient> CODEC =
            ParamCodecs.validated(
                    ParamCodecs.single(EntityTarget.CODEC)
                            .xmap(EntityIngredient::new, EntityIngredient::targets),
                    EntityIngredient::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, EntityIngredient> STREAM_CODEC =
            ParamCodecs.validatedStream(
                    EntityTarget.STREAM_CODEC.apply(ByteBufCodecs.list())
                            .map(EntityIngredient::new, EntityIngredient::targets),
                    EntityIngredient::validate
            );
}