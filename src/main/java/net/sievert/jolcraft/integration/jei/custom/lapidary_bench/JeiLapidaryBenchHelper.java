package net.sievert.jolcraft.integration.jei.custom.lapidary_bench;

import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.integration.jei.util.recipe.ItemInputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.recipe.ItemOutputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiItemOutcome;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeAccess;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.lapidary_bench.LapidaryBenchRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class JeiLapidaryBenchHelper {

    private JeiLapidaryBenchHelper() {
    }

    public static @NotNull List<JeiLapidaryBenchRecipe> getRecipes() {
        return JeiRecipeAccess.translateSorted(
                JolCraftRecipes
                        .LAPIDARY_BENCH_TYPE
                        .get(),
                holder -> translate(
                        holder.value()
                )
        );
    }

    private static @NotNull List<JeiLapidaryBenchRecipe> translate(
            @NotNull LapidaryBenchRecipe recipe
    ) {
        List<ItemStack> inputs =
                ItemInputJeiTranslator.translate(
                        recipe.input()
                );

        List<ItemStack> tools =
                ItemInputJeiTranslator.translate(
                        recipe.tool()
                );

        List<JeiLapidaryBenchRecipe> result =
                new ArrayList<>();

        for (JeiItemOutcome outcome :
                ItemOutputJeiTranslator.translate(
                        recipe.result()
                )) {
            result.add(
                    new JeiLapidaryBenchRecipe(
                            recipe,
                            inputs,
                            tools,
                            outcome
                    )
            );
        }

        return List.copyOf(
                result
        );
    }
}
