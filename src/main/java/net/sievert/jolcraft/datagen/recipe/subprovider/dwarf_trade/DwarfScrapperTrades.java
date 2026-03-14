package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeGroup;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeLookups;
import net.sievert.jolcraft.datagen.recipe.builder.custom.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

public final class DwarfScrapperTrades implements RecipeSubProvider {

    private static final DwarfProfession PROFESSION = DwarfProfession.SCRAPPER;

    @Override
    public @NotNull String folder() {
        return PROFESSION.professionName();
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull RecipeLookups lookups
    ) {
        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .tradeGroup(TradeGroup.MAIN)
                        .costACoins(8, 15)
                        .noCostB()
                        .result(JolCraftItems.COPPER_SPANNER.get().asItem(), 1)
                        .maxUses(3)
                        .dwarfXp(10)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.APPRENTICE)
                        .tradeGroup(TradeGroup.MAIN)
                        .costA(JolCraftItems.SCRAP.get().asItem(), 1)
                        .noCostB()
                        .coinsResult(1)
                        .maxUses(256)
                        .dwarfXp(5)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.JOURNEYMAN)
                        .tradeGroup(TradeGroup.MAIN)
                        .costACoins(24, 32)
                        .noCostB()
                        .result(JolCraftItems.IRON_SPANNER.get().asItem(), 1)
                        .maxUses(3)
                        .dwarfXp(40)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.EXPERT)
                        .tradeGroup(TradeGroup.MAIN)
                        .costA(JolCraftItems.SCRAP_HEAP.get().asItem(), 1)
                        .noCostB()
                        .coinsResult(4, 7)
                        .maxUses(50)
                        .dwarfXp(4)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.MASTER)
                        .tradeGroup(TradeGroup.MAIN)
                        .costACoins(1, 15)
                        .costB(JolCraftItems.SCRAP_HEAP.get().asItem(), 1)
                        .result(JolCraftItems.RUSTAGATE.get().asItem(), 1)
                        .maxUses(3)
                        .dwarfXp(0)
                        .priceMultiplier(0.05F)
        );

        pooledSalvage(executor, JolCraftItems.EXPIRED_POTION.get(), 1, 3, 1);
        pooledSalvage(executor, JolCraftItems.OLD_FABRIC.get(), 1, 3, 1);
        pooledSalvage(executor, JolCraftItems.BROKEN_PICKAXE.get(), 1, 4, 1);
        pooledSalvage(executor, JolCraftItems.BROKEN_AMULET.get(), 1, 4, 1);
        pooledSalvage(executor, JolCraftItems.BROKEN_BELT.get(), 1, 4, 1);
        pooledSalvage(executor, JolCraftItems.BROKEN_COINS.get(), 1, 4, 1);
        pooledSalvage(executor, JolCraftItems.RUSTY_TONGS.get(), 1, 4, 1);
        pooledSalvage(executor, JolCraftItems.INGOT_MOULD.get(), 1, 4, 1);

        pooledSalvage(executor, JolCraftItems.DEEPSLATE_MUG.get(), 3, 5, 3);
        pooledSalvage(executor, JolCraftItems.BROKEN_TABLET.get(), 3, 5, 3);

        pooledSalvage(executor, JolCraftItems.BROKEN_DEEPSLATE_PICKAXE_HEAD.get(), 3, 5, 3);
        pooledSalvage(executor, JolCraftItems.BROKEN_DEEPSLATE_GEAR.get(), 3, 5, 3);
        pooledSalvage(executor, JolCraftItems.BROKEN_DEEPSLATE_PLATES.get(), 3, 5, 3);

        pooledSalvage(executor, JolCraftItems.MITHRIL_SCRAP.get(), 5, 10, 5);
        pooledSalvage(executor, JolCraftItems.BROKEN_MITHRIL_PLATE.get(), 5, 10, 5);
        pooledSalvage(executor, JolCraftItems.BROKEN_MITHRIL_SWORD.get(), 5, 10, 5);
    }

    private static void pooledSalvage(
            RecipeEmissionExecutor executor,
            ItemLike item,
            int minGold,
            int maxGold,
            int dwarfXp
    ) {
        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .tradeGroup(TradeGroup.GLOBAL_POOL)
                        .noMerchantLevel()
                        .weight(1)
                        .maxUses(5)
                        .dwarfXp(dwarfXp)
                        .priceMultiplier(0.05F)
                        .costA(item.asItem(), 1)
                        .noCostB()
                        .coinsResult(minGold, maxGold)
        );
    }
}