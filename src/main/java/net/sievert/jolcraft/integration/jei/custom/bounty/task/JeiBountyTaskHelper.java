package net.sievert.jolcraft.integration.jei.custom.bounty.task;

import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeAccess;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class JeiBountyTaskHelper {

    private JeiBountyTaskHelper() {
    }

    public static @NotNull List<JeiBountyTaskRecipe> getRecipes() {
        return JeiRecipeAccess.translateSorted(
                JolCraftRecipes
                        .BOUNTY_TASK_TYPE
                        .get(),
                holder ->
                        JeiBountyTaskRecipe.create(
                                holder.value()
                        )
        );
    }
}
