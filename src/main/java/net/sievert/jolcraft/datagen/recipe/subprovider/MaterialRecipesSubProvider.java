package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla.VanillaRecipeBuilder;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

public final class MaterialRecipesSubProvider implements RecipeSubProvider {

    @Override
    public @NotNull String folder() {
        return JolCraftDictionary.MATERIAL;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    ) {
        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, JolCraftItems.DEEPSLATE_PLATE.get())
                )
                .requires(JolCraftItems.DEEPSLATE_BULBS.get())
                .requires(Items.IRON_INGOT)
                .unlockedByHas(JolCraftItems.DEEPSLATE_BULBS.get())
                .save(
                        output,
                        ResourceKey.create(
                                Registries.RECIPE,
                                ResourceLocation.parse(JolCraftItems.DEEPSLATE_PLATE.getId().getPath() + "_from_bulbs")
                        )
                );

        VanillaRecipeBuilder.Storage.nineBlock(
                items,
                output,
                RecipeCategory.MISC,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                RecipeCategory.MISC,
                JolCraftBlocks.DEEPSLATE_PLATE_BLOCK.get(),
                ResourceKey.create(
                        Registries.RECIPE,
                        ResourceLocation.parse(JolCraftBlocks.DEEPSLATE_PLATE_BLOCK.getId().getPath())
                ),
                ResourceKey.create(
                        Registries.RECIPE,
                        ResourceLocation.parse(JolCraftItems.DEEPSLATE_PLATE.getId().getPath())
                )
        );

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, JolCraftItems.DEEPSLATE_ROD.get(), 4)
                )
                .pattern("B")
                .pattern("B")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .unlockedByHas(JolCraftItems.DEEPSLATE_PLATE.get())
                .save(output);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get())
                )
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', JolCraftItems.IMPURE_MITHRIL.get())
                .unlockedByHas(JolCraftItems.IMPURE_MITHRIL.get())
                .save(output);

        VanillaRecipeBuilder.Storage.nineBlock(
                items,
                output,
                RecipeCategory.MISC,
                JolCraftItems.PURE_MITHRIL.get(),
                RecipeCategory.MISC,
                JolCraftBlocks.PURE_MITHRIL_BLOCK.get(),
                ResourceKey.create(
                        Registries.RECIPE,
                        ResourceLocation.parse(JolCraftBlocks.PURE_MITHRIL_BLOCK.getId().getPath())
                ),
                ResourceKey.create(
                        Registries.RECIPE,
                        ResourceLocation.parse(JolCraftItems.PURE_MITHRIL.getId().getPath())
                )
        );

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, JolCraftItems.MITHRIL_INGOT.get())
                )
                .pattern("NNN")
                .pattern("NNN")
                .pattern("NNN")
                .define('N', JolCraftItems.MITHRIL_NUGGET.get())
                .unlockedByHas(JolCraftItems.MITHRIL_NUGGET.get())
                .save(
                        output,
                        ResourceKey.create(
                                Registries.RECIPE,
                                ResourceLocation.parse(JolCraftItems.MITHRIL_INGOT.getId().getPath() + "_from_nuggets")
                        )
                );

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, JolCraftItems.MITHRIL_NUGGET.get(), 9)
                )
                .requires(JolCraftItems.MITHRIL_INGOT.get())
                .unlockedByHas(JolCraftItems.MITHRIL_INGOT.get())
                .save(
                        output,
                        ResourceKey.create(
                                Registries.RECIPE,
                                ResourceLocation.parse(JolCraftItems.MITHRIL_NUGGET.getId().getPath() + "s_from_ingot")
                        )
                );

        VanillaRecipeBuilder.Storage.nineBlock(
                items,
                output,
                RecipeCategory.MISC,
                JolCraftItems.MITHRIL_INGOT.get(),
                RecipeCategory.MISC,
                JolCraftBlocks.MITHRIL_BLOCK.get(),
                ResourceKey.create(
                        Registries.RECIPE,
                        ResourceLocation.parse(JolCraftBlocks.MITHRIL_BLOCK.getId().getPath())
                ),
                ResourceKey.create(
                        Registries.RECIPE,
                        ResourceLocation.parse(JolCraftItems.MITHRIL_INGOT.getId().getPath())
                )
        );

        VanillaRecipeBuilder.Cooking.blasting(
                        JolCraftItems.IMPURE_MITHRIL.get(),
                        RecipeCategory.MISC,
                        JolCraftItems.PURE_MITHRIL.get(),
                        0.7F,
                        200
                )
                .unlockedByHas(JolCraftItems.IMPURE_MITHRIL.get())
                .save(output);

        VanillaRecipeBuilder.Cooking.blasting(
                        JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get(),
                        RecipeCategory.MISC,
                        JolCraftItems.PURE_MITHRIL.get(),
                        0.7F,
                        400
                )
                .unlockedByHas(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get())
                .save(output);

        VanillaRecipeBuilder.Cooking.blasting(
                        JolCraftItems.PURE_MITHRIL.get(),
                        RecipeCategory.MISC,
                        JolCraftItems.MITHRIL_INGOT.get(),
                        0.7F,
                        100
                )
                .unlockedByHas(JolCraftItems.PURE_MITHRIL.get())
                .save(output);

        VanillaRecipeBuilder.Cooking.smelting(
                        JolCraftItems.PURE_MITHRIL.get(),
                        RecipeCategory.MISC,
                        JolCraftItems.MITHRIL_INGOT.get(),
                        0.7F,
                        200
                )
                .unlockedByHas(JolCraftItems.PURE_MITHRIL.get())
                .save(output);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.MISC, JolCraftItems.MITHRIL_CHAINWEAVE.get())
                )
                .pattern("BB")
                .pattern("BB")
                .pattern("BB")
                .define('B', JolCraftItems.MITHRIL_NUGGET.get())
                .unlockedByHas(JolCraftItems.MITHRIL_INGOT.get())
                .save(output);

        VanillaRecipeBuilder.shapeless(
                        ShapelessRecipeBuilder.shapeless(items, RecipeCategory.MISC, JolCraftItems.MITHRIL_NUGGET.get(), 6)
                )
                .requires(JolCraftItems.MITHRIL_CHAINWEAVE.get())
                .unlockedByHas(JolCraftItems.MITHRIL_NUGGET.get())
                .save(
                        output,
                        ResourceKey.create(
                                Registries.RECIPE,
                                ResourceLocation.parse(JolCraftItems.MITHRIL_NUGGET.getId().getPath() + "s_from_chainweave")
                        )
                );
    }
}