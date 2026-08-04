package net.sievert.jolcraft.integration.jei.custom.bounty.reward;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.integration.jei.util.item.JeiStacks;
import net.sievert.jolcraft.integration.jei.util.recipe.ItemOutputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiItemOutcome;
import net.sievert.jolcraft.util.JolCraftStrings;
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
        @NotNull JeiItemOutcome reward,
        @NotNull ItemStack rewardCrate
) {

    public JeiBountyRewardRecipe {
        Objects.requireNonNull(recipe, JolCraftDictionary.RECIPE);
        Objects.requireNonNull(
                inputs,
                JolCraftStrings.plural(JolCraftDictionary.INPUT)
        );
        Objects.requireNonNull(reward, JolCraftDictionary.REWARD);
        Objects.requireNonNull(rewardCrate, JolCraftDictionary.CRATE);

        inputs =
                JeiStacks.copyRequired(
                        inputs,
                        JolCraftStrings.plural(JolCraftDictionary.INPUT)
                );

        rewardCrate = rewardCrate.copy();
    }

    public static @NotNull List<JeiBountyRewardRecipe> create(
            @NotNull ResourceLocation recipeId,
            @NotNull BountyRewardRecipe rewardRecipe,
            @NotNull List<BountyTaskRecipe> taskRecipes
    ) {
        Objects.requireNonNull(recipeId, JolCraftDictionary.ID);
        Objects.requireNonNull(
                rewardRecipe,
                JolCraftStrings.underscored(
                        JolCraftDictionary.REWARD,
                        JolCraftDictionary.RECIPE
                )
        );
        Objects.requireNonNull(
                taskRecipes,
                JolCraftStrings.underscored(
                        JolCraftDictionary.TASK,
                        JolCraftStrings.plural(JolCraftDictionary.RECIPE)
                )
        );

        List<ItemStack> inputs =
                createInputs(
                        rewardRecipe,
                        taskRecipes
                );

        ItemStack rewardCrate =
                rewardRecipe.createRewardCrate(
                        recipeId
                );

        return createRewards(rewardRecipe)
                .stream()
                .map(reward ->
                        new JeiBountyRewardRecipe(
                                rewardRecipe,
                                inputs,
                                reward,
                                rewardCrate
                        )
                )
                .toList();
    }

    private static @NotNull List<ItemStack> createInputs(
            @NotNull BountyRewardRecipe rewardRecipe,
            @NotNull List<BountyTaskRecipe> taskRecipes
    ) {
        List<RecipeOutput> objectives =
                taskRecipes.stream()
                        .filter(taskRecipe ->
                                taskRecipe.bountyType()
                                        == rewardRecipe.bountyType()
                        )
                        .filter(taskRecipe ->
                                taskRecipe.tier()
                                        == rewardRecipe.tier()
                        )
                        .flatMap(taskRecipe ->
                                taskRecipe.objectives()
                                        .unwrap()
                                        .stream()
                        )
                        .map(WeightedEntry.Wrapper::data)
                        .toList();

        ItemStack bounty =
                objectives.stream()
                        .filter(EntityOutput.class::isInstance)
                        .map(EntityOutput.class::cast)
                        .findFirst()
                        .map(objective ->
                                createEntityBounty(rewardRecipe)
                        )
                        .orElse(ItemStack.EMPTY);

        ItemStack bountyCrate =
                objectives.stream()
                        .filter(ItemOutput.class::isInstance)
                        .map(ItemOutput.class::cast)
                        .findFirst()
                        .map(objective ->
                                createItemBounty(rewardRecipe)
                        )
                        .orElse(ItemStack.EMPTY);

        List<ItemStack> inputs = new ArrayList<>(2);

        if (!bounty.isEmpty()) {
            inputs.add(bounty);
        }

        if (!bountyCrate.isEmpty()) {
            inputs.add(bountyCrate);
        }

        return List.copyOf(inputs);
    }

    private static @NotNull ItemStack createEntityBounty(
            @NotNull BountyRewardRecipe rewardRecipe
    ) {
        return createCompletedBounty(
                new ItemStack(JolCraftItems.BOUNTY.get()),
                rewardRecipe
        );
    }

    private static @NotNull ItemStack createItemBounty(
            @NotNull BountyRewardRecipe rewardRecipe
    ) {
        return createCompletedBounty(
                new ItemStack(JolCraftItems.BOUNTY_CRATE.get()),
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
        List<JeiItemOutcome> rewards = new ArrayList<>();

        for (RecipeOutput reward : recipe.rewards()) {
            rewards.addAll(
                    ItemOutputJeiTranslator.translate(
                            (ItemOutput) reward
                    )
            );
        }

        return List.copyOf(rewards);
    }

}
