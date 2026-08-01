package net.sievert.jolcraft.integration.jei.custom.bounty.task;

import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.integration.jei.util.recipe.ItemOutputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiItemOutcome;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiNumberRangeTranslator;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiNumberRangeTranslator.NumberRange;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.recipe.base.output.RecipeOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.EntityOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRecipe;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyTaskRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record JeiBountyTaskRecipe(
        @NotNull BountyTaskRecipe recipe,
        @NotNull ItemStack bounty,
        @NotNull Objective objective,
        int weight,
        int totalWeight
) {

    public JeiBountyTaskRecipe {
        Objects.requireNonNull(
                recipe,
                JolCraftDictionary.RECIPE
        );

        Objects.requireNonNull(
                bounty,
                JolCraftDictionary.BOUNTY
        );

        Objects.requireNonNull(
                objective,
                JolCraftDictionary.OBJECTIVE
        );

        if (weight < 1) {
            throw new IllegalArgumentException(
                    "weight must be at least 1"
            );
        }

        if (totalWeight < weight) {
            throw new IllegalArgumentException(
                    "totalWeight must be greater than or equal to weight"
            );
        }

        bounty = bounty.copy();
    }

    public static @NotNull List<JeiBountyTaskRecipe> create(
            @NotNull BountyTaskRecipe recipe
    ) {
        Objects.requireNonNull(
                recipe,
                JolCraftDictionary.RECIPE
        );

        List<WeightedEntry.Wrapper<RecipeOutput>> entries =
                recipe.objectives()
                        .unwrap();

        int totalWeight = 0;

        for (
                WeightedEntry.Wrapper<RecipeOutput> entry :
                entries
        ) {
            totalWeight +=
                    entry.weight()
                            .asInt();
        }

        if (totalWeight < 1) {
            return List.of();
        }

        ItemStack bounty =
                createBountyStack(
                        recipe
                );

        List<JeiBountyTaskRecipe> recipes =
                new ArrayList<>();

        for (
                WeightedEntry.Wrapper<RecipeOutput> entry :
                entries
        ) {
            int weight =
                    entry.weight()
                            .asInt();

            if (weight < 1) {
                continue;
            }

            switch (entry.data()) {
                case ItemOutput itemOutput -> {
                    for (JeiItemOutcome outcome :
                            ItemOutputJeiTranslator.translate(
                                    itemOutput
                            )) {
                        recipes.add(
                                new JeiBountyTaskRecipe(
                                        recipe,
                                        bounty,
                                        new ItemObjective(
                                                outcome
                                        ),
                                        weight,
                                        totalWeight
                                )
                        );
                    }
                }

                case EntityOutput entityOutput ->
                        recipes.add(
                                new JeiBountyTaskRecipe(
                                        recipe,
                                        bounty,
                                        new EntityObjective(
                                                entityOutput,
                                                JeiNumberRangeTranslator.translate(
                                                        entityOutput.count()
                                                )
                                        ),
                                        weight,
                                        totalWeight
                                )
                        );

                default -> {
                }
            }
        }

        return List.copyOf(
                recipes
        );
    }

    private static @NotNull ItemStack createBountyStack(
            @NotNull BountyTaskRecipe recipe
    ) {
        ItemStack bounty =
                new ItemStack(
                        JolCraftItems.BOUNTY.get()
                );

        BountyRecipe.setType(
                bounty,
                recipe.bountyType()
        );

        BountyRecipe.setTier(
                bounty,
                recipe.tier()
        );

        return bounty;
    }

    public double chancePerRoll() {
        double objectiveChance =
                (double) weight / totalWeight;

        return switch (objective) {
            case ItemObjective itemObjective ->
                    objectiveChance
                            * itemObjective.outcome()
                            .chancePerRoll();

            case EntityObjective ignored ->
                    objectiveChance;
        };
    }

    public int minRolls() {
        return switch (objective) {
            case ItemObjective itemObjective ->
                    itemObjective.outcome()
                            .minRolls();

            case EntityObjective ignored ->
                    1;
        };
    }

    public int maxRolls() {
        return switch (objective) {
            case ItemObjective itemObjective ->
                    itemObjective.outcome()
                            .maxRolls();

            case EntityObjective ignored ->
                    1;
        };
    }

    public sealed interface Objective
            permits ItemObjective,
            EntityObjective {
    }

    public record ItemObjective(
            @NotNull JeiItemOutcome outcome
    ) implements Objective {

        public ItemObjective {
            Objects.requireNonNull(
                    outcome,
                    JolCraftDictionary.OBJECTIVE
            );
        }
    }

    public record EntityObjective(
            @NotNull EntityOutput output,
            @NotNull NumberRange amount
    ) implements Objective {

        public EntityObjective {
            Objects.requireNonNull(
                    output,
                    JolCraftDictionary.OBJECTIVE
            );

            Objects.requireNonNull(
                    amount,
                    JolCraftDictionary.COUNT
            );
        }
    }
}
