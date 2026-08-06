package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import org.jetbrains.annotations.NotNull;

public record DwarfArtisanTrades(JolCraftDataProvider<RecipeOutput> parent) implements DwarfTradeSubProvider {

    private static final DwarfProfession PROFESSION =
            DwarfProfession.ARTISAN;

    public DwarfArtisanTrades(
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

        //Vanilla

        sellVanillaGem(output, tracking,
                Items.LAPIS_LAZULI,
                3,
                5,
                1,
                2,
                1
        );

        sellVanillaGem(output, tracking,
                Items.QUARTZ,
                3,
                5,
                2,
                3,
                1
        );

        sellVanillaGem(output, tracking,
                Items.PRISMARINE_SHARD,
                2,
                4,
                2,
                4,
                2
        );

        sellVanillaGem(output, tracking,
                Items.AMETHYST_SHARD,
                2,
                4,
                2,
                4,
                2
        );

        sellVanillaGem(output, tracking,
                Items.EMERALD,
                2,
                3,
                3,
                4,
                3
        );

        sellVanillaGem(output, tracking,
                Items.DIAMOND,
                1,
                2,
                4,
                6,
                5
        );

        //Custom Gems

        sellCustomGem(
                output,
                tracking,
                JolCraftItems.AEGISCORE.get()
        );

        sellCustomGem(
                output,
                tracking,
                JolCraftItems.ASHFANG.get()
        );

        sellCustomGem(
                output,
                tracking,
                JolCraftItems.DEEPMARROW.get()
        );

        sellCustomGem(
                output,
                tracking,
                JolCraftItems.EARTHBLOOD.get()
        );

        sellCustomGem(
                output,
                tracking,
                JolCraftItems.EMBERGLASS.get()
        );

        sellCustomGem(
                output,
                tracking,
                JolCraftItems.FROSTVEIN.get()
        );

        sellCustomGem(
                output,
                tracking,
                JolCraftItems.GRIMSTONE.get()
        );

        sellCustomGem(
                output,
                tracking,
                JolCraftItems.IRONHEART.get()
        );

        sellCustomGem(
                output,
                tracking,
                JolCraftItems.LUMIERE.get()
        );

        sellCustomGem(
                output,
                tracking,
                JolCraftItems.MOONSHARD.get()
        );

        sellCustomGem(
                output,
                tracking,
                JolCraftItems.RUSTAGATE.get()
        );

        sellCustomGem(
                output,
                tracking,
                JolCraftItems.SKYBURROW.get()
        );

        sellCustomGem(
                output,
                tracking,
                JolCraftItems.SUNGLEAM.get()
        );

        sellCustomGem(
                output,
                tracking,
                JolCraftItems.VERDANITE.get()
        );

        sellCustomGem(
                output,
                tracking,
                JolCraftItems.WOECRYSTAL.get()
        );

        //Extras

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(
                                DwarfMerchantData.Level.APPRENTICE
                        )
                        .costACoins(
                                3,
                                5
                        )
                        .noCostB()
                        .result(
                                JolCraftItems.DEEPSLATE_ARTISAN_HAMMER
                        )
                        .maxUses(3)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(
                                DwarfMerchantData.Level.JOURNEYMAN
                        )
                        .costACoins(
                                3,
                                5
                        )
                        .noCostB()
                        .result(
                                JolCraftItems.DEEPSLATE_CHISEL
                        )
                        .maxUses(3)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.buyLegendaryLoreTome(
                        PROFESSION,
                        DwarfMerchantData.Level.MASTER,
                        DwarfLoreKey.ANCIENT_GEMCRAFT,
                        25,
                        35,
                        80,
                        120
                )
        );
    }

    private void sellVanillaGem(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull ItemLike gem,
            int minAmount,
            int maxAmount,
            int minCoins,
            int maxCoins,
            int dwarfXp
    ) {
        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .tradeGroup(DwarfTradeRecipe.TradeGroup.CUMULATIVE_POOL)
                        .costA(
                                gem,
                                minAmount,
                                maxAmount
                        )
                        .noCostB()
                        .coinsResult(
                                minCoins,
                                maxCoins
                        )
                        .maxUses(20)
                        .dwarfXp(dwarfXp)
                        .priceMultiplier(0.05F)
        );
    }

    private void sellCustomGem(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull ItemLike gem
    ) {
        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.APPRENTICE)
                        .tradeGroup(DwarfTradeRecipe.TradeGroup.CUMULATIVE_POOL)
                        .costA(gem)
                        .noCostB()
                        .coinsResult(
                                4,
                                8
                        )
                        .maxUses(10)
                        .dwarfXp(25)
                        .priceMultiplier(0.05F)
        );
    }
}