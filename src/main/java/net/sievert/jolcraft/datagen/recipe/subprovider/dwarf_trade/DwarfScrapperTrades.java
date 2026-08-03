package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.data.recipes.RecipeOutput;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.custom.crate.RewardCrateType;
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
                .merchantLevel(DwarfMerchantData.Level.APPRENTICE)
                .tradeGroup(TradeGroup.MAIN)
                .costA(JolCraftItems.SCRAP_HEAP.get())
                .noCostB()
                .coinsResult(10)
                .maxUses(256)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.JOURNEYMAN)
                .tradeGroup(TradeGroup.MAIN)
                .costA(JolCraftItems.SCRAP.get(), 8, 12)
                .noCostB()
                .result(JolCraftItems.DEEPSLATE_SPANNER)
                .maxUses(3)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        addCrateTrade(
                output,
                tracking,
                DwarfMerchantData.Level.NOVICE,
                RewardCrateType.SUPPLY_CRATE,
                5,
                8

        );

        addCrateTrade(
                output,
                tracking,
                DwarfMerchantData.Level.NOVICE,
                RewardCrateType.FISHING_LOOT,
                5,
                8
        );

        addCrateTrade(
                output,
                tracking,
                DwarfMerchantData.Level.NOVICE,
                RewardCrateType.FARMING_SUPPLIES,
                5,
                8

        );

        addCrateTrade(
                output,
                tracking,
                DwarfMerchantData.Level.APPRENTICE,
                RewardCrateType.MONSTER_SLAYER_LOOT,
                8,
                12

        );

        addCrateTrade(
                output,
                tracking,
                DwarfMerchantData.Level.APPRENTICE,
                RewardCrateType.ALCHEMY_SUPPLIES,
                8,
                12
        );

        addCrateTrade(
                output,
                tracking,
                DwarfMerchantData.Level.JOURNEYMAN,
                RewardCrateType.DWARVEN_FORTRESS_EXCAVATION,
                12,
                16

        );

        addCrateTrade(
                output,
                tracking,
                DwarfMerchantData.Level.JOURNEYMAN,
                RewardCrateType.MINING_CACHE,
                12,
                16
        );

        addCrateTrade(
                output,
                tracking,
                DwarfMerchantData.Level.EXPERT,
                RewardCrateType.BLACKSMITH_SUPPLIES,
                16,
                22

        );

        addCrateTrade(
                output,
                tracking,
                DwarfMerchantData.Level.EXPERT,
                RewardCrateType.ARTISAN_SUPPLIES,
                16,
                22

        );

        addCrateTrade(
                output,
                tracking,
                DwarfMerchantData.Level.MASTER,
                RewardCrateType.VAULT_LOOT,
                22,
                30

        );
    }

    private void addCrateTrade(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level level,
            @NotNull RewardCrateType crateType,
            int minimumCost,
            int maximumCost
    ) {
        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(level)
                .tradeGroup(TradeGroup.CUMULATIVE_POOL)
                .costA(JolCraftItems.SCRAP.get(), minimumCost, maximumCost)
                .noCostB()
                .rewardCrateResult(crateType)
                .maxUses(1)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );
    }
}