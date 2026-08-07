package net.sievert.jolcraft.integration.jei.custom.vanilla.brewing;

import mezz.jei.api.recipe.vanilla.IJeiBrewingRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record JolCraftJeiBrewingRecipe(
        List<ItemStack> ingredients,
        List<ItemStack> potionInputs,
        ItemStack potionOutput,
        ResourceLocation uid,
        int brewingSteps
) implements IJeiBrewingRecipe {

    @Override
    public @NotNull List<ItemStack> getIngredients() {
        return ingredients;
    }

    @Override
    public @NotNull List<ItemStack> getPotionInputs() {
        return potionInputs;
    }

    @Override
    public @NotNull ItemStack getPotionOutput() {
        return potionOutput;
    }

    @Override
    public ResourceLocation getUid() {
        return uid;
    }

    @Override
    public int getBrewingSteps() {
        return brewingSteps;
    }
}