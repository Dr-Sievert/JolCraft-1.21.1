package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.reward;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.datagen.recipe.subprovider.bounty.reward.util.AbstractBountyRewards;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.item.util.bounty.BountyTier;
import net.sievert.jolcraft.world.item.util.bounty.BountyType;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfMerchantBountyRewards extends AbstractBountyRewards {

    @Override
    protected @NotNull BountyType bountyType() {
        return BountyType.MERCHANT;
    }

    @Override
    public void addRewards(@NotNull AbstractRecipeProvider p) {

        reward(p, BountyTier.NOVICE,     BountyRewardRecipe.RewardPool.MAIN, 1,
                redeemItem(JolCraftItems.BOUNTY_CRATE.get()),
                resultItem(JolCraftItems.GOLD_COIN.get()),
                amount(4, 6),
                give(JolCraftItems.GOLD_COIN.get()));

        reward(p, BountyTier.APPRENTICE, BountyRewardRecipe.RewardPool.MAIN, 1,
                redeemItem(JolCraftItems.BOUNTY_CRATE.get()),
                resultItem(JolCraftItems.GOLD_COIN.get()),
                amount(7, 10),
                give(JolCraftItems.GOLD_COIN.get()));

        reward(p, BountyTier.JOURNEYMAN, BountyRewardRecipe.RewardPool.MAIN, 1,
                redeemItem(JolCraftItems.BOUNTY_CRATE.get()),
                resultItem(JolCraftItems.GOLD_COIN.get()),
                amount(12, 16),
                give(JolCraftItems.GOLD_COIN.get()));

        reward(p, BountyTier.EXPERT,     BountyRewardRecipe.RewardPool.MAIN, 1,
                redeemItem(JolCraftItems.BOUNTY_CRATE.get()),
                resultItem(JolCraftItems.GOLD_COIN.get()),
                amount(20, 27),
                give(JolCraftItems.GOLD_COIN.get()));

        reward(p, BountyTier.MASTER,     BountyRewardRecipe.RewardPool.MAIN, 1,
                redeemItem(JolCraftItems.BOUNTY_CRATE.get()),
                resultItem(JolCraftItems.GOLD_COIN.get()),
                amount(30, 39),
                give(JolCraftItems.GOLD_COIN.get()));

        reward(p, BountyTier.APPRENTICE, BountyRewardRecipe.RewardPool.BONUS, 1,
                redeemItem(JolCraftItems.BOUNTY_CRATE.get()),
                resultItem(JolCraftItems.RESTOCK_CRATE.get()),
                amount(1),
                give(JolCraftItems.RESTOCK_CRATE.get()));

        reward(p, BountyTier.APPRENTICE, BountyRewardRecipe.RewardPool.BONUS, 1,
                redeemItem(JolCraftItems.BOUNTY_CRATE.get()),
                resultItem(JolCraftItems.REROLL_CRATE.get()),
                amount(1),
                give(JolCraftItems.REROLL_CRATE.get()));

        reward(p, BountyTier.JOURNEYMAN, BountyRewardRecipe.RewardPool.BONUS, 1,
                redeemItem(JolCraftItems.BOUNTY_CRATE.get()),
                resultItem(JolCraftItems.RESTOCK_CRATE.get()),
                amount(1),
                give(JolCraftItems.RESTOCK_CRATE.get()));

        reward(p, BountyTier.JOURNEYMAN, BountyRewardRecipe.RewardPool.BONUS, 1,
                redeemItem(JolCraftItems.BOUNTY_CRATE.get()),
                resultItem(JolCraftItems.REROLL_CRATE.get()),
                amount(1),
                give(JolCraftItems.REROLL_CRATE.get()));

        reward(p, BountyTier.EXPERT, BountyRewardRecipe.RewardPool.BONUS, 1,
                redeemItem(JolCraftItems.BOUNTY_CRATE.get()),
                resultItem(JolCraftItems.RESTOCK_CRATE.get()),
                amount(1),
                give(JolCraftItems.RESTOCK_CRATE.get()));

        reward(p, BountyTier.EXPERT, BountyRewardRecipe.RewardPool.BONUS, 1,
                redeemItem(JolCraftItems.BOUNTY_CRATE.get()),
                resultItem(JolCraftItems.REROLL_CRATE.get()),
                amount(1),
                give(JolCraftItems.REROLL_CRATE.get()));

        reward(p, BountyTier.MASTER, BountyRewardRecipe.RewardPool.BONUS, 1,
                redeemItem(JolCraftItems.BOUNTY_CRATE.get()),
                resultItem(JolCraftItems.RESTOCK_CRATE.get()),
                amount(1),
                give(JolCraftItems.RESTOCK_CRATE.get()));

        reward(p, BountyTier.MASTER, BountyRewardRecipe.RewardPool.BONUS, 1,
                redeemItem(JolCraftItems.BOUNTY_CRATE.get()),
                resultItem(JolCraftItems.REROLL_CRATE.get()),
                amount(1),
                give(JolCraftItems.REROLL_CRATE.get()));
    }
}