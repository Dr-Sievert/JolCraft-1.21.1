package net.sievert.jolcraft.world.recipe.output;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootContextUser;
import net.sievert.jolcraft.world.recipe.output.hook.JolCraftRecipeHooks;

import java.util.List;

public interface RecipeOutput extends LootContextUser {

    RecipeOutputType getType();

    List<ResourceLocation> hooks();

    default boolean applyHooks(
            LootContext context,
            Object generatedOutput,
            RecipeInput recipeInput
    ) {
        for (ResourceLocation hookId : hooks()) {
            if (!JolCraftRecipeHooks.apply(
                    hookId,
                    context,
                    generatedOutput,
                    recipeInput
            )) {
                return false;
            }
        }

        return true;
    }
}