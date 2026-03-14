package net.sievert.jolcraft.datagen.recipe.subprovider.bounty;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeLookups;
import net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task.DwarfMerchantBountyTasks;
import net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task.DwarfMinerBountyTasks;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfBountyTaskSubProvider implements RecipeSubProvider {

    private static final List<RecipeSubProvider> SUBS = List.of(
            new DwarfMerchantBountyTasks(),
            new DwarfMinerBountyTasks()
    );

    @Override
    public @NotNull String folder() {
        return JolCraftDictionary.TASK;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull RecipeLookups lookups
    ) {
        for (RecipeSubProvider sub : SUBS) {
            sub.register(executor, output, lookups);
        }
    }
}