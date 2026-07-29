package net.sievert.jolcraft.integration.jei.custom.hand_interaction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.sievert.jolcraft.integration.jei.util.ItemInputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.ItemOutputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.JeiItemOutcome;
import net.sievert.jolcraft.integration.jei.util.JeiNumberRangeTranslator;
import net.sievert.jolcraft.integration.jei.util.JeiNumberRangeTranslator.NumberRange;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.output.RecipeOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.EffectOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.EntityOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.custom.hand.HandInteractionRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class JeiHandInteractionHelper {

    private JeiHandInteractionHelper() {
    }

    public static @NotNull List<JeiHandInteractionRecipe>
    getAllHandInteractionRecipes() {
        ClientLevel clientLevel =
                Minecraft.getInstance().level;

        if (clientLevel == null) {
            return List.of();
        }

        List<RecipeHolder<HandInteractionRecipe>> recipes =
                new ArrayList<>(
                        clientLevel.getRecipeManager()
                                .getAllRecipesFor(
                                        JolCraftRecipes
                                                .HAND_INTERACTION_TYPE
                                                .get()
                                )
                );

        recipes.sort(
                Comparator.comparing(
                        RecipeHolder::id
                )
        );

        List<JeiHandInteractionRecipe> result =
                new ArrayList<>();

        for (
                RecipeHolder<HandInteractionRecipe> holder :
                recipes
        ) {
            addRecipeEntries(
                    holder.value(),
                    result
            );
        }

        return List.copyOf(
                result
        );
    }

    private static void addRecipeEntries(
            @NotNull HandInteractionRecipe recipe,
            @NotNull List<JeiHandInteractionRecipe> result
    ) {
        List<ItemStack> ingredientAExamples =
                ItemInputJeiTranslator.translate(
                        recipe.ingredientA()
                );

        List<ItemStack> ingredientBExamples =
                ItemInputJeiTranslator.translate(
                        recipe.ingredientB()
                );

        if (
                ingredientAExamples.isEmpty()
                        || ingredientBExamples.isEmpty()
        ) {
            return;
        }

        for (RecipeOutput output : recipe.outputs()) {
            JeiHandInteractionRecipe.Result translated =
                    translateOutput(
                            output
                    );

            if (translated == null) {
                continue;
            }

            result.add(
                    new JeiHandInteractionRecipe(
                            recipe,
                            ingredientAExamples,
                            ingredientBExamples,
                            translated
                    )
            );
        }
    }

    private static @Nullable JeiHandInteractionRecipe.Result translateOutput(
            @NotNull RecipeOutput output
    ) {
        switch (output) {
            case ItemOutput itemOutput -> {
                List<JeiItemOutcome> outcomes =
                        ItemOutputJeiTranslator.translate(
                                itemOutput
                        );

                if (outcomes.isEmpty()) {
                    return null;
                }

                return new JeiHandInteractionRecipe.ItemResult(
                        outcomes
                );
            }

            case EntityOutput entityOutput -> {
                SpawnEggItem spawnEgg =
                        SpawnEggItem.byId(
                                entityOutput.entity()
                        );

                if (spawnEgg == null) {
                    return null;
                }

                NumberRange count =
                        JeiNumberRangeTranslator.translate(
                                entityOutput.count()
                        );

                return new JeiHandInteractionRecipe.EntityResult(
                        entityOutput.entity(),
                        new ItemStack(
                                spawnEgg
                        ),
                        count.min(),
                        count.max()
                );
            }

            case EffectOutput effectOutput -> {
                return new JeiHandInteractionRecipe.EffectResult(
                        effectOutput.effect()
                );
            }

            default -> {
                return null;
            }
        }
    }
}