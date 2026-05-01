package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.minecraft.data.recipes.RecipeOutput;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.custom.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

public record DwarfMinerTrades(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public DwarfMinerTrades(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
        this.parent = parent;
    }

    @Override
    public @NotNull JolCraftDataProvider<RecipeOutput> parent() {
        return parent;
    }

    private static final DwarfProfession PROFESSION = DwarfProfession.MINER;

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
        java.util.List<DwarfTradeRecipeBuilder> bountyTrades = new java.util.ArrayList<>();
        DwarfTradeRecipeBuilder.addBountyTrades(bountyTrades, PROFESSION);
        for (var trade : bountyTrades) {
            emitOrdered(output, tracking, trade);
        }
    }
}