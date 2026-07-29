package net.sievert.jolcraft.integration.jei.custom.lapidary_bench;

import net.minecraft.world.item.crafting.RecipeHolder;
import net.sievert.jolcraft.integration.jei.util.ItemInputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.ItemOutputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.JeiRecipeAccess;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.lapidary_bench.LapidaryBenchRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class JeiLapidaryBenchHelper {
    private JeiLapidaryBenchHelper() {}

    public static @NotNull List<JeiLapidaryBenchRecipe> getRecipes() {
        List<JeiLapidaryBenchRecipe> result = new ArrayList<>();
        for (RecipeHolder<LapidaryBenchRecipe> holder :
                JeiRecipeAccess.getSorted(JolCraftRecipes.LAPIDARY_BENCH_TYPE.get())) {
            LapidaryBenchRecipe recipe = holder.value();
            var inputs = ItemInputJeiTranslator.translate(recipe.input());
            var tools = ItemInputJeiTranslator.translate(recipe.tool());
            for (var outcome : ItemOutputJeiTranslator.translate(recipe.result())) {
                result.add(new JeiLapidaryBenchRecipe(recipe, inputs, tools, outcome));
            }
        }
        return List.copyOf(result);
    }
}
