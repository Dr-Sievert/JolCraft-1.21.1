package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.crafting.DataComponentIngredient;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.block.fluid.JolCraftFluids;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.custom.crate.RewardCrateType;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.item.registry.JolCraftBrewingItems;
import net.sievert.jolcraft.world.recipe.base.input.custom.BrewAgeIngredient;
import org.jetbrains.annotations.NotNull;

public record DwarfBrewmasterTrades(JolCraftDataProvider<RecipeOutput> parent) implements DwarfTradeSubProvider {

    private static final DwarfProfession PROFESSION = DwarfProfession.BREWMASTER;

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
        sellYeast(
                output,
                tracking,
                1.0F,
                1,
                5
        );

        sellYeast(
                output,
                tracking,
                1.5F,
                2,
                7
        );

        sellYeast(
                output,
                tracking,
                2.0F,
                3,
                10
        );

        sellYeast(
                output,
                tracking,
                2.5F,
                4,
                15
        );

        sellYeast(
                output,
                tracking,
                3.0F,
                5,
                25
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create()
                .profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.NOVICE)
                .costA(
                        DwarfTradeRecipeBuilder.cost(
                                DataComponentIngredient.of(
                                        true,
                                        JolCraftBrewingItems.createTanninStack(
                                                JolCraftItems.TANNIN,
                                                JolCraftFluids.TANNIN.get(),
                                                DwarvenBrewAge.MATURED
                                        )
                                ),
                                1,
                                1
                        )
                )
                .costAName(JolCraftItems.TANNIN.getId().getPath())
                .noCostB()
                .coinsResult(2)
                .maxUses(10)
                .dwarfXp(10)
                .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create()
                .profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.NOVICE)
                .costA(
                        DwarfTradeRecipeBuilder.cost(
                                DataComponentIngredient.of(
                                        true,
                                        JolCraftBrewingItems.createTanninStack(
                                                JolCraftItems.TANNIN,
                                                JolCraftFluids.REFINED_TANNIN.get(),
                                                DwarvenBrewAge.VINTAGE
                                        )
                                ),
                                1,
                                1
                        )
                )
                .costAName(JolCraftItems.TANNIN.getId().getPath())
                .fileNameSuffix(JolCraftDictionary.REFINED)
                .noCostB()
                .coinsResult(3)
                .maxUses(10)
                .dwarfXp(15)
                .priceMultiplier(0.05F)
        );

        sellDwarvenBrew(output, tracking,
                DwarvenBrewAge.FRESH,
                3,
                12
                );

        sellDwarvenBrew(output, tracking,
                DwarvenBrewAge.AGED,
                4,
                15
        );

        sellDwarvenBrew(output, tracking,
                DwarvenBrewAge.MATURED,
                5,
                20
        );

        sellDwarvenBrew(output, tracking,
                DwarvenBrewAge.VINTAGE,
                6,
                25
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.NOVICE)
                .costACoins(1)
                .noCostB()
                .result(Items.GLASS_BOTTLE, 3, 5)
                .maxUses(5)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.APPRENTICE)
                .costACoins(1)
                .noCostB()
                .result(JolCraftItems.GLASS_MUG, 2, 4)
                .maxUses(5)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.JOURNEYMAN)
                .costACoins(1)
                .noCostB()
                .result(Items.SUGAR, 1, 2)
                .maxUses(20)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.EXPERT)
                .costACoins(8, 14)
                .noCostB()
                .result(Items.CAULDRON)
                .maxUses(5)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.EXPERT)
                        .costACoins(
                                8,
                                16
                        )
                        .noCostB()
                        .rewardCrateResult(RewardCrateType.SUPPLY_CRATE)
                        .maxUses(1)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.buyLegendaryLoreTome(
                        PROFESSION,
                        DwarfMerchantData.Level.MASTER,
                        DwarfLoreKey.FORGOTTEN_BREW_FORMULAS,
                        25,
                        35,
                        80,
                        120
                )
        );
    }

    private void sellYeast(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            float brewingSpeed,
            int coins,
            int dwarfXp
    ) {
        String speedSuffix =
                Float.toString(brewingSpeed).replace('.', '_') + "x";

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costA(
                                DwarfTradeRecipeBuilder.cost(
                                        DataComponentIngredient.of(
                                                true,
                                                JolCraftBrewingItems.createYeastStack(
                                                        JolCraftItems.YEAST,
                                                        brewingSpeed
                                                )
                                        ),
                                        1,
                                        1
                                )
                        )
                        .costAName(
                                JolCraftItems.YEAST.getId().getPath()
                        )
                        .fileNameSuffix(
                                speedSuffix
                        )
                        .noCostB()
                        .coinsResult(coins)
                        .maxUses(10)
                        .dwarfXp(dwarfXp)
                        .priceMultiplier(0.05F)
        );
    }

    private void sellDwarvenBrew(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            DwarvenBrewAge age,
            int coins,
            int dwarfXp
    ) {
        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.NOVICE)
                .costA(
                        DwarfTradeRecipeBuilder.cost(
                                new BrewAgeIngredient(age).toVanilla(),
                                1,
                                1
                        )
                )
                .costAName(JolCraftItems.DWARVEN_BREW.getId().getPath())
                .fileNameSuffix(age.getId())
                .noCostB()
                .coinsResult(coins)
                .maxUses(10)
                .dwarfXp(dwarfXp)
                .priceMultiplier(0.05F)
        );
    }
}