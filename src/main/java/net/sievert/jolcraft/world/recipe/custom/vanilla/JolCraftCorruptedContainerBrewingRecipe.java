package net.sievert.jolcraft.world.recipe.custom.vanilla;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import org.jetbrains.annotations.NotNull;

public final class JolCraftCorruptedContainerBrewingRecipe
        implements IBrewingRecipe {

    @Override
    public boolean isInput(
            @NotNull ItemStack stack
    ) {
        return stack.has(
                JolCraftDataComponents.CORRUPTION_DATA.get()
        ) && (stack.is(Items.POTION)
                || stack.is(Items.SPLASH_POTION));
    }

    @Override
    public boolean isIngredient(
            @NotNull ItemStack stack
    ) {
        return stack.is(Items.GUNPOWDER)
                || stack.is(Items.DRAGON_BREATH);
    }

    @Override
    public @NotNull ItemStack getOutput(
            @NotNull ItemStack inputStack,
            @NotNull ItemStack ingredientStack
    ) {
        if (!isInput(inputStack)
                || !isIngredient(ingredientStack)) {
            return ItemStack.EMPTY;
        }

        Item outputItem;

        if (inputStack.is(Items.POTION)
                && ingredientStack.is(Items.GUNPOWDER)) {
            outputItem = Items.SPLASH_POTION;
        } else if (inputStack.is(Items.SPLASH_POTION)
                && ingredientStack.is(Items.DRAGON_BREATH)) {
            outputItem = Items.LINGERING_POTION;
        } else {
            return ItemStack.EMPTY;
        }

        ItemStack output =
                new ItemStack(
                        outputItem
                );

        output.applyComponents(
                inputStack.getComponentsPatch()
        );

        return output;
    }
}
