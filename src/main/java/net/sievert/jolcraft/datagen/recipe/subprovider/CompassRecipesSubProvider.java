package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.crafting.CompoundIngredient;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.vanilla.ComponentPreservingShapelessRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.vanilla.DyeColorRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.vanilla.VanillaRecipeBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassDialColor;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassStructureGroup;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

public record CompassRecipesSubProvider(
        JolCraftDataProvider<RecipeOutput> parent
) implements RecipeSubProvider {

    public CompassRecipesSubProvider(
            @NotNull JolCraftDataProvider<RecipeOutput> parent
    ) {
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
                .save(
                        output,
                        folder(),
                        JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()
                );

        for (var recipe : DyeColorRecipeBuilder.buildAll(
                CraftingBookCategory.MISC,
                JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get(),
                Ingredient.of(Items.WATER_BUCKET)
        )) {
            recipe.builder().save(
                    output,
                    recipeKey(recipe.file())
            );
        }

        for (var recipe : DyeColorRecipeBuilder.buildAll(
                CraftingBookCategory.MISC,
                JolCraftItems.DEEPSLATE_COMPASS.get(),
                Ingredient.of(Items.WATER_BUCKET)
        )) {
            recipe.builder().save(
                    output,
                    recipeKey(recipe.file())
            );
        }

        ComponentPreservingShapelessRecipeBuilder.create(
                        CraftingBookCategory.MISC,
                        Ingredient.of(JolCraftItems.DEEPSLATE_COMPASS.get())
                )
                .result(
                        new ItemStack(
                                JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()
                        )
                )
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

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(
                                RecipeCategory.MISC,
                                JolCraftItems.DEEPSLATE_COMPASS_DIAL.get()
                        )
                )
                .pattern(" X")
                .pattern("B ")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Items.REDSTONE)
                .unlockedByHas(JolCraftItems.DEEPSLATE_PLATE.get())
                .save(
                        output,
                        folder(),
                        JolCraftItems.DEEPSLATE_COMPASS_DIAL.getId().getPath()
                                + "_right"
                );

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(
                                RecipeCategory.MISC,
                                JolCraftItems.DEEPSLATE_COMPASS_DIAL.get()
                        )
                )
                .pattern("X ")
                .pattern(" B")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Items.REDSTONE)
                .unlockedByHas(JolCraftItems.DEEPSLATE_PLATE.get())
                .save(
                        output,
                        folder(),
                        JolCraftItems.DEEPSLATE_COMPASS_DIAL.getId().getPath()
                                + "_left"
                );

        ComponentPreservingShapelessRecipeBuilder.create(
                        CraftingBookCategory.MISC,
                        configuredDialIngredient()
                )
                .result(
                        new ItemStack(
                                JolCraftItems.DEEPSLATE_COMPASS_DIAL.get()
                        )
                )
                .remove(JolCraftDataComponents.STRUCTURE_GROUP.get())
                .remove(
                        JolCraftDataComponents
                                .DEEPSLATE_COMPASS_DIAL_COLOR
                                .get()
                )
                .save(
                        output,
                        recipeKey(
                                JolCraftStrings.underscored(
                                        JolCraftDictionary.REMOVE,
                                        JolCraftItemIds.DEEPSLATE_COMPASS_DIAL,
                                        JolCraftDictionary.STRUCTURE
                                )
                        )
                );
    }

    private static @NotNull Ingredient configuredDialIngredient() {
        Ingredient[] variants = Arrays.stream(
                        DeepslateCompassStructureGroup.values()
                )
                .map(group -> {
                    ItemStack stack = new ItemStack(
                            JolCraftItems.DEEPSLATE_COMPASS_DIAL.get()
                    );

                    stack.set(
                            JolCraftDataComponents.STRUCTURE_GROUP.get(),
                            group.getId()
                    );

                    stack.set(
                            JolCraftDataComponents
                                    .DEEPSLATE_COMPASS_DIAL_COLOR
                                    .get(),
                            new DeepslateCompassDialColor(group.color())
                    );

                    return DataComponentIngredient.of(
                            false,
                            stack
                    );
                })
                .toArray(Ingredient[]::new);

        return CompoundIngredient.of(variants);
    }

    private @NotNull ResourceLocation recipeKey(
            @NotNull String file
    ) {
        return JolCraft.location(
                JolCraftStrings.slashed(
                        folder(),
                        file
                )
        );
    }
}