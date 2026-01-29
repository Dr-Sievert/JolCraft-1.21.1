package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

public final class CompassRecipesSubProvider implements AbstractRecipeProvider.RecipeSubProvider {

    private static final String FOLDER = "deepslate_compass";

    @SuppressWarnings("deprecation")
    @Override
    public void addRecipes(@NotNull AbstractRecipeProvider p) {

        p.modShaped(RecipeCategory.MISC, JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                .pattern(" B ")
                .pattern("B B")
                .pattern(" B ")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .unlockedBy(p.hasName(JolCraftItems.DEEPSLATE_PLATE.get()), p.hasItem(JolCraftItems.DEEPSLATE_PLATE.get()))
                .save(p.out(), p.inFolder(FOLDER, p.itemName(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())));

        for (int i = 0; i < AbstractRecipeProvider.DYES.size(); i++) {
            Item dyeItem = AbstractRecipeProvider.DYES.get(i);
            DyeColor dyeColor = DyeColor.values()[i];
            int colorInt = dyeColor.getFireworkColor();

            ItemStack dyedEmpty = new ItemStack(
                    JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get().builtInRegistryHolder(),
                    1,
                    DataComponentPatch.builder()
                            .set(DataComponents.DYED_COLOR, new DyedItemColor(colorInt, true))
                            .build()
            );

            String idPath = p.itemName(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()) + "_" + dyeColor.getName();

            p.modShapeless(RecipeCategory.MISC, dyedEmpty.getItem())
                    .requires(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                    .requires(dyeItem)
                    .unlockedBy(p.hasName(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()), p.hasItem(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()))
                    .unlockedBy(p.hasName(dyeItem), p.hasItem(dyeItem))
                    .save(p.out(), p.inFolder(FOLDER, idPath));
        }

        p.modShapeless(RecipeCategory.MISC, JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                .requires(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                .unlockedBy(p.hasName(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()), p.hasItem(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()))
                .save(p.out(), p.inFolder(FOLDER, p.itemName(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()) + "_remove_dye"));

        for (int i = 0; i < AbstractRecipeProvider.DYES.size(); i++) {
            Item dyeItem = AbstractRecipeProvider.DYES.get(i);
            DyeColor dyeColor = DyeColor.values()[i];
            int colorInt = dyeColor.getFireworkColor();

            ItemStack dyedFull = new ItemStack(
                    JolCraftItems.DEEPSLATE_COMPASS.get().builtInRegistryHolder(),
                    1,
                    DataComponentPatch.builder()
                            .set(DataComponents.DYED_COLOR, new DyedItemColor(colorInt, true))
                            .build()
            );

            String idPath = p.itemName(JolCraftItems.DEEPSLATE_COMPASS.get()) + "_" + dyeColor.getName();

            p.modShapeless(RecipeCategory.MISC, dyedFull.getItem())
                    .requires(JolCraftItems.DEEPSLATE_COMPASS.get())
                    .requires(dyeItem)
                    .unlockedBy(p.hasName(JolCraftItems.DEEPSLATE_COMPASS.get()), p.hasItem(JolCraftItems.DEEPSLATE_COMPASS.get()))
                    .unlockedBy(p.hasName(dyeItem), p.hasItem(dyeItem))
                    .save(p.out(), p.inFolder(FOLDER, idPath));
        }

        p.modShapeless(RecipeCategory.MISC, JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                .requires(JolCraftItems.DEEPSLATE_COMPASS.get())
                .unlockedBy(p.hasName(JolCraftItems.DEEPSLATE_COMPASS.get()), p.hasItem(JolCraftItems.DEEPSLATE_COMPASS.get()))
                .save(p.out(), p.inFolder(FOLDER, p.itemName(JolCraftItems.DEEPSLATE_COMPASS.get()) + "_remove_dial"));
    }
}