package net.sievert.jolcraft.integration.jei.custom.bounty.reward;

import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.integration.jei.util.ItemOutputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.JeiItemOutcome;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.recipe.base.output.RecipeOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.EntityOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRecipe;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyTaskRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public record JeiBountyRewardRecipe(
        @NotNull BountyRewardRecipe recipe,
        @NotNull List<ItemStack> inputs,
        @NotNull JeiItemOutcome reward
) {

    public JeiBountyRewardRecipe {
        Objects.requireNonNull(
                recipe,
                "recipe"
        );

        Objects.requireNonNull(
                inputs,
                "inputs"
        );

        Objects.requireNonNull(
                reward,
                "reward"
        );

        inputs = inputs.stream()
                .map(ItemStack::copy)
                .toList();

        if (inputs.isEmpty()) {
            throw new IllegalArgumentException(
                    "inputs must contain at least one completed bounty"
            );
        }

    }

    public static @NotNull List<JeiBountyRewardRecipe> create(
            @NotNull BountyRewardRecipe rewardRecipe,
            @NotNull List<BountyTaskRecipe> taskRecipes
    ) {
        Objects.requireNonNull(
                rewardRecipe,
                "rewardRecipe"
        );

        Objects.requireNonNull(
                taskRecipes,
                "taskRecipes"
        );

        List<ItemStack> inputs =
                createInputs(
                        rewardRecipe,
                        taskRecipes
                );

        List<JeiItemOutcome> rewards =
                createRewards(
                        rewardRecipe
                );

        return rewards.stream()
                .map(reward -> new JeiBountyRewardRecipe(
                        rewardRecipe,
                        inputs,
                        reward
                ))
                .toList();
    }

    private static @NotNull List<ItemStack> createInputs(
            @NotNull BountyRewardRecipe rewardRecipe,
            @NotNull List<BountyTaskRecipe> taskRecipes
    ) {
        List<RecipeOutput> objectives =
                taskRecipes.stream()
                        .filter(
                                taskRecipe ->
                                        taskRecipe.bountyType()
                                                == rewardRecipe.bountyType()
                        )
                        .filter(
                                taskRecipe ->
                                        taskRecipe.tier()
                                                == rewardRecipe.tier()
                        )
                        .flatMap(
                                taskRecipe ->
                                        taskRecipe.objectives()
                                                .unwrap()
                                                .stream()
                        )
                        .map(
                                WeightedEntry.Wrapper::data
                        )
                        .toList();

        ItemStack bounty =
                objectives.stream()
                        .filter(
                                EntityOutput.class::isInstance
                        )
                        .map(
                                EntityOutput.class::cast
                        )
                        .findFirst()
                        .map(
                                objective ->
                                        createEntityBounty(
                                                rewardRecipe
                                        )
                        )
                        .orElse(
                                ItemStack.EMPTY
                        );

        ItemStack bountyCrate =
                objectives.stream()
                        .filter(
                                ItemOutput.class::isInstance
                        )
                        .map(
                                ItemOutput.class::cast
                        )
                        .findFirst()
                        .map(
                                objective ->
                                        createItemBounty(
                                                rewardRecipe
                                        )
                        )
                        .orElse(
                                ItemStack.EMPTY
                        );

        List<ItemStack> inputs =
                new ArrayList<>(2);

        if (!bounty.isEmpty()) {
            inputs.add(
                    bounty
            );
        }

        if (!bountyCrate.isEmpty()) {
            inputs.add(
                    bountyCrate
            );
        }

        return List.copyOf(
                inputs
        );
    }

    private static @NotNull ItemStack createEntityBounty(
            @NotNull BountyRewardRecipe rewardRecipe
    ) {
        return createCompletedBounty(
                new ItemStack(
                        JolCraftItems.BOUNTY.get()
                ),
                rewardRecipe
        );
    }

    private static @NotNull ItemStack createItemBounty(
            @NotNull BountyRewardRecipe rewardRecipe
    ) {
        return createCompletedBounty(
                new ItemStack(
                        JolCraftItems.BOUNTY_CRATE.get()
                ),
                rewardRecipe
        );
    }

    private static @NotNull ItemStack createCompletedBounty(
            @NotNull ItemStack stack,
            @NotNull BountyRewardRecipe rewardRecipe
    ) {
        BountyRecipe.setType(
                stack,
                rewardRecipe.bountyType()
        );

        BountyRecipe.setTier(
                stack,
                rewardRecipe.tier()
        );

        stack.set(
                JolCraftDataComponents.BOUNTY_COMPLETE.get(),
                true
        );

        return stack;
    }

    private static @NotNull List<JeiItemOutcome> createRewards(
            @NotNull BountyRewardRecipe recipe
    ) {
        List<JeiItemOutcome> rewards =
                new ArrayList<>();

        for (RecipeOutput reward : recipe.rewards()) {
            if (!(reward instanceof ItemOutput itemOutput)) {
                continue;
            }

            rewards.addAll(
                    ItemOutputJeiTranslator.translate(
                            itemOutput
                    )
            );
        }

        return List.copyOf(
                rewards
        );
    }
}
