package net.sievert.jolcraft.datagen.recipe.subprovider.dwarf_trade;

import net.minecraft.world.item.Items;
import net.sievert.jolcraft.data.component.JolCraftDataComponents;
import net.sievert.jolcraft.data.recipe.param.output.custom.item.ItemOutput;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.custom.DwarfTradeRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.ItemOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.transform.ComponentTransformBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.transform.ItemTransformsBuilder;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.util.compass.DeepslateCompassHelper;
import net.sievert.jolcraft.world.item.util.compass.DialItemColor;
import net.sievert.jolcraft.world.item.util.compass.StructureGroup;
import org.jetbrains.annotations.NotNull;

public final class DwarfExplorerTrades implements RecipeSubProvider {

    private static final DwarfProfession PROFESSION = DwarfProfession.EXPLORER;

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

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(DwarfMerchantData.Level.NOVICE)
                        .costACoins(5, 10)
                        .noCostB()
                        .result(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get().asItem(), 1)
                        .maxUses(3)
                        .dwarfXp(0)
                        .priceMultiplier(0.0F)
        );

        addDialTrade(executor, DwarfMerchantData.Level.NOVICE, StructureGroup.DWARVEN);
        addDialTrade(executor, DwarfMerchantData.Level.APPRENTICE, StructureGroup.ANCIENT);
    }

    private static void addDialTrade(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull DwarfMerchantData.Level level,
            @NotNull StructureGroup group
    ) {

        int color = DeepslateCompassHelper.getColor(group);

        ItemOutput out = ItemOutputBuilder.create()
                .result(JolCraftItems.DEEPSLATE_COMPASS_DIAL.get().asItem())
                .transforms(
                        ItemTransformsBuilder.create()
                                .component(
                                        ComponentTransformBuilder.create()
                                                .set(JolCraftDataComponents.STRUCTURE_GROUP.get(), group.getId())
                                                .set(JolCraftDataComponents.DEEPSLATE_COMPASS_DIAL_COLOR.get(), new DialItemColor(color))
                                )
                )
                .build();

        executor.emitOrdered(
                DwarfTradeRecipeBuilder.create()
                        .profession(PROFESSION)
                        .merchantLevel(level)
                        .costACoins(5)
                        .costB(Items.REDSTONE, 1)
                        .result(out)
                        .maxUses(3)
                        .dwarfXp(0)
                        .priceMultiplier(0.0F)
        );
    }
}