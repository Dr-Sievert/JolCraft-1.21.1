package net.sievert.jolcraft.integration.jei.custom.lapidary_bench;

import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.integration.jei.util.JeiItemOutcome;
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
        Objects.requireNonNull(recipe, "recipe");
        Objects.requireNonNull(outcome, "outcome");
        inputExamples = inputExamples.stream().map(ItemStack::copy).toList();
        toolExamples = toolExamples.stream().map(ItemStack::copy).toList();
        if (inputExamples.isEmpty() || toolExamples.isEmpty()) {
            throw new IllegalArgumentException("Lapidary JEI recipe requires input and tool examples");
        }
    }

    public @NotNull ItemStack outputExample() {
        return outcome.stack().copy();
    }
}
