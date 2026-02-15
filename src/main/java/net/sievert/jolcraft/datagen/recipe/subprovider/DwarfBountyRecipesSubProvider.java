package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.sievert.jolcraft.datagen.recipe.subprovider.bounty.reward.*;
import net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task.*;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfBountyRecipesSubProvider implements AbstractRecipeProvider.RecipeSubProvider {

    @Override
    public void addRecipes(@NotNull AbstractRecipeProvider p) {

        //Tasks
        new DwarfMerchantBountyTasks().addTasks(p);
        new DwarfMinerBountyTasks().addTasks(p);

        //Rewards
        new DwarfMerchantBountyRewards().addRewards(p);
        new DwarfMinerBountyRewards().addRewards(p);
    }
}