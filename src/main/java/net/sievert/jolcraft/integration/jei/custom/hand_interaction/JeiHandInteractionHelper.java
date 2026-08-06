package net.sievert.jolcraft.integration.jei.custom.hand_interaction;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.sievert.jolcraft.integration.jei.util.recipe.ItemInputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.recipe.ItemOutputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiItemOutcome;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiNumberRangeTranslator;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiNumberRangeTranslator.NumberRange;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeAccess;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassDialColor;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassStructureGroup;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.output.RecipeOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.EffectOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.EntityOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.base.output.hook.JolCraftRecipeHooks;
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
                JolCraftRecipes.HAND_INTERACTION_TYPE.get(),
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
                List<JeiItemOutcome> outcomes =
                        ItemOutputJeiTranslator.translate(
                                itemOutput
                        );

                if (itemOutput.hooks().contains(
                        JolCraftRecipeHooks.DEEPSLATE_COMPASS
                )) {
                    result.addAll(
                            translateDeepslateCompass(
                                    recipe,
                                    ingredientAExamples,
                                    ingredientBExamples,
                                    outcomes
                            )
                    );

                    continue;
                }

                for (JeiItemOutcome outcome : outcomes) {
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

    private static @NotNull List<JeiHandInteractionRecipe> translateDeepslateCompass(
            @NotNull HandInteractionRecipe recipe,
            @NotNull List<ItemStack> ingredientAExamples,
            @NotNull List<ItemStack> ingredientBExamples,
            @NotNull List<JeiItemOutcome> outcomes
    ) {
        List<JeiHandInteractionRecipe> result =
                new ArrayList<>();

        for (DeepslateCompassStructureGroup group :
                DeepslateCompassStructureGroup.values()) {
            ItemStack dial =
                    new ItemStack(
                            JolCraftItems.DEEPSLATE_COMPASS_DIAL.get()
                    );

            DeepslateCompassDialColor dialColor =
                    new DeepslateCompassDialColor(
                            group.color()
                    );

            dial.set(
                    JolCraftDataComponents.STRUCTURE_GROUP.get(),
                    group.getId()
            );

            dial.set(
                    JolCraftDataComponents.DEEPSLATE_COMPASS_DIAL_COLOR.get(),
                    dialColor
            );

            List<ItemStack> ingredientA =
                    replaceDialExamples(
                            ingredientAExamples,
                            dial
                    );

            List<ItemStack> ingredientB =
                    replaceDialExamples(
                            ingredientBExamples,
                            dial
                    );

            for (JeiItemOutcome outcome : outcomes) {
                if (!outcome.stack().is(
                        JolCraftItems.DEEPSLATE_COMPASS.get()
                )) {
                    continue;
                }

                ItemStack compass =
                        outcome.stack()
                                .copyWithCount(1);

                compass.set(
                        JolCraftDataComponents.STRUCTURE_GROUP.get(),
                        group.displayStructure()
                                .location()
                                .toString()
                );

                compass.set(
                        JolCraftDataComponents.DEEPSLATE_COMPASS_DIAL_COLOR.get(),
                        dialColor
                );

                result.add(
                        new JeiHandInteractionRecipe(
                                recipe,
                                ingredientA,
                                ingredientB,
                                new JeiHandInteractionRecipe.ItemResult(
                                        copyOutcome(
                                                outcome,
                                                compass
                                        )
                                )
                        )
                );
            }
        }

        return List.copyOf(
                result
        );
    }

    private static @NotNull List<ItemStack> replaceDialExamples(
            @NotNull List<ItemStack> examples,
            @NotNull ItemStack dial
    ) {
        List<ItemStack> replaced =
                new ArrayList<>(
                        examples.size()
                );

        for (ItemStack example : examples) {
            replaced.add(
                    example.is(
                            JolCraftItems.DEEPSLATE_COMPASS_DIAL.get()
                    )
                            ? dial.copy()
                            : example.copy()
            );
        }

        return List.copyOf(
                replaced
        );
    }

    private static @NotNull JeiItemOutcome copyOutcome(
            @NotNull JeiItemOutcome outcome,
            @NotNull ItemStack stack
    ) {
        return new JeiItemOutcome(
                stack,
                outcome.minCount(),
                outcome.maxCount(),
                outcome.weight(),
                outcome.totalWeight(),
                outcome.minRolls(),
                outcome.maxRolls(),
                outcome.conditions()
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