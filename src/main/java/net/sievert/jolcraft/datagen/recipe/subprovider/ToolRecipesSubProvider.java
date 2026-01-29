package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ToolRecipesSubProvider implements AbstractRecipeProvider.RecipeSubProvider {

    private static final String FOLDER = "tool";

    @Override
    public void addRecipes(@NotNull AbstractRecipeProvider p) {

        toolSet(
                p,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                Items.STICK,
                JolCraftItems.DEEPSLATE_SWORD.get(),
                JolCraftItems.DEEPSLATE_PICKAXE.get(),
                JolCraftItems.DEEPSLATE_AXE.get(),
                JolCraftItems.DEEPSLATE_SHOVEL.get(),
                JolCraftItems.DEEPSLATE_HOE.get()
        );

        warhammer(
                p,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                Items.STICK,
                JolCraftItems.DEEPSLATE_WARHAMMER.get()
        );

        toolSet(
                p,
                JolCraftItems.MITHRIL_INGOT.get(),
                JolCraftItems.DEEPSLATE_ROD.get(),
                JolCraftItems.MITHRIL_SWORD.get(),
                JolCraftItems.MITHRIL_PICKAXE.get(),
                JolCraftItems.MITHRIL_AXE.get(),
                JolCraftItems.MITHRIL_SHOVEL.get(),
                JolCraftItems.MITHRIL_HOE.get()
        );

        warhammer(
                p,
                JolCraftItems.MITHRIL_INGOT.get(),
                JolCraftItems.DEEPSLATE_ROD.get(),
                JolCraftItems.MITHRIL_WARHAMMER.get()
        );

        artisanHammer(
                p,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                Items.STICK,
                JolCraftItems.DEEPSLATE_ARTISAN_HAMMER.get()
        );

        artisanHammer(
                p,
                JolCraftItems.MITHRIL_INGOT.get(),
                JolCraftItems.DEEPSLATE_ROD.get(),
                JolCraftItems.MITHRIL_ARTISAN_HAMMER.get()
        );

        chisel(
                p,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                Items.STICK,
                JolCraftItems.DEEPSLATE_CHISEL.get()
        );

        chisel(
                p,
                JolCraftItems.MITHRIL_INGOT.get(),
                JolCraftItems.DEEPSLATE_ROD.get(),
                JolCraftItems.MITHRIL_CHISEL.get()
        );

        pestle(
                p,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                JolCraftItems.DEEPSLATE_ROD.get(),
                JolCraftItems.DEEPSLATE_PESTLE.get()
        );

        pestle(
                p,
                JolCraftItems.MITHRIL_INGOT.get(),
                JolCraftItems.DEEPSLATE_ROD.get(),
                JolCraftItems.MITHRIL_PESTLE.get()
        );
    }

    private static void toolSet(
            AbstractRecipeProvider p,
            ItemLike head,
            ItemLike rod,
            ItemLike sword,
            ItemLike pickaxe,
            ItemLike axe,
            ItemLike shovel,
            ItemLike hoe
    ) {
        sword(p, head, rod, sword);
        pickaxe(p, head, rod, pickaxe);
        axe(p, head, rod, axe);
        shovel(p, head, rod, shovel);
        hoe(p, head, rod, hoe);
    }

    private static void sword(AbstractRecipeProvider p, ItemLike head, ItemLike rod, ItemLike out) {
        p.modShaped(RecipeCategory.COMBAT, out)
                .pattern("B")
                .pattern("B")
                .pattern("X")
                .define('B', head)
                .define('X', rod)
                .unlockedBy(p.hasName(head), p.hasItem(head))
                .save(p.out(), p.inFolder(FOLDER, out));
    }

    private static void pickaxe(AbstractRecipeProvider p, ItemLike head, ItemLike rod, ItemLike out) {
        p.modShaped(RecipeCategory.TOOLS, out)
                .pattern("BBB")
                .pattern(" X ")
                .pattern(" X ")
                .define('B', head)
                .define('X', rod)
                .unlockedBy(p.hasName(head), p.hasItem(head))
                .save(p.out(), p.inFolder(FOLDER, out));
    }

    private static void shovel(AbstractRecipeProvider p, ItemLike head, ItemLike rod, ItemLike out) {
        p.modShaped(RecipeCategory.TOOLS, out)
                .pattern("B")
                .pattern("X")
                .pattern("X")
                .define('B', head)
                .define('X', rod)
                .unlockedBy(p.hasName(head), p.hasItem(head))
                .save(p.out(), p.inFolder(FOLDER, out));
    }

    private static void axe(AbstractRecipeProvider p, ItemLike head, ItemLike rod, ItemLike out) {
        String baseId = p.itemName(out.asItem());

        p.modShaped(RecipeCategory.TOOLS, out)
                .pattern("BB")
                .pattern("BX")
                .pattern(" X")
                .define('B', head)
                .define('X', rod)
                .unlockedBy(p.hasName(head), p.hasItem(head))
                .save(p.out(), p.inFolder(FOLDER, baseId + "_left"));

        p.modShaped(RecipeCategory.TOOLS, out)
                .pattern("BB")
                .pattern("XB")
                .pattern("X ")
                .define('B', head)
                .define('X', rod)
                .unlockedBy(p.hasName(head), p.hasItem(head))
                .save(p.out(), p.inFolder(FOLDER, baseId + "_right"));
    }

    private static void hoe(AbstractRecipeProvider p, ItemLike head, ItemLike rod, ItemLike out) {
        String baseId = p.itemName(out.asItem());

        p.modShaped(RecipeCategory.TOOLS, out)
                .pattern("BB")
                .pattern(" X")
                .pattern(" X")
                .define('B', head)
                .define('X', rod)
                .unlockedBy(p.hasName(head), p.hasItem(head))
                .save(p.out(), p.inFolder(FOLDER, baseId + "_left"));

        p.modShaped(RecipeCategory.TOOLS, out)
                .pattern("BB")
                .pattern("X ")
                .pattern("X ")
                .define('B', head)
                .define('X', rod)
                .unlockedBy(p.hasName(head), p.hasItem(head))
                .save(p.out(), p.inFolder(FOLDER, baseId + "_right"));
    }

    private static void warhammer(AbstractRecipeProvider p, ItemLike head, ItemLike rod, ItemLike out) {
        String baseId = p.itemName(out.asItem());

        p.modShaped(RecipeCategory.COMBAT, out)
                .pattern("BB")
                .pattern("BB")
                .pattern(" X")
                .define('B', head)
                .define('X', rod)
                .unlockedBy(p.hasName(head), p.hasItem(head))
                .save(p.out(), p.inFolder(FOLDER, baseId + "_left"));

        p.modShaped(RecipeCategory.COMBAT, out)
                .pattern("BB")
                .pattern("BB")
                .pattern("X ")
                .define('B', head)
                .define('X', rod)
                .unlockedBy(p.hasName(head), p.hasItem(head))
                .save(p.out(), p.inFolder(FOLDER, baseId + "_right"));
    }

    private static void artisanHammer(AbstractRecipeProvider p, ItemLike head, ItemLike rod, ItemLike out) {
        p.modShaped(RecipeCategory.TOOLS, out)
                .pattern("B")
                .pattern("X")
                .define('B', head)
                .define('X', rod)
                .unlockedBy(p.hasName(head), p.hasItem(head))
                .save(p.out(), p.inFolder(FOLDER, out));
    }

    private static void chisel(AbstractRecipeProvider p, ItemLike head, ItemLike rod, ItemLike out) {
        String baseId = p.itemName(out.asItem());

        p.modShaped(RecipeCategory.TOOLS, out)
                .pattern(" B")
                .pattern("X ")
                .define('B', head)
                .define('X', rod)
                .unlockedBy(p.hasName(head), p.hasItem(head))
                .save(p.out(), p.inFolder(FOLDER, baseId + "_right"));

        p.modShaped(RecipeCategory.TOOLS, out)
                .pattern("B ")
                .pattern(" X")
                .define('B', head)
                .define('X', rod)
                .unlockedBy(p.hasName(head), p.hasItem(head))
                .save(p.out(), p.inFolder(FOLDER, baseId + "_left"));
    }

    private static void pestle(AbstractRecipeProvider p, ItemLike head, ItemLike rod, ItemLike out) {
        String baseId = p.itemName(out.asItem());

        p.modShaped(RecipeCategory.TOOLS, out)
                .pattern("X ")
                .pattern(" B")
                .define('B', head)
                .define('X', rod)
                .unlockedBy(p.hasName(head), p.hasItem(head))
                .save(p.out(), p.inFolder(FOLDER, baseId + "_right"));

        p.modShaped(RecipeCategory.TOOLS, out)
                .pattern(" X")
                .pattern("B ")
                .define('B', head)
                .define('X', rod)
                .unlockedBy(p.hasName(head), p.hasItem(head))
                .save(p.out(), p.inFolder(FOLDER, baseId + "_left"));
    }
}