package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeLookups;
import net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla.VanillaRecipeBuilder;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings({"SameParameterValue", "deprecation"})
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ToolRecipesSubProvider implements RecipeSubProvider {

    private static final String FOLDER = JolCraftDictionary.TOOL;

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
        toolSet(
                lookups.items(),
                output,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                Items.STICK,
                JolCraftItems.DEEPSLATE_SWORD.get(),
                JolCraftItems.DEEPSLATE_PICKAXE.get(),
                JolCraftItems.DEEPSLATE_AXE.get(),
                JolCraftItems.DEEPSLATE_SHOVEL.get(),
                JolCraftItems.DEEPSLATE_HOE.get()
        );

        warhammer(
                lookups.items(),
                output,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                Items.STICK,
                JolCraftItems.DEEPSLATE_WARHAMMER.get()
        );

        toolSet(
                lookups.items(),
                output,
                JolCraftItems.MITHRIL_INGOT.get(),
                JolCraftItems.DEEPSLATE_ROD.get(),
                JolCraftItems.MITHRIL_SWORD.get(),
                JolCraftItems.MITHRIL_PICKAXE.get(),
                JolCraftItems.MITHRIL_AXE.get(),
                JolCraftItems.MITHRIL_SHOVEL.get(),
                JolCraftItems.MITHRIL_HOE.get()
        );

        warhammer(
                lookups.items(),
                output,
                JolCraftItems.MITHRIL_INGOT.get(),
                JolCraftItems.DEEPSLATE_ROD.get(),
                JolCraftItems.MITHRIL_WARHAMMER.get()
        );

        artisanHammer(
                lookups.items(),
                output,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                Items.STICK,
                JolCraftItems.DEEPSLATE_ARTISAN_HAMMER.get()
        );

        artisanHammer(
                lookups.items(),
                output,
                JolCraftItems.MITHRIL_INGOT.get(),
                JolCraftItems.DEEPSLATE_ROD.get(),
                JolCraftItems.MITHRIL_ARTISAN_HAMMER.get()
        );

        chisel(
                lookups.items(),
                output,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                Items.STICK,
                JolCraftItems.DEEPSLATE_CHISEL.get()
        );

        chisel(
                lookups.items(),
                output,
                JolCraftItems.MITHRIL_INGOT.get(),
                JolCraftItems.DEEPSLATE_ROD.get(),
                JolCraftItems.MITHRIL_CHISEL.get()
        );

        pestle(
                lookups.items(),
                output,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                JolCraftItems.DEEPSLATE_ROD.get(),
                JolCraftItems.DEEPSLATE_PESTLE.get()
        );

        pestle(
                lookups.items(),
                output,
                JolCraftItems.MITHRIL_INGOT.get(),
                JolCraftItems.DEEPSLATE_ROD.get(),
                JolCraftItems.MITHRIL_PESTLE.get()
        );
    }

    private static void toolSet(
            HolderGetter<Item> items,
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike sword,
            ItemLike pickaxe,
            ItemLike axe,
            ItemLike shovel,
            ItemLike hoe
    ) {
        sword(items, out, head, rod, sword);
        pickaxe(items, out, head, rod, pickaxe);
        axe(items, out, head, rod, axe);
        shovel(items, out, head, rod, shovel);
        hoe(items, out, head, rod, hoe);
    }

    private static void sword(
            HolderGetter<Item> items,
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.COMBAT, outItem)
                )
                .pattern("B")
                .pattern("B")
                .pattern("X")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, outItem);
    }

    private static void pickaxe(
            HolderGetter<Item> items,
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, outItem)
                )
                .pattern("BBB")
                .pattern(" X ")
                .pattern(" X ")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, outItem);
    }

    private static void shovel(
            HolderGetter<Item> items,
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, outItem)
                )
                .pattern("B")
                .pattern("X")
                .pattern("X")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, outItem);
    }

    private static void axe(
            HolderGetter<Item> items,
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        String baseId = itemName(outItem);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, outItem)
                )
                .pattern("BB")
                .pattern("BX")
                .pattern(" X")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, baseId + "_left");

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, outItem)
                )
                .pattern("BB")
                .pattern("XB")
                .pattern("X ")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, baseId + "_right");
    }

    private static void hoe(
            HolderGetter<Item> items,
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        String baseId = itemName(outItem);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, outItem)
                )
                .pattern("BB")
                .pattern(" X")
                .pattern(" X")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, baseId + "_left");

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, outItem)
                )
                .pattern("BB")
                .pattern("X ")
                .pattern("X ")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, baseId + "_right");
    }

    private static void warhammer(
            HolderGetter<Item> items,
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        String baseId = itemName(outItem);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.COMBAT, outItem)
                )
                .pattern("BB")
                .pattern("BB")
                .pattern(" X")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, baseId + "_left");

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.COMBAT, outItem)
                )
                .pattern("BB")
                .pattern("BB")
                .pattern("X ")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, baseId + "_right");
    }

    private static void artisanHammer(
            HolderGetter<Item> items,
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, outItem)
                )
                .pattern("B")
                .pattern("X")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, outItem);
    }

    private static void chisel(
            HolderGetter<Item> items,
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        String baseId = itemName(outItem);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, outItem)
                )
                .pattern(" B")
                .pattern("X ")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, baseId + "_right");

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, outItem)
                )
                .pattern("B ")
                .pattern(" X")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, baseId + "_left");
    }

    private static void pestle(
            HolderGetter<Item> items,
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        String baseId = itemName(outItem);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, outItem)
                )
                .pattern("X ")
                .pattern(" B")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, baseId + "_right");

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(items, RecipeCategory.TOOLS, outItem)
                )
                .pattern(" X")
                .pattern("B ")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, baseId + "_left");
    }

    private static @NotNull String itemName(ItemLike item) {
        return item.asItem().builtInRegistryHolder().key().location().getPath();
    }
}