package net.sievert.jolcraft.world.recipe.custom.bounty;

import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.CustomRecipe;
import net.sievert.jolcraft.world.recipe.base.RecipeValidation;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.base.output.JolCraftRecipeOutputTypes;
import net.sievert.jolcraft.world.recipe.base.output.RecipeOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.SoundOutput;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Redeem-complete bounty -> rewards.
 *
 * Rewards are items only.
 *
 * FX:
 * - sound is recipe data
 * - particles are action-owned presentation and are not stored in the recipe
 */
public record BountyRewardRecipe(
        @NotNull DwarfProfession bountyType,
        @NotNull DwarfMerchantData.Level tier,
        @NotNull List<RecipeOutput> rewards,
        @NotNull SoundOutput sound
) implements CustomRecipe<BountyRecipeInput> {

    public static final String REWARDS_KEY =
            JolCraftStrings.plural(
                    JolCraftDictionary.REWARD
            );

    private static final LootContextParamSet OUTPUT_CONTEXT_PARAMS =
            new LootContextParamSet.Builder()
                    .required(LootContextParams.THIS_ENTITY)
                    .required(LootContextParams.ORIGIN)
                    .build();

    public BountyRewardRecipe {
        Objects.requireNonNull(
                bountyType,
                BountyRecipe.TYPE_KEY
        );

        Objects.requireNonNull(
                tier,
                BountyRecipe.TIER_KEY
        );

        rewards = List.copyOf(rewards);

        Objects.requireNonNull(
                sound,
                JolCraftDictionary.SOUND
        );
    }

    @Override
    public boolean matches(
            @NotNull BountyRecipeInput input,
            @NotNull Level level
    ) {
        if (level.isClientSide) {
            return false;
        }

        ItemStack redeem = input.redeemStack();

        if (redeem.isEmpty()) {
            return false;
        }

        if (!isRewardBountyStack(redeem)) {
            return false;
        }

        if (isIncompleteRewardBountyStack(redeem)) {
            return false;
        }

        return input.type() == bountyType
                && input.tier() == tier;
    }

    /**
     * Generates every configured item reward.
     *
     * The input is supplied so reward hooks may inspect or modify behavior
     * using the redeemed bounty stack.
     */
    public void generateRewards(
            @NotNull LootContext context,
            @NotNull BountyRecipeInput input,
            @NotNull Consumer<ItemStack> output
    ) {
        Objects.requireNonNull(
                context,
                JolCraftDictionary.CONTEXT
        );

        Objects.requireNonNull(
                input,
                JolCraftDictionary.INPUT
        );

        Objects.requireNonNull(
                output,
                JolCraftDictionary.OUTPUT
        );

        for (RecipeOutput reward : rewards) {
            if (reward instanceof ItemOutput itemOutput) {
                itemOutput.generate(
                        context,
                        input,
                        output
                );
            }
        }
    }

    /**
     * Resolves the recipe sound once using the supplied runtime context.
     */
    public void generateSound(
            @NotNull LootContext context,
            @NotNull BountyRecipeInput input,
            @NotNull Consumer<SoundOutput.GeneratedSound> output
    ) {
        Objects.requireNonNull(
                context,
                JolCraftDictionary.CONTEXT
        );

        Objects.requireNonNull(
                input,
                JolCraftDictionary.INPUT
        );

        Objects.requireNonNull(
                output,
                JolCraftDictionary.OUTPUT
        );

        sound.generate(
                context,
                input,
                output
        );
    }

    @Override
    public @NotNull RecipeSerializer<
            ? extends Recipe<BountyRecipeInput>
            > getSerializer() {
        return JolCraftRecipes.BOUNTY_REWARD_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<
            ? extends Recipe<BountyRecipeInput>
            > getType() {
        return JolCraftRecipes.BOUNTY_REWARD_TYPE.get();
    }

    public static boolean isRewardBountyStack(
            @NotNull ItemStack stack
    ) {
        if (!BountyRecipe.isValidBountyStack(stack)) {
            return false;
        }

        if (!stack.has(
                JolCraftDataComponents.BOUNTY_DATA.get()
        )) {
            return false;
        }

        if (!stack.has(
                JolCraftDataComponents.BOUNTY_FILL.get()
        )) {
            return false;
        }

        return stack.has(
                JolCraftDataComponents.BOUNTY_COMPLETE.get()
        );
    }

    public static boolean isIncompleteRewardBountyStack(
            @NotNull ItemStack stack
    ) {
        if (!isRewardBountyStack(stack)) {
            return true;
        }

        return !Boolean.TRUE.equals(
                stack.get(
                        JolCraftDataComponents.BOUNTY_COMPLETE.get()
                )
        );
    }

    public static final class Serializer
            implements RecipeSerializer<BountyRewardRecipe> {

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                DwarfProfession
                > BOUNTY_TYPE_STREAM_CODEC =
                StreamCodec.of(
                        (buffer, value) ->
                                buffer.writeUtf(
                                        value.professionName()
                                ),
                        buffer -> {
                            String raw =
                                    buffer.readUtf();

                            DwarfProfession type =
                                    BountyRecipe.parseType(raw);

                            if (type == null) {
                                throw new IllegalArgumentException(
                                        "unknown bounty type '"
                                                + raw
                                                + "'"
                                );
                            }

                            return type;
                        }
                );

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                DwarfMerchantData.Level
                > BOUNTY_TIER_STREAM_CODEC =
                StreamCodec.of(
                        (buffer, value) ->
                                buffer.writeVarInt(
                                        value.getId()
                                ),
                        buffer -> {
                            int raw =
                                    buffer.readVarInt();

                            DwarfMerchantData.Level tier =
                                    BountyRecipe.parseTier(raw);

                            if (tier == null) {
                                throw new IllegalArgumentException(
                                        "unknown bounty tier '"
                                                + raw
                                                + "'"
                                );
                            }

                            return tier;
                        }
                );

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                List<RecipeOutput>
                > REWARD_LIST_STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(
                        JolCraftRecipeOutputTypes.LIST_CODEC
                );

        private static final StreamCodec<
                RegistryFriendlyByteBuf,
                SoundOutput
                > SOUND_OUTPUT_STREAM_CODEC =
                ByteBufCodecs.fromCodecWithRegistries(
                        SoundOutput.CODEC.codec()
                );

        public static final MapCodec<BountyRewardRecipe> CODEC =
                RecordCodecBuilder
                        .<BountyRewardRecipe>mapCodec(instance ->
                                instance.group(
                                        BountyRecipe.BOUNTY_TYPE_CODEC
                                                .fieldOf(
                                                        BountyRecipe.TYPE_KEY
                                                )
                                                .forGetter(
                                                        BountyRewardRecipe::bountyType
                                                ),

                                        BountyRecipe.BOUNTY_TIER_CODEC
                                                .fieldOf(
                                                        BountyRecipe.TIER_KEY
                                                )
                                                .forGetter(
                                                        BountyRewardRecipe::tier
                                                ),

                                        JolCraftRecipeOutputTypes.LIST_CODEC
                                                .fieldOf(
                                                        REWARDS_KEY
                                                )
                                                .forGetter(
                                                        BountyRewardRecipe::rewards
                                                ),

                                        SoundOutput.CODEC
                                                .codec()
                                                .fieldOf(
                                                        JolCraftDictionary.SOUND
                                                )
                                                .forGetter(
                                                        BountyRewardRecipe::sound
                                                )
                                ).apply(
                                        instance,
                                        BountyRewardRecipe::new
                                )
                        )
                        .flatXmap(
                                Serializer::validate,
                                DataResult::success
                        );

        public static final StreamCodec<
                RegistryFriendlyByteBuf,
                BountyRewardRecipe
                > STREAM_CODEC =
                StreamCodec.composite(
                        BOUNTY_TYPE_STREAM_CODEC,
                        BountyRewardRecipe::bountyType,

                        BOUNTY_TIER_STREAM_CODEC,
                        BountyRewardRecipe::tier,

                        REWARD_LIST_STREAM_CODEC,
                        BountyRewardRecipe::rewards,

                        SOUND_OUTPUT_STREAM_CODEC,
                        BountyRewardRecipe::sound,

                        BountyRewardRecipe::new
                );

        @Override
        public @NotNull MapCodec<BountyRewardRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<
                RegistryFriendlyByteBuf,
                BountyRewardRecipe
                > streamCodec() {
            return STREAM_CODEC;
        }

        public static @NotNull DataResult<BountyRewardRecipe> validate(
                BountyRewardRecipe recipe
        ) {
            DataResult<BountyRewardRecipe> base =
                    RecipeValidation.validate(recipe)
                            .require(
                                    recipe.bountyType(),
                                    BountyRecipe.TYPE_KEY
                            )
                            .require(
                                    recipe.tier(),
                                    BountyRecipe.TIER_KEY
                            )
                            .require(
                                    recipe.rewards(),
                                    REWARDS_KEY
                            )
                            .require(
                                    recipe.sound(),
                                    JolCraftDictionary.SOUND
                            )
                            .rule(
                                    !recipe.rewards().isEmpty(),
                                    () -> REWARDS_KEY
                                            + " must contain at least one output"
                            )
                            .done();

            if (base.error().isPresent()) {
                return base;
            }

            DataResult<BountyRecipe.BountyInfo> infoResult =
                    BountyRecipe.validateInfo(
                            recipe.bountyType(),
                            recipe.tier()
                    );

            if (infoResult.error().isPresent()) {
                String message =
                        infoResult.error()
                                .map(DataResult.Error::message)
                                .orElse("invalid bounty");

                return DataResult.error(
                        () -> message
                );
            }

            for (int index = 0;
                 index < recipe.rewards().size();
                 index++) {

                RecipeOutput reward =
                        recipe.rewards().get(index);

                if (reward == null) {
                    int invalidIndex =
                            index;

                    return DataResult.error(() ->
                            REWARDS_KEY
                                    + "["
                                    + invalidIndex
                                    + "] is required"
                    );
                }

                if (!(reward instanceof ItemOutput)) {
                    int invalidIndex =
                            index;

                    String outputType =
                            reward.getType() == null
                                    ? "unknown"
                                    : reward.getType().toString();

                    return DataResult.error(() ->
                            REWARDS_KEY
                                    + "["
                                    + invalidIndex
                                    + "] must be an item output; got "
                                    + outputType
                    );
                }

                DataResult<Void> rewardValidation =
                        RecipeValidation.validateOutput(
                                reward,
                                OUTPUT_CONTEXT_PARAMS
                        );

                if (rewardValidation.error().isPresent()) {
                    int invalidIndex =
                            index;

                    String message =
                            rewardValidation.error()
                                    .map(DataResult.Error::message)
                                    .orElse("invalid reward output");

                    return DataResult.error(() ->
                            REWARDS_KEY
                                    + "["
                                    + invalidIndex
                                    + "]: "
                                    + message
                    );
                }
            }

            DataResult<Void> soundValidation =
                    RecipeValidation.validateOutput(
                            recipe.sound(),
                            OUTPUT_CONTEXT_PARAMS
                    );

            if (soundValidation.error().isPresent()) {
                String message =
                        soundValidation.error()
                                .map(DataResult.Error::message)
                                .orElse("invalid reward sound");

                return DataResult.error(() ->
                        JolCraftDictionary.SOUND
                                + ": "
                                + message
                );
            }

            return DataResult.success(recipe);
        }
    }
}