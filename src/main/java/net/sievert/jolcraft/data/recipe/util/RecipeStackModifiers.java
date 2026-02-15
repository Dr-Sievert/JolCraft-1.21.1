package net.sievert.jolcraft.data.recipe.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public final class RecipeStackModifiers {

    private static final Map<ResourceLocation, Consumer<ItemStack>> REGISTRY = new HashMap<>();

    private RecipeStackModifiers() {}

    public static void register(ResourceLocation id, Consumer<ItemStack> modifier) {
        REGISTRY.put(id, modifier);
    }

    public static Consumer<ItemStack> resolve(String id) {
        if (id == null || id.isBlank()) return s -> {};
        ResourceLocation rl = ResourceLocation.tryParse(id.trim());
        if (rl == null) return s -> {};
        return REGISTRY.getOrDefault(rl, s -> {});
    }
}