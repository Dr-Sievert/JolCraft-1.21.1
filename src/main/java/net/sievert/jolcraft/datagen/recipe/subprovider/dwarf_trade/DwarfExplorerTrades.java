package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassDialColor;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassStructureGroup;
import net.sievert.jolcraft.world.recipe.output.ItemOutput;
import org.jetbrains.annotations.NotNull;

public record DwarfExplorerTrades(
        JolCraftDataProvider<RecipeOutput> parent
) implements RecipeSubProvider {

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
                        .merchantLevel(
                                DwarfMerchantData.Level.NOVICE
                        )
                        .costACoins(
                                5,
                                10
                        )
                        .noCostB()
                        .result(
                                JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get(),
                                1
                        )
                        .maxUses(3)
                        .dwarfXp(0)
                        .priceMultiplier(0.0F)
        );

        addDialTrade(
                output,
                tracking,
                DwarfMerchantData.Level.NOVICE,
                DeepslateCompassStructureGroup.SURFACE
        );

        addDialTrade(
                output,
                tracking,
                DwarfMerchantData.Level.APPRENTICE,
                DeepslateCompassStructureGroup.VILLAGES
        );
    }

    private void addDialTrade(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking,
            @NotNull DwarfMerchantData.Level level,
            @NotNull DeepslateCompassStructureGroup group
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
                        .costACoins(5)
                        .costB(
                                Items.REDSTONE,
                                1
                        )
                        .result(
                                dialOutput,
                                group.getId()
                                        + "_deepslate_compass_dial"
                        )
                        .maxUses(3)
                        .dwarfXp(0)
                        .priceMultiplier(0.0F)
        );
    }
}