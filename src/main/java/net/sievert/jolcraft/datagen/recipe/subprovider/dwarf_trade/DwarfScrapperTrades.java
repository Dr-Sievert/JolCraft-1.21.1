package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Rarity;
import net.sievert.jolcraft.data.JolCraftEnumExtensions;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.loot.JolCraftLootTables;
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
                .dwarfXp(3)
                .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.NOVICE)
                .tradeGroup(TradeGroup.MAIN)
                .costA(JolCraftItems.SCRAP_HEAP.get())
                .noCostB()
                .coinsResult(10)
                .maxUses(256)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.NOVICE)
                .costA(JolCraftItems.SCRAP.get(), 10, 20)
                .noCostB()
                .rewardCrateLootTableResult(
                        Rarity.COMMON,
                        JolCraftLootTables.Chests.UNCUT_GEMS
                )
                .maxUses(1)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.APPRENTICE)
                .costA(JolCraftItems.SCRAP.get(),10, 20)
                .noCostB()
                .result(JolCraftItems.DEEPSLATE_SPANNER)
                .maxUses(3)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.APPRENTICE)
                .costA(JolCraftItems.SCRAP.get(), 10, 20)
                .noCostB()
                .rewardCrateLootTableResult(
                        Rarity.UNCOMMON,
                        JolCraftLootTables.Chests.UNCUT_GEMS
                )
                .maxUses(1)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.JOURNEYMAN)
                .costA(JolCraftItems.SCRAP.get(), 10, 20)
                .noCostB()
                .rewardCrateLootTableResult(
                        Rarity.RARE,
                        JolCraftLootTables.Chests.UNCUT_GEMS
                )
                .maxUses(1)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.EXPERT)
                .costA(JolCraftItems.SCRAP.get(), 10, 20)
                .noCostB()
                .rewardCrateLootTableResult(
                        Rarity.EPIC,
                        JolCraftLootTables.Chests.UNCUT_GEMS
                )
                .maxUses(1)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.MASTER)
                .costA(JolCraftItems.SCRAP.get(), 10, 20)
                .noCostB()
                .rewardCrateLootTableResult(
                        JolCraftEnumExtensions.Rarity.LEGENDARY.getValue(),
                        JolCraftLootTables.Chests.UNCUT_GEMS
                )
                .maxUses(1)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );
    }
}