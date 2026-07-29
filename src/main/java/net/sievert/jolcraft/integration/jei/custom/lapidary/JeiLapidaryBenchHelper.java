package net.sievert.jolcraft.integration.jei.custom.lapidary;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.sievert.jolcraft.integration.jei.util.ItemInputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.ItemOutputJeiTranslator;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.lapidary_bench.LapidaryBenchRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class JeiLapidaryBenchHelper {

    private JeiLapidaryBenchHelper() {
    }

    public static @NotNull List<JeiLapidaryBenchRecipe> getRecipes() {
        Minecraft minecraft =
                Minecraft.getInstance();

        if (minecraft.level == null) {
            return List.of();
        }

        return minecraft.level
                .getRecipeManager()
                .getAllRecipesFor(
                        JolCraftRecipes
                                .LAPIDARY_BENCH_TYPE
                                .get()
                )
                .stream()
                .map(
                        RecipeHolder::value
                )
                .map(
                        JeiLapidaryBenchHelper::translate
                )
                .toList();
    }

    private static @NotNull JeiLapidaryBenchRecipe translate(
            @NotNull LapidaryBenchRecipe recipe
    ) {
        return new JeiLapidaryBenchRecipe(
                recipe,
                ItemInputJeiTranslator.translate(
                        recipe.input()
                ),
                ItemInputJeiTranslator.translate(
                        recipe.tool()
                ),
                ItemOutputJeiTranslator.translate(
                        recipe.result()
                )
        );
    }
}