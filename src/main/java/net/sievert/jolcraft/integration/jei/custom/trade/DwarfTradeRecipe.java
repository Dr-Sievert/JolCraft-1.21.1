package net.sievert.jolcraft.integration.jei.custom.trade;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.world.entity.custom.util.dwarf.profession.DwarfProfession;

import javax.annotation.Nullable;

/**
 * Represents a single Dwarf trade entry for JEI, including support for input/output count ranges.
 */
public record DwarfTradeRecipe(
        DwarfProfession profession,
        int level,
        ItemStack inputA,
        @Nullable ItemStack inputB,
        ItemStack output,
        DeferredItem<Item> spawnEgg,
        int inputAMin, int inputAMax,
        int inputBMin, int inputBMax,
        int outputMin, int outputMax
) {
    @Override
    public @Nullable ItemStack inputB() {
        return inputB;
    }

    public DwarfProfession getProfession() {
        return profession;
    }

}
