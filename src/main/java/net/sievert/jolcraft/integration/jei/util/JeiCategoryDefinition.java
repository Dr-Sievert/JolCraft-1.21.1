package net.sievert.jolcraft.integration.jei.util;

import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public record JeiCategoryDefinition<R>(
        @NotNull Function<IGuiHelper, IRecipeCategory<R>> categoryFactory,
        @NotNull RecipeType<R> recipeType,
        @NotNull Supplier<List<R>> recipeSupplier,
        @NotNull List<Supplier<ItemStack>> catalysts
) {

    public @NotNull IRecipeCategory<R> createCategory(
            @NotNull IGuiHelper guiHelper
    ) {
        return categoryFactory.apply(
                guiHelper
        );
    }

    public void registerRecipes(
            @NotNull IRecipeRegistration registration
    ) {
        List<R> recipes =
                recipeSupplier.get();

        if (!recipes.isEmpty()) {
            registration.addRecipes(
                    recipeType,
                    recipes
            );
        }
    }

    public void registerCatalysts(
            @NotNull IRecipeCatalystRegistration registration
    ) {
        for (Supplier<ItemStack> catalyst : catalysts) {
            registration.addRecipeCatalyst(
                    catalyst.get(),
                    recipeType
            );
        }
    }
}
