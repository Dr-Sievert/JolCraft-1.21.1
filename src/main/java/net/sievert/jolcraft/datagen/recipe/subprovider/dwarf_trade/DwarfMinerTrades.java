package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.data.recipes.RecipeOutput;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.custom.crate.RewardCrateType;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import org.jetbrains.annotations.NotNull;

public record DwarfMinerTrades(JolCraftDataProvider<RecipeOutput> parent) implements DwarfTradeSubProvider {

    private static final DwarfProfession PROFESSION = DwarfProfession.MINER;

    public DwarfMinerTrades(
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
        addBountyTrades(
                output,
                tracking,
                PROFESSION
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
                                6,
                                12
                        )
                        .noCostB()
                        .result(
                                JolCraftItems.DEEPSLATE_SHOVEL.get(),
                                1
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
                                10,
                                14
                        )
                        .noCostB()
                        .result(
                                JolCraftItems.DEEPSLATE_PICKAXE
                        )
                        .maxUses(3)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.EXPERT)
                        .costACoins(
                                18,
                                36
                        )
                        .noCostB()
                        .rewardCrateResult(RewardCrateType.MINING_CACHE)
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
                        DwarfLoreKey.MINING_RHYTHM,
                        25,
                        35,
                        80,
                        120
                )
        );
    }
}