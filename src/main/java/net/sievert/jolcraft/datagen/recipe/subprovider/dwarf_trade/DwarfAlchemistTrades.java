package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
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

public final class DwarfAlchemistTrades implements RecipeSubProvider {

    private static final DwarfProfession PROFESSION = DwarfProfession.ALCHEMIST;

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
                        .costACoins(4, 7)
                        .noCostB()
                        .result(JolCraftItems.DEEPSLATE_MORTAR_ITEM.get(), 1)
                        .maxUses(6)
                        .dwarfXp(0)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costACoins(1, 4)
                        .noCostB()
                        .result(JolCraftItems.DEEPSLATE_PESTLE.get(), 1)
                        .maxUses(6)
                        .dwarfXp(0)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.buyLegendaryLoreTome(
                        DwarfMerchantData.Level.MASTER,
                        PROFESSION,
                        DwarfLoreKey.ALCHEMY_RECIPES,
                        IntRange.fixed(20),
                        IntRange.fixed(30)
                )
        );
    }
}