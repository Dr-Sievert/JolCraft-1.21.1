package net.sievert.jolcraft.datagen.recipe.subprovider.trade;

import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

public class DwarfPriestTrades extends AbstractDwarfTrades {

    @Override
    protected @NotNull DwarfProfession profession() {
        return DwarfProfession.PRIEST;
    }

    @Override
    public void addTrades(@NotNull AbstractRecipeProvider p) {

    }
}
