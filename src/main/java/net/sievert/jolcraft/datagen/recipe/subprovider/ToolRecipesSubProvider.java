package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.custom.vanilla.VanillaRecipeBuilder;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings({"SameParameterValue", "deprecation"})
public record ToolRecipesSubProvider(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    private static final String FOLDER = JolCraftDictionary.TOOL;

    public ToolRecipesSubProvider(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
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
        toolSet(
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
                output,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                Items.STICK,
                JolCraftItems.DEEPSLATE_WARHAMMER.get()
        );

        toolSet(
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
                output,
                JolCraftItems.MITHRIL_INGOT.get(),
                JolCraftItems.DEEPSLATE_ROD.get(),
                JolCraftItems.MITHRIL_WARHAMMER.get()
        );

        artisanHammer(
                output,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                Items.STICK,
                JolCraftItems.DEEPSLATE_ARTISAN_HAMMER.get()
        );

        artisanHammer(
                output,
                JolCraftItems.MITHRIL_INGOT.get(),
                JolCraftItems.DEEPSLATE_ROD.get(),
                JolCraftItems.MITHRIL_ARTISAN_HAMMER.get()
        );

        chisel(
                output,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                Items.STICK,
                JolCraftItems.DEEPSLATE_CHISEL.get()
        );

        chisel(
                output,
                JolCraftItems.MITHRIL_INGOT.get(),
                JolCraftItems.DEEPSLATE_ROD.get(),
                JolCraftItems.MITHRIL_CHISEL.get()
        );

        pestle(
                output,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                JolCraftItems.DEEPSLATE_ROD.get(),
                JolCraftItems.DEEPSLATE_PESTLE.get()
        );

        pestle(
                output,
                JolCraftItems.MITHRIL_INGOT.get(),
                JolCraftItems.DEEPSLATE_ROD.get(),
                JolCraftItems.MITHRIL_PESTLE.get()
        );
    }

    private static void toolSet(
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike sword,
            ItemLike pickaxe,
            ItemLike axe,
            ItemLike shovel,
            ItemLike hoe
    ) {
        sword(out, head, rod, sword);
        pickaxe(out, head, rod, pickaxe);
        axe(out, head, rod, axe);
        shovel(out, head, rod, shovel);
        hoe(out, head, rod, hoe);
    }

    private static void sword(
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, outItem)
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
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, outItem)
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
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, outItem)
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
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        String baseId = itemName(outItem);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, outItem)
                )
                .pattern("BB")
                .pattern("BX")
                .pattern(" X")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, baseId + "_left");

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, outItem)
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
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        String baseId = itemName(outItem);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, outItem)
                )
                .pattern("BB")
                .pattern(" X")
                .pattern(" X")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, baseId + "_left");

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, outItem)
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
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        String baseId = itemName(outItem);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, outItem)
                )
                .pattern("BB")
                .pattern("BB")
                .pattern(" X")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, baseId + "_left");

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, outItem)
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
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, outItem)
                )
                .pattern("B")
                .pattern("X")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, outItem);
    }

    private static void chisel(
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        String baseId = itemName(outItem);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, outItem)
                )
                .pattern(" B")
                .pattern("X ")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, baseId + "_right");

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, outItem)
                )
                .pattern("B ")
                .pattern(" X")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, baseId + "_left");
    }

    private static void pestle(
            RecipeOutput out,
            ItemLike head,
            ItemLike rod,
            ItemLike outItem
    ) {
        String baseId = itemName(outItem);

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, outItem)
                )
                .pattern("X ")
                .pattern(" B")
                .define('B', head)
                .define('X', rod)
                .unlockedByHas(head)
                .save(out, FOLDER, baseId + "_right");

        VanillaRecipeBuilder.shaped(
                        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, outItem)
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