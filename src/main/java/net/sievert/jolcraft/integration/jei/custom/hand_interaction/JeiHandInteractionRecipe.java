package net.sievert.jolcraft.integration.jei.custom.hand_interaction;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiItemOutcome;
import net.sievert.jolcraft.integration.jei.util.item.JeiStacks;
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
                JolCraftDictionary.RECIPE
        );

        ingredientAExamples =
                JeiStacks.copyRequired(
                        ingredientAExamples,
                        "ingredientAExamples"
                );

        ingredientBExamples =
                JeiStacks.copyRequired(
                        ingredientBExamples,
                        "ingredientBExamples"
                );

        Objects.requireNonNull(
                result,
                JolCraftDictionary.RESULT
        );
    }

    public sealed interface Result
            permits ItemResult,
            EntityResult,
            EffectResult {
    }

    public record ItemResult(
            @NotNull JeiItemOutcome outcome,
            @NotNull List<ItemStack> examples
    ) implements Result {

        public ItemResult(
                @NotNull JeiItemOutcome outcome
        ) {
            this(
                    outcome,
                    List.of(
                            outcome.stack()
                    )
            );
        }

        public ItemResult {
            Objects.requireNonNull(
                    outcome,
                    "outcome"
            );

            examples =
                    JeiStacks.copyRequired(
                            examples,
                            "examples"
                    );
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
                    JolCraftDictionary.EFFECT
            );

            effect =
                    new MobEffectInstance(
                            effect
                    );
        }
    }
}