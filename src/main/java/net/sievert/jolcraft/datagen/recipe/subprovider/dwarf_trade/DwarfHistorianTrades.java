package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.recipe.param.quantity.IntRange;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.custom.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SameParameterValue")
public final class DwarfHistorianTrades implements RecipeSubProvider {

    private static final DwarfProfession PROFESSION = DwarfProfession.HISTORIAN;

    @Override
    public @NotNull String folder() {
        return PROFESSION.professionName();
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    ) {

        // =========================================================
        // NOVICE
        // =========================================================

        executor.emitOrdered(trade(DwarfMerchantData.Level.NOVICE, JolCraftItems.DWARVEN_TOME_COMMON.get(), 3, 10, 5));
        executor.emitOrdered(trade(DwarfMerchantData.Level.NOVICE, JolCraftItems.DWARVEN_TOME_UNCOMMON.get(), 6, 10, 35));
        executor.emitOrdered(trade(DwarfMerchantData.Level.NOVICE, JolCraftItems.DWARVEN_TOME_RARE.get(), 10, 10, 75));
        executor.emitOrdered(trade(DwarfMerchantData.Level.NOVICE, JolCraftItems.DWARVEN_TOME_EPIC.get(), 22, 10, 125));

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costACoins(1, 2)
                        .noCostB()
                        .result(JolCraftItems.PARCHMENT.get().asItem(), 1, 3)
                        .maxUses(6)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        // =========================================================
        // APPRENTICE
        // =========================================================

        executor.emitOrdered(trade(DwarfMerchantData.Level.APPRENTICE, JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON.get(), 6, 10, 5));
        executor.emitOrdered(trade(DwarfMerchantData.Level.APPRENTICE, JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON.get(), 8, 10, 35));
        executor.emitOrdered(trade(DwarfMerchantData.Level.APPRENTICE, JolCraftItems.ANCIENT_DWARVEN_TOME_RARE.get(), 14, 10, 75));
        executor.emitOrdered(trade(DwarfMerchantData.Level.APPRENTICE, JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC.get(), 28, 10, 125));
        executor.emitOrdered(trade(DwarfMerchantData.Level.APPRENTICE, JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get(), 35, 10, 250));

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.APPRENTICE)
                        .costACoins(2, 4)
                        .noCostB()
                        .result(JolCraftItems.CONTRACT_BLANK.get().asItem(), 1, 2)
                        .maxUses(5)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        // =========================================================
        // JOURNEYMAN
        // =========================================================

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.JOURNEYMAN)
                        .costACoins(8)
                        .noCostB()
                        .result(JolCraftItems.UNIDENTIFIED_DWARVEN_TOME.get().asItem(), 1)
                        .maxUses(3)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.JOURNEYMAN)
                        .costACoins(1, 3)
                        .noCostB()
                        .result(JolCraftItems.QUILL_EMPTY.get().asItem(), 1, 2)
                        .maxUses(6)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        // =========================================================
        // EXPERT
        // =========================================================

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.EXPERT)
                        .costACoins(13)
                        .noCostB()
                        .result(JolCraftItems.UNIDENTIFIED_ANCIENT_DWARVEN_TOME.get().asItem(), 1)
                        .maxUses(3)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.EXPERT)
                        .costACoins(3, 6)
                        .noCostB()
                        .result(Items.INK_SAC, 1, 2)
                        .maxUses(6)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );

        // =========================================================
        // MASTER
        // =========================================================

        buyLegendaryPages(executor, 1, JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON.get());
        buyLegendaryPages(executor, 2, JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON.get());
        buyLegendaryPages(executor, 3, JolCraftItems.ANCIENT_DWARVEN_TOME_RARE.get());
        buyLegendaryPages(executor, 4, JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC.get());
        buyLegendaryPages(executor, 5, JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY.get());

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.MASTER)
                        .costACoins(15)
                        .costB(JolCraftItems.LEGENDARY_PAGE.get().asItem(), 10)
                        .result(JolCraftItems.UNIDENTIFIED_LEGENDARY_ANCIENT_DWARVEN_TOME.get().asItem(), 1)
                        .maxUses(10)
                        .dwarfXp(0)
                        .priceMultiplier(0.05F)
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.buyLegendaryLoreTome(
                        DwarfMerchantData.Level.MASTER,
                        PROFESSION,
                        DwarfLoreKey.MITHRIL_FORGE_TECHNIQUE,
                        IntRange.fixed(20),
                        IntRange.fixed(30)
                )
        );

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.buyLegendaryLoreTome(
                        DwarfMerchantData.Level.MASTER,
                        PROFESSION,
                        DwarfLoreKey.COIN_PRESS_MANUAL,
                        IntRange.fixed(20),
                        IntRange.fixed(30)
                )
        );
    }

    private static @NotNull DwarfTradeRecipeBuilder trade(
            @NotNull DwarfMerchantData.Level level,
            @NotNull ItemLike tome,
            int coins,
            int maxUses,
            int dwarfXp
    ) {
        return DwarfTradeRecipeBuilder.create()
                .profession(PROFESSION)
                .merchantLevel(level)
                .costA(tome.asItem(), 1)
                .coinsResult(coins)
                .maxUses(maxUses)
                .dwarfXp(dwarfXp)
                .priceMultiplier(0.05F);
    }

    private static void buyLegendaryPages(
            @NotNull RecipeEmissionExecutor executor,
            int pages,
            @NotNull ItemLike ancientTome
    ) {

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.MASTER)
                        .costACoins(pages)
                        .costB(ancientTome.asItem(), 1)
                        .result(JolCraftItems.LEGENDARY_PAGE.get().asItem(), pages)
                        .maxUses(100)
                        .dwarfXp(0)
                        .priceMultiplier(0.05F)
        );
    }
}