package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.util.equipment.JolCraftEquipmentHelper;
import org.jetbrains.annotations.NotNull;

public final class EquipmentRecipesSubProvider implements AbstractRecipeProvider.RecipeSubProvider {

    private static final String FOLDER = "equipment";

    @Override
    public void addRecipes(@NotNull AbstractRecipeProvider p) {

        armorSetSimple(
                p,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                JolCraftItems.DEEPSLATE_ARMOR_SET,
                p.itemName(JolCraftItems.DEEPSLATE_PLATE.get())
        );

        armorSetWithLining(
                p,
                JolCraftItems.MITHRIL_INGOT.get(),
                JolCraftItems.MITHRIL_CHAINWEAVE.get(),
                JolCraftItems.MITHRIL_ARMOR_SET,
                p.itemName(JolCraftItems.MITHRIL_INGOT.get())
        );
    }

    private static void armorSetSimple(
            @NotNull AbstractRecipeProvider p,
            @NotNull ItemLike material,
            @NotNull JolCraftEquipmentHelper.ArmorSet<DeferredItem<Item>> set,
            @NotNull String unlockItemName
    ) {
        String unlock = "has_" + unlockItemName;

        ItemLike helmet = set.get(JolCraftEquipmentHelper.ArmorPiece.HELMET).get();
        ItemLike chestplate = set.get(JolCraftEquipmentHelper.ArmorPiece.CHESTPLATE).get();
        ItemLike leggings = set.get(JolCraftEquipmentHelper.ArmorPiece.LEGGINGS).get();
        ItemLike boots = set.get(JolCraftEquipmentHelper.ArmorPiece.BOOTS).get();

        p.modShaped(RecipeCategory.COMBAT, helmet)
                .pattern("BBB")
                .pattern("B B")
                .define('B', material)
                .unlockedBy(unlock, p.hasItem(material))
                .save(p.out(), p.inFolder(FOLDER, helmet));

        p.modShaped(RecipeCategory.COMBAT, chestplate)
                .pattern("B B")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', material)
                .unlockedBy(unlock, p.hasItem(material))
                .save(p.out(), p.inFolder(FOLDER, chestplate));

        p.modShaped(RecipeCategory.COMBAT, leggings)
                .pattern("BBB")
                .pattern("B B")
                .pattern("B B")
                .define('B', material)
                .unlockedBy(unlock, p.hasItem(material))
                .save(p.out(), p.inFolder(FOLDER, leggings));

        p.modShaped(RecipeCategory.COMBAT, boots)
                .pattern("B B")
                .pattern("B B")
                .define('B', material)
                .unlockedBy(unlock, p.hasItem(material))
                .save(p.out(), p.inFolder(FOLDER, boots));
    }

    private static void armorSetWithLining(
            @NotNull AbstractRecipeProvider p,
            @NotNull ItemLike ingot,
            @NotNull ItemLike lining,
            @NotNull JolCraftEquipmentHelper.ArmorSet<DeferredItem<Item>> set,
            @NotNull String unlockItemName
    ) {
        String unlock = "has_" + unlockItemName;

        ItemLike helmet = set.get(JolCraftEquipmentHelper.ArmorPiece.HELMET).get();
        ItemLike chestplate = set.get(JolCraftEquipmentHelper.ArmorPiece.CHESTPLATE).get();
        ItemLike leggings = set.get(JolCraftEquipmentHelper.ArmorPiece.LEGGINGS).get();
        ItemLike boots = set.get(JolCraftEquipmentHelper.ArmorPiece.BOOTS).get();

        p.modShaped(RecipeCategory.COMBAT, helmet)
                .pattern("BBB")
                .pattern("X X")
                .define('B', ingot)
                .define('X', lining)
                .unlockedBy(unlock, p.hasItem(ingot))
                .save(p.out(), p.inFolder(FOLDER, helmet));

        p.modShaped(RecipeCategory.COMBAT, chestplate)
                .pattern("B B")
                .pattern("XXX")
                .pattern("XXX")
                .define('B', ingot)
                .define('X', lining)
                .unlockedBy(unlock, p.hasItem(ingot))
                .save(p.out(), p.inFolder(FOLDER, chestplate));

        p.modShaped(RecipeCategory.COMBAT, leggings)
                .pattern("BBB")
                .pattern("X X")
                .pattern("X X")
                .define('B', ingot)
                .define('X', lining)
                .unlockedBy(unlock, p.hasItem(ingot))
                .save(p.out(), p.inFolder(FOLDER, leggings));

        p.modShaped(RecipeCategory.COMBAT, boots)
                .pattern("B B")
                .pattern("B B")
                .define('B', ingot)
                .unlockedBy(unlock, p.hasItem(ingot))
                .save(p.out(), p.inFolder(FOLDER, boots));
    }
}