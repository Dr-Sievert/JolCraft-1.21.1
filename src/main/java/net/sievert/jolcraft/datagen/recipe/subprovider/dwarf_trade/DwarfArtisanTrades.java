package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeLookups;
import net.sievert.jolcraft.datagen.recipe.builder.custom.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

public final class DwarfArtisanTrades implements RecipeSubProvider {

    private static final DwarfProfession PROFESSION = DwarfProfession.ARTISAN;

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

        sellGem(executor, JolCraftItems.AEGISCORE.get());
        sellGem(executor, JolCraftItems.ASHFANG.get());
        sellGem(executor, JolCraftItems.DEEPMARROW.get());
        sellGem(executor, JolCraftItems.EARTHBLOOD.get());
        sellGem(executor, JolCraftItems.EMBERGLASS.get());
        sellGem(executor, JolCraftItems.FROSTVEIN.get());
        sellGem(executor, JolCraftItems.GRIMSTONE.get());
        sellGem(executor, JolCraftItems.IRONHEART.get());
        sellGem(executor, JolCraftItems.LUMIERE.get());
        sellGem(executor, JolCraftItems.MOONSHARD.get());
        sellGem(executor, JolCraftItems.RUSTAGATE.get());
        sellGem(executor, JolCraftItems.SKYBURROW.get());
        sellGem(executor, JolCraftItems.SUNGLEAM.get());
        sellGem(executor, JolCraftItems.VERDANITE.get());
        sellGem(executor, JolCraftItems.WOECRYSTAL.get());

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costA(Items.DIAMOND, 1)
                        .coinsResult(5, 7)
                        .maxUses(5)
                        .dwarfXp(5)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costA(Items.EMERALD, 1)
                        .coinsResult(3, 5)
                        .maxUses(5)
                        .dwarfXp(5)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costA(Items.AMETHYST_SHARD, 2, 4)
                        .coinsResult(2, 4)
                        .maxUses(5)
                        .dwarfXp(5)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costA(Items.LAPIS_LAZULI, 3, 5)
                        .coinsResult(1, 2)
                        .maxUses(5)
                        .dwarfXp(5)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costA(Items.PRISMARINE_SHARD, 3, 5)
                        .coinsResult(2, 3)
                        .maxUses(5)
                        .dwarfXp(5)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costA(Items.QUARTZ, 3, 5)
                        .coinsResult(2, 5)
                        .maxUses(5)
                        .dwarfXp(5)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.APPRENTICE)
                        .costACoins(10, 20)
                        .noCostB()
                        .result(JolCraftBlocks.LAPIDARY_BENCH.get().asItem(), 1)
                        .maxUses(3)
                        .dwarfXp(10)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.JOURNEYMAN)
                        .costACoins(2, 4)
                        .noCostB()
                        .result(JolCraftItems.DEEPSLATE_ARTISAN_HAMMER.get().asItem(), 1)
                        .maxUses(3)
                        .dwarfXp(10)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.EXPERT)
                        .costACoins(2, 4)
                        .noCostB()
                        .result(JolCraftItems.DEEPSLATE_CHISEL.get().asItem(), 1)
                        .maxUses(3)
                        .dwarfXp(10)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.buyLegendaryLoreTome(
                        DwarfMerchantData.Level.MASTER,
                        PROFESSION,
                        DwarfLoreKey.ANCIENT_GEMCRAFT,
                        IntRange.fixed(20),
                        IntRange.fixed(30)
                )
        );
    }

    private static void sellGem(@NotNull RecipeEmissionExecutor executor, @NotNull ItemLike gem) {
        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costA(gem.asItem(), 1)
                        .coinsResult(8, 15)
                        .maxUses(5)
                        .dwarfXp(10)
                        .priceMultiplier(0.05F)
        );
    }
}