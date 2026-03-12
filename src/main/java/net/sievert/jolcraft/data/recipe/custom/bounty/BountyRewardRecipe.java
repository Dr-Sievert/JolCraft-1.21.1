package net.sievert.jolcraft.data.recipe.custom.bounty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.base.CustomOutputRecipe;
import net.sievert.jolcraft.data.recipe.custom.base.RecipeValidation;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.data.recipe.param.output.base.Output;
import net.sievert.jolcraft.data.recipe.param.output.base.OutputParam;
import net.sievert.jolcraft.data.recipe.param.output.base.Outputs;
import net.sievert.jolcraft.data.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemOutput;
import net.sievert.jolcraft.data.recipe.param.output.pool.Pool;
import net.sievert.jolcraft.data.recipe.param.output.pool.PoolEntry;
import net.sievert.jolcraft.data.recipe.param.output.pool.Pools;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Redeem-complete bounty -> rewards.
 *
 * Rewards are items only.
 *
 * FX:
 * - sound (SoundOutput) is recipe data
 * - particles are action-owned presentation and are not stored in the recipe
 *
 * JSON supports shorthand via REWARDS_CODEC:
 * - item_output (single output param)
 * - Pools
 * - [Pool]
 * - Outputs
 */
public record BountyRewardRecipe(
        @NotNull DwarfProfession bountyType,
        @NotNull DwarfMerchantData.Level tier,
        @NotNull Outputs rewards,
        @NotNull SoundOutput sound
) implements CustomOutputRecipe<BountyRecipeInput, List<Output>> {

    @Override
    public boolean matches(@NotNull BountyRecipeInput in, Level level) {
        if (level.isClientSide) {
            return false;
        }

        ItemStack redeem = in.redeemStack();
        if (redeem.isEmpty()) {
            return false;
        }

        if (!BountyRecipe.isValidBountyStack(redeem)) {
            return false;
        }

        if (isIncompleteRewardBountyStack(redeem)) {
            return false;
        }

        if (in.type() != bountyType() || in.tier() != tier()) {
            return false;
        }

        return redeem.getCount() >= 1;
    }

    @Override
    public @NotNull List<Output> roll(@NotNull BountyRecipeInput input, @NotNull WorldContext ctx) {
        return rewards.generate(ctx);
    }

    @Override
    public @NotNull RecipeSerializer<? extends Recipe<BountyRecipeInput>> getSerializer() {
        return JolCraftRecipes.BOUNTY_REWARD_SERIALIZER.get();
    }

    @Override
    public @NotNull RecipeType<? extends Recipe<BountyRecipeInput>> getType() {
        return JolCraftRecipes.BOUNTY_REWARD_TYPE.get();
    }

    public static boolean isRewardBountyStack(ItemStack stack) {
        if (!BountyRecipe.isValidBountyStack(stack)) {
            return false;
        }
        if (!stack.has(JolCraftDataComponents.BOUNTY_DATA.get())) {
            return false;
        }
        if (!stack.has(JolCraftDataComponents.BOUNTY_FILL.get())) {
            return false;
        }
        return stack.has(JolCraftDataComponents.BOUNTY_COMPLETE.get());
    }

    public static boolean isIncompleteRewardBountyStack(ItemStack stack) {
        if (!isRewardBountyStack(stack)) {
            return true;
        }
        return !Boolean.TRUE.equals(stack.get(JolCraftDataComponents.BOUNTY_COMPLETE.get()));
    }

    public static final class Serializer implements RecipeSerializer<BountyRewardRecipe> {

        private static final Codec<Outputs> REWARDS_CODEC =
                Outputs.codecShorthand(OutputParam.CODEC);

        private static final String REWARDS_KEY =
                JolCraftStrings.plural(JolCraftDictionary.REWARD);

        private static final StreamCodec<RegistryFriendlyByteBuf, DwarfProfession> BOUNTY_TYPE_STREAM_CODEC =
                StreamCodec.of(
                        (buf, value) -> buf.writeUtf(value.professionName()),
                        buf -> {
                            String raw = buf.readUtf();
                            DwarfProfession type = BountyRecipe.parseType(raw);
                            if (type == null) {
                                throw new IllegalArgumentException("unknown bounty type '" + raw + "'");
                            }
                            return type;
                        }
                );

        private static final StreamCodec<RegistryFriendlyByteBuf, DwarfMerchantData.Level> BOUNTY_TIER_STREAM_CODEC =
                StreamCodec.of(
                        (buf, value) -> buf.writeVarInt(value.getId()),
                        buf -> {
                            int raw = buf.readVarInt();
                            DwarfMerchantData.Level tier = BountyRecipe.parseTier(raw);
                            if (tier == null) {
                                throw new IllegalArgumentException("unknown bounty tier id " + raw);
                            }
                            return tier;
                        }
                );

        public static final MapCodec<BountyRewardRecipe> CODEC =
                RecordCodecBuilder.mapCodec((RecordCodecBuilder.Instance<BountyRewardRecipe> inst) -> inst.group(
                        BountyRecipe.BOUNTY_TYPE_CODEC
                                .fieldOf(BountyRecipe.TYPE_KEY)
                                .forGetter(BountyRewardRecipe::bountyType),

                        BountyRecipe.BOUNTY_TIER_CODEC
                                .fieldOf(BountyRecipe.TIER_KEY)
                                .forGetter(BountyRewardRecipe::tier),

                        REWARDS_CODEC
                                .fieldOf(REWARDS_KEY)
                                .forGetter(BountyRewardRecipe::rewards),

                        SoundOutput.CODEC
                                .fieldOf(JolCraftDictionary.SOUND)
                                .forGetter(BountyRewardRecipe::sound)
                ).apply(inst, BountyRewardRecipe::new)).validate(BountyRewardRecipe::validateRecipe);

        public static final StreamCodec<RegistryFriendlyByteBuf, BountyRewardRecipe> STREAM_CODEC =
                StreamCodec.composite(
                        BOUNTY_TYPE_STREAM_CODEC,
                        BountyRewardRecipe::bountyType,

                        BOUNTY_TIER_STREAM_CODEC,
                        BountyRewardRecipe::tier,

                        Outputs.STREAM_CODEC,
                        BountyRewardRecipe::rewards,

                        SoundOutput.STREAM_CODEC,
                        BountyRewardRecipe::sound,

                        BountyRewardRecipe::new
                );

        @Override
        public @NotNull MapCodec<BountyRewardRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, BountyRewardRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }

    public static @NotNull DataResult<BountyRewardRecipe> validateRecipe(BountyRewardRecipe recipe) {
        String rewardsKey = JolCraftStrings.plural(JolCraftDictionary.REWARD);

        DataResult<BountyRewardRecipe> base = RecipeValidation.validate(recipe)
                .require(recipe.bountyType(), BountyRecipe.TYPE_KEY)
                .require(recipe.tier(), BountyRecipe.TIER_KEY)
                .requireValid(recipe.rewards(), rewardsKey)
                .requireValid(recipe.sound(), JolCraftDictionary.SOUND)
                .done();

        if (base.error().isPresent()) {
            return base;
        }

        DataResult<BountyRecipe.BountyInfo> infoRes =
                BountyRecipe.validateInfo(recipe.bountyType(), recipe.tier());

        if (infoRes.error().isPresent()) {
            String msg = infoRes.error().map(DataResult.Error::message).orElse("invalid bounty");
            return DataResult.error(() -> msg);
        }

        Outputs rewards = recipe.rewards();
        Pools pools = rewards.pools();

        if (!rewards.hasAnyEntries()) {
            return DataResult.error(() ->
                    "rewards must contain at least one output entry"
            );
        }

        for (Pool pool : pools.pools()) {
            for (PoolEntry entry : pool.entries()) {
                OutputParam output = entry.output();
                if (!output.typeId().equals(ItemOutput.TYPE_ID)) {
                    var typeId = output.typeId();
                    return DataResult.error(() ->
                            "rewards output must be item_output; got " + typeId
                    );
                }
            }
        }

        if (Outputs.anyItemOutputRequiresInputSource(recipe.rewards())) {
            return DataResult.error(() ->
                    "bounty reward recipes do not support input-sourced item transforms"
            );
        }

        return DataResult.success(recipe);
    }
}