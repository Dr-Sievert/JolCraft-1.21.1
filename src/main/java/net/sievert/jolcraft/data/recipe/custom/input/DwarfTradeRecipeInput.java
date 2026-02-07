package net.sievert.jolcraft.data.recipe.custom.input;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import org.jetbrains.annotations.NotNull;

public record DwarfTradeRecipeInput(DwarfProfession profession, int merchantLevel) implements RecipeInput {

    @Override
    public @NotNull ItemStack getItem(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 0;
    }
}