package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla.VanillaRecipeBuilder;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.equipment.JolCraftArmorItemSet;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SameParameterValue")
public record EquipmentRecipesSubProvider(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    private static final String FOLDER = JolCraftDictionary.EQUIPMENT;

    public EquipmentRecipesSubProvider(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
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
        return FOLDER;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {
        armorSetSimple(
                output,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                JolCraftItems.DEEPSLATE_ARMOR_SET,
                JolCraftItems.DEEPSLATE_PLATE.get()
        );

        armorSetWithLining(
                output,
                JolCraftItems.MITHRIL_INGOT.get(),
                JolCraftItems.MITHRIL_CHAINWEAVE.get(),
                JolCraftItems.MITHRIL_ARMOR_SET,
                JolCraftItems.MITHRIL_INGOT.get()
        );
    }

    private static void armorSetSimple(
            RecipeOutput out,
            ItemLike material,
            JolCraftArmorItemSet set,
            ItemLike unlockItem
    ) {
        ItemLike helmet = set.get(ArmorItem.Type.HELMET).get();
        ItemLike chestplate = set.get(ArmorItem.Type.CHESTPLATE).get();
        ItemLike leggings = set.get(ArmorItem.Type.LEGGINGS).get();
        ItemLike boots = set.get(ArmorItem.Type.BOOTS).get();

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, helmet)
                )
                .pattern("BBB")
                .pattern("B B")
                .define('B', material)
                .unlockedByHas(unlockItem)
                .save(out, FOLDER, helmet);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, chestplate)
                )
                .pattern("B B")
                .pattern("BBB")
                .pattern("BBB")
                .define('B', material)
                .unlockedByHas(unlockItem)
                .save(out, FOLDER, chestplate);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, leggings)
                )
                .pattern("BBB")
                .pattern("B B")
                .pattern("B B")
                .define('B', material)
                .unlockedByHas(unlockItem)
                .save(out, FOLDER, leggings);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, boots)
                )
                .pattern("B B")
                .pattern("B B")
                .define('B', material)
                .unlockedByHas(unlockItem)
                .save(out, FOLDER, boots);
    }

    private static void armorSetWithLining(
            RecipeOutput out,
            ItemLike ingot,
            ItemLike lining,
            JolCraftArmorItemSet set,
            ItemLike unlockItem
    ) {
        ItemLike helmet = set.get(ArmorItem.Type.HELMET).get();
        ItemLike chestplate = set.get(ArmorItem.Type.CHESTPLATE).get();
        ItemLike leggings = set.get(ArmorItem.Type.LEGGINGS).get();
        ItemLike boots = set.get(ArmorItem.Type.BOOTS).get();

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, helmet)
                )
                .pattern("BBB")
                .pattern("X X")
                .define('B', ingot)
                .define('X', lining)
                .unlockedByHas(unlockItem)
                .save(out, FOLDER, helmet);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, chestplate)
                )
                .pattern("B B")
                .pattern("XXX")
                .pattern("XXX")
                .define('B', ingot)
                .define('X', lining)
                .unlockedByHas(unlockItem)
                .save(out, FOLDER, chestplate);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, leggings)
                )
                .pattern("BBB")
                .pattern("X X")
                .pattern("X X")
                .define('B', ingot)
                .define('X', lining)
                .unlockedByHas(unlockItem)
                .save(out, FOLDER, leggings);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, boots)
                )
                .pattern("B B")
                .pattern("B B")
                .define('B', ingot)
                .unlockedByHas(unlockItem)
                .save(out, FOLDER, boots);
    }
}