package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeIds;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.subprovider.hand.CompassHandInteractions;
import net.sievert.jolcraft.datagen.recipe.subprovider.hand.SpannerHandInteractions;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class HandInteractionRecipesSubProvider implements RecipeSubProvider {

    private static final List<RecipeSubProvider> SUBS = List.of(
            new CompassHandInteractions(),
            new SpannerHandInteractions()
    );

    @Override
    public @NotNull String folder() {
        return JolCraftRecipeIds.HAND_INTERACTION;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    ) {
        for (RecipeSubProvider sub : SUBS) {
            sub.register(executor, output, items);
        }
    }
}