package net.sievert.jolcraft.integration.jei.custom.mortar;

import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.integration.jei.util.item.JeiStacks;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiItemOutcome;
import net.sievert.jolcraft.world.recipe.custom.mortar.MortarRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public record JeiMortarRecipe(
        @NotNull MortarRecipe recipe,
        @NotNull List<List<ItemStack>> inputExamples,
        @NotNull List<ItemStack> pestleExamples,
        @NotNull JeiItemOutcome outcome
) {

    public JeiMortarRecipe {
        Objects.requireNonNull(
                recipe,
                JolCraftDictionary.RECIPE
        );

        Objects.requireNonNull(
                inputExamples,
                "inputExamples"
        );

        Objects.requireNonNull(
                outcome,
                "outcome"
        );

        if (inputExamples.isEmpty()
                || inputExamples.size() > 3) {
            throw new IllegalArgumentException(
                    "inputExamples must contain one to three inputs"
            );
        }

        inputExamples = inputExamples.stream()
                .map(examples ->
                        JeiStacks.copyRequired(
                                examples,
                                "inputExamples"
                        )
                )
                .toList();

        pestleExamples = JeiStacks.copyRequired(
                pestleExamples,
                "pestleExamples"
        );
    }

    public @NotNull ItemStack outputExample() {
        return outcome.stack().copy();
    }
}