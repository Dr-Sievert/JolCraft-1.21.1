package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeGroup;
import org.jetbrains.annotations.NotNull;

public record DwarfScrapperTrades(JolCraftDataProvider<RecipeOutput> parent) implements DwarfTradeSubProvider {

    private static final DwarfProfession PROFESSION =
            DwarfProfession.SCRAPPER;

    public DwarfScrapperTrades(
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

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.NOVICE)
                .tradeGroup(TradeGroup.MAIN)
                .costA(JolCraftItems.SCRAP.get())
                .noCostB()
                .coinsResult(1)
                .maxUses(256)
                .dwarfXp(5)
                .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.APPRENTICE)
                .tradeGroup(TradeGroup.MAIN)
                .costACoins(8, 15)
                .noCostB()
                .result(JolCraftItems.DEEPSLATE_SPANNER)
                .maxUses(3)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.JOURNEYMAN)
                        .tradeGroup(TradeGroup.MAIN)
                        .costA(JolCraftItems.SCRAP_HEAP)
                        .noCostB()
                        .coinsResult(4, 7)
                        .maxUses(50)
                        .dwarfXp(4)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.MASTER)
                        .tradeGroup(TradeGroup.MAIN)
                        .costACoins(1, 15)
                        .costB(JolCraftItems.SCRAP_HEAP)
                        .result(JolCraftItems.RUSTAGATE)
                        .maxUses(3)
                        .dwarfXp(0)
                        .priceMultiplier(0.05F)
        );

        pooledSalvage(output, tracking, JolCraftItems.EXPIRED_POTION.get(), 1, 3, 3, 1, 3);
        pooledSalvage(output, tracking, JolCraftItems.OLD_FABRIC.get(), 1, 3, 5, 1, 3);
        pooledSalvage(output, tracking, JolCraftItems.BROKEN_PICKAXE.get(), 1, 4, 3, 1, 2);
        pooledSalvage(output, tracking, JolCraftItems.BROKEN_AMULET.get(), 1, 4, 3, 1, 2);
        pooledSalvage(output, tracking, JolCraftItems.BROKEN_BELT.get(), 1, 4, 3, 1, 2);
        pooledSalvage(output, tracking, JolCraftItems.BROKEN_COINS.get(), 1, 3, 5, 3, 3);
        pooledSalvage(output, tracking, JolCraftItems.RUSTY_TONGS.get(), 1, 4, 3, 1, 2);
        pooledSalvage(output, tracking, JolCraftItems.INGOT_MOULD.get(), 1, 4, 3, 1, 2);
        pooledSalvage(output, tracking, JolCraftItems.DEEPSLATE_MUG.get(), 3, 5, 3, 1, 3);
        pooledSalvage(output, tracking, JolCraftItems.BROKEN_TABLET.get(), 3, 5, 3, 1, 2);
        pooledSalvage(output, tracking, JolCraftItems.BROKEN_DEEPSLATE_PICKAXE_HEAD.get(), 3, 5, 3, 1, 2);
        pooledSalvage(output, tracking, JolCraftItems.BROKEN_DEEPSLATE_GEAR.get(), 3, 5, 3, 1, 2);
        pooledSalvage(output, tracking, JolCraftItems.BROKEN_DEEPSLATE_PLATES.get(), 3, 5, 3, 1, 2);
        pooledMithrilSalvage(output, tracking, JolCraftItems.MITHRIL_SCRAP.get());
        pooledMithrilSalvage(output, tracking, JolCraftItems.BROKEN_MITHRIL_PLATE.get());
        pooledMithrilSalvage(output, tracking, JolCraftItems.BROKEN_MITHRIL_SWORD.get());
    }

    private void pooledSalvage(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull ItemLike item,
            int minGold,
            int maxGold,
            int maxUses,
            int count,
            int weight
    ) {
        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .tradeGroup(TradeGroup.GLOBAL_POOL)
                        .noMerchantLevel()
                        .weight(weight)
                        .maxUses(maxUses)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
                        .costA(item, count)
                        .noCostB()
                        .coinsResult(minGold, maxGold)
        );
    }

    private void pooledMithrilSalvage(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull ItemLike item
    ) {
        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .tradeGroup(TradeGroup.GLOBAL_POOL)
                        .noMerchantLevel()
                        .maxUses(5)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
                        .costA(item)
                        .noCostB()
                        .coinsResult(3, 10)
        );
    }
}