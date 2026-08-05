package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.potion.JolCraftPotions;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeGroup;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SameParameterValue")
public record DwarfMerchantTrades(
        JolCraftDataProvider<RecipeOutput> parent
) implements DwarfTradeSubProvider {

    private static final DwarfProfession PROFESSION =
            DwarfProfession.MERCHANT;

    public DwarfMerchantTrades(
            @NotNull JolCraftDataProvider<RecipeOutput> parent
    ) {
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
        return PROFESSION.professionName();
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {

        addBountyTrades(output, tracking, PROFESSION);

        // ---------------------------------------------------------------------
        // Novice
        // ---------------------------------------------------------------------

        // Mining & Materials

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.TORCH, 1, 3, 8, 16
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.COAL, 2, 4, 8, 12
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.FLINT, 1, 2, 6, 12
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.COPPER_INGOT, 2, 4, 6, 6
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.IRON_NUGGET, 3, 5, 14, 20
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Blocks.COBBLED_DEEPSLATE, 2, 4, 16, 32
        );

        // Farming & Food

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.WHEAT_SEEDS, 1, 2, 4, 8, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.BEETROOT_SEEDS, 1, 3, 3, 5, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.WHEAT, 3, 5, 6, 12
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.BEETROOT, 2, 4, 5, 8
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.BAKED_POTATO, 2, 3, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.APPLE, 1, 3, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.EGG, 2, 3, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.FEATHER, 3, 5, 3, 5
        );

        // Plants & Nature

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.ALLIUM, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.AZURE_BLUET, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.BLUE_ORCHID, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.CORNFLOWER, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.DANDELION, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.LILY_OF_THE_VALLEY, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.OXEYE_DAISY, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.POPPY, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.ORANGE_TULIP, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.PINK_TULIP, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.RED_TULIP, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.WHITE_TULIP, 1, 3, 1, 3, 3, 1
        );

        // Utility & Crafting

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.STRING, 2, 3, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.PAPER, 2, 4, 4, 8
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.CHAIN, 1, 3, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                JolCraftItems.QUILL_EMPTY, 2, 4, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                JolCraftItems.GLASS_MUG, 1, 3, 1, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.GLASS_BOTTLE, 1, 3, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.CANDLE, 2, 4, 2, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.FLOWER_POT, 1, 2, 1, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.BLACK_DYE, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.BLUE_DYE, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.BROWN_DYE, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.CYAN_DYE, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.GRAY_DYE, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.GREEN_DYE, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.LIGHT_BLUE_DYE, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.LIGHT_GRAY_DYE, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.LIME_DYE, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.MAGENTA_DYE, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.ORANGE_DYE, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.PINK_DYE, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.PURPLE_DYE, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.RED_DYE, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.WHITE_DYE, 1, 3, 1, 3, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.YELLOW_DYE, 1, 3, 1, 3, 3, 1
        );

        // ---------------------------------------------------------------------
        // Apprentice
        // ---------------------------------------------------------------------

        // Mining & Materials

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.IRON_INGOT, 4, 8, 2, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Blocks.DEEPSLATE_TILES, 4, 6, 16, 32
        );

        // Farming & Food

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                JolCraftItems.BARLEY, 2, 4, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.CARROT, 5, 8, 3, 5, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.POTATO, 5, 8, 3, 5, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.MELON_SLICE, 5, 8, 3, 5, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.PUMPKIN, 5, 8, 1, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.SUGAR_CANE, 2, 4, 5, 8, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.CACTUS, 5, 8, 3, 5, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.COCOA_BEANS, 5, 8, 3, 5, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.SWEET_BERRIES, 5, 8, 3, 5, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.GLOW_BERRIES, 5, 8, 3, 5, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.BREAD, 3, 5, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.COOKED_MUTTON, 5, 7, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.COOKED_RABBIT, 5, 7, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.COOKIE, 3, 5, 3, 5
        );

        // Plants & Nature

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                JolCraftBlocks.DUSKCAP, 6, 8, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                JolCraftBlocks.FESTERLING, 5, 7, 1, 2
        );

        // Utility & Crafting

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.HONEYCOMB, 3, 5, 2, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.HONEY_BOTTLE, 2, 4, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.BUCKET, 7, 10, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.FISHING_ROD, 3, 5, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.FLINT_AND_STEEL, 3, 5, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.BRUSH, 3, 5, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.LANTERN, 3, 5, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                JolCraftItems.PARCHMENT, 3, 6, 2, 4
        );

        // ---------------------------------------------------------------------
        // Journeyman
        // ---------------------------------------------------------------------

        // Mining & Materials

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.LAPIS_LAZULI, 5, 8, 6, 12
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.REDSTONE, 3, 5, 6, 12
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.QUARTZ, 3, 5, 5, 8
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.GLOWSTONE_DUST, 5, 8, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.GEODE_SMALL, 15, 25, 1, 1, 2
        );

        // Farming & Food

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.COOKED_BEEF, 5, 8, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.COOKED_CHICKEN, 5, 8, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.COOKED_PORKCHOP, 5, 8, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.NETHER_WART, 5, 8, 1, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.ASGARNIAN_HOPS, 5, 8, 3, 6
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.DUSKHOLD_HOPS, 5, 8, 3, 6
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.KRANDONIAN_HOPS, 5, 8, 3, 6
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.YANILLIAN_HOPS, 5, 8, 3, 6
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.DEEPSLATE_BULBS, 5, 8, 1, 3, 1
        );

        // Mob Materials

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.BONE, 1, 3, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.GUNPOWDER, 2, 4, 1, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.SPIDER_EYE, 3, 5, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.INK_SAC, 3, 5, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.MUFFHORN_FUR, 5, 8, 1, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.MUFFHORN_MILK_BUCKET, 3, 5, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.LEATHER, 3, 5, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.SLIME_BALL, 5, 8, 2, 4
        );

        // Utility & Crafting

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.YEAST, 6, 9, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.INVERIX, 5, 8, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.CONTRACT_BLANK, 3, 5, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.LOCKPICK, 4, 8, 5, 10
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.BOOK, 3, 6, 2, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.SPYGLASS, 5, 10, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.LEAD, 5, 10, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.QUILL_FULL, 5, 10, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.ITEM_FRAME, 3, 5, 2, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.PAINTING, 5, 8, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.SADDLE, 10, 15, 1, 1, 1
        );

        // Potions

        pooledPotionBuy(output, tracking,
                DwarfMerchantData.Level.JOURNEYMAN,
                Potions.STRONG_HEALING,
                8,
                12,
                3
        );

        pooledPotionBuy(output, tracking,
                DwarfMerchantData.Level.JOURNEYMAN,
                Potions.SWIFTNESS,
                12,
                18,
                3
        );

        pooledPotionBuy(output, tracking,
                DwarfMerchantData.Level.JOURNEYMAN,
                Potions.WATER_BREATHING,
                12,
                18,
                3
        );

        pooledPotionBuy(output, tracking,
                DwarfMerchantData.Level.JOURNEYMAN,
                Potions.NIGHT_VISION,
                12,
                18,
                3
        );

        // ---------------------------------------------------------------------
        // Expert
        // ---------------------------------------------------------------------

        // Mining & Materials

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.GOLD_INGOT, 6, 9, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.EMERALD, 5, 8, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.AMETHYST_SHARD, 5, 10, 2, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.DEEPSLATE_PLATE, 6, 12, 2, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.DIAMOND, 12, 18, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.GEODE_MEDIUM, 20, 30, 1, 1, 1
        );

        // Mob Materials

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.GHAST_TEAR, 7, 14, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.PHANTOM_MEMBRANE, 6, 12, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.ENDER_PEARL, 6, 12, 1, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.BLAZE_POWDER, 6, 12, 1, 3
        );

        // Equipment

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.DEEPSLATE_WARHAMMER, 18, 30, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.DEEPSLATE_SWORD, 12, 24, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.DEEPSLATE_PICKAXE, 14, 28, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.DEEPSLATE_SHOVEL, 12, 20, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.DEEPSLATE_AXE, 14, 28, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.DEEPSLATE_HOE, 12, 20, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.DEEPSLATE_HELMET, 18, 30, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.DEEPSLATE_CHESTPLATE, 25, 40, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.DEEPSLATE_LEGGINGS, 20, 35, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.DEEPSLATE_BOOTS, 16, 24, 1, 1, 1
        );

        // Utility

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.GOLDEN_APPLE, 16, 24, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.CAULDRON, 6, 12, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Blocks.BOOKSHELF, 6, 12, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.STRONGBOX_ITEM, 16, 24, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.EXPERIENCE_BOTTLE, 10, 16, 3, 6
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.NAME_TAG, 6, 12, 1, 1
        );

        // Gemstone Dusts

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.AEGISCORE_DUST, 6, 12, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.ASHFANG_DUST, 6, 12, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.DEEPMARROW_DUST, 6, 12, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.EARTHBLOOD_DUST, 6, 12, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.EMBERGLASS_DUST, 6, 12, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.FROSTVEIN_DUST, 6, 12, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.GRIMSTONE_DUST, 6, 12, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.IRONHEART_DUST, 6, 12, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.LUMIERE_DUST, 6, 12, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.MOONSHARD_DUST, 6, 12, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.SKYBURROW_DUST, 6, 12, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.SUNGLEAM_DUST, 6, 12, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.VERDANITE_DUST, 6, 12, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.WOECRYSTAL_DUST, 6, 12, 1, 1, 2
        );

        // Tomes

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.UNIDENTIFIED_DWARVEN_TOME, 4, 8, 1, 1
        );

        // Potions

        pooledPotionBuy(output, tracking,
                DwarfMerchantData.Level.EXPERT,
                JolCraftPotions.CORROSION,
                14,
                20,
                3
        );

        pooledPotionBuy(output, tracking,
                DwarfMerchantData.Level.EXPERT,
                JolCraftPotions.BULWARK,
                14,
                20,
                3
        );

        pooledPotionBuy(output, tracking,
                DwarfMerchantData.Level.EXPERT,
                JolCraftPotions.DWARVEN_HASTE,
                14,
                20,
                3
        );

        pooledPotionBuy(output, tracking,
                DwarfMerchantData.Level.EXPERT,
                JolCraftPotions.LOCKPICKING,
                14,
                20,
                3
        );

        // ---------------------------------------------------------------------
        // Master
        // ---------------------------------------------------------------------

        // Mining & Materials

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.GEODE_LARGE, 30, 40, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                Items.ECHO_SHARD, 20, 30, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                Items.NETHERITE_SCRAP, 30, 60, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.IMPURE_MITHRIL, 40, 60, 1, 1, 1
        );

        // Gemstones

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.AEGISCORE, 16, 32, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.ASHFANG, 16, 32, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.DEEPMARROW, 16, 32, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.EARTHBLOOD, 16, 32, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.EMBERGLASS, 16, 32, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.FROSTVEIN, 16, 32, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.GRIMSTONE, 16, 32, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.IRONHEART, 16, 32, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.LUMIERE, 16, 32, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.MOONSHARD, 16, 32, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.SKYBURROW, 16, 32, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.SUNGLEAM, 16, 32, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.VERDANITE, 16, 32, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.WOECRYSTAL, 16, 32, 1, 1, 1
        );

        // Tomes & Pages

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.UNIDENTIFIED_ANCIENT_DWARVEN_TOME, 15, 25, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.LEGENDARY_PAGE, 15, 25, 2, 6, 1
        );

        pooledCrate(output, tracking,
                DwarfMerchantData.Level.MASTER,
                JolCraftItems.RESTOCK_CRATE.get(),
                15,
                30
        );

        pooledCrate(output, tracking,
                DwarfMerchantData.Level.MASTER,
                JolCraftItems.REROLL_CRATE.get(),
                20,
                40
        );
    }

    private void pooledBuy(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level level,
            @NotNull ItemLike result,
            int minCoins,
            int maxCoins,
            int minCount,
            int maxCount,
            int maxUses,
            int weight
    ) {
        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(level)
                        .tradeGroup(TradeGroup.CUMULATIVE_POOL)
                        .weight(weight)
                        .maxUses(maxUses)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
                        .costACoins(minCoins, maxCoins)
                        .noCostB()
                        .result(result, minCount, maxCount)
        );
    }

    private void pooledBuy(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level level,
            @NotNull ItemLike result,
            int minCoins,
            int maxCoins,
            int minCount,
            int maxCount,
            int maxUses
    ) {
        pooledBuy(output, tracking, level, result, minCoins, maxCoins, minCount, maxCount, maxUses, 3);
    }

    private void pooledBuy(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level level,
            @NotNull ItemLike result,
            int minCoins,
            int maxCoins,
            int minCount,
            int maxCount
    ) {
        pooledBuy(output, tracking, level, result, minCoins, maxCoins, minCount, maxCount, 3, 3);
    }

    private void pooledCrate(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level level,
            @NotNull ItemLike crate,
            int minCoins,
            int maxCoins
    ) {
        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(level)
                        .tradeGroup(TradeGroup.EXACT_LEVEL_POOL)
                        .weight(1)
                        .maxUses(1)
                        .dwarfXp(0)
                        .priceMultiplier(0.0F)
                        .costACoins(minCoins, maxCoins)
                        .costB(JolCraftItems.SUNGLEAM_CUT.get())
                        .result(crate)
        );
    }

    private void pooledPotionBuy(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level level,
            @NotNull Holder<Potion> potion,
            int minCoins,
            int maxCoins,
            int weight
    ) {
        String potionName = potion.unwrapKey()
                .orElseThrow()
                .location()
                .getPath();

        ItemOutput potionOutput = ItemOutput.item(
                LootItem.lootTableItem(Items.POTION)
                        .apply(
                                SetComponentsFunction.setComponent(
                                        DataComponents.POTION_CONTENTS,
                                        new PotionContents(potion)
                                )
                        )
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(level)
                        .tradeGroup(TradeGroup.CUMULATIVE_POOL)
                        .weight(weight)
                        .maxUses(1)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
                        .costACoins(minCoins, maxCoins)
                        .noCostB()
                        .result(
                                potionOutput,
                                potionName + "_potion"
                        )
        );
    }
}