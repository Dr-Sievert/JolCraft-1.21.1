package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.world.item.Items;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeLookups;
import net.sievert.jolcraft.datagen.recipe.builder.custom.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SameParameterValue")
public final class DwarfKeeperTrades implements RecipeSubProvider {

    private static final DwarfProfession PROFESSION = DwarfProfession.KEEPER;

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
                        .costACoins(1, 2)
                        .noCostB()
                        .result(JolCraftItems.BARLEY_SEEDS.get().asItem(), 1, 3)
                        .maxUses(10)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.APPRENTICE)
                        .costA(JolCraftItems.BARLEY.get().asItem(), 15, 22)
                        .coinsResult(1, 2)
                        .maxUses(10)
                        .dwarfXp(25)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.JOURNEYMAN)
                        .costA(JolCraftItems.MUFFHORN_FUR.get().asItem(), 1)
                        .coinsResult(2, 4)
                        .maxUses(15)
                        .dwarfXp(5)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.JOURNEYMAN)
                        .costA(JolCraftItems.MUFFHORN_MILK_BUCKET.get().asItem(), 1)
                        .coinsResult(5)
                        .maxUses(10)
                        .dwarfXp(30)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.EXPERT)
                        .costA(JolCraftItems.DEEPSLATE_BULBS.get().asItem(), 1, 2)
                        .coinsResult(3, 5)
                        .maxUses(10)
                        .dwarfXp(30)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.MASTER)
                        .costACoins(2, 5)
                        .noCostB()
                        .result(Items.BONE_MEAL, 3, 5)
                        .maxUses(5)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.MASTER)
                        .costACoins(5, 9)
                        .noCostB()
                        .result(JolCraftItems.DEEPSLATE_BULBS.get().asItem(), 1, 5)
                        .maxUses(3)
                        .dwarfXp(0)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(seedTrade(DwarfMerchantData.Level.MASTER, JolCraftItems.ASGARNIAN_SEEDS.get().asItem()));
        executor.emitOrdered(seedTrade(DwarfMerchantData.Level.MASTER, JolCraftItems.DUSKHOLD_SEEDS.get().asItem()));
        executor.emitOrdered(seedTrade(DwarfMerchantData.Level.MASTER, JolCraftItems.KRANDONIAN_SEEDS.get().asItem()));
        executor.emitOrdered(seedTrade(DwarfMerchantData.Level.MASTER, JolCraftItems.YANILLIAN_SEEDS.get().asItem()));
    }

    private static @NotNull DwarfTradeRecipeBuilder seedTrade(
            @NotNull DwarfMerchantData.Level level,
            @NotNull net.minecraft.world.level.ItemLike seed
    ) {
        return DwarfTradeRecipeBuilder.create()
                .profession(PROFESSION)
                .merchantLevel(level)
                .costACoins(5)
                .noCostB()
                .result(seed.asItem(), 1)
                .maxUses(3)
                .dwarfXp(0)
                .priceMultiplier(0.05F);
    }
}