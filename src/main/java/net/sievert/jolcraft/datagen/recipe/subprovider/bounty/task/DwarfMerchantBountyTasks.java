package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task.util.AbstractBountyTasks;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.util.bounty.BountyTier;
import net.sievert.jolcraft.world.item.util.bounty.BountyType;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfMerchantBountyTasks extends AbstractBountyTasks {

    @Override
    protected @NotNull BountyType bountyType() {
        return BountyType.MERCHANT;
    }

    @Override
    public void addTasks(@NotNull AbstractRecipeProvider p) {

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.NOVICE, 1,
                collect(Items.COAL, amount(5, 12)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.NOVICE, 1,
                collect(Items.FLINT, amount(5, 12)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.NOVICE, 1,
                collect(Items.COPPER_INGOT, amount(5, 12)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.NOVICE, 1,
                collect(Items.COBBLED_DEEPSLATE, amount(5, 12)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.NOVICE, 1,
                collect(Items.TORCH, amount(5, 12)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.NOVICE, 1,
                collect(Items.CLAY_BALL, amount(5, 12)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.NOVICE, 1,
                collect(Items.IRON_NUGGET, amount(5, 12)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.APPRENTICE, 1,
                collect(Items.IRON_INGOT, amount(4, 8)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.APPRENTICE, 1,
                collect(Items.LAPIS_LAZULI, amount(4, 8)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.APPRENTICE, 1,
                collect(Items.REDSTONE, amount(4, 8)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.APPRENTICE, 1,
                collect(Items.GLOW_INK_SAC, amount(3, 6)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.APPRENTICE, 1,
                collect(Items.SPIDER_EYE, amount(3, 6)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.APPRENTICE, 1,
                collect(Items.GUNPOWDER, amount(3, 6)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.APPRENTICE, 1,
                collect(Items.BONE, amount(5, 9)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.JOURNEYMAN, 1,
                collect(Items.GOLD_INGOT, amount(3, 6)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.JOURNEYMAN, 1,
                collect(Items.EMERALD, amount(2, 5)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.JOURNEYMAN, 1,
                collect(Items.AMETHYST_SHARD, amount(3, 6)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.JOURNEYMAN, 1,
                collect(Items.BLAZE_POWDER, amount(3, 6)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.JOURNEYMAN, 1,
                collect(Items.INK_SAC, amount(3, 6)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.EXPERT, 1,
                collect(Items.ANVIL, amount(1)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.EXPERT, 1,
                collect(Items.GOLDEN_APPLE, amount(1, 2)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.EXPERT, 1,
                collect(Items.BOOK, amount(1, 2)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.EXPERT, 1,
                collect(Items.CAULDRON, amount(1)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.EXPERT, 1,
                collect(Items.ITEM_FRAME, amount(1, 3)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.EXPERT, 1,
                collect(Items.ENDER_PEARL, amount(1)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.MASTER, 1,
                collect(Items.NETHERITE_SCRAP, amount(1, 2)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.MASTER, 1,
                collect(Items.HEART_OF_THE_SEA, amount(1)));

        task(p, JolCraftItems.BOUNTY_CRATE.get(), BountyTier.MASTER, 1,
                collect(Items.DRAGON_BREATH, amount(1, 2)));
    }
}