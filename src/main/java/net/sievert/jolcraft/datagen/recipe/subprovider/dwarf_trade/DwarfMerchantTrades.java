package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.build.custom.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.data.recipe.custom.dwarf_trade.DwarfTradeRecipe.TradeGroup;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SameParameterValue")
public final class DwarfMerchantTrades implements RecipeSubProvider {

    private static final DwarfProfession PROFESSION = DwarfProfession.GUILDMASTER;

    @Override
    public @NotNull String folder() {
        return PROFESSION.getId();
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    ) {

        DwarfTradeRecipeBuilder.addBountyTrades(executor, PROFESSION);

        pooledBuy(executor, DwarfMerchantData.Level.NOVICE, Items.TORCH, 1, 2, 12);
        pooledBuy(executor, DwarfMerchantData.Level.NOVICE, Items.COAL, 1, 2, 5);
        pooledBuy(executor, DwarfMerchantData.Level.NOVICE, Items.FLINT, 1, 2, 5);
        pooledBuy(executor, DwarfMerchantData.Level.NOVICE, Items.COPPER_INGOT, 1, 2, 2);
        pooledBuy(executor, DwarfMerchantData.Level.NOVICE, Items.COBBLED_DEEPSLATE, 1, 2, 12);
        pooledBuy(executor, DwarfMerchantData.Level.NOVICE, Items.IRON_NUGGET, 1, 2, 12);
        pooledBuy(executor, DwarfMerchantData.Level.NOVICE, Items.BRICK, 1, 2, 4);
        pooledBuy(executor, DwarfMerchantData.Level.NOVICE, Items.STRING, 1, 2, 3);
        pooledBuy(executor, DwarfMerchantData.Level.NOVICE, JolCraftItems.DEEPSLATE_MUG.get(), 1, 2, 3);

        pooledBuy(executor, DwarfMerchantData.Level.APPRENTICE, Items.IRON_INGOT, 2, 3, 2);
        pooledBuy(executor, DwarfMerchantData.Level.APPRENTICE, Items.LAPIS_LAZULI, 1, 2, 6);
        pooledBuy(executor, DwarfMerchantData.Level.APPRENTICE, Items.REDSTONE, 1, 2, 6);
        pooledBuy(executor, DwarfMerchantData.Level.APPRENTICE, Items.FEATHER, 1, 2, 3);
        pooledBuy(executor, DwarfMerchantData.Level.APPRENTICE, Items.LEATHER, 1, 2, 2);
        pooledBuy(executor, DwarfMerchantData.Level.APPRENTICE, Items.WHITE_WOOL, 1, 2, 2);
        pooledBuy(executor, DwarfMerchantData.Level.APPRENTICE, JolCraftItems.PARCHMENT.get(), 1, 2, 3);

        pooledBuy(executor, DwarfMerchantData.Level.JOURNEYMAN, Items.GOLD_INGOT, 5, 7, 2);
        pooledBuy(executor, DwarfMerchantData.Level.JOURNEYMAN, Items.EMERALD, 2, 4, 2);
        pooledBuy(executor, DwarfMerchantData.Level.JOURNEYMAN, Items.AMETHYST_SHARD, 1, 2, 2);
        pooledBuy(executor, DwarfMerchantData.Level.JOURNEYMAN, Items.BLAZE_POWDER, 1, 2, 1);
        pooledBuy(executor, DwarfMerchantData.Level.JOURNEYMAN, Items.SPIDER_EYE, 1, 2, 1);
        pooledBuy(executor, DwarfMerchantData.Level.JOURNEYMAN, Items.GUNPOWDER, 1, 2, 2);
        pooledBuy(executor, DwarfMerchantData.Level.JOURNEYMAN, Items.BONE, 1, 2, 3);
        pooledBuy(executor, DwarfMerchantData.Level.JOURNEYMAN, Items.INK_SAC, 1, 2, 1);
        pooledBuy(executor, DwarfMerchantData.Level.JOURNEYMAN, JolCraftItems.QUILL_EMPTY.get(), 1, 2, 1);
        pooledBuy(executor, DwarfMerchantData.Level.JOURNEYMAN, JolCraftItems.CONTRACT_BLANK.get(), 1, 2, 1);

        pooledBuy(executor, DwarfMerchantData.Level.EXPERT, Items.GOLDEN_APPLE, 4, 6, 1);
        pooledBuy(executor, DwarfMerchantData.Level.EXPERT, Items.BOOK, 1, 2, 1);
        pooledBuy(executor, DwarfMerchantData.Level.EXPERT, Items.CAULDRON, 10, 14, 1);
        pooledBuy(executor, DwarfMerchantData.Level.EXPERT, Items.ITEM_FRAME, 1, 2, 1);
        pooledBuy(executor, DwarfMerchantData.Level.EXPERT, Items.ENDER_PEARL, 2, 4, 1);

        pooledCrate(executor, DwarfMerchantData.Level.MASTER, JolCraftItems.RESTOCK_CRATE.get());
        pooledCrate(executor, DwarfMerchantData.Level.MASTER, JolCraftItems.REROLL_CRATE.get());
    }

    private void pooledBuy(
            RecipeEmissionExecutor executor,
            DwarfMerchantData.Level level,
            ItemLike result,
            int minCoins,
            int maxCoins,
            int count
    ) {
        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(level)
                        .tradeGroup(TradeGroup.CUMULATIVE_POOL)
                        .weight(1)
                        .maxUses(3)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
                        .costACoins(minCoins, maxCoins)
                        .noCostB()
                        .result(result.asItem(), count)
        );
    }

    private void pooledCrate(
            RecipeEmissionExecutor executor,
            DwarfMerchantData.Level level,
            ItemLike crate
    ) {
        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(level)
                        .tradeGroup(TradeGroup.EXACT_LEVEL_POOL)
                        .weight(1)
                        .maxUses(3)
                        .dwarfXp(0)
                        .priceMultiplier(0.0F)
                        .costACoins(5, 15)
                        .costB(JolCraftItems.SUNGLEAM_CUT.get().asItem())
                        .result(crate.asItem())
        );
    }
}