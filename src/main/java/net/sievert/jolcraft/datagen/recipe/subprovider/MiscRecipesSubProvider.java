package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class MiscRecipesSubProvider implements AbstractRecipeProvider.RecipeSubProvider {

    @Override
    public void addRecipes(AbstractRecipeProvider p) {

        p.modShaped(RecipeCategory.MISC, JolCraftItems.DEEPSLATE_MORTAR_ITEM.get())
                .pattern("B B")
                .pattern(" B ")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .unlockedBy(p.hasName(JolCraftItems.DEEPSLATE_PLATE.get()), p.hasItem(JolCraftItems.DEEPSLATE_PLATE.get()))
                .save(p.out());

        p.modShaped(RecipeCategory.MISC, JolCraftItems.PARCHMENT.get())
                .pattern("B")
                .pattern("B")
                .pattern("B")
                .define('B', Items.PAPER)
                .unlockedBy(p.hasName(Items.PAPER), p.hasItem(Items.PAPER))
                .save(p.out());

        p.modShaped(RecipeCategory.MISC, JolCraftItems.CONTRACT_BLANK.get())
                .pattern("BB")
                .pattern("BB")
                .pattern("BB")
                .define('B', Items.PAPER)
                .unlockedBy(p.hasName(Items.PAPER), p.hasItem(Items.PAPER))
                .save(p.out());

        p.modShapeless(RecipeCategory.MISC, JolCraftItems.CONTRACT_WRITTEN.get())
                .requires(JolCraftItems.CONTRACT_BLANK.get())
                .requires(JolCraftTags.Items.INK_AND_QUILLS)
                .unlockedBy(p.hasName(JolCraftItems.CONTRACT_BLANK.get()), p.hasItem(JolCraftItems.CONTRACT_BLANK.get()))
                .save(p.out());

        p.modShapeless(RecipeCategory.MISC, JolCraftItems.CONTRACT_GUILDMASTER.get())
                .requires(JolCraftItems.GUILD_SIGIL.get())
                .requires(JolCraftItems.CONTRACT_SIGNED.get())
                .unlockedBy(p.hasName(JolCraftItems.CONTRACT_SIGNED.get()), p.hasItem(JolCraftItems.CONTRACT_SIGNED.get()))
                .save(p.out());

        p.modShaped(RecipeCategory.MISC, JolCraftItems.GLASS_MUG.get())
                .pattern("B ")
                .pattern("BB")
                .pattern("B ")
                .define('B', Items.GLASS)
                .unlockedBy(p.hasName(Items.GLASS), p.hasItem(Items.GLASS))
                .save(p.out());

        p.modShapeless(RecipeCategory.MISC, JolCraftItems.QUILL_EMPTY.get())
                .requires(Items.GLASS)
                .requires(Items.FEATHER)
                .unlockedBy(p.hasName(Items.FEATHER), p.hasItem(Items.FEATHER))
                .save(p.out());

        p.modShapeless(RecipeCategory.MISC, JolCraftItems.QUILL_FULL.get())
                .requires(Items.GLASS)
                .requires(Items.FEATHER)
                .requires(Items.INK_SAC)
                .unlockedBy(p.hasName(Items.INK_SAC), p.hasItem(Items.INK_SAC))
                .save(p.out());

        p.modShapeless(RecipeCategory.MISC, JolCraftItems.QUILL_FULL.get())
                .requires(JolCraftItems.QUILL_EMPTY.get())
                .requires(Items.INK_SAC)
                .unlockedBy(p.hasName(JolCraftItems.QUILL_EMPTY.get()), p.hasItem(JolCraftItems.QUILL_EMPTY.get()))
                .save(p.out(), p.itemName(JolCraftItems.QUILL_FULL.get()) + "_refill");

        p.modShaped(RecipeCategory.MISC, JolCraftItems.COIN_POUCH.get())
                .pattern("XBX")
                .pattern("B B")
                .pattern("BBB")
                .define('B', Items.LEATHER)
                .define('X', Items.STRING)
                .unlockedBy(p.hasName(JolCraftItems.GOLD_COIN.get()), p.hasItem(JolCraftItems.GOLD_COIN.get()))
                .save(p.out());

        p.modShaped(RecipeCategory.MISC, JolCraftItems.STRONGBOX_ITEM.get())
                .pattern("BXB")
                .pattern("X X")
                .pattern("BXB")
                .define('B', JolCraftItems.DEEPSLATE_PLATE.get())
                .define('X', Items.DEEPSLATE_TILES)
                .unlockedBy(p.hasName(JolCraftItems.DEEPSLATE_PLATE.get()), p.hasItem(JolCraftItems.DEEPSLATE_PLATE.get()))
                .save(p.out());

        p.modShaped(RecipeCategory.MISC, JolCraftItems.LOCKPICK.get())
                .pattern("  B")
                .pattern(" B ")
                .pattern("B  ")
                .define('B', Items.IRON_NUGGET)
                .unlockedBy(p.hasName(Items.IRON_NUGGET), p.hasItem(Items.IRON_NUGGET))
                .save(p.out(), p.itemName(JolCraftItems.LOCKPICK.get()) + "_right");

        p.modShaped(RecipeCategory.MISC, JolCraftItems.LOCKPICK.get())
                .pattern("B  ")
                .pattern(" B ")
                .pattern("  B")
                .define('B', Items.IRON_NUGGET)
                .unlockedBy(p.hasName(Items.IRON_NUGGET), p.hasItem(Items.IRON_NUGGET))
                .save(p.out(), p.itemName(JolCraftItems.LOCKPICK.get()) + "_left");

        p.nineBlockStorageRecipesAuto(
                RecipeCategory.MISC,
                JolCraftItems.SCRAP.get(),
                RecipeCategory.MISC,
                JolCraftItems.SCRAP_HEAP.get()
        );

        p.nineBlockStorageRecipesAuto(
                RecipeCategory.MISC,
                JolCraftItems.BARLEY.get(),
                RecipeCategory.MISC,
                JolCraftBlocks.BARLEY_BLOCK.get()
        );

        p.modShaped(RecipeCategory.MISC, JolCraftBlocks.MUFFHORN_FUR_BLOCK.get())
                .pattern("BB")
                .pattern("BB")
                .define('B', JolCraftItems.MUFFHORN_FUR.get())
                .unlockedBy(p.hasName(JolCraftItems.MUFFHORN_FUR.get()), p.hasItem(JolCraftItems.MUFFHORN_FUR.get()))
                .save(p.out());

        p.modShapeless(RecipeCategory.MISC, JolCraftItems.MUFFHORN_FUR.get(), 4)
                .requires(JolCraftBlocks.MUFFHORN_FUR_BLOCK.get())
                .unlockedBy(p.hasName(JolCraftItems.MUFFHORN_FUR.get()), p.hasItem(JolCraftItems.MUFFHORN_FUR.get()))
                .save(p.out());

        p.modShapeless(RecipeCategory.MISC, JolCraftBlocks.VERDANT_SOIL.get())
                .requires(Blocks.MUD)
                .requires(JolCraftItems.VERDANITE_DUST.get())
                .unlockedBy(p.hasName(JolCraftItems.VERDANITE_DUST.get()), p.hasItem(JolCraftItems.VERDANITE_DUST.get()))
                .save(p.out());

        p.modShapeless(RecipeCategory.MISC, JolCraftItems.INVERIX.get(), 3)
                .requires(JolCraftItems.MUFFHORN_MILK_BUCKET.get())
                .requires(Items.COAL)
                .unlockedBy(p.hasName(JolCraftItems.MUFFHORN_MILK_BUCKET.get()), p.hasItem(JolCraftItems.MUFFHORN_MILK_BUCKET.get()))
                .save(p.out(), p.itemName(JolCraftItems.INVERIX.get()) + "_coal");

        p.modShapeless(RecipeCategory.MISC, JolCraftItems.INVERIX.get(), 3)
                .requires(JolCraftItems.MUFFHORN_MILK_BUCKET.get())
                .requires(Items.CHARCOAL)
                .unlockedBy(p.hasName(JolCraftItems.MUFFHORN_MILK_BUCKET.get()), p.hasItem(JolCraftItems.MUFFHORN_MILK_BUCKET.get()))
                .save(p.out(), p.itemName(JolCraftItems.INVERIX.get()) + "_charcoal");

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(JolCraftItems.BARLEY.get()),
                        RecipeCategory.FOOD,
                        JolCraftItems.BARLEY_MALT.get(),
                        0.35f,
                        200
                )
                .unlockedBy(p.hasName(JolCraftItems.BARLEY.get()), p.hasItem(JolCraftItems.BARLEY.get()))
                .save(
                        p.out(),
                        ResourceKey.create(
                                Registries.RECIPE,
                                JolCraft.location(p.itemName(JolCraftItems.BARLEY_MALT.get()) + "_from_smelting")
                        )
                );

        SimpleCookingRecipeBuilder.smoking(
                        Ingredient.of(JolCraftItems.BARLEY.get()),
                        RecipeCategory.FOOD,
                        JolCraftItems.BARLEY_MALT.get(),
                        0.35f,
                        100
                )
                .unlockedBy(p.hasName(JolCraftItems.BARLEY.get()), p.hasItem(JolCraftItems.BARLEY.get()))
                .save(
                        p.out(),
                        ResourceKey.create(
                                Registries.RECIPE,
                                JolCraft.location(p.itemName(JolCraftItems.BARLEY_MALT.get()) + "_from_smoking")
                        )
                );
    }
}