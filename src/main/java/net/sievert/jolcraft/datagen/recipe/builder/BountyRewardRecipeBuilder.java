package net.sievert.jolcraft.datagen.recipe.builder;

import com.mojang.serialization.DataResult;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.builder.JolCraftEmissionBuilder;
import net.sievert.jolcraft.datagen.base.output.JolCraftDataEmission;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRecipe;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.world.recipe.output.ItemOutput;
import net.sievert.jolcraft.world.recipe.output.ItemOutputs;
import net.sievert.jolcraft.world.recipe.output.SoundOutput;
import net.sievert.jolcraft.world.recipe.output.SoundOutputs;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public final class BountyRewardRecipeBuilder
        implements JolCraftEmissionBuilder<RecipeOutput> {

    private @Nullable String id;

    private @Nullable DwarfProfession bountyType;
    private @Nullable DwarfMerchantData.Level tier;

    /*
     * Every ItemOutput in this list is one independent reward table.
     *
     * All tables are executed when the bounty is redeemed. Each table handles
     * its own rolls, weights, conditions, and functions through vanilla loot
     * behavior.
     */
    private final List<ItemOutput> rewards =
            new ArrayList<>();

    /*
     * Preserves the existing reward(entry, weight) API.
     *
     * Consecutive calls are accumulated into one weighted reward table and
     * flushed into rewards before another independent table is added or the
     * recipe is built.
     */
    private final List<WeightedReward> pendingWeightedRewards =
            new ArrayList<>();

    private @Nullable SoundOutput sound;

    private BountyRewardRecipeBuilder() {}

    public static BountyRewardRecipeBuilder create() {
        return new BountyRewardRecipeBuilder();
    }

    public BountyRewardRecipeBuilder id(
            @NotNull String id
    ) {
        this.id = id;
        return this;
    }

    public BountyRewardRecipeBuilder bountyType(
            @NotNull DwarfProfession bountyType
    ) {
        this.bountyType = bountyType;
        return this;
    }

    public BountyRewardRecipeBuilder tier(
            @NotNull DwarfMerchantData.Level tier
    ) {
        this.tier = tier;
        return this;
    }

    /**
     * Adds one weighted alternative to the builder's default reward table.
     *
     * Multiple calls create one pool which rolls exactly once.
     */
    public BountyRewardRecipeBuilder reward(
            @NotNull LootPoolSingletonContainer.Builder<?> reward,
            int weight
    ) {
        if (weight < 1) {
            throw new IllegalArgumentException(
                    "reward weight must be at least 1"
            );
        }

        pendingWeightedRewards.add(
                new WeightedReward(
                        reward,
                        weight
                )
        );

        return this;
    }

    /**
     * Adds one guaranteed reward as its own independent table.
     */
    public BountyRewardRecipeBuilder guaranteedReward(
            @NotNull LootPoolSingletonContainer.Builder<?> reward
    ) {
        flushPendingWeightedRewards();

        LootPool.Builder pool =
                LootPool.lootPool()
                        .setRolls(
                                ConstantValue.exactly(
                                        1.0F
                                )
                        )
                        .add(reward);

        rewards.add(
                ItemOutputs.pool(pool)
        );

        return this;
    }

    /**
     * Adds a fully configured vanilla loot pool as one independent reward
     * table.
     */
    public BountyRewardRecipeBuilder rewardPool(
            @NotNull LootPool.Builder pool
    ) {
        flushPendingWeightedRewards();

        rewards.add(
                ItemOutputs.pool(pool)
        );

        return this;
    }

    /**
     * Adds one independent weighted reward table using the convenience group
     * builder.
     */
    public BountyRewardRecipeBuilder rewardPool(
            @NotNull Consumer<RewardPoolBuilder> configuration
    ) {
        flushPendingWeightedRewards();

        RewardPoolBuilder poolBuilder =
                new RewardPoolBuilder();

        configuration.accept(
                poolBuilder
        );

        rewards.add(
                poolBuilder.build()
        );

        return this;
    }

    /**
     * Adds an already constructed item output as an independent reward table.
     */
    public BountyRewardRecipeBuilder rewardOutput(
            @NotNull ItemOutput reward
    ) {
        flushPendingWeightedRewards();

        rewards.add(
                reward
        );

        return this;
    }

    public BountyRewardRecipeBuilder sound(
            @NotNull SoundEvent sound
    ) {
        this.sound =
                SoundOutputs.sound(sound);

        return this;
    }

    public BountyRewardRecipeBuilder sound(
            @NotNull SoundOutput sound
    ) {
        this.sound = sound;
        return this;
    }

    @Override
    public @NotNull DataResult<
            JolCraftDataEmission<RecipeOutput>
            > buildValidated() {
        flushPendingWeightedRewards();

        if (bountyType == null) {
            return DataResult.error(() ->
                    BountyRecipe.TYPE_KEY
                            + " is required"
            );
        }

        if (tier == null) {
            return DataResult.error(() ->
                    BountyRecipe.TIER_KEY
                            + " is required"
            );
        }

        if (rewards.isEmpty()) {
            return DataResult.error(() ->
                    BountyRewardRecipe.REWARDS_KEY
                            + " must contain at least one reward"
            );
        }

        if (sound == null) {
            return DataResult.error(() ->
                    JolCraftDictionary.SOUND
                            + " is required"
            );
        }

        BountyRewardRecipe recipe =
                new BountyRewardRecipe(
                        bountyType,
                        tier,
                        List.copyOf(rewards),
                        sound
                );

        DataResult<BountyRewardRecipe> validated =
                BountyRewardRecipe.Serializer.validate(
                        recipe
                );

        if (validated.error().isPresent()) {
            String message =
                    validated.error()
                            .map(DataResult.Error::message)
                            .orElse(
                                    "invalid bounty reward recipe"
                            );

            return DataResult.error(
                    () -> message
            );
        }

        String resolvedId =
                id != null && !id.isBlank()
                        ? id
                        : tier.name()
                        .toLowerCase(Locale.ROOT);

        return DataResult.success(
                new JolCraftDataEmission<>(
                        resolvedId,
                        (recipeOutput, path) ->
                                recipeOutput.accept(
                                        ResourceLocation
                                                .fromNamespaceAndPath(
                                                        JolCraft.MOD_ID,
                                                        path
                                                ),
                                        recipe,
                                        null
                                )
                )
        );
    }

    /**
     * Converts the entries added through reward(entry, weight) into one
     * independent weighted reward table.
     */
    private void flushPendingWeightedRewards() {
        if (pendingWeightedRewards.isEmpty()) {
            return;
        }

        LootPool.Builder pool =
                LootPool.lootPool()
                        .setRolls(
                                ConstantValue.exactly(
                                        1.0F
                                )
                        );

        for (WeightedReward reward : pendingWeightedRewards) {
            pool.add(
                    reward.entry()
                            .setWeight(
                                    reward.weight()
                            )
            );
        }

        rewards.add(
                ItemOutputs.pool(pool)
        );

        pendingWeightedRewards.clear();
    }

    private record WeightedReward(
            @NotNull LootPoolSingletonContainer.Builder<?> entry,
            int weight
    ) {}

    /**
     * Convenience builder for one independent weighted reward table.
     */
    public static final class RewardPoolBuilder {

        private final List<WeightedReward> entries =
                new ArrayList<>();

        private RewardPoolBuilder() {}

        public RewardPoolBuilder reward(
                @NotNull LootPoolSingletonContainer.Builder<?> reward,
                int weight
        ) {
            if (weight < 1) {
                throw new IllegalArgumentException(
                        "reward pool weight must be at least 1"
                );
            }

            entries.add(
                    new WeightedReward(
                            reward,
                            weight
                    )
            );

            return this;
        }

        private @NotNull ItemOutput build() {
            if (entries.isEmpty()) {
                throw new IllegalStateException(
                        "reward pool must contain at least one reward"
                );
            }

            LootPool.Builder pool =
                    LootPool.lootPool()
                            .setRolls(
                                    ConstantValue.exactly(
                                            1.0F
                                    )
                            );

            for (WeightedReward reward : entries) {
                pool.add(
                        reward.entry()
                                .setWeight(
                                        reward.weight()
                                )
                );
            }

            return ItemOutputs.pool(pool);
        }
    }
}