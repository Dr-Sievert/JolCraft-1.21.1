package net.sievert.jolcraft.world.recipe.custom.vanilla;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.brewing.IBrewingRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

public record JolCraftEssenceBrewingRecipe(
        Holder<Potion> input,
        Ingredient ingredient,
        Holder<Potion> output,
        int color
) implements IBrewingRecipe {

    @Override
    public boolean isInput(@NotNull ItemStack stack) {
        PotionContents contents = stack.get(DataComponents.POTION_CONTENTS);

        return contents != null
                && contents.potion().filter(input::equals).isPresent();
    }

    @Override
    public boolean isIngredient(@NotNull ItemStack stack) {
        return ingredient.test(stack);
    }

    @Override
    public @NotNull ItemStack getOutput(
            @NotNull ItemStack inputStack,
            @NotNull ItemStack ingredientStack
    ) {
        if (!isInput(inputStack) || !isIngredient(ingredientStack)) {
            return ItemStack.EMPTY;
        }

        ItemStack result = new ItemStack(inputStack.getItem());
        result.set(
                DataComponents.POTION_CONTENTS,
                new PotionContents(
                        Optional.of(output),
                        Optional.of(color),
                        List.of()
                )
        );
        return result;
    }
}
