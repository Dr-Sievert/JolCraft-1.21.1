package net.sievert.jolcraft.data.recipe.custom.dwarf_trade;

import net.sievert.jolcraft.data.recipe.util.EmptyRecipeInput;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;

public final class DwarfTradeRecipeInput extends EmptyRecipeInput {

    private final DwarfProfession profession;
    private final int merchantLevel;

    public DwarfTradeRecipeInput(DwarfProfession profession, int merchantLevel) {
        super();
        this.profession = profession;
        this.merchantLevel = merchantLevel;
    }

    public DwarfProfession profession() {
        return profession;
    }

    public int merchantLevel() {
        return merchantLevel;
    }
}