package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeIds;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.JolCraftSubDataProvider;
import net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade.DwarfAlchemistTrades;
import net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade.DwarfArcanistTrades;
import net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade.DwarfArtisanTrades;
import net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade.DwarfBaseTrades;
import net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade.DwarfBrewmasterTrades;
import net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade.DwarfExplorerTrades;
import net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade.DwarfGuardTrades;
import net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade.DwarfGuildmasterTrades;
import net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade.DwarfHistorianTrades;
import net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade.DwarfKeeperTrades;
import net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade.DwarfMerchantTrades;
import net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade.DwarfMinerTrades;
import net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade.DwarfPriestTrades;
import net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade.DwarfScrapperTrades;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record DwarfTradeRecipesSubProvider(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public DwarfTradeRecipesSubProvider(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
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
        return JolCraftRecipeIds.DWARF_TRADE;
    }

    @Override
    public @NotNull List<? extends JolCraftSubDataProvider<RecipeOutput>> subProviders() {
        return List.of(
                new DwarfBaseTrades(this),
                new DwarfAlchemistTrades(this),
                new DwarfArcanistTrades(this),
                new DwarfArtisanTrades(this),
                new DwarfBrewmasterTrades(this),
                new DwarfExplorerTrades(this),
                new DwarfGuardTrades(this),
                new DwarfGuildmasterTrades(this),
                new DwarfHistorianTrades(this),
                new DwarfKeeperTrades(this),
                new DwarfMerchantTrades(this),
                new DwarfMinerTrades(this),
                new DwarfPriestTrades(this),
                new DwarfScrapperTrades(this)
        );
    }
}
