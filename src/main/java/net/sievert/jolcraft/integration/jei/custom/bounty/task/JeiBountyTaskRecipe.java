package net.sievert.jolcraft.integration.jei.custom.bounty.task;

import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
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
        @NotNull RecipeOutput objective,
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

        if (
                !(objective instanceof ItemOutput)
                        && !(objective instanceof EntityOutput)
        ) {
            throw new IllegalArgumentException(
                    "objective must be an item or entity output"
            );
        }

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
                new ArrayList<>(
                        entries.size()
                );

        for (
                WeightedEntry.Wrapper<RecipeOutput> entry :
                entries
        ) {
            RecipeOutput objective =
                    entry.data();

            if (
                    !(objective instanceof ItemOutput)
                            && !(objective instanceof EntityOutput)
            ) {
                continue;
            }

            recipes.add(
                    new JeiBountyTaskRecipe(
                            recipe,
                            bounty,
                            objective,
                            entry.weight()
                                    .asInt(),
                            totalWeight
                    )
            );
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

    public double chance() {
        return (double) weight / totalWeight;
    }
}