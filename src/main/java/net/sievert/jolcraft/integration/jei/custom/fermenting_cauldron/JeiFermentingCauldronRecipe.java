package net.sievert.jolcraft.integration.jei.custom.fermenting_cauldron;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public record JeiFermentingCauldronRecipe(
        @NotNull ResourceLocation id,
        @NotNull PreviousInput previousInput,
        @NotNull List<ItemStack> ingredientExamples,
        @NotNull Result result
) {

    public JeiFermentingCauldronRecipe {
        Objects.requireNonNull(
                id,
                "id"
        );

        Objects.requireNonNull(
                previousInput,
                "previousInput"
        );

        ingredientExamples =
                copyStacks(
                        ingredientExamples,
                        "ingredientExamples"
                );

        Objects.requireNonNull(
                result,
                "result"
        );
    }

    private static @NotNull List<ItemStack> copyStacks(
            @NotNull List<ItemStack> stacks,
            @NotNull String name
    ) {
        Objects.requireNonNull(
                stacks,
                name
        );

        List<ItemStack> copies =
                stacks.stream()
                        .map(
                                ItemStack::copy
                        )
                        .toList();

        if (copies.isEmpty()) {
            throw new IllegalArgumentException(
                    name
                            + " must contain at least one stack"
            );
        }

        return copies;
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
                    copyStacks(
                            examples,
                            "examples"
                    );
        }
    }

    public record FluidInput(
            @NotNull FluidStack fluid
    ) implements PreviousInput {

        public FluidInput {
            Objects.requireNonNull(
                    fluid,
                    "fluid"
            );

            if (fluid.isEmpty()) {
                throw new IllegalArgumentException(
                        "Fluid input must not be empty"
                );
            }

            fluid =
                    fluid.copy();
        }
    }

    public sealed interface Result
            permits ItemResult,
            FluidResult,
            EffectResult {
    }

    public record ItemResult(
            @NotNull List<ItemStack> examples
    ) implements Result {

        public ItemResult {
            examples =
                    copyStacks(
                            examples,
                            "examples"
                    );
        }
    }

    public record FluidResult(
            @NotNull FluidStack fluid
    ) implements Result {

        public FluidResult {
            Objects.requireNonNull(
                    fluid,
                    "fluid"
            );

            if (fluid.isEmpty()) {
                throw new IllegalArgumentException(
                        "Fluid result must not be empty"
                );
            }

            fluid =
                    fluid.copy();
        }
    }

    public record EffectResult(
            @NotNull MobEffectInstance effect
    ) implements Result {

        public EffectResult {
            Objects.requireNonNull(
                    effect,
                    "effect"
            );

            effect =
                    new MobEffectInstance(
                            effect
                    );
        }
    }
}