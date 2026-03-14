package net.sievert.jolcraft.datagen.recipe.builder.base;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import org.jetbrains.annotations.NotNull;

public record RecipeLookups(HolderLookup.Provider provider) {

    public RecipeLookups {
        if (provider == null) {
            throw new IllegalArgumentException("provider cannot be null");
        }
    }

    public <T> @NotNull HolderGetter<T> lookup(@NotNull ResourceKey<? extends Registry<T>> registryKey) {
        return provider.lookupOrThrow(registryKey);
    }

    public @NotNull HolderGetter<Item> items() {
        return lookup(Registries.ITEM);
    }

    public @NotNull HolderGetter<Biome> biomes() {
        return lookup(Registries.BIOME);
    }
}