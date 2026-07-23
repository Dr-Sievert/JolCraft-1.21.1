package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import org.jetbrains.annotations.NotNull;

public record DwarfBrewmasterTrades(
        JolCraftDataProvider<RecipeOutput> parent
) implements RecipeSubProvider {

    private static final DwarfProfession PROFESSION =
            DwarfProfession.BREWMASTER;

    public DwarfBrewmasterTrades(
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
        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(
                                DwarfMerchantData.Level.NOVICE
                        )
                        .costA(
                                JolCraftItems.GLASS_MUG.get(),
                                1,
                                2
                        )
                        .noCostB()
                        .coinsResult(
                                1,
                                3
                        )
                        .maxUses(5)
                        .dwarfXp(2)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(
                                DwarfMerchantData.Level.NOVICE
                        )
                        .costACoins(
                                1,
                                2
                        )
                        .noCostB()
                        .result(
                                Items.SUGAR,
                                1,
                                2
                        )
                        .maxUses(10)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(
                                DwarfMerchantData.Level.APPRENTICE
                        )
                        .costACoins(
                                7,
                                12
                        )
                        .noCostB()
                        .result(
                                Items.CAULDRON,
                                1
                        )
                        .maxUses(9)
                        .dwarfXp(10)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(
                                DwarfMerchantData.Level.APPRENTICE
                        )
                        .costA(
                                JolCraftItems.BARLEY_MALT.get(),
                                12,
                                22
                        )
                        .noCostB()
                        .coinsResult(
                                1,
                                3
                        )
                        .maxUses(5)
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
                        .costA(
                                JolCraftItems.ASGARNIAN_HOPS.get(),
                                10,
                                20
                        )
                        .noCostB()
                        .coinsResult(
                                1,
                                3
                        )
                        .maxUses(5)
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
                        .costA(
                                JolCraftItems.DUSKHOLD_HOPS.get(),
                                10,
                                20
                        )
                        .noCostB()
                        .coinsResult(
                                1,
                                3
                        )
                        .maxUses(5)
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
                        .costA(
                                JolCraftItems.KRANDONIAN_HOPS.get(),
                                10,
                                20
                        )
                        .noCostB()
                        .coinsResult(
                                1,
                                3
                        )
                        .maxUses(5)
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
                        .costA(
                                JolCraftItems.YANILLIAN_HOPS.get(),
                                10,
                                20
                        )
                        .noCostB()
                        .coinsResult(
                                1,
                                3
                        )
                        .maxUses(5)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(
                                DwarfMerchantData.Level.EXPERT
                        )
                        .costA(
                                JolCraftItems.DWARVEN_BREW.get(),
                                1,
                                5
                        )
                        .noCostB()
                        .coinsResult(6)
                        .maxUses(5)
                        .dwarfXp(3)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(
                                DwarfMerchantData.Level.EXPERT
                        )
                        .costACoins(
                                1,
                                2
                        )
                        .noCostB()
                        .result(
                                JolCraftItems.YEAST.get(),
                                3,
                                5
                        )
                        .maxUses(5)
                        .dwarfXp(10)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.buyLegendaryLoreTome(
                        DwarfMerchantData.Level.MASTER,
                        PROFESSION,
                        DwarfLoreKey.FORGOTTEN_BREW_FORMULAS,
                        20,
                        20,
                        30,
                        30
                )
        );
    }
}