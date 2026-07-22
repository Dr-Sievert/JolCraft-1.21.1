package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeGroup;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SameParameterValue")
public record DwarfMerchantTrades(
        JolCraftDataProvider<RecipeOutput> parent
) implements RecipeSubProvider {

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
        List<DwarfTradeRecipeBuilder> bountyTrades =
                new ArrayList<>();

        DwarfTradeRecipeBuilder.addBountyTrades(
                bountyTrades,
                PROFESSION
        );

        for (DwarfTradeRecipeBuilder trade : bountyTrades) {
            emitOrdered(
                    output,
                    tracking,
                    trade
            );
        }

        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE, Items.TORCH, 1, 2, 12);
        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE, Items.COAL, 1, 2, 5);
        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE, Items.FLINT, 1, 2, 5);
        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE, Items.COPPER_INGOT, 1, 2, 2);
        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE, Items.COBBLED_DEEPSLATE, 1, 2, 12);
        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE, Items.IRON_NUGGET, 1, 2, 12);
        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE, Items.BRICK, 1, 2, 4);
        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE, Items.STRING, 1, 2, 3);
        pooledBuy(output, tracking, DwarfMerchantData.Level.NOVICE, JolCraftItems.DEEPSLATE_MUG.get(), 1, 2, 3);

        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE, Items.IRON_INGOT, 2, 3, 2);
        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE, Items.LAPIS_LAZULI, 1, 2, 6);
        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE, Items.REDSTONE, 1, 2, 6);
        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE, Items.FEATHER, 1, 2, 3);
        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE, Items.LEATHER, 1, 2, 2);
        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE, Items.WHITE_WOOL, 1, 2, 2);
        pooledBuy(output, tracking, DwarfMerchantData.Level.APPRENTICE, JolCraftItems.PARCHMENT.get(), 1, 2, 3);

        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN, Items.GOLD_INGOT, 5, 7, 2);
        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN, Items.EMERALD, 2, 4, 2);
        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN, Items.AMETHYST_SHARD, 1, 2, 2);
        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN, Items.BLAZE_POWDER, 1, 2, 1);
        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN, Items.SPIDER_EYE, 1, 2, 1);
        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN, Items.GUNPOWDER, 1, 2, 2);
        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN, Items.BONE, 1, 2, 3);
        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN, Items.INK_SAC, 1, 2, 1);
        pooledBuy(output, tracking, DwarfMerchantData.Level.JOURNEYMAN, JolCraftItems.CONTRACT_BLANK.get(), 1, 2, 1);

        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT, Items.GOLDEN_APPLE, 4, 6, 1);
        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT, Items.BOOK, 1, 2, 1);
        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT, Items.CAULDRON, 10, 14, 1);
        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT, Items.ITEM_FRAME, 1, 2, 1);
        pooledBuy(output, tracking, DwarfMerchantData.Level.EXPERT, Items.ENDER_PEARL, 2, 4, 1);

        pooledCrate(
                output,
                tracking,
                DwarfMerchantData.Level.MASTER,
                JolCraftItems.RESTOCK_CRATE.get()
        );

        pooledCrate(
                output,
                tracking,
                DwarfMerchantData.Level.MASTER,
                JolCraftItems.REROLL_CRATE.get()
        );
    }

    private void pooledBuy(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level level,
            @NotNull ItemLike result,
            int minCoins,
            int maxCoins,
            int count
    ) {
        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(level)
                        .tradeGroup(
                                TradeGroup.CUMULATIVE_POOL
                        )
                        .weight(1)
                        .maxUses(3)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
                        .costACoins(
                                minCoins,
                                maxCoins
                        )
                        .noCostB()
                        .result(
                                result,
                                count
                        )
        );
    }

    private void pooledCrate(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level level,
            @NotNull ItemLike crate
    ) {
        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(level)
                        .tradeGroup(
                                TradeGroup.EXACT_LEVEL_POOL
                        )
                        .weight(1)
                        .maxUses(3)
                        .dwarfXp(0)
                        .priceMultiplier(0.0F)
                        .costACoins(
                                5,
                                15
                        )
                        .costB(
                                JolCraftItems.SUNGLEAM_CUT.get(),
                                1
                        )
                        .result(
                                crate,
                                1
                        )
        );
    }
}