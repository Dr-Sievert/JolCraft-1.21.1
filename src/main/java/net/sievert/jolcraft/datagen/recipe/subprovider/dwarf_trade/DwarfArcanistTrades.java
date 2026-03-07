package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

public final class DwarfArcanistTrades implements RecipeSubProvider {

    private static final DwarfProfession PROFESSION = DwarfProfession.ARCANIST;

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

    }
}