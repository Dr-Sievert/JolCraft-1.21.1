package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.world.level.ItemLike;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.custom.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

public final class DwarfGuildmasterTrades implements RecipeSubProvider {

    private static final DwarfProfession PROFESSION = DwarfProfession.GUILDMASTER;

    @Override
    public @NotNull String folder() {
        return PROFESSION.professionName();
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    ) {

        executor.emitOrdered(
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

        addContractTrade(executor, DwarfMerchantData.Level.NOVICE, JolCraftItems.CONTRACT_HISTORIAN.get());
        addContractTrade(executor, DwarfMerchantData.Level.NOVICE, JolCraftItems.CONTRACT_MERCHANT.get());
        addContractTrade(executor, DwarfMerchantData.Level.NOVICE, JolCraftItems.CONTRACT_SCRAPPER.get());

        addContractTrade(executor, DwarfMerchantData.Level.APPRENTICE, JolCraftItems.CONTRACT_BREWMASTER.get());
        addContractTrade(executor, DwarfMerchantData.Level.APPRENTICE, JolCraftItems.CONTRACT_GUARD.get());
        addContractTrade(executor, DwarfMerchantData.Level.APPRENTICE, JolCraftItems.CONTRACT_KEEPER.get());

        addContractTrade(executor, DwarfMerchantData.Level.JOURNEYMAN, JolCraftItems.CONTRACT_ARTISAN.get());
        addContractTrade(executor, DwarfMerchantData.Level.JOURNEYMAN, JolCraftItems.CONTRACT_EXPLORER.get());
        addContractTrade(executor, DwarfMerchantData.Level.JOURNEYMAN, JolCraftItems.CONTRACT_MINER.get());

        addContractTrade(executor, DwarfMerchantData.Level.EXPERT, JolCraftItems.CONTRACT_ARCANIST.get());
        addContractTrade(executor, DwarfMerchantData.Level.EXPERT, JolCraftItems.CONTRACT_ALCHEMIST.get());
        addContractTrade(executor, DwarfMerchantData.Level.EXPERT, JolCraftItems.CONTRACT_PRIEST.get());

        addContractTrade(executor, DwarfMerchantData.Level.MASTER, JolCraftItems.CONTRACT_BLACKSMITH.get());
        addContractTrade(executor, DwarfMerchantData.Level.MASTER, JolCraftItems.CONTRACT_CHAMPION.get());
        addContractTrade(executor, DwarfMerchantData.Level.MASTER, JolCraftItems.CONTRACT_SMELTER.get());
    }

    private static void addContractTrade(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull DwarfMerchantData.Level level,
            @NotNull ItemLike contract
    ) {

        executor.emitOrdered(
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