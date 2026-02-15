package net.sievert.jolcraft.datagen.recipe;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.sievert.jolcraft.datagen.recipe.subprovider.*;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftRecipeProvider extends AbstractRecipeProvider {

    @Nullable
    private final CountingRecipeOutput counting;

    public JolCraftRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
        super(provider, recipeOutput);
        this.counting = (recipeOutput instanceof CountingRecipeOutput c) ? c : null;
    }

    public static final class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> provider) {
            super(packOutput, provider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider provider, RecipeOutput recipeOutput) {
            return new JolCraftRecipeProvider(provider, new CountingRecipeOutput(recipeOutput));
        }

        @Override
        public String getName() {
            return "JolCraft Recipes";
        }
    }

    @Override
    protected void buildRecipes() {
        List<RecipeSubProvider> subs = List.of(
                new CompassRecipesSubProvider(),
                new DwarfBountyRecipesSubProvider(),
                new DwarfTradeRecipesSubProvider(),
                new EquipmentRecipesSubProvider(),
                new FermentingCauldronRecipesSubProvider(),
                new LapidaryRecipesSubProvider(),
                new MaterialRecipesSubProvider(),
                new MiscRecipesSubProvider(),
                new ToolRecipesSubProvider(),
                new TrimRecipesSubProvider()
        );

        if (counting == null) {
            runAll(subs);
            return;
        }

        int beforeTotal = counting.count();

        for (RecipeSubProvider sub : subs) {
            int before = counting.count();
            String name = sub.getClass().getSimpleName();

            try {
                sub.addRecipes(this);
            } catch (RuntimeException e) {
                JolCraftLogs.error(
                        JolCraftLogTags.DATAGEN,
                        "Recipe subprovider {} failed.",
                        e,
                        name
                );
                throw e;
            }
            int added = counting.count() - before;
            JolCraftLogs.debug(JolCraftLogTags.DATAGEN, "Recipe subprovider {}: +{} recipes", name, added);

            if (added == 0) {
                JolCraftLogs.warn(JolCraftLogTags.DATAGEN, "Recipe subprovider {} added 0 recipes.", name);
            }
        }

        int totalAdded = counting.count() - beforeTotal;
        JolCraftLogs.info(JolCraftLogTags.DATAGEN, "Total recipes generated: {} ({} subproviders)", totalAdded, subs.size());
    }

    private static final class CountingRecipeOutput implements RecipeOutput {

        private final RecipeOutput delegate;
        private int count;

        private CountingRecipeOutput(RecipeOutput delegate) {
            this.delegate = delegate;
        }

        private int count() {
            return count;
        }

        @Override
        public void accept(ResourceKey<Recipe<?>> key, Recipe<?> recipe, @Nullable AdvancementHolder advancement, ICondition... conditions) {
            count++;
            delegate.accept(key, recipe, advancement, conditions);
        }

        @Override
        public Advancement.Builder advancement() {
            return delegate.advancement();
        }

        @Override
        public void includeRootAdvancement() {
            delegate.includeRootAdvancement();
        }
    }
}
