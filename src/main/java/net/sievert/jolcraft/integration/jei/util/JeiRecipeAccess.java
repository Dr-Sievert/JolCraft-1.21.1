package net.sievert.jolcraft.integration.jei.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.item.crafting.RecipeType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class JeiRecipeAccess {

    private JeiRecipeAccess() {
    }

    public static <
            I extends RecipeInput,
            T extends Recipe<I>
            > @NotNull List<RecipeHolder<T>> getSorted(
            @NotNull RecipeType<T> recipeType
    ) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) {
            return List.of();
        }

        List<RecipeHolder<T>> recipes = new ArrayList<>(
                level.getRecipeManager().getAllRecipesFor(
                        recipeType
                )
        );

        recipes.sort(
                Comparator.comparing(
                        RecipeHolder::id
                )
        );

        return List.copyOf(
                recipes
        );
    }
}
