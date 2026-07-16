package net.sievert.jolcraft.world.recipe.output;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

public final class EntityOutputs {

    private EntityOutputs() {}

    public static EntityOutput entity(
            EntityType<?> entity
    ) {
        return EntityOutput.of(entity);
    }

    public static EntityOutput entity(
            EntityType<?> entity,
            NumberProvider count
    ) {
        return EntityOutput.of(entity, count);
    }
}