package net.sievert.jolcraft.data.recipe.custom.bounty;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;
import net.sievert.jolcraft.world.item.util.bounty.BountyTier;
import net.sievert.jolcraft.world.item.util.bounty.BountyType;
import org.jetbrains.annotations.NotNull;

public record BountyRecipeInput(
        ItemStack redeemStack,
        BountyType type,
        BountyTier tier
) implements RecipeInput {

    @Override
    public @NotNull ItemStack getItem(int index) {
        return (index == 0) ? redeemStack : ItemStack.EMPTY;
    }

    @Override
    public int size() {
        return 1;
    }
}