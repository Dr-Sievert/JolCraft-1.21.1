package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.world.item.Items;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.custom.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

public final class DwarfBrewmasterTrades implements RecipeSubProvider {

    private static final DwarfProfession PROFESSION = DwarfProfession.BREWMASTER;

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
                        .costA(JolCraftItems.GLASS_MUG.get().asItem(), 1, 2)
                        .coinsResult(1, 3)
                        .maxUses(5)
                        .dwarfXp(2)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costACoins(1, 2)
                        .noCostB()
                        .result(Items.SUGAR, 1, 2)
                        .maxUses(10)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.APPRENTICE)
                        .costACoins(7, 12)
                        .noCostB()
                        .result(Items.CAULDRON, 1)
                        .maxUses(9)
                        .dwarfXp(10)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.APPRENTICE)
                        .costA(JolCraftItems.BARLEY_MALT.get().asItem(), 12, 22)
                        .coinsResult(1, 3)
                        .maxUses(5)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.JOURNEYMAN)
                        .costA(JolCraftItems.ASGARNIAN_HOPS.get().asItem(), 10, 20)
                        .coinsResult(1, 3)
                        .maxUses(5)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.JOURNEYMAN)
                        .costA(JolCraftItems.DUSKHOLD_HOPS.get().asItem(), 10, 20)
                        .coinsResult(1, 3)
                        .maxUses(5)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.JOURNEYMAN)
                        .costA(JolCraftItems.KRANDONIAN_HOPS.get().asItem(), 10, 20)
                        .coinsResult(1, 3)
                        .maxUses(5)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.JOURNEYMAN)
                        .costA(JolCraftItems.YANILLIAN_HOPS.get().asItem(), 10, 20)
                        .coinsResult(1, 3)
                        .maxUses(5)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.EXPERT)
                        .costA(JolCraftItems.DWARVEN_BREW.get().asItem(), 1, 5)
                        .coinsResult(6)
                        .maxUses(5)
                        .dwarfXp(3)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.EXPERT)
                        .costACoins(1, 2)
                        .noCostB()
                        .result(JolCraftItems.YEAST.get().asItem(), 3, 5)
                        .maxUses(5)
                        .dwarfXp(10)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.buyLegendaryLoreTome(
                        DwarfMerchantData.Level.MASTER,
                        PROFESSION,
                        DwarfLoreKey.FORGOTTEN_BREW_FORMULAS,
                        IntRange.fixed(20),
                        IntRange.fixed(30)
                )
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.MASTER)
                        .costACoins(20, 40)
                        .costB(JolCraftItems.EMBERGLASS_CUT.get().asItem(), 2)
                        .result(JolCraftBlocks.HEARTH.get().asItem(), 1)
                        .maxUses(1)
                        .dwarfXp(0)
                        .priceMultiplier(0.0F)
        );
    }
}