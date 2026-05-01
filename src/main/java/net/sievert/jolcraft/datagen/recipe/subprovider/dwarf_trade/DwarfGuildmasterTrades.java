package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.minecraft.world.level.ItemLike;
import net.minecraft.data.recipes.RecipeOutput;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.custom.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

public record DwarfGuildmasterTrades(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public DwarfGuildmasterTrades(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
        this.parent = parent;
    }

    @Override
    public @NotNull JolCraftDataProvider<RecipeOutput> parent() {
        return parent;
    }

    private static final DwarfProfession PROFESSION = DwarfProfession.GUILDMASTER;

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

        emitOrdered(output, tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costACoins(15)
                        .noCostB()
                        .result(JolCraftItems.REPUTATION_TABLET_0.get().asItem(), 1)
                        .maxUses(5)
                        .dwarfXp(0)
                        .priceMultiplier(0.0F)
        );

        addContractTrade(output, tracking, DwarfMerchantData.Level.NOVICE, JolCraftItems.CONTRACT_HISTORIAN.get());
        addContractTrade(output, tracking, DwarfMerchantData.Level.NOVICE, JolCraftItems.CONTRACT_MERCHANT.get());
        addContractTrade(output, tracking, DwarfMerchantData.Level.NOVICE, JolCraftItems.CONTRACT_SCRAPPER.get());

        addContractTrade(output, tracking, DwarfMerchantData.Level.APPRENTICE, JolCraftItems.CONTRACT_BREWMASTER.get());
        addContractTrade(output, tracking, DwarfMerchantData.Level.APPRENTICE, JolCraftItems.CONTRACT_GUARD.get());
        addContractTrade(output, tracking, DwarfMerchantData.Level.APPRENTICE, JolCraftItems.CONTRACT_KEEPER.get());

        addContractTrade(output, tracking, DwarfMerchantData.Level.JOURNEYMAN, JolCraftItems.CONTRACT_ARTISAN.get());
        addContractTrade(output, tracking, DwarfMerchantData.Level.JOURNEYMAN, JolCraftItems.CONTRACT_EXPLORER.get());
        addContractTrade(output, tracking, DwarfMerchantData.Level.JOURNEYMAN, JolCraftItems.CONTRACT_MINER.get());

        addContractTrade(output, tracking, DwarfMerchantData.Level.EXPERT, JolCraftItems.CONTRACT_ARCANIST.get());
        addContractTrade(output, tracking, DwarfMerchantData.Level.EXPERT, JolCraftItems.CONTRACT_ALCHEMIST.get());
        addContractTrade(output, tracking, DwarfMerchantData.Level.EXPERT, JolCraftItems.CONTRACT_PRIEST.get());

        addContractTrade(output, tracking, DwarfMerchantData.Level.MASTER, JolCraftItems.CONTRACT_BLACKSMITH.get());
        addContractTrade(output, tracking, DwarfMerchantData.Level.MASTER, JolCraftItems.CONTRACT_CHAMPION.get());
        addContractTrade(output, tracking, DwarfMerchantData.Level.MASTER, JolCraftItems.CONTRACT_SMELTER.get());
    }

    private void addContractTrade(
            @NotNull RecipeOutput output,
            JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level level,
            @NotNull ItemLike contract
    ) {

        emitOrdered(output, tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(level)
                        .costACoins(30)
                        .costB(JolCraftItems.CONTRACT_SIGNED.get().asItem(), 1)
                        .result(contract.asItem(), 1)
                        .maxUses(1)
                        .dwarfXp(0)
                        .priceMultiplier(0.05F)
        );
    }
}