package net.sievert.jolcraft.integration.jei.custom.hand_interaction;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.integration.jei.util.JeiItemOutcome;
import net.sievert.jolcraft.world.recipe.custom.hand.HandInteractionRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public record JeiHandInteractionRecipe(
        @NotNull HandInteractionRecipe recipe,
        @NotNull List<ItemStack> ingredientAExamples,
        @NotNull List<ItemStack> ingredientBExamples,
        @NotNull Result result
) {

    public JeiHandInteractionRecipe {
        Objects.requireNonNull(
                recipe,
                "recipe"
        );

        ingredientAExamples =
                copyStacks(
                        ingredientAExamples,
                        "ingredientAExamples"
                );

        ingredientBExamples =
                copyStacks(
                        ingredientBExamples,
                        "ingredientBExamples"
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

    public sealed interface Result
            permits ItemResult,
            EntityResult,
            EffectResult {
    }

    public record ItemResult(
            @NotNull JeiItemOutcome outcome
    ) implements Result {

        public ItemResult {
            Objects.requireNonNull(
                    outcome,
                    "outcome"
            );
        }

        public @NotNull ItemStack example() {
            return outcome.stack().copy();
        }
    }

    public record EntityResult(
            @NotNull EntityType<?> entityType,
            @NotNull ItemStack spawnEgg,
            int minCount,
            int maxCount
    ) implements Result {

        public EntityResult {
            Objects.requireNonNull(
                    entityType,
                    "entityType"
            );

            Objects.requireNonNull(
                    spawnEgg,
                    "spawnEgg"
            );

            if (spawnEgg.isEmpty()) {
                throw new IllegalArgumentException(
                        "Entity result requires a spawn egg"
                );
            }

            spawnEgg =
                    spawnEgg.copy();

            spawnEgg.setCount(
                    1
            );

            if (minCount < 0) {
                throw new IllegalArgumentException(
                        "minCount must be at least 0"
                );
            }

            if (maxCount < minCount) {
                throw new IllegalArgumentException(
                        "maxCount must be at least minCount"
                );
            }
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