package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.data.recipes.RecipeOutput;
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

public record DwarfAlchemistTrades(
        JolCraftDataProvider<RecipeOutput> parent
) implements RecipeSubProvider {

    private static final DwarfProfession PROFESSION =
            DwarfProfession.ALCHEMIST;

    public DwarfAlchemistTrades(
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
                        .costACoins(
                                4,
                                7
                        )
                        .noCostB()
                        .result(
                                JolCraftItems.DEEPSLATE_MORTAR_ITEM.get(),
                                1
                        )
                        .maxUses(6)
                        .dwarfXp(0)
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
                                4
                        )
                        .noCostB()
                        .result(
                                JolCraftItems.DEEPSLATE_PESTLE.get(),
                                1
                        )
                        .maxUses(6)
                        .dwarfXp(0)
                        .priceMultiplier(0.05F)
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.buyLegendaryLoreTome(
                        DwarfMerchantData.Level.MASTER,
                        PROFESSION,
                        DwarfLoreKey.ALCHEMY_RECIPES,
                        20,
                        20,
                        30,
                        30
                )
        );
    }
}