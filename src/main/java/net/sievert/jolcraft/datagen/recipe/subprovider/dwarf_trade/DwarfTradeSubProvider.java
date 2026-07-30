package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.data.recipes.RecipeOutput;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public interface DwarfTradeSubProvider extends RecipeSubProvider {

    default void addBountyTrades(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @Nullable DwarfProfession profession
    ) {
        for (DwarfTradeRecipeBuilder builder
                : DwarfTradeRecipeBuilder.bountyTrades(
                profession
        )) {
            emitOrdered(
                    output,
                    tracking,
                    builder
            );
        }
    }
}