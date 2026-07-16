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

public final class BountyRewardRecipeBuilder
        implements JolCraftEmissionBuilder<RecipeOutput> {

    private @Nullable String id;

    private @Nullable DwarfProfession bountyType;
    private @Nullable DwarfMerchantData.Level tier;

    /*
     * Every configured reward is a weighted alternative in one pool.
     * Redeeming a bounty rolls this pool exactly once.
     */
    private final List<WeightedReward> rewards =
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

    public BountyRewardRecipeBuilder reward(
            @NotNull LootPoolSingletonContainer.Builder<?> reward,
            int weight
    ) {
        this.rewards.add(
                new WeightedReward(
                        reward,
                        weight
                )
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

        for (int index = 0;
             index < rewards.size();
             index++) {
            WeightedReward reward =
                    rewards.get(index);

            if (reward.weight() < 1) {
                int invalidIndex = index;

                return DataResult.error(() ->
                        BountyRewardRecipe.REWARDS_KEY
                                + "["
                                + invalidIndex
                                + "].weight must be at least 1"
                );
            }
        }

        if (sound == null) {
            return DataResult.error(() ->
                    JolCraftDictionary.SOUND
                            + " is required"
            );
        }

        ItemOutput rewardOutput =
                buildRewardOutput();

        BountyRewardRecipe recipe =
                new BountyRewardRecipe(
                        bountyType,
                        tier,
                        List.of(rewardOutput),
                        sound
                );

        DataResult<BountyRewardRecipe> validated =
                BountyRewardRecipe.Serializer.validate(
                        recipe
                );

        if (validated.error().isPresent()) {
            String message = validated.error()
                    .map(DataResult.Error::message)
                    .orElse(
                            "invalid bounty reward recipe"
                    );

            return DataResult.error(() -> message);
        }

        /*
         * Reward subproviders are already separated by profession folders,
         * so the tier name is sufficient as the default recipe id.
         */
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

    private @NotNull ItemOutput buildRewardOutput() {
        LootPool.Builder pool =
                LootPool.lootPool()
                        .setRolls(
                                ConstantValue.exactly(
                                        1.0F
                                )
                        );

        for (WeightedReward reward : rewards) {
            pool.add(
                    reward.entry()
                            .setWeight(
                                    reward.weight()
                            )
            );
        }

        return ItemOutputs.pool(pool);
    }

    private record WeightedReward(
            @NotNull LootPoolSingletonContainer.Builder<?> entry,
            int weight
    ) {}
}