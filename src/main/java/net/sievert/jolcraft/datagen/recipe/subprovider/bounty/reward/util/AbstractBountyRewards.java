package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.reward.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Recipe;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.util.bounty.BountyTier;
import net.sievert.jolcraft.world.item.util.bounty.BountyType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@SuppressWarnings("deprecation")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class AbstractBountyRewards {

    private static final String ROOT_FOLDER = JolCraftDictionary.BOUNTY;

    protected abstract @NotNull BountyType bountyType();

    public abstract void addRewards(@NotNull AbstractRecipeProvider p);

    protected final String fullFolder() {
        return JolCraftStrings.slashed(
                ROOT_FOLDER,
                JolCraftDictionary.REWARD,
                bountyType().getId()
        );
    }

    // =====================================================================
    // Amount helpers
    // =====================================================================

    protected static BountyRewardRecipe.Amount amount(int value) {
        return BountyRewardRecipe.Amount.fixed(value);
    }

    protected static BountyRewardRecipe.Amount amount(int min, int max) {
        return new BountyRewardRecipe.Amount(min, max);
    }

    // =====================================================================
    // Ingredients (redeem + result)
    // =====================================================================

    protected static BountyRewardRecipe.RedeemIngredient redeemItem(Item item) {
        return new BountyRewardRecipe.RedeemIngredient.ItemIngredient(item.builtInRegistryHolder());
    }

    protected static BountyRewardRecipe.RedeemIngredient redeemTag(TagKey<Item> tag) {
        return new BountyRewardRecipe.RedeemIngredient.TagIngredient(tag);
    }

    protected static BountyRewardRecipe.RewardIngredient resultItem(Item item) {
        return new BountyRewardRecipe.RewardIngredient.ItemIngredient(item.builtInRegistryHolder());
    }

    protected static BountyRewardRecipe.RewardIngredient resultTag(TagKey<Item> tag) {
        return new BountyRewardRecipe.RewardIngredient.TagIngredient(tag);
    }

    // =====================================================================
    // Core emitters
    // =====================================================================

    protected final void reward(
            AbstractRecipeProvider p,
            BountyTier tier,
            BountyRewardRecipe.RewardPool pool,
            int weight,
            BountyRewardRecipe.RedeemIngredient redeemIngredient,
            BountyRewardRecipe.RewardIngredient resultIngredient,
            BountyRewardRecipe.Amount resultAmount,
            String idPath
    ) {
        // trade-style: amount lives INSIDE the wrapper objects
        BountyRewardRecipe.RedeemCost redeem =
                new BountyRewardRecipe.RedeemCost(redeemIngredient, BountyRewardRecipe.Amount.fixed(1));

        BountyRewardRecipe.RewardResult result =
                new BountyRewardRecipe.RewardResult(resultIngredient, resultAmount);

        BountyRewardRecipe recipe = new BountyRewardRecipe(
                redeem,
                bountyType(),
                tier.getValue(),
                pool,
                weight,
                result
        );

        save(p, tier, pool, idPath, recipe);
    }

    protected final void rewardRedeemTag(
            AbstractRecipeProvider p,
            BountyTier tier,
            BountyRewardRecipe.RewardPool pool,
            int weight,
            TagKey<Item> redeemTag,
            BountyRewardRecipe.RewardIngredient resultIngredient,
            BountyRewardRecipe.Amount resultAmount,
            String idPath
    ) {
        reward(p, tier, pool, weight, redeemTag(redeemTag), resultIngredient, resultAmount, idPath);
    }

    // =====================================================================
    // Save (no advancement)
    // =====================================================================

    private void save(
            AbstractRecipeProvider p,
            BountyTier tier,
            BountyRewardRecipe.RewardPool pool,
            String idPath,
            BountyRewardRecipe recipe
    ) {
        String file = JolCraftStrings.underscored(
                tierId(tier),
                poolId(pool),
                idPath
        );

        ResourceLocation id = JolCraft.location(p.inFolder(fullFolder(), file));
        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, id);

        p.out().accept(key, recipe, null);
    }

    // =====================================================================
    // Stable id helpers
    // =====================================================================

    protected static String tierId(BountyTier tier) {
        return tier.name().toLowerCase();
    }

    protected static String poolId(BountyRewardRecipe.RewardPool pool) {
        return pool.name().toLowerCase();
    }

    @SuppressWarnings("deprecation")
    protected static String itemId(Item item) {
        return item.builtInRegistryHolder().key().location().getPath();
    }

    protected static String give(Item item) {
        return JolCraftStrings.underscored(
                JolCraftDictionary.GIVE,
                itemId(item)
        );
    }

    protected static String giveFor(Item redeem, Item result) {
        return JolCraftStrings.underscored(
                JolCraftDictionary.GIVE,
                itemId(result),
                JolCraftDictionary.FOR,
                itemId(redeem)
        );
    }

    protected static String giveTagFor(TagKey<Item> redeemTag, Item result) {
        return JolCraftStrings.underscored(
                JolCraftDictionary.GIVE,
                itemId(result),
                JolCraftDictionary.FOR,
                redeemTag.location().getPath()
        );
    }
}