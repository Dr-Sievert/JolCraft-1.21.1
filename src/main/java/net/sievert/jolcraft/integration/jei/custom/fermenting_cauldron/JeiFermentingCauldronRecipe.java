package net.sievert.jolcraft.integration.jei.custom.fermenting_cauldron;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.integration.jei.util.item.JeiStacks;
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
                    JolCraftDictionary.FLUID
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
                    JeiStacks.copyRequired(
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
                    JolCraftDictionary.FLUID
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
                    JolCraftDictionary.EFFECT
            );

            effect =
                    new MobEffectInstance(
                            effect
                    );
        }
    }
}