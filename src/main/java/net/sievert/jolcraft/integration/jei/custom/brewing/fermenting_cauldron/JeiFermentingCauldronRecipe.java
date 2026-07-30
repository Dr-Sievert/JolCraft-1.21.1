package net.sievert.jolcraft.integration.jei.custom.brewing.fermenting_cauldron;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.integration.jei.util.item.JeiStacks;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public record JeiFermentingCauldronRecipe(
        @NotNull ResourceLocation id,
        @NotNull PreviousInput previousInput,
        @NotNull List<ItemStack> ingredientExamples,
        @NotNull Result result,
        int brewTicks
) {

    public JeiFermentingCauldronRecipe {
        Objects.requireNonNull(
                id,
                JolCraftDictionary.ID
        );

        Objects.requireNonNull(
                previousInput,
                "previousInput"
        );

        ingredientExamples =
                JeiStacks.copyRequired(
                        ingredientExamples,
                        "ingredientExamples"
                );

        Objects.requireNonNull(
                result,
                JolCraftDictionary.RESULT
        );

        if (brewTicks < 0) {
            throw new IllegalArgumentException(
                    "Brew ticks must not be negative"
            );
        }
    }

    public sealed interface PreviousInput
            permits ItemInput,
            FluidInput {
    }

    public record ItemInput(
            @NotNull List<ItemStack> examples
    ) implements PreviousInput {

        public ItemInput {
            examples =
                    JeiStacks.copyRequired(
                            examples,
                            JolCraftStrings.plural(
                                    JolCraftDictionary.ITEM
                            )
                    );
        }
    }

    public record FluidInput(
            @NotNull FluidStack fluid
    ) implements PreviousInput {

        public FluidInput {
            Objects.requireNonNull(
                    fluid,
                    JolCraftDictionary.FLUID
            );

            if (fluid.isEmpty()) {
                throw new IllegalArgumentException(
                        "Previous input fluid must not be empty"
                );
            }

            fluid =
                    fluid.copy();
        }
    }

    public sealed interface Result
            permits ItemResult,
            FluidResult {
    }

    public record ItemResult(
            @NotNull List<ItemStack> examples
    ) implements Result {

        public ItemResult {
            examples =
                    JeiStacks.copyRequired(
                            examples,
                            JolCraftStrings.plural(
                                    JolCraftDictionary.ITEM
                            )
                    );
        }
    }

    public record FluidResult(
            @NotNull FluidStack fluid
    ) implements Result {

        public FluidResult {
            Objects.requireNonNull(
                    fluid,
                    JolCraftDictionary.FLUID
            );

            if (fluid.isEmpty()) {
                throw new IllegalArgumentException(
                        "Result fluid must not be empty"
                );
            }

            fluid =
                    fluid.copy();
        }
    }
}