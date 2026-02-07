package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.sievert.jolcraft.datagen.recipe.subprovider.trade.*;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfTradeRecipesSubProvider implements AbstractRecipeProvider.RecipeSubProvider {

    @Override
    public void addRecipes(@NotNull AbstractRecipeProvider p) {
        new DwarfBaseTrades().addTrades(p);
        new DwarfAlchemistTrades().addTrades(p);
        new DwarfArcanistTrades().addTrades(p);
        new DwarfArtisanTrades().addTrades(p);
        new DwarfBrewmasterTrades().addTrades(p);
        new DwarfExplorerTrades().addTrades(p);
        new DwarfGuardTrades().addTrades(p);
        new DwarfGuildmasterTrades().addTrades(p);
        new DwarfHistorianTrades().addTrades(p);
        new DwarfKeeperTrades().addTrades(p);
        new DwarfMerchantTrades().addTrades(p);
        new DwarfMinerTrades().addTrades(p);
        new DwarfPriestTrades().addTrades(p);
        new DwarfScrapperTrades().addTrades(p);
    }
}
