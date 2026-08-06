package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassDialColor;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassStructureGroup;
import net.sievert.jolcraft.world.item.component.custom.crate.RewardCrateType;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import org.jetbrains.annotations.NotNull;

public record DwarfExplorerTrades(JolCraftDataProvider<RecipeOutput> parent) implements DwarfTradeSubProvider {

    private static final DwarfProfession PROFESSION =
            DwarfProfession.EXPLORER;

    public DwarfExplorerTrades(
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
        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE
                        )
                        .costACoins(
                                5,
                                10
                        )
                        .noCostB()
                        .result(JolCraftItems.EMPTY_DEEPSLATE_COMPASS)
                        .maxUses(3)
                        .dwarfXp(1)
                        .priceMultiplier(0.0F)
        );

        addDialTrade(
                output,
                tracking,
                DwarfMerchantData.Level.NOVICE,
                DeepslateCompassStructureGroup.SURFACE,
                1,
                3
        );

        addDialTrade(
                output,
                tracking,
                DwarfMerchantData.Level.APPRENTICE,
                DeepslateCompassStructureGroup.VILLAGES,
                3,
                5
        );

        addDialTrade(
                output,
                tracking,
                DwarfMerchantData.Level.APPRENTICE,
                DeepslateCompassStructureGroup.NETHER_PORTALS,
                3,
                5
        );

        addDialTrade(
                output,
                tracking,
                DwarfMerchantData.Level.JOURNEYMAN,
                DeepslateCompassStructureGroup.PILLAGERS,
                5,
                8
        );

        addDialTrade(
                output,
                tracking,
                DwarfMerchantData.Level.JOURNEYMAN,
                DeepslateCompassStructureGroup.RUINS,
                5,
                8
        );

        addDialTrade(
                output,
                tracking,
                DwarfMerchantData.Level.JOURNEYMAN,
                DeepslateCompassStructureGroup.OCEAN,
                5,
                8
        );

        addDialTrade(
                output,
                tracking,
                DwarfMerchantData.Level.EXPERT,
                DeepslateCompassStructureGroup.UNDERGROUND,
                8,
                12
        );

        addDialTrade(
                output,
                tracking,
                DwarfMerchantData.Level.MASTER,
                DeepslateCompassStructureGroup.DWARVEN,
                12,
                16
        );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create().profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.EXPERT)
                        .costACoins(
                                8,
                                16
                        )
                        .noCostB()
                        .rewardCrateResult(RewardCrateType.FISHING_LOOT)
                        .maxUses(1)
                        .dwarfXp(1)
                        .priceMultiplier(0.05F)
        );
    }

    private void addDialTrade(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level level,
            @NotNull DeepslateCompassStructureGroup group,
            int minCost,
            int maxCost
    ) {
        ItemOutput dialOutput =
                ItemOutput.of(
                        LootPool.lootPool()
                                .add(
                                        LootItem.lootTableItem(
                                                        JolCraftItems
                                                                .DEEPSLATE_COMPASS_DIAL
                                                                .get()
                                                )
                                                .apply(
                                                        SetComponentsFunction
                                                                .setComponent(
                                                                        JolCraftDataComponents
                                                                                .STRUCTURE_GROUP
                                                                                .get(),
                                                                        group.getId()
                                                                )
                                                )
                                                .apply(
                                                        SetComponentsFunction
                                                                .setComponent(
                                                                        JolCraftDataComponents
                                                                                .DEEPSLATE_COMPASS_DIAL_COLOR
                                                                                .get(),
                                                                        new DeepslateCompassDialColor(
                                                                                group.color()
                                                                        )
                                                                )
                                                )
                                )
                );

        emitOrdered(
                output,
                tracking,
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(level)
                        .tradeGroup(DwarfTradeRecipe.TradeGroup.CUMULATIVE_POOL)
                        .costACoins(
                                minCost,
                                maxCost
                        )
                        .costB(JolCraftItems.DEEPSLATE_COMPASS_DIAL)
                        .result(
                                dialOutput,
                                JolCraftStrings.underscored(group.getId() + JolCraftItemIds.DEEPSLATE_COMPASS_DIAL)
                        )
                        .maxUses(1)
                        .dwarfXp(1)
                        .priceMultiplier(0.5F)
        );
    }
}