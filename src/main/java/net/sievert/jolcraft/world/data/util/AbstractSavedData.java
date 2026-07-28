package net.sievert.jolcraft.world.data.util;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiFunction;
import java.util.function.Supplier;

/**
 * Base class for JolCraft saved data.
 *
 * Handles generic SavedData creation, loading, retrieval, and serialization.
 * Subclasses remain responsible for their own state and behavior.
 */
public abstract class AbstractSavedData extends SavedData {

    protected AbstractSavedData() {}

    /**
     * Writes the subclass-specific data.
     */
    protected abstract void saveData(
            CompoundTag tag,
            HolderLookup.Provider registries
    );

    @Override
    public final @NotNull CompoundTag save(
            @NotNull CompoundTag tag,
            HolderLookup.@NotNull Provider registries
    ) {
        saveData(
                tag,
                registries
        );

        return tag;
    }

    /**
     * Defines how a specific saved-data implementation is created, loaded, and retrieved.
     */
    protected static final class Type<T extends AbstractSavedData> {

        private final String name;
        private final Factory<T> factory;

        public Type(
                String name,
                Supplier<T> constructor,
                BiFunction<
                        CompoundTag,
                        HolderLookup.Provider,
                        T
                        > loader
        ) {
            this.name = name;
            this.factory = new Factory<>(
                    constructor,
                    loader
            );
        }

        public T get(
                ServerLevel level
        ) {
            return level
                    .getDataStorage()
                    .computeIfAbsent(
                            factory,
                            name
                    );
        }
    }
}