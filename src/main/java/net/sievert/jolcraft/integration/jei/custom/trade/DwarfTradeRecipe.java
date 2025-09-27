package net.sievert.jolcraft.integration.jei.custom.trade;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.registries.DeferredItem;
import javax.annotation.Nullable;

/**
 * Represents a single Dwarf trade entry for JEI, including support for input/output count ranges.
 */
public record DwarfTradeRecipe(String profession, int level, ItemStack inputA, ItemStack inputB, ItemStack output,
                               DeferredItem<Item> spawnEgg, int inputAMin, int inputAMax, int inputBMin, int inputBMax,
                               int outputMin, int outputMax) {
    public DwarfTradeRecipe(
            String profession,
            int level,
            ItemStack inputA,
            @Nullable ItemStack inputB,
            ItemStack output,
            DeferredItem<Item> spawnEgg,
            int inputAMin, int inputAMax,
            int inputBMin, int inputBMax,
            int outputMin, int outputMax
    ) {
        this.profession = profession;
        this.level = level;
        this.inputA = inputA;
        this.inputB = inputB;
        this.output = output;
        this.spawnEgg = spawnEgg;
        this.inputAMin = inputAMin;
        this.inputAMax = inputAMax;
        this.inputBMin = inputBMin;
        this.inputBMax = inputBMax;
        this.outputMin = outputMin;
        this.outputMax = outputMax;
    }

    @Override
    public @Nullable ItemStack inputB() {
        return inputB;
    }
}
