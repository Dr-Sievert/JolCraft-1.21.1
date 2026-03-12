package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla.ComponentPreservingShapelessRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla.DyeColorRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla.VanillaRecipeBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

public final class CompassRecipesSubProvider implements RecipeSubProvider {

    @Override
    public @NotNull String folder() {
        return JolCraftItemIds.DEEPSLATE_COMPASS;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    ) {

        // =========================================================
        // EMPTY DEEPSLATE COMPASS
        // =========================================================

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                )
                .pattern(" B ")
                .pattern("B B")
                .pattern(" B ")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .unlockedByHas(JolCraftItems.DEEPSLATE_PLATE.get())
                .save(output, folder(), JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get());

        for (var r : DyeColorRecipeBuilder.buildAll(
                CraftingBookCategory.MISC,
                JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get(),
                Ingredient.of(Items.WATER_BUCKET)
        )) {
            r.builder().save(output, recipeKey(r.file()));
        }

        // =========================================================
        // DEEPSLATE COMPASS
        // =========================================================

        for (var r : DyeColorRecipeBuilder.buildAll(
                CraftingBookCategory.MISC,
                JolCraftItems.DEEPSLATE_COMPASS.get(),
                Ingredient.of(Items.WATER_BUCKET)
        )) {
            r.builder().save(output, recipeKey(r.file()));
        }

        ComponentPreservingShapelessRecipeBuilder.create(
                        CraftingBookCategory.MISC,
                        Ingredient.of(JolCraftItems.DEEPSLATE_COMPASS.get())
                )
                .result(new ItemStack(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()))
                .keep(DataComponents.DYED_COLOR)
                .save(
                        output,
                        recipeKey(
                                JolCraftStrings.underscored(
                                        JolCraftItemIds.DEEPSLATE_COMPASS,
                                        JolCraftDictionary.REMOVE,
                                        JolCraftDictionary.DIAL
                                )
                        )
                );
    }

    private @NotNull ResourceKey<Recipe<?>> recipeKey(@NotNull String file) {
        return ResourceKey.create(
                Registries.RECIPE,
                JolCraft.location(
                        JolCraftStrings.slashed(folder(), file)
                )
        );
    }
}