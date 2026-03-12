package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla.VanillaRecipeBuilder;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.util.equipment.JolCraftEquipmentHelper;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings({"SameParameterValue"})
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class EquipmentRecipesSubProvider implements RecipeSubProvider {

    private static final String FOLDER = JolCraftDictionary.EQUIPMENT;

    @Override
    public @NotNull String folder() {
        return FOLDER;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    ) {
        armorSetSimple(
                items,
                output,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                JolCraftItems.DEEPSLATE_ARMOR_SET,
                JolCraftItems.DEEPSLATE_PLATE.get()
        );

        armorSetWithLining(
                items,
                output,
                JolCraftItems.MITHRIL_INGOT.get(),
                JolCraftItems.MITHRIL_CHAINWEAVE.get(),
                JolCraftItems.MITHRIL_ARMOR_SET,
                JolCraftItems.MITHRIL_INGOT.get()
        );
    }

    private static void armorSetSimple(
            HolderGetter<Item> items,
            RecipeOutput out,
            ItemLike material,
            JolCraftEquipmentHelper.ArmorSet<DeferredItem<Item>> set,
            ItemLike unlockItem
    ) {
        ItemLike helmet = set.get(JolCraftEquipmentHelper.ArmorPiece.HELMET).get();
        ItemLike chestplate = set.get(JolCraftEquipmentHelper.ArmorPiece.CHESTPLATE).get();
        ItemLike leggings = set.get(JolCraftEquipmentHelper.ArmorPiece.LEGGINGS).get();
        ItemLike boots = set.get(JolCraftEquipmentHelper.ArmorPiece.BOOTS).get();

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.COMBAT, helmet)
                )
                .pattern("BBB")
                .pattern("B B")
                .define('B', material)
                .unlockedByHas(unlockItem)
                .save(out, FOLDER, helmet);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.COMBAT, chestplate)
                )
                .pattern("B B")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', material)
                .unlockedByHas(unlockItem)
                .save(out, FOLDER, chestplate);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.COMBAT, leggings)
                )
                .pattern("BBB")
                .pattern("B B")
                .pattern("B B")
                .define('B', material)
                .unlockedByHas(unlockItem)
                .save(out, FOLDER, leggings);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.COMBAT, boots)
                )
                .pattern("B B")
                .pattern("B B")
                .define('B', material)
                .unlockedByHas(unlockItem)
                .save(out, FOLDER, boots);
    }

    private static void armorSetWithLining(
            HolderGetter<Item> items,
            RecipeOutput out,
            ItemLike ingot,
            ItemLike lining,
            JolCraftEquipmentHelper.ArmorSet<DeferredItem<Item>> set,
            ItemLike unlockItem
    ) {
        ItemLike helmet = set.get(JolCraftEquipmentHelper.ArmorPiece.HELMET).get();
        ItemLike chestplate = set.get(JolCraftEquipmentHelper.ArmorPiece.CHESTPLATE).get();
        ItemLike leggings = set.get(JolCraftEquipmentHelper.ArmorPiece.LEGGINGS).get();
        ItemLike boots = set.get(JolCraftEquipmentHelper.ArmorPiece.BOOTS).get();

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.COMBAT, helmet)
                )
                .pattern("BBB")
                .pattern("X X")
                .define('B', ingot)
                .define('X', lining)
                .unlockedByHas(unlockItem)
                .save(out, FOLDER, helmet);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.COMBAT, chestplate)
                )
                .pattern("B B")
                .pattern("XXX")
                .pattern("XXX")
                .define('B', ingot)
                .define('X', lining)
                .unlockedByHas(unlockItem)
                .save(out, FOLDER, chestplate);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.COMBAT, leggings)
                )
                .pattern("BBB")
                .pattern("X X")
                .pattern("X X")
                .define('B', ingot)
                .define('X', lining)
                .unlockedByHas(unlockItem)
                .save(out, FOLDER, leggings);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.COMBAT, boots)
                )
                .pattern("B B")
                .pattern("B B")
                .define('B', ingot)
                .unlockedByHas(unlockItem)
                .save(out, FOLDER, boots);
    }
}