package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SameParameterValue")
public record DwarfHistorianTrades(JolCraftDataProvider<RecipeOutput> parent) implements DwarfTradeSubProvider {

    private static final DwarfProfession PROFESSION =
            DwarfProfession.HISTORIAN;

    public DwarfHistorianTrades(
            @NotNull JolCraftDataProvider<RecipeOutput> parent
    ) {
        this.parent = parent;
    }

    @Override
    public @NotNull JolCraftDataProvider<RecipeOutput> parent() {
        return parent;
    }

    @Override
    public @NotNull String id() {
        return folder();
    }

    @Override
    public @NotNull String folder() {
        return PROFESSION.professionName();
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {

        // NOVICE

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.NOVICE)
                .costACoins(1, 2)
                .noCostB()
                .result(Items.PAPER, 3, 5)
                .maxUses(3)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, trade(
                DwarfMerchantData.Level.NOVICE,
                JolCraftItems.DWARVEN_TOME_COMMON,
                2,
                10,
                5
        ));

        emitOrdered(output, tracking, trade(
                DwarfMerchantData.Level.NOVICE,
                JolCraftItems.DWARVEN_TOME_UNCOMMON,
                4,
                10,
                35
        ));

        emitOrdered(output, tracking, trade(
                DwarfMerchantData.Level.NOVICE,
                JolCraftItems.DWARVEN_TOME_RARE,
                6,
                10,
                75
        ));

        emitOrdered(output, tracking, trade(
                DwarfMerchantData.Level.NOVICE,
                JolCraftItems.DWARVEN_TOME_EPIC,
                8,
                10,
                125
        ));

        // APPRENTICE

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.APPRENTICE)
                .costACoins(1, 3)
                .noCostB()
                .result(JolCraftItems.PARCHMENT, 1, 3)
                .maxUses(5)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.APPRENTICE)
                .costACoins(3, 5)
                .costB(JolCraftItems.QUILL_EMPTY)
                .result(JolCraftItems.QUILL_FULL)
                .maxUses(3)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        // JOURNEYMAN

        emitOrdered(output, tracking, trade(
                DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON,
                6,
                10,
                5
        ));

        emitOrdered(output, tracking, trade(
                DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON,
                8,
                10,
                35
        ));

        emitOrdered(output, tracking, trade(
                DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.ANCIENT_DWARVEN_TOME_RARE,
                13,
                10,
                75
        ));

        emitOrdered(output, tracking, trade(
                DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC,
                25,
                10,
                125
        ));

        emitOrdered(output, tracking, trade(
                DwarfMerchantData.Level.JOURNEYMAN,
                JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY,
                50,
                10,
                250
        ));

        // EXPERT

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.EXPERT)
                .costACoins(8, 12)
                .noCostB()
                .result(JolCraftItems.UNIDENTIFIED_DWARVEN_TOME)
                .maxUses(5)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        emitOrdered(output, tracking, DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(DwarfMerchantData.Level.EXPERT)
                .costACoins(13, 16)
                .noCostB()
                .result(JolCraftItems.UNIDENTIFIED_ANCIENT_DWARVEN_TOME)
                .maxUses(3)
                .dwarfXp(1)
                .priceMultiplier(0.05F)
        );

        // MASTER

        buyLegendaryPages(output, tracking,
                1,
                JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON
        );

        buyLegendaryPages(output, tracking,
                2,
                JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON
        );

        buyLegendaryPages(output, tracking,
                4,
                JolCraftItems.ANCIENT_DWARVEN_TOME_RARE
        );

        buyLegendaryPages(output, tracking,
                8,
                JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC
        );

        buyLegendaryPages(output, tracking,
                16,
                JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY
        );
    }

    private static @NotNull DwarfTradeRecipeBuilder trade(
            @NotNull DwarfMerchantData.Level level,
            @NotNull ItemLike tome,
            int coins,
            int maxUses,
            int dwarfXp
    ) {
        return DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                .merchantLevel(level)
                .costA(tome)
                .noCostB()
                .coinsResult(coins)
                .maxUses(maxUses)
                .dwarfXp(dwarfXp)
                .priceMultiplier(0.05F);
    }

    private void buyLegendaryPages(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            int pages,
            @NotNull ItemLike ancientTome
    ) {
        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.MASTER)
                        .costACoins(pages)
                        .costB(ancientTome)
                        .result(JolCraftItems.LEGENDARY_PAGE.get(), pages)
                        .maxUses(10)
                        .dwarfXp(0)
                        .priceMultiplier(0.05F)
        );
    }
}