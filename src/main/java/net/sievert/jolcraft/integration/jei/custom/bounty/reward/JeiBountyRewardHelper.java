package net.sievert.jolcraft.integration.jei.custom.bounty.reward;

import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeAccess;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyTaskRecipe;
import org.jetbrains.annotations.NotNull;

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
                JeiRecipeAccess.translateSorted(
                        JolCraftRecipes
                                .BOUNTY_REWARD_TYPE
                                .get(),
                        holder ->
                                JeiBountyRewardRecipe.create(
                                                holder.id(),
                                                holder.value(),
                                                taskRecipes
                                        )
                                        .stream()
                                        .filter(
                                                recipe ->
                                                        !recipe.inputs()
                                                                .isEmpty()
                                        )
                                        .toList()
                );

        return List.copyOf(
                recipes
        );
    }
}
