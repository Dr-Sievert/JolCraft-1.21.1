package net.sievert.jolcraft.world.entity.util;

import net.minecraft.network.syncher.EntityDataAccessor;

/**
 * Generic interface for entities with arbitrary synced entity data fields.
 * Implement this on your entity to standardize access to all EntityDataAccessors via type-safe methods.
 */
public interface EntityData {

    /**
     * Sets a value for the specified EntityDataAccessor on this entity.
     * @param accessor The data accessor (field) to set
     * @param value    The value to assign
     * @param <T>      The type of the field (Boolean, Integer, etc)
     */
    <T> void setData(EntityDataAccessor<T> accessor, T value);

    /**
     * Gets the current value of the specified EntityDataAccessor on this entity.
     * @param accessor The data accessor (field) to read
     * @param <T>      The type of the field (Boolean, Integer, etc)
     * @return         The current value for this entity's field
     */
    <T> T getData(EntityDataAccessor<T> accessor);
}
