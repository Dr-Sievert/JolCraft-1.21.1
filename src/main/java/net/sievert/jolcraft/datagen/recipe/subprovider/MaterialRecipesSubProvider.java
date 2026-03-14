package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeLookups;
import net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla.VanillaRecipeBuilder;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

public final class MaterialRecipesSubProvider implements RecipeSubProvider {

    private static final String FOLDER = JolCraftDictionary.MATERIAL;

    @Override
    public @NotNull String folder() {
        return FOLDER;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull RecipeLookups lookups
    ) {
        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(lookups.items(), RecipeCategory.MISC, JolCraftItems.DEEPSLATE_PLATE.get())
                )
                .requires(JolCraftItems.DEEPSLATE_BULBS.get())
                .requires(Items.IRON_INGOT)
                .unlockedByHas(JolCraftItems.DEEPSLATE_BULBS.get())
                .save(output, folder(), JolCraftItems.DEEPSLATE_PLATE.getId().getPath() + "_from_bulbs");

        VanillaRecipeBuilder.Storage.nineBlock(
                lookups.items(),
                output,
                folder(),
                RecipeCategory.MISC,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                RecipeCategory.MISC,
                JolCraftBlocks.DEEPSLATE_PLATE_BLOCK.get()
        );

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(lookups.items(), RecipeCategory.MISC, JolCraftItems.DEEPSLATE_ROD.get(), 4)
                )
                .pattern("B")
                .pattern("B")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .unlockedByHas(JolCraftItems.DEEPSLATE_PLATE.get())
                .save(output, folder(), JolCraftItems.DEEPSLATE_ROD.get());

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(lookups.items(), RecipeCategory.MISC, JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get())
                )
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', JolCraftItems.IMPURE_MITHRIL.get())
                .unlockedByHas(JolCraftItems.IMPURE_MITHRIL.get())
                .save(output, folder(), JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get());

        VanillaRecipeBuilder.Storage.nineBlock(
                lookups.items(),
                output,
                folder(),
                RecipeCategory.MISC,
                JolCraftItems.PURE_MITHRIL.get(),
                RecipeCategory.MISC,
                JolCraftBlocks.PURE_MITHRIL_BLOCK.get()
        );

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(lookups.items(), RecipeCategory.MISC, JolCraftItems.MITHRIL_INGOT.get())
                )
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', JolCraftItems.MITHRIL_NUGGET.get())
                .unlockedByHas(JolCraftItems.MITHRIL_NUGGET.get())
                .save(output, folder(), JolCraftItems.MITHRIL_INGOT.getId().getPath() + "_from_nuggets");

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(lookups.items(), RecipeCategory.MISC, JolCraftItems.MITHRIL_NUGGET.get(), 9)
                )
                .requires(JolCraftItems.MITHRIL_INGOT.get())
                .unlockedByHas(JolCraftItems.MITHRIL_INGOT.get())
                .save(output, folder(), JolCraftItems.MITHRIL_NUGGET.getId().getPath() + "s_from_ingot");

        VanillaRecipeBuilder.Storage.nineBlock(
                lookups.items(),
                output,
                folder(),
                RecipeCategory.MISC,
                JolCraftItems.MITHRIL_INGOT.get(),
                RecipeCategory.MISC,
                JolCraftBlocks.MITHRIL_BLOCK.get()
        );

        VanillaRecipeBuilder.Cooking.blasting(
                        JolCraftItems.IMPURE_MITHRIL.get(),
                        RecipeCategory.MISC,
                        JolCraftItems.PURE_MITHRIL.get(),
                        0.7F,
                        200
                )
                .unlockedByHas(JolCraftItems.IMPURE_MITHRIL.get())
                .save(output, folder());

        VanillaRecipeBuilder.Cooking.blasting(
                        JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get(),
                        RecipeCategory.MISC,
                        JolCraftItems.PURE_MITHRIL.get(),
                        0.7F,
                        400
                )
                .unlockedByHas(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get())
                .save(output, folder());

        VanillaRecipeBuilder.Cooking.blasting(
                        JolCraftItems.PURE_MITHRIL.get(),
                        RecipeCategory.MISC,
                        JolCraftItems.MITHRIL_INGOT.get(),
                        0.7F,
                        100
                )
                .unlockedByHas(JolCraftItems.PURE_MITHRIL.get())
                .save(output, folder());

        VanillaRecipeBuilder.Cooking.smelting(
                        JolCraftItems.PURE_MITHRIL.get(),
                        RecipeCategory.MISC,
                        JolCraftItems.MITHRIL_INGOT.get(),
                        0.7F,
                        200
                )
                .unlockedByHas(JolCraftItems.PURE_MITHRIL.get())
                .save(output, folder());

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(lookups.items(), RecipeCategory.MISC, JolCraftItems.MITHRIL_CHAINWEAVE.get())
                )
                .pattern("BB")
                .pattern("BB")
                .pattern("BB")
                .define('B', JolCraftItems.MITHRIL_NUGGET.get())
                .unlockedByHas(JolCraftItems.MITHRIL_INGOT.get())
                .save(output, folder(), JolCraftItems.MITHRIL_CHAINWEAVE.get());

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(lookups.items(), RecipeCategory.MISC, JolCraftItems.MITHRIL_NUGGET.get(), 6)
                )
                .requires(JolCraftItems.MITHRIL_CHAINWEAVE.get())
                .unlockedByHas(JolCraftItems.MITHRIL_NUGGET.get())
                .save(output, folder(), JolCraftItems.MITHRIL_NUGGET.getId().getPath() + "s_from_chainweave");
    }
}