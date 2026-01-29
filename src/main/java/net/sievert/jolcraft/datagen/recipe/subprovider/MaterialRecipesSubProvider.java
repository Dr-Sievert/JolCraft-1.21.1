package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.recipes.RecipeCategory;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class MaterialRecipesSubProvider implements AbstractRecipeProvider.RecipeSubProvider {

    @Override
    public void addRecipes(AbstractRecipeProvider p) {

        p.modShapeless(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_PLATE.get())
                .requires(JolCraftItems.DEEPSLATE_BULBS.get())
                .requires(net.minecraft.world.item.Items.IRON_INGOT)
                .unlockedBy(p.hasName(JolCraftItems.DEEPSLATE_BULBS.get()), p.hasItem(JolCraftItems.DEEPSLATE_BULBS.get()))
                .save(p.out(), p.itemName(JolCraftItems.DEEPSLATE_PLATE.get()) + "_from_bulbs");

        p.nineBlockStorageRecipesAuto(
                RecipeCategory.MISC,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                RecipeCategory.MISC,
                JolCraftBlocks.DEEPSLATE_PLATE_BLOCK.get()
        );

        p.modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_ROD.get(), 4)
                .pattern("B")
                .pattern("B")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .unlockedBy(p.hasName(JolCraftItems.DEEPSLATE_PLATE.get()), p.hasItem(JolCraftItems.DEEPSLATE_PLATE.get()))
                .save(p.out());

        p.modShaped(RecipeCategory.MISC, JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get())
                .pattern("BBB")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', JolCraftItems.IMPURE_MITHRIL.get())
                .unlockedBy(p.hasName(JolCraftItems.IMPURE_MITHRIL.get()), p.hasItem(JolCraftItems.IMPURE_MITHRIL.get()))
                .save(p.out());

        p.nineBlockStorageRecipesAuto(
                RecipeCategory.MISC,
                JolCraftItems.PURE_MITHRIL.get(),
                RecipeCategory.MISC,
                JolCraftBlocks.PURE_MITHRIL_BLOCK.get()
        );

        p.modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_INGOT.get())
                .pattern("NNN")
                .pattern("NNN")
                .pattern("NNN")
                .define('N', JolCraftItems.MITHRIL_NUGGET.get())
                .unlockedBy(p.hasName(JolCraftItems.MITHRIL_NUGGET.get()), p.hasItem(JolCraftItems.MITHRIL_NUGGET.get()))
                .save(p.out(), p.itemName(JolCraftItems.MITHRIL_INGOT.get()) + "_from_nuggets");

        p.modShapeless(RecipeCategory.MISC, JolCraftItems.MITHRIL_NUGGET.get(), 9)
                .requires(JolCraftItems.MITHRIL_INGOT.get())
                .unlockedBy(p.hasName(JolCraftItems.MITHRIL_INGOT.get()), p.hasItem(JolCraftItems.MITHRIL_INGOT.get()))
                .save(p.out(), p.itemName(JolCraftItems.MITHRIL_NUGGET.get()) + "s_from_ingot");

        p.nineBlockStorageRecipesAuto(
                RecipeCategory.MISC,
                JolCraftItems.MITHRIL_INGOT.get(),
                RecipeCategory.MISC,
                JolCraftBlocks.MITHRIL_BLOCK.get()
        );

        p.oreBlasting(
                List.of(JolCraftItems.IMPURE_MITHRIL.get()),
                RecipeCategory.MISC,
                JolCraftItems.PURE_MITHRIL.get(),
                0.7F,
                200,
                "mithril_purification_from_impure"
        );

        p.oreBlasting(
                List.of(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get()),
                RecipeCategory.MISC,
                JolCraftItems.PURE_MITHRIL.get(),
                0.7F,
                400,
                "mithril_purification_from_ore"
        );

        p.oreBlasting(
                List.of(JolCraftItems.PURE_MITHRIL.get()),
                RecipeCategory.MISC,
                JolCraftItems.MITHRIL_INGOT.get(),
                0.7F,
                100,
                "mithril_ingot_from_blasting"
        );

        p.oreSmelting(
                List.of(JolCraftItems.PURE_MITHRIL.get()),
                RecipeCategory.MISC,
                JolCraftItems.MITHRIL_INGOT.get(),
                0.7F,
                200,
                "mithril_ingot_from_smelting"
        );

        p.modShaped(RecipeCategory.MISC, JolCraftItems.MITHRIL_CHAINWEAVE.get())
                .pattern("BB")
                .pattern("BB")
                .pattern("BB")
                .define('B', JolCraftItems.MITHRIL_NUGGET.get())
                .unlockedBy(p.hasName(JolCraftItems.MITHRIL_INGOT.get()), p.hasItem(JolCraftItems.MITHRIL_INGOT.get()))
                .save(p.out());

        p.modShapeless(RecipeCategory.MISC, JolCraftItems.MITHRIL_NUGGET.get(), 6)
                .requires(JolCraftItems.MITHRIL_CHAINWEAVE.get())
                .unlockedBy(p.hasName(JolCraftItems.MITHRIL_NUGGET.get()), p.hasItem(JolCraftItems.MITHRIL_NUGGET.get()))
                .save(p.out(), p.itemName(JolCraftItems.MITHRIL_NUGGET.get()) + "s_from_chainweave");
    }
}