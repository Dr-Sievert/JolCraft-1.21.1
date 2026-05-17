package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.param.custom.quantity.IntRange;
import net.minecraft.data.recipes.RecipeOutput;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.custom.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

public record DwarfArtisanTrades(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public DwarfArtisanTrades(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
        this.parent = parent;
    }

    @Override
    public @NotNull JolCraftDataProvider<RecipeOutput> parent() {
        return parent;
    }

    private static final DwarfProfession PROFESSION = DwarfProfession.ARTISAN;

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

        sellGem(output, tracking, JolCraftItems.AEGISCORE.get());
        sellGem(output, tracking, JolCraftItems.ASHFANG.get());
        sellGem(output, tracking, JolCraftItems.DEEPMARROW.get());
        sellGem(output, tracking, JolCraftItems.EARTHBLOOD.get());
        sellGem(output, tracking, JolCraftItems.EMBERGLASS.get());
        sellGem(output, tracking, JolCraftItems.FROSTVEIN.get());
        sellGem(output, tracking, JolCraftItems.GRIMSTONE.get());
        sellGem(output, tracking, JolCraftItems.IRONHEART.get());
        sellGem(output, tracking, JolCraftItems.LUMIERE.get());
        sellGem(output, tracking, JolCraftItems.MOONSHARD.get());
        sellGem(output, tracking, JolCraftItems.RUSTAGATE.get());
        sellGem(output, tracking, JolCraftItems.SKYBURROW.get());
        sellGem(output, tracking, JolCraftItems.SUNGLEAM.get());
        sellGem(output, tracking, JolCraftItems.VERDANITE.get());
        sellGem(output, tracking, JolCraftItems.WOECRYSTAL.get());

        emitOrdered(output, tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costA(Items.DIAMOND, 1)
                        .coinsResult(5, 7)
                        .maxUses(5)
                        .dwarfXp(5)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costA(Items.EMERALD, 1)
                        .coinsResult(3, 5)
                        .maxUses(5)
                        .dwarfXp(5)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costA(Items.AMETHYST_SHARD, 2, 4)
                        .coinsResult(2, 4)
                        .maxUses(5)
                        .dwarfXp(5)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costA(Items.LAPIS_LAZULI, 3, 5)
                        .coinsResult(1, 2)
                        .maxUses(5)
                        .dwarfXp(5)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costA(Items.PRISMARINE_SHARD, 3, 5)
                        .coinsResult(2, 3)
                        .maxUses(5)
                        .dwarfXp(5)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costA(Items.QUARTZ, 3, 5)
                        .coinsResult(2, 5)
                        .maxUses(5)
                        .dwarfXp(5)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking,
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

        emitOrdered(output, tracking,
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

        emitOrdered(output, tracking,
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

        emitOrdered(output, tracking,
                DwarfTradeRecipeBuilder.buyLegendaryLoreTome(
                        DwarfMerchantData.Level.MASTER,
                        PROFESSION,
                        DwarfLoreKey.ANCIENT_GEMCRAFT,
                        IntRange.fixed(20),
                        IntRange.fixed(30)
                )
        );
    }

    private void sellGem(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull ItemLike gem
    ) {
        emitOrdered(output, tracking,
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