package net.sievert.jolcraft.datagen.recipe.subprovider.bounty.task;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.EntityType;
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
public final class DwarfMinerBountyTasks extends AbstractBountyTasks {

    @Override
    protected @NotNull BountyType bountyType() {
        return BountyType.MINER;
    }

    @Override
    public void addTasks(@NotNull AbstractRecipeProvider p) {

        task(p,
                JolCraftItems.BOUNTY.get(),
                BountyTier.NOVICE,
                1,
                slay(EntityType.ZOMBIE, amount(1, 3)));

        /*

        task(p, JolCraftItems.BOUNTY.get(), BountyTier.NOVICE, 1,
                collect(Items.STONE, amount(8, 15)));

        task(p, JolCraftItems.BOUNTY.get(), BountyTier.NOVICE, 1,
                collect(Items.GRANITE, amount(8, 15)));

        task(p, JolCraftItems.BOUNTY.get(), BountyTier.NOVICE, 1,
                collect(Items.DIORITE, amount(8, 15)));

        task(p, JolCraftItems.BOUNTY.get(), BountyTier.NOVICE, 1,
                collect(Items.ANDESITE, amount(8, 15)));

        task(p, JolCraftItems.BOUNTY.get(), BountyTier.NOVICE, 1,
                collect(Items.TUFF, amount(8, 15)));

        task(p, JolCraftItems.BOUNTY.get(), BountyTier.APPRENTICE, 1,
                collect(Items.IRON_ORE, amount(4, 8)));

        task(p, JolCraftItems.BOUNTY.get(), BountyTier.APPRENTICE, 1,
                collect(Items.COPPER_ORE, amount(4, 8)));

        task(p, JolCraftItems.BOUNTY.get(), BountyTier.APPRENTICE, 1,
                collect(Items.DEEPSLATE_IRON_ORE, amount(4, 8)));

        task(p, JolCraftItems.BOUNTY.get(), BountyTier.JOURNEYMAN, 1,
                collect(Items.GOLD_ORE, amount(3, 6)));

        task(p, JolCraftItems.BOUNTY.get(), BountyTier.JOURNEYMAN, 1,
                collect(Items.EMERALD_ORE, amount(2, 4)));

        task(p, JolCraftItems.BOUNTY.get(), BountyTier.EXPERT, 1,
                collect(Items.DIAMOND_ORE, amount(1, 2)));

        task(p, JolCraftItems.BOUNTY.get(), BountyTier.EXPERT, 1,
                collect(Items.DEEPSLATE_DIAMOND_ORE, amount(1, 2)));

        task(p, JolCraftItems.BOUNTY.get(), BountyTier.MASTER, 1,
                collect(Items.ANCIENT_DEBRIS, amount(1)));

         */
    }
}