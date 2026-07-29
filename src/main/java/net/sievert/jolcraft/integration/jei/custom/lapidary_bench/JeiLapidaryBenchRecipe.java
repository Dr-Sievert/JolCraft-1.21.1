package net.sievert.jolcraft.integration.jei.custom.lapidary_bench;

import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiItemOutcome;
import net.sievert.jolcraft.integration.jei.util.item.JeiStacks;
import net.sievert.jolcraft.world.recipe.custom.lapidary_bench.LapidaryBenchRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public record JeiLapidaryBenchRecipe(
        @NotNull LapidaryBenchRecipe recipe,
        @NotNull List<ItemStack> inputExamples,
        @NotNull List<ItemStack> toolExamples,
        @NotNull JeiItemOutcome outcome
) {
    public JeiLapidaryBenchRecipe {
        Objects.requireNonNull(recipe, JolCraftDictionary.RECIPE);
        Objects.requireNonNull(outcome, "outcome");
        inputExamples = JeiStacks.copyRequired(
                inputExamples,
                "inputExamples"
        );
        toolExamples = JeiStacks.copyRequired(
                toolExamples,
                "toolExamples"
        );
    }

    public @NotNull ItemStack outputExample() {
        return outcome.stack().copy();
    }
}
