package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.reward;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.datagen.recipe.subprovider.bounty.reward.util.AbstractBountyRewards;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.util.bounty.BountyTier;
import net.sievert.jolcraft.world.item.util.bounty.BountyType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DwarfMinerBountyRewards extends AbstractBountyRewards {

    @Override
    protected @NotNull BountyType bountyType() {
        return BountyType.MINER;
    }

    @Override
    public void addRewards(@NotNull AbstractRecipeProvider p) {

        geodes(p, BountyTier.NOVICE,
                4, 2, 1,
                a(),
                a(),
                a());

        geodes(p, BountyTier.APPRENTICE,
                4, 3, 1,
                a(1, 2),
                a(),
                a());

        geodes(p, BountyTier.JOURNEYMAN,
                3, 2, 1,
                a(1, 3),
                a(1, 2),
                a());

        geodes(p, BountyTier.EXPERT,
                2, 2, 1,
                a(2, 3),
                a(1, 3),
                a());

        geodes(p, BountyTier.MASTER,
                1, 2, 2,
                a(3, 4),
                a(2, 3),
                a(1, 2));
    }

    // ---------------------------------------------------------------------
    // Internals: ergonomic amount specs (no Amount at call sites)
    // ---------------------------------------------------------------------

    private record Amt(int min, int max) {}

    private static Amt a() {
        return new Amt(1, 1);
    }

    private static Amt a(int min, int max) {
        return new Amt(min, max);
    }

    private static BountyRewardRecipe.Amount toAmount(Amt a) {
        return (a.min == a.max) ? amount(a.min) : amount(a.min, a.max);
    }

    private void geodes(
            AbstractRecipeProvider p,
            BountyTier tier,
            int smallW,
            int medW,
            int largeW,
            Amt smallAmt,
            Amt medAmt,
            Amt largeAmt
    ) {
        reward(p, tier, BountyRewardRecipe.RewardPool.MAIN, smallW,
                redeemItem(JolCraftItems.BOUNTY.get()),
                resultItem(JolCraftItems.GEODE_SMALL.get()),
                toAmount(smallAmt),
                give(JolCraftItems.GEODE_SMALL.get()));

        reward(p, tier, BountyRewardRecipe.RewardPool.MAIN, medW,
                redeemItem(JolCraftItems.BOUNTY.get()),
                resultItem(JolCraftItems.GEODE_MEDIUM.get()),
                toAmount(medAmt),
                give(JolCraftItems.GEODE_MEDIUM.get()));

        reward(p, tier, BountyRewardRecipe.RewardPool.MAIN, largeW,
                redeemItem(JolCraftItems.BOUNTY.get()),
                resultItem(JolCraftItems.GEODE_LARGE.get()),
                toAmount(largeAmt),
                give(JolCraftItems.GEODE_LARGE.get()));
    }
}