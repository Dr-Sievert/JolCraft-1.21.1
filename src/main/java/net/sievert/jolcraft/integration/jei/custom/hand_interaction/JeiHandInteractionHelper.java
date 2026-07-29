package net.sievert.jolcraft.integration.jei.custom.hand_interaction;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.sievert.jolcraft.integration.jei.util.recipe.ItemInputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.recipe.ItemOutputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiItemOutcome;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiNumberRangeTranslator;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiNumberRangeTranslator.NumberRange;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeAccess;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.output.RecipeOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.EffectOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.EntityOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.custom.hand.HandInteractionRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public final class JeiHandInteractionHelper {

    private JeiHandInteractionHelper() {
    }

    public static @NotNull List<JeiHandInteractionRecipe> getRecipes() {
        return JeiRecipeAccess.translateSorted(
                JolCraftRecipes
                        .HAND_INTERACTION_TYPE
                        .get(),
                holder -> translate(
                        holder.value()
                )
        );
    }

    private static @NotNull List<JeiHandInteractionRecipe> translate(
            @NotNull HandInteractionRecipe recipe
    ) {
        List<ItemStack> ingredientAExamples =
                ItemInputJeiTranslator.translate(
                        recipe.ingredientA()
                );

        List<ItemStack> ingredientBExamples =
                ItemInputJeiTranslator.translate(
                        recipe.ingredientB()
                );

        List<JeiHandInteractionRecipe> result =
                new ArrayList<>();

        for (RecipeOutput output : recipe.outputs()) {
            if (output instanceof ItemOutput itemOutput) {
                for (JeiItemOutcome outcome :
                        ItemOutputJeiTranslator.translate(
                                itemOutput
                        )) {
                    result.add(
                            new JeiHandInteractionRecipe(
                                    recipe,
                                    ingredientAExamples,
                                    ingredientBExamples,
                                    new JeiHandInteractionRecipe.ItemResult(
                                            outcome
                                    )
                            )
                    );
                }

                continue;
            }

            JeiHandInteractionRecipe.Result translated =
                    translateOutput(
                            output
                    );

            if (translated != null) {
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

        return List.copyOf(
                result
        );
    }

    private static @Nullable JeiHandInteractionRecipe.Result translateOutput(
            @NotNull RecipeOutput output
    ) {
        return switch (output) {
            case EntityOutput entityOutput -> {
                SpawnEggItem spawnEgg =
                        SpawnEggItem.byId(
                                entityOutput.entity()
                        );

                if (spawnEgg == null) {
                    yield null;
                }

                NumberRange count =
                        JeiNumberRangeTranslator.translate(
                                entityOutput.count()
                        );

                yield new JeiHandInteractionRecipe.EntityResult(
                        entityOutput.entity(),
                        new ItemStack(
                                spawnEgg
                        ),
                        count.min(),
                        count.max()
                );
            }

            case EffectOutput effectOutput ->
                    new JeiHandInteractionRecipe.EffectResult(
                            effectOutput.effect()
                    );

            default ->
                    null;
        };
    }
}
