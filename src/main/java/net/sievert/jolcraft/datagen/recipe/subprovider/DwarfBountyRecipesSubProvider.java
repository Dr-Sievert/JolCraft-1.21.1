package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.subprovider.bounty.DwarfBountyRewardSubProvider;
import net.sievert.jolcraft.datagen.recipe.subprovider.bounty.DwarfBountyTaskSubProvider;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfBountyRecipesSubProvider implements RecipeSubProvider {

    private static final List<RecipeSubProvider> SUBS = List.of(
            new DwarfBountyTaskSubProvider(),
            new DwarfBountyRewardSubProvider()
    );

    @Override
    public String folder() {
        return JolCraftDictionary.BOUNTY;
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