package net.sievert.jolcraft.integration.jei.custom.bounty.reward;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyTaskRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class JeiBountyRewardHelper {

    private JeiBountyRewardHelper() {}

    public static @NotNull List<JeiBountyRewardRecipe> getRecipes() {
        Minecraft minecraft =
                Minecraft.getInstance();

        if (minecraft.level == null) {
            return List.of();
        }

        RecipeManager recipeManager =
                minecraft.level
                        .getRecipeManager();

        List<BountyTaskRecipe> taskRecipes =
                recipeManager.getAllRecipesFor(
                                JolCraftRecipes
                                        .BOUNTY_TASK_TYPE
                                        .get()
                        )
                        .stream()
                        .map(RecipeHolder::value)
                        .toList();

        List<JeiBountyRewardRecipe> recipes =
                new ArrayList<>();

        for (
                RecipeHolder<BountyRewardRecipe> holder :
                recipeManager.getAllRecipesFor(
                        JolCraftRecipes
                                .BOUNTY_REWARD_TYPE
                                .get()
                )
        ) {
            BountyRewardRecipe rewardRecipe =
                    holder.value();

            List<BountyTaskRecipe> matchingTasks =
                    taskRecipes.stream()
                            .filter(
                                    taskRecipe ->
                                            taskRecipe.bountyType()
                                                    == rewardRecipe.bountyType()
                            )
                            .filter(
                                    taskRecipe ->
                                            taskRecipe.tier()
                                                    == rewardRecipe.tier()
                            )
                            .toList();

            if (matchingTasks.isEmpty()) {
                continue;
            }

            JeiBountyRewardRecipe recipe =
                    JeiBountyRewardRecipe.create(
                            rewardRecipe,
                            matchingTasks
                    );

            if (
                    recipe.inputs().isEmpty()
                            || recipe.rewards().isEmpty()
            ) {
                continue;
            }

            recipes.add(
                    recipe
            );
        }

        return List.copyOf(
                recipes
        );
    }
}