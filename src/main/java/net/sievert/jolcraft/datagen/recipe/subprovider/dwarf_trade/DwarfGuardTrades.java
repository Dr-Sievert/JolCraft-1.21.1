package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.build.custom.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

public final class DwarfGuardTrades implements RecipeSubProvider {

    private static final DwarfProfession PROFESSION = DwarfProfession.GUARD;

    @Override
    public @NotNull String folder() {
        return PROFESSION.getId();
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
                        .merchantLevel(DwarfMerchantData.Level.MASTER)
                        .costA(JolCraftItems.AEGISCORE.get().asItem(), 1)
                        .result(JolCraftItems.GOLD_COIN.get().asItem(), 30)
                        .maxUses(1)
                        .dwarfXp(0)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.MASTER)
                        .costA(JolCraftItems.GOLD_COIN.get().asItem(), 30)
                        .costB(JolCraftItems.AEGISCORE.get().asItem(), 1)
                        .result(JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get().asItem(), 1)
                        .maxUses(1)
                        .dwarfXp(0)
                        .priceMultiplier(0.05F)
        );
    }
}