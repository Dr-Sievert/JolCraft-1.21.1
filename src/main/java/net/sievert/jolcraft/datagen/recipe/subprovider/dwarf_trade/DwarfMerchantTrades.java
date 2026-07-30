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

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.TORCH, 1, 3, 8, 16, 10
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.COAL, 2, 4, 4, 8, 8
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.FLINT, 1, 3, 3, 6, 7
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.COPPER_INGOT, 3, 5, 2, 4, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Blocks.COBBLED_DEEPSLATE, 2, 4, 16, 32, 6
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.IRON_NUGGET, 2, 4, 9, 18, 7
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.STRING, 2, 4, 3, 6, 6
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.PAPER, 1, 3, 4, 8, 8
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                JolCraftItems.GLASS_MUG, 2, 4, 1, 1, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.BAKED_POTATO, 2, 4, 4, 8, 7
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.WHEAT_SEEDS, 1, 2, 4, 8, 8
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.CANDLE, 2, 4, 2, 4, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.POPPY, 1, 2, 3, 6, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.DANDELION, 1, 2, 3, 6, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.GLASS_BOTTLE, 1, 3, 3, 6, 7
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.EGG, 2, 3, 4, 8, 6
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.FLOWER_POT, 2, 4, 1, 2, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.CHAIN, 4, 7, 1, 2, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.GLOW_BERRIES, 2, 4, 3, 6, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                JolCraftItems.QUILL_EMPTY, 2, 4, 1, 1, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.WHEAT, 3, 5, 6, 12, 7
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE,
                Items.FEATHER, 3, 5, 4, 8, 7
        );

        // ---------------------------------------------------------------------
        // Apprentice
        // ---------------------------------------------------------------------

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.IRON_INGOT, 6, 9, 2, 3, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.WHITE_WOOL, 3, 5, 2, 4, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Blocks.DEEPSLATE_TILES, 4, 7, 16, 32, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                JolCraftItems.BARLEY, 3, 5, 6, 12, 7
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.BREAD, 3, 5, 3, 6, 8
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.COOKED_MUTTON, 5, 8, 3, 6, 6
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.CARROT, 2, 4, 4, 8, 7
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.POTATO, 2, 4, 4, 8, 7
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.BEETROOT_SEEDS, 2, 3, 4, 8, 6
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.SUGAR_CANE, 2, 4, 4, 8, 6
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.SWEET_BERRIES, 2, 4, 4, 8, 6
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.MELON_SEEDS, 2, 3, 3, 6, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.PUMPKIN_SEEDS, 2, 3, 3, 6, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.CACTUS, 2, 4, 3, 6, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.COCOA_BEANS, 3, 5, 3, 6, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                JolCraftItems.DEEPSLATE_MUG, 4, 7, 1, 1, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.BUCKET, 7, 10, 1, 1, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.FISHING_ROD, 6, 10, 1, 1, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.FLINT_AND_STEEL, 5, 8, 1, 1, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.BRUSH, 5, 8, 1, 1, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                Items.LANTERN, 4, 7, 1, 2, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                JolCraftItems.GEODE_SMALL, 5, 9, 1, 1, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                JolCraftBlocks.DUSKCAP, 5, 9, 1, 1, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE,
                JolCraftBlocks.FESTERLING, 5, 9, 1, 1, 3
        );




        // ---------------------------------------------------------------------
        // Journeyman
        // ---------------------------------------------------------------------

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.GOLD_INGOT, 8, 12, 2, 3, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.LAPIS_LAZULI, 4, 7, 8, 16, 6
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.REDSTONE, 4, 7, 8, 16, 6
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.SPIDER_EYE, 3, 5, 3, 6, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.GUNPOWDER, 5, 8, 4, 8, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.BONE, 3, 5, 6, 12, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.INK_SAC, 3, 5, 3, 6, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.CONTRACT_BLANK, 8, 12, 1, 1, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.MUFFHORN_FUR, 4, 7, 2, 4, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.MUFFHORN_MILK_BUCKET, 7, 11, 1, 1, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.LEATHER, 4, 7, 3, 6, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.PARCHMENT, 3, 6, 2, 4, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.LOCKPICK, 8, 13, 1, 2, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.BOOK, 4, 7, 2, 4, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.COOKED_BEEF, 6, 9, 4, 8, 6
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.YEAST, 6, 10, 2, 4, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.SPYGLASS, 9, 14, 1, 1, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.LEAD, 6, 10, 1, 2, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.SLIME_BALL, 7, 11, 2, 4, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.GLOWSTONE, 8, 12, 2, 4, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.QUILL_FULL, 6, 10, 1, 1, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.GEODE_MEDIUM, 10, 16, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.QUARTZ, 5, 8, 6, 12, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                Items.NETHER_WART, 7, 11, 3, 6, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.INVERIX, 12, 18, 1, 1, 2
        );


        pooledPotionBuy(
                output,
                tracking,
                DwarfMerchantData.Level.JOURNEYMAN,
                Potions.STRONG_HEALING,
                14,
                20,
                2
        );


        pooledPotionBuy(
                output,
                tracking,
                DwarfMerchantData.Level.JOURNEYMAN,
                Potions.SWIFTNESS,
                14,
                20,
                2
        );


        // ---------------------------------------------------------------------
        // Expert
        // ---------------------------------------------------------------------

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.GOLDEN_APPLE, 16, 24, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.GHAST_TEAR, 14, 22, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.PHANTOM_MEMBRANE, 10, 16, 2, 4, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.ITEM_FRAME, 5, 8, 2, 4, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.PAINTING, 5, 8, 1, 2, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.CAULDRON, 8, 12, 1, 1, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.ENDER_PEARL, 10, 16, 2, 4, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.BLAZE_POWDER, 8, 13, 3, 6, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.EMERALD, 8, 12, 2, 4, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.AMETHYST_SHARD, 6, 10, 4, 8, 5
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.MITHRIL_SCRAP, 18, 30, 1, 2, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Blocks.BOOKSHELF, 7, 11, 1, 2, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.ASGARNIAN_HOPS, 8, 12, 3, 6, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.DUSKHOLD_HOPS, 8, 12, 3, 6, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.KRANDONIAN_HOPS, 8, 12, 3, 6, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.YANILLIAN_HOPS, 8, 12, 3, 6, 4
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.STRONGBOX_ITEM, 18, 28, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.DEEPSLATE_PLATE, 12, 20, 2, 4, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.SADDLE, 14, 22, 1, 1, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.EXPERIENCE_BOTTLE, 10, 16, 3, 6, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                Items.NAME_TAG, 12, 18, 1, 1, 3
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.AEGISCORE_DUST, 4, 7, 1, 2, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.ASHFANG_DUST, 4, 7, 1, 2, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.DEEPMARROW_DUST, 4, 7, 1, 2, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.EARTHBLOOD_DUST, 4, 7, 1, 2, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.EMBERGLASS_DUST, 4, 7, 1, 2, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.FROSTVEIN_DUST, 4, 7, 1, 2, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.GRIMSTONE_DUST, 4, 7, 1, 2, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.IRONHEART_DUST, 4, 7, 1, 2, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.LUMIERE_DUST, 4, 7, 1, 2, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.MOONSHARD_DUST, 4, 7, 1, 2, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.SKYBURROW_DUST, 4, 7, 1, 2, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.SUNGLEAM_DUST, 4, 7, 1, 2, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.VERDANITE_DUST, 4, 7, 1, 2, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.WOECRYSTAL_DUST, 4, 7, 1, 2, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT,
                JolCraftItems.UNIDENTIFIED_DWARVEN_TOME, 10, 16, 1, 1, 2
        );

        pooledPotionBuy(
                output,
                tracking,
                DwarfMerchantData.Level.EXPERT,
                JolCraftPotions.CORROSION,
                14,
                20,
                2
        );

        pooledPotionBuy(
                output,
                tracking,
                DwarfMerchantData.Level.EXPERT,
                JolCraftPotions.DWARVEN_HASTE,
                14,
                20,
                2
        );

        pooledPotionBuy(
                output,
                tracking,
                DwarfMerchantData.Level.EXPERT,
                JolCraftPotions.LOCKPICKING,
                14,
                20,
                2
        );

        // ---------------------------------------------------------------------
        // Master
        // ---------------------------------------------------------------------

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.GEODE_LARGE, 20, 32, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                Items.DIAMOND, 24, 38, 1, 3, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.LEGENDARY_PAGE, 80, 140, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.IMPURE_MITHRIL, 36, 60, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                Items.ECHO_SHARD, 28, 46, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                Items.NETHERITE_SCRAP, 48, 76, 1, 1, 1
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.AEGISCORE, 10, 16, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.ASHFANG, 10, 16, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.DEEPMARROW, 10, 16, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.EARTHBLOOD, 10, 16, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.EMBERGLASS, 10, 16, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.FROSTVEIN, 10, 16, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.GRIMSTONE, 10, 16, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.IRONHEART, 10, 16, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.LUMIERE, 10, 16, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.MOONSHARD, 10, 16, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.SKYBURROW, 10, 16, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.SUNGLEAM, 10, 16, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.VERDANITE, 10, 16, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.WOECRYSTAL, 10, 16, 1, 1, 2
        );

        pooledBuy(output, tracking, DwarfMerchantData.Level.MASTER,
                JolCraftItems.UNIDENTIFIED_ANCIENT_DWARVEN_TOME, 10, 16, 1, 1, 2
        );


        pooledCrate(
                output,
                tracking,
                DwarfMerchantData.Level.MASTER,
                JolCraftItems.RESTOCK_CRATE.get(),
                15,
                30
        );

        pooledCrate(
                output,
                tracking,
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
                        .maxUses(3)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
                        .costACoins(minCoins, maxCoins)
                        .noCostB()
                        .result(result, minCount, maxCount)
        );
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
                        .maxUses(3)
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