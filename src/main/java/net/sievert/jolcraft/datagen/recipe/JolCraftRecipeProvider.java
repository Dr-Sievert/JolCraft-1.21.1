package net.sievert.jolcraft.datagen.recipe;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.sievert.jolcraft.datagen.recipe.subprovider.*;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftRecipeProvider extends AbstractRecipeProvider {

    public JolCraftRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        super(provider, recipeOutput);
    }

    public static final class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
            super(packOutput, provider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
            return new JolCraftRecipeProvider(provider, recipeOutput);
        }

        @Override
        public String getName() {
            return "JolCraft Recipes";
        }
    }

    @Override
    protected void buildRecipes() {
        runAll(List.of(
                new CompassRecipesSubProvider(),
                new EquipmentRecipesSubProvider(),
                new FermentingCauldronRecipesSubProvider(),
                new LapidaryRecipesSubProvider(),
                new MaterialsRecipesSubProvider(),
                new MiscRecipesSubProvider(),
                new ToolsRecipesSubProvider(),
                new TrimRecipesSubProvider()
        ));
    }
}
