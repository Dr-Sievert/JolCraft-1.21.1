package net.sievert.jolcraft.world.item.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.custom.food.brewing.DwarvenBrewBucketItem;
import net.sievert.jolcraft.world.item.custom.food.brewing.DwarvenBrewItem;
import net.sievert.jolcraft.world.item.custom.tooltip.SimpleTooltipItem;
import net.sievert.jolcraft.world.item.food.JolCraftFoodProperties;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

import java.util.function.Supplier;

public final class JolCraftBrewingItems {

    private JolCraftBrewingItems() {}

    public static DeferredItem<Item> registerBarleyMalt() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.BARLEY_MALT,
                props -> new SimpleTooltipItem(props, JolCraftLanguageKeys.TOOLTIP_MALT)
        );
    }

    public static DeferredItem<Item> registerYeast() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.YEAST,
                props -> new SimpleTooltipItem(props.stacksTo(16), JolCraftLanguageKeys.TOOLTIP_YEAST)
        );
    }

    public static DeferredItem<Item> registerGlassMug() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.GLASS_MUG,
                props -> new SimpleTooltipItem(props.stacksTo(16), JolCraftLanguageKeys.TOOLTIP_GLASS_MUG)
        );
    }

    public static DeferredItem<Item> registerDwarvenBrew(DeferredItem<Item> glassMug) {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.DWARVEN_BREW,
                props -> new DwarvenBrewItem(
                        props.food(JolCraftFoodProperties.DWARVEN_BREW)
                                .craftRemainder(glassMug.get())
                                .stacksTo(1)
                )
        );
    }

    public static DeferredItem<Item> registerDwarvenBrewBucket(
            Supplier<? extends Fluid> fluid
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.DWARVEN_BREW_BUCKET,
                props -> new DwarvenBrewBucketItem(
                        fluid.get(),
                        props.craftRemainder(
                                        Items.BUCKET
                                )
                                .stacksTo(1)
                )
        );
    }
}