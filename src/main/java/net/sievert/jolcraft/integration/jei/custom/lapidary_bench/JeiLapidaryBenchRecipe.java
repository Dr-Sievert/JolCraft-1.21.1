package net.sievert.jolcraft.integration.jei.custom.lapidary_bench;

import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.integration.jei.util.JeiItemOutcome;
import net.sievert.jolcraft.world.recipe.custom.lapidary_bench.LapidaryBenchRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record JeiLapidaryBenchRecipe(
        @NotNull LapidaryBenchRecipe recipe,
        @NotNull List<ItemStack> inputExamples,
        @NotNull List<ItemStack> toolExamples,
        @NotNull List<JeiItemOutcome> outcomes
) {

    public JeiLapidaryBenchRecipe {
        inputExamples = List.copyOf(
                inputExamples
        );

        toolExamples = List.copyOf(
                toolExamples
        );

        outcomes = List.copyOf(
                outcomes
        );

        if (inputExamples.isEmpty()) {
            throw new IllegalArgumentException(
                    "Lapidary JEI recipe requires at least one input example"
            );
        }

        if (toolExamples.isEmpty()) {
            throw new IllegalArgumentException(
                    "Lapidary JEI recipe requires at least one tool example"
            );
        }

        if (outcomes.isEmpty()) {
            throw new IllegalArgumentException(
                    "Lapidary JEI recipe requires at least one output outcome"
            );
        }
    }

    public @NotNull List<ItemStack> outputExamples() {
        return outcomes.stream()
                .map(
                        JeiItemOutcome::stack
                )
                .toList();
    }
}