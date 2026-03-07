package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeIds;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade.*;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfTradeRecipesSubProvider implements RecipeSubProvider {

    private static final List<RecipeSubProvider> SUBS = List.of(
            new DwarfBaseTrades(),
            new DwarfAlchemistTrades(),
            new DwarfArcanistTrades(),
            new DwarfArtisanTrades(),
            new DwarfBrewmasterTrades(),
            new DwarfExplorerTrades(),
            new DwarfGuardTrades(),
            new DwarfGuildmasterTrades(),
            new DwarfHistorianTrades(),
            new DwarfKeeperTrades(),
            new DwarfMerchantTrades(),
            new DwarfMinerTrades(),
            new DwarfPriestTrades(),
            new DwarfScrapperTrades()
    );

    @Override
    public @NotNull String folder() {
        return JolCraftRecipeIds.DWARF_TRADE;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    ) {
        for (RecipeSubProvider sub : SUBS) {
            sub.register(executor, output, items);
        }
    }
}