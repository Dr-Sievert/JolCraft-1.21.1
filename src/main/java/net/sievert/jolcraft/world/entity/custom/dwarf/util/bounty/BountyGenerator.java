package net.sievert.jolcraft.world.entity.custom.dwarf.util.bounty;

import net.minecraft.core.HolderLookup;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRecipe;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRecipeInput;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyTaskRecipe;
import net.sievert.jolcraft.world.item.util.bounty.BountyData;
import net.sievert.jolcraft.world.item.util.bounty.BountyTier;
import net.sievert.jolcraft.world.item.util.bounty.BountyType;

import java.util.List;

public final class BountyGenerator {

    private BountyGenerator() {}

    // =====================================================================
    // TASK ROLLING
    // =====================================================================

    public static final class Task {

        private Task() {}

        /**
         * Rolls a bounty task result (bounty OR bounty_crate) for the given redeem stack.
         * The redeem stack is the "token" item the player hands to the dwarf (usually jolcraft:bounty).
         */
        public static ItemStack roll(
                ServerLevel level,
                ItemStack redeemStack
        ) {
            if (redeemStack.isEmpty()) return ItemStack.EMPTY;

            BountyType type = BountyRecipe.readType(redeemStack);
            BountyTier tier = BountyRecipe.readTier(redeemStack);
            if (type == BountyType.UNKNOWN || tier == BountyTier.UNKNOWN) return ItemStack.EMPTY;

            BountyRecipeInput input = new BountyRecipeInput(redeemStack, type, tier);

            RecipeManager manager = level.getServer().getRecipeManager();
            List<BountyTaskRecipe> candidates = manager.recipeMap()
                    .getRecipesFor(JolCraftRecipes.BOUNTY_TASK_TYPE.get(), input, level)
                    .map(RecipeHolder::value)
                    .filter(r -> r.weight() > 0)
                    .toList();

            if (candidates.isEmpty()) return ItemStack.EMPTY;

            BountyTaskRecipe chosen = rollWeighted(level.getRandom(), candidates);
            if (chosen == null) return ItemStack.EMPTY;

            ItemStack out = chosen.assemble(input, level.registryAccess());

            BountyData.BountyObjective rolled = chosen.objective().roll(level.getRandom());
            out.set(JolCraftDataComponents.BOUNTY_DATA.get(), new BountyData(rolled));

            return out;
        }
    }

    // =====================================================================
    // REWARD ROLLING
    // =====================================================================

    public static final class Reward {

        private Reward() {}

        public static ItemStack roll(
                ServerLevel level,
                ItemStack redeemStack,
                BountyRewardRecipe.RewardPool pool
        ) {
            if (redeemStack.isEmpty()) return ItemStack.EMPTY;
            if (!BountyRewardRecipe.isCompletedBountyStack(redeemStack)) return ItemStack.EMPTY;

            BountyType type = BountyRecipe.readType(redeemStack);
            BountyTier tier = BountyRecipe.readTier(redeemStack);
            if (type == BountyType.UNKNOWN || tier == BountyTier.UNKNOWN) return ItemStack.EMPTY;

            BountyRecipeInput input = new BountyRecipeInput(redeemStack, type, tier);

            RecipeManager manager = level.getServer().getRecipeManager();
            List<BountyRewardRecipe> candidates = manager.recipeMap()
                    .getRecipesFor(JolCraftRecipes.BOUNTY_REWARD_TYPE.get(), input, level)
                    .map(RecipeHolder::value)
                    .filter(r -> r.pool() == pool)
                    .filter(r -> r.weight() > 0)
                    .toList();

            if (candidates.isEmpty()) return ItemStack.EMPTY;

            BountyRewardRecipe chosen = rollWeighted(level.getRandom(), candidates);
            if (chosen == null) return ItemStack.EMPTY;

            HolderLookup.Provider registries = level.registryAccess();
            return chosen.rollResult(registries, level.getRandom());
        }
    }

    // =====================================================================
    // Shared helpers
    // =====================================================================

    private static <T> T rollWeighted(RandomSource random, List<T> list) {
        int total = 0;
        for (T t : list) {
            total += weightOf(t);
        }
        if (total <= 0) return null;

        int roll = random.nextInt(total);
        int acc = 0;

        for (T t : list) {
            acc += weightOf(t);
            if (roll < acc) return t;
        }

        return list.getLast();
    }

    private static int weightOf(Object o) {
        if (o instanceof BountyTaskRecipe r) return r.weight();
        if (o instanceof BountyRewardRecipe r) return r.weight();
        return 0;
    }
}