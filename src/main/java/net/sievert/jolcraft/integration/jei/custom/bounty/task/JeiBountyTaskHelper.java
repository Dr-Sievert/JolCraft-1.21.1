package net.sievert.jolcraft.integration.jei.custom.bounty.task;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyTaskRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class JeiBountyTaskHelper {

    private JeiBountyTaskHelper() {}

    public static @NotNull List<JeiBountyTaskRecipe> getRecipes() {
        Minecraft minecraft =
                Minecraft.getInstance();

        if (minecraft.level == null) {
            return List.of();
        }

        RecipeManager recipeManager =
                minecraft.level
                        .getRecipeManager();

        List<JeiBountyTaskRecipe> recipes =
                new ArrayList<>();

        for (
                RecipeHolder<BountyTaskRecipe> holder :
                recipeManager.getAllRecipesFor(
                        JolCraftRecipes
                                .BOUNTY_TASK_TYPE
                                .get()
                )
        ) {
            recipes.addAll(
                    JeiBountyTaskRecipe.create(
                            holder.value()
                    )
            );
        }

        return List.copyOf(
                recipes
        );
    }
}