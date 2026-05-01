package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla.ComponentPreservingShapelessRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla.DyeColorRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla.VanillaRecipeBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

public record CompassRecipesSubProvider(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public CompassRecipesSubProvider(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
        this.parent = parent;
    }

    @Override
    public @NotNull JolCraftDataProvider<RecipeOutput> parent() {
        return parent;
    }

    @Override
    public @NotNull String id() {
        return folder();
    }

    @Override
    public @NotNull String folder() {
        return JolCraftItemIds.DEEPSLATE_COMPASS;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {

        // =========================================================
        // EMPTY DEEPSLATE COMPASS
        // =========================================================

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(
                                RecipeCategory.TOOLS,
                                JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()
                        )
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

    private @NotNull ResourceLocation recipeKey(@NotNull String file) {
        return JolCraft.location(
                JolCraftStrings.slashed(folder(), file)
        );
    }
}