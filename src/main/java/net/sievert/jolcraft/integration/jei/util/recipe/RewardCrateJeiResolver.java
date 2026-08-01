package net.sievert.jolcraft.integration.jei.util.recipe;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.mixin.LootTableAccessor;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.RewardCrateSource;
import net.sievert.jolcraft.world.loot.custom.reward.RewardCrateSourceFinder;
import net.sievert.jolcraft.world.loot.custom.reward.client.RewardLootTableClientCache;
import net.sievert.jolcraft.world.recipe.base.output.RecipeOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRewardRecipe;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Expands deferred reward-crate sources into their possible loot outcomes for JEI.
 */
public final class RewardCrateJeiResolver {

    private RewardCrateJeiResolver() {}

    public static @NotNull List<ResolvedOutcome> translate(
            @NotNull ItemOutput output
    ) {
        Objects.requireNonNull(output, JolCraftDictionary.OUTPUT);

        Set<RewardCrateSource> sources =
                RewardCrateSourceFinder.find(output);

        if (sources.isEmpty()) {
            return ItemOutputJeiTranslator.translate(output)
                    .stream()
                    .map(outcome ->
                            new ResolvedOutcome(
                                    outcome,
                                    null
                            )
                    )
                    .toList();
        }

        List<ResolvedOutcome> resolved =
                new ArrayList<>();

        for (RewardCrateSource source : sources) {
            ItemStack crate =
                    new ItemStack(
                            JolCraftItems.REWARD_CRATE.get()
                    );

            crate.set(
                    JolCraftDataComponents.REWARD_CRATE_SOURCE.get(),
                    source
            );

            for (JeiItemOutcome reward : resolve(source)) {
                resolved.add(
                        new ResolvedOutcome(
                                reward,
                                crate
                        )
                );
            }
        }

        return List.copyOf(resolved);
    }

    private static @NotNull List<JeiItemOutcome> resolve(
            @NotNull RewardCrateSource source
    ) {
        if (source instanceof RewardCrateSource.LootTableSource(
                net.minecraft.resources.ResourceKey<LootTable> lootTable
        )) {
            LootTable table =
                    RewardLootTableClientCache.get(
                            lootTable
                    ).orElseThrow(() ->
                            new IllegalArgumentException(
                                    "Missing synced reward loot table for JEI: "
                                            + lootTable.location()
                            )
                    );

            return translate(table);
        }

        RewardCrateSource.RecipeSource recipeSource =
                (RewardCrateSource.RecipeSource) source;

        ClientLevel level =
                Minecraft.getInstance().level;

        if (level == null) {
            throw new IllegalStateException(
                    "Cannot resolve reward recipe for JEI without a client level"
            );
        }

        RecipeHolder<?> holder =
                level.getRecipeManager()
                        .byKey(recipeSource.recipeId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Missing reward recipe for JEI: "
                                                + recipeSource.recipeId()
                                )
                        );

        if (!(holder.value() instanceof BountyRewardRecipe recipe)) {
            throw new IllegalArgumentException(
                    "Reward crate recipe source is not a bounty reward recipe: "
                            + recipeSource.recipeId()
            );
        }

        List<JeiItemOutcome> outcomes =
                new ArrayList<>();

        for (RecipeOutput reward : recipe.rewards()) {
            outcomes.addAll(
                    ItemOutputJeiTranslator.translate(
                            (ItemOutput) reward
                    )
            );
        }

        return List.copyOf(outcomes);
    }

    private static @NotNull List<JeiItemOutcome> translate(
            @NotNull LootTable table
    ) {
        List<JeiItemOutcome> outcomes =
                new ArrayList<>();

        LootTableAccessor accessor =
                (LootTableAccessor) table;

        for (LootPool pool : accessor.jolcraft$getPools()) {
            outcomes.addAll(
                    ItemOutputJeiTranslator.translate(
                            pool,
                            accessor.jolcraft$getFunctions()
                    )
            );
        }

        if (outcomes.isEmpty()) {
            throw new IllegalArgumentException(
                    "Reward loot table produced no supported JEI item outcomes"
            );
        }

        return List.copyOf(outcomes);
    }

    public record ResolvedOutcome(
            @NotNull JeiItemOutcome outcome,
            @Nullable ItemStack rewardCrate
    ) {

        public ResolvedOutcome {
            Objects.requireNonNull(
                    outcome,
                    "outcome"
            );

            rewardCrate =
                    rewardCrate == null
                            ? null
                            : rewardCrate.copy();
        }

        public boolean deliveredByRewardCrate() {
            return rewardCrate != null;
        }
    }
}