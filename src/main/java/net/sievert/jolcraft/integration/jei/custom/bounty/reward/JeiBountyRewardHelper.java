package net.sievert.jolcraft.integration.jei.custom.bounty.reward;

import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeAccess;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyTaskRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class JeiBountyRewardHelper {

    private JeiBountyRewardHelper() {
    }

    public static @NotNull List<JeiBountyRewardRecipe> getRecipes() {
        /*
         * Task and reward recipes remain separate recipe types. Both sets are
         * loaded once; the wrapper performs the single profession/tier match.
         */
        List<BountyTaskRecipe> taskRecipes =
                JeiRecipeAccess.getSortedValues(
                        JolCraftRecipes
                                .BOUNTY_TASK_TYPE
                                .get()
                );

        List<JeiBountyRewardRecipe> recipes =
                new ArrayList<>();

        for (var holder : JeiRecipeAccess.getSorted(
                JolCraftRecipes
                        .BOUNTY_REWARD_TYPE
                        .get()
        )) {
            BountyRewardRecipe rewardRecipe =
                    holder.value();

            for (JeiBountyRewardRecipe recipe :
                    JeiBountyRewardRecipe.create(
                            rewardRecipe,
                            taskRecipes
                    )) {
                if (!recipe.inputs().isEmpty()) {
                    recipes.add(
                            recipe
                    );
                }
            }
        }

        return List.copyOf(
                recipes
        );
    }
}
