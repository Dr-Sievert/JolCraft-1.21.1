package net.sievert.jolcraft.world.item.registry;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.block.fluid.util.brewing.BrewingColors;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.custom.food.brewing.DwarvenBrewBucketItem;
import net.sievert.jolcraft.world.item.custom.food.brewing.DwarvenBrewItem;
import net.sievert.jolcraft.world.item.custom.tooltip.SimpleTooltipItem;
import net.sievert.jolcraft.world.item.food.JolCraftFoodProperties;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

import java.util.function.Supplier;

public final class JolCraftBrewingItems {

    public static final int YEAST_BOTTLE_VOLUME = 250;

    private JolCraftBrewingItems() {}

    public static DeferredItem<Item> registerBarleyMalt() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.BARLEY_MALT,
                properties -> new SimpleTooltipItem(
                        properties,
                        JolCraftLanguageKeys.TOOLTIP_MALT
                )
        );
    }

    public static DeferredItem<Item> registerYeast(
            Supplier<? extends Fluid> fluid
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.YEAST,
                properties -> new SimpleTooltipItem(
                        properties
                                .craftRemainder(
                                        Items.GLASS_BOTTLE
                                )
                                .stacksTo(
                                        16
                                )
                                .component(
                                        JolCraftDataComponents.FLUID_CONTENT.get(),
                                        SimpleFluidContent.copyOf(
                                                createYeastFluid(
                                                        fluid.get()
                                                )
                                        )
                                ),
                        JolCraftLanguageKeys.TOOLTIP_YEAST
                )
        );
    }

    public static DeferredItem<Item> registerGlassMug() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.GLASS_MUG,
                properties -> new SimpleTooltipItem(
                        properties.stacksTo(
                                16
                        ),
                        JolCraftLanguageKeys.TOOLTIP_GLASS_MUG
                )
        );
    }

    public static DeferredItem<Item> registerDwarvenBrew(
            DeferredItem<Item> glassMug,
            Supplier<? extends Fluid> fluid
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.DWARVEN_BREW,
                properties -> new DwarvenBrewItem(
                        properties.food(
                                        JolCraftFoodProperties.DWARVEN_BREW
                                )
                                .craftRemainder(
                                        glassMug.get()
                                )
                                .stacksTo(
                                        1
                                )
                                .component(
                                        JolCraftDataComponents.FLUID_CONTENT.get(),
                                        SimpleFluidContent.copyOf(
                                                createBrewFluid(
                                                        fluid.get(),
                                                        DwarvenBrewFluidHelper.MUG_VOLUME
                                                )
                                        )
                                )
                )
        );
    }

    public static DeferredItem<Item> registerDwarvenBrewBucket(
            Supplier<? extends Fluid> fluid
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.DWARVEN_BREW_BUCKET,
                properties -> new DwarvenBrewBucketItem(
                        properties.craftRemainder(
                                        Items.BUCKET
                                )
                                .stacksTo(
                                        1
                                )
                                .component(
                                        JolCraftDataComponents.FLUID_CONTENT.get(),
                                        SimpleFluidContent.copyOf(
                                                createBrewFluid(
                                                        fluid.get(),
                                                        FluidType.BUCKET_VOLUME
                                                )
                                        )
                                )
                )
        );
    }

    private static FluidStack createYeastFluid(
            Fluid fluid
    ) {
        FluidStack yeast =
                new FluidStack(
                        fluid,
                        YEAST_BOTTLE_VOLUME
                );

        yeast.set(
                JolCraftDataComponents.BREW_COLOR.get(),
                BrewingColors.YEAST
        );

        return yeast;
    }

    private static FluidStack createBrewFluid(
            Fluid fluid,
            int amount
    ) {
        FluidStack brew =
                new FluidStack(
                        fluid,
                        amount
                );

        brew.set(
                JolCraftDataComponents.BREW_COLOR.get(),
                BrewingColors.DWARVEN_BREW
        );

        brew.set(
                JolCraftDataComponents.BREW_AGE.get(),
                0L
        );

        brew.set(
                DataComponents.POTION_CONTENTS,
                PotionContents.EMPTY
        );

        return brew;
    }
}