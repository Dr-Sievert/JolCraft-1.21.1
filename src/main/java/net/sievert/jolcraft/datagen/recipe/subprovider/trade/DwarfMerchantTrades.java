package net.sievert.jolcraft.datagen.recipe.subprovider.trade;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.recipe.custom.DwarfTradeRecipe;
import net.sievert.jolcraft.datagen.recipe.util.AbstractRecipeProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public final class DwarfMerchantTrades extends AbstractDwarfTrades {

    @Override
    protected @NotNull DwarfProfession profession() {
        return DwarfProfession.MERCHANT;
    }

    @Override
    public void addTrades(@NotNull AbstractRecipeProvider p) {

        addBountyTrades(p);

        pooledBuy(p, NOVICE, Items.TORCH, 1, 2, 12);
        pooledBuy(p, NOVICE, Items.COAL, 1, 2, 5);
        pooledBuy(p, NOVICE, Items.FLINT, 1, 2, 5);
        pooledBuy(p, NOVICE, Items.COPPER_INGOT, 1, 2, 2);
        pooledBuy(p, NOVICE, Items.COBBLED_DEEPSLATE, 1, 2, 12);
        pooledBuy(p, NOVICE, Items.IRON_NUGGET, 1, 2, 12);
        pooledBuy(p, NOVICE, Items.BRICK, 1, 2, 4);
        pooledBuy(p, NOVICE, Items.STRING, 1, 2, 3);
        pooledBuy(p, NOVICE, JolCraftItems.DEEPSLATE_MUG.get(), 1, 2, 3);

        pooledBuy(p, APPRENTICE, Items.IRON_INGOT, 2, 3, 2);
        pooledBuy(p, APPRENTICE, Items.LAPIS_LAZULI, 1, 2, 6);
        pooledBuy(p, APPRENTICE, Items.REDSTONE, 1, 2, 6);
        pooledBuy(p, APPRENTICE, Items.FEATHER, 1, 2, 3);
        pooledBuy(p, APPRENTICE, Items.LEATHER, 1, 2, 2);
        pooledBuy(p, APPRENTICE, Items.WHITE_WOOL, 1, 2, 2);
        pooledBuy(p, APPRENTICE, JolCraftItems.PARCHMENT.get(), 1, 2, 3);

        pooledBuy(p, JOURNEYMAN, Items.GOLD_INGOT, 5, 7, 2);
        pooledBuy(p, JOURNEYMAN, Items.EMERALD, 2, 4, 2);
        pooledBuy(p, JOURNEYMAN, Items.AMETHYST_SHARD, 1, 2, 2);
        pooledBuy(p, JOURNEYMAN, Items.BLAZE_POWDER, 1, 2, 1);
        pooledBuy(p, JOURNEYMAN, Items.SPIDER_EYE, 1, 2, 1);
        pooledBuy(p, JOURNEYMAN, Items.GUNPOWDER, 1, 2, 2);
        pooledBuy(p, JOURNEYMAN, Items.BONE, 1, 2, 3);
        pooledBuy(p, JOURNEYMAN, Items.INK_SAC, 1, 2, 1);
        pooledBuy(p, JOURNEYMAN, JolCraftItems.QUILL_EMPTY.get(), 1, 2, 1);
        pooledBuy(p, JOURNEYMAN, JolCraftItems.CONTRACT_BLANK.get(), 1, 2, 1);

        pooledBuy(p, EXPERT, Items.GOLDEN_APPLE, 4, 6, 1);
        pooledBuy(p, EXPERT, Items.BOOK, 1, 2, 1);
        pooledBuy(p, EXPERT, Items.CAULDRON, 10, 14, 1);
        pooledBuy(p, EXPERT, Items.ITEM_FRAME, 1, 2, 1);
        pooledBuy(p, EXPERT, Items.ENDER_PEARL, 2, 4, 1);

        pooledCrate(p, MASTER, JolCraftItems.RESTOCK_CRATE.get());
        pooledCrate(p, MASTER, JolCraftItems.REROLL_CRATE.get());
    }

    private void pooledBuy(
            AbstractRecipeProvider p,
            Level level,
            ItemLike result,
            int minCoins,
            int maxCoins,
            int count
    ) {
        pooledTrade(
                p,
                level,
                DwarfTradeRecipe.TradePool.RESTOCK_POOL,
                1,
                coins(minCoins, maxCoins),
                Optional.empty(),
                itemResult(result, count),
                Hooks.EMPTY,
                3, 1, 0.05F,
                buy(result),
                Optional.empty()
        );
    }

    private void pooledCrate(
            AbstractRecipeProvider p,
            Level level,
            ItemLike crate
    ) {
        pooledTrade(
                p,
                level,
                DwarfTradeRecipe.TradePool.RESTOCK_POOL,
                1,
                true,
                coins(5, 15),
                Optional.of(cost(JolCraftItems.SUNGLEAM_CUT.get(), 1)),
                itemResult(crate, 1),
                Hooks.EMPTY,
                3, 0, 0.0F,
                buyFor(JolCraftItems.SUNGLEAM_CUT.get(), crate),
                Optional.empty()
        );
    }
}