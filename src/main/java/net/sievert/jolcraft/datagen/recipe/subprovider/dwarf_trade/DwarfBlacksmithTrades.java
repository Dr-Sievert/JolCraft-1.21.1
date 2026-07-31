package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.data.recipes.RecipeOutput;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import org.jetbrains.annotations.NotNull;

public record DwarfBlacksmithTrades(JolCraftDataProvider<RecipeOutput> parent) implements DwarfTradeSubProvider {

    private static final DwarfProfession PROFESSION = DwarfProfession.BLACKSMITH;

    public DwarfBlacksmithTrades(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
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
        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.buyLegendaryLoreTome(
                PROFESSION,
                DwarfMerchantData.Level.MASTER,
                DwarfLoreKey.MITHRIL_FORGE_TECHNIQUE,
                25,
                35,
                80,
                120
        ));
    }
}