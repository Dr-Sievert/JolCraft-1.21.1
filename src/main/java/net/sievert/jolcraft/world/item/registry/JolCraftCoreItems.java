package net.sievert.jolcraft.world.item.registry;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.component.ItemContainerContents;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.JolCraftEnumExtensions;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.custom.compass.DeepslateCompassDialItem;
import net.sievert.jolcraft.world.item.custom.compass.DeepslateCompassItem;
import net.sievert.jolcraft.world.item.custom.tablet.ReputationTabletItem;
import net.sievert.jolcraft.world.item.custom.container.CoinPouchItem;
import net.sievert.jolcraft.world.item.custom.container.strongbox.StrongboxItem;
import net.sievert.jolcraft.world.item.custom.tooltip.SimpleTooltipItem;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

public final class JolCraftCoreItems {

    private JolCraftCoreItems() {}

    public static DeferredItem<Item> registerDevKey() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.DEV_KEY,
                props -> new SimpleTooltipItem(
                        props.rarity(Rarity.EPIC).stacksTo(1),
                        JolCraftLanguageKeys.TOOLTIP_DEV_KEY
                )
        );
    }

    public static DeferredItem<Item> registerGoldCoin() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.GOLD_COIN,
                Item::new,
                new Item.Properties().rarity(Rarity.UNCOMMON)
        );
    }

    public static DeferredItem<Item> registerCoinPouch() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.COIN_POUCH,
                CoinPouchItem::new,
                new Item.Properties().stacksTo(1)
        );
    }

    public static DeferredItem<BlockItem> registerStrongboxItem() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.STRONGBOX,
                props -> new StrongboxItem(
                        JolCraftBlocks.STRONGBOX.get(),
                        props.stacksTo(1).component(DataComponents.CONTAINER, ItemContainerContents.EMPTY)
                )
        );
    }

    public static DeferredItem<Item> registerLockpick() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.LOCKPICK,
                props -> new SimpleTooltipItem(props, JolCraftLanguageKeys.TOOLTIP_LOCKPICK)
        );
    }

    public static DeferredItem<Item> registerEmptyDeepslateCompass() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.EMPTY_DEEPSLATE_COMPASS,
                new Item.Properties().stacksTo(16)
        );
    }

    public static DeferredItem<Item> registerDeepslateCompass() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.DEEPSLATE_COMPASS,
                DeepslateCompassItem::new,
                new Item.Properties().stacksTo(1)
        );
    }

    public static DeferredItem<Item> registerDeepslateCompassDial() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.DEEPSLATE_COMPASS_DIAL,
                DeepslateCompassDialItem::new,
                new Item.Properties().stacksTo(16)
        );
    }

    public static DeferredItem<Item> registerDialDust() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.DIAL_DUST,
                Item::new,
                new Item.Properties().rarity(Rarity.UNCOMMON)
        );
    }

    public static DeferredItem<Item> registerReputationTablet0() {
        return registerReputationTablet(
                JolCraftItemIds.REPUTATION_TABLET_0,
                Rarity.COMMON
        );
    }

    public static DeferredItem<Item> registerReputationTablet1() {
        return registerReputationTablet(
                JolCraftItemIds.REPUTATION_TABLET_1,
                Rarity.UNCOMMON
        );
    }

    public static DeferredItem<Item> registerReputationTablet2() {
        return registerReputationTablet(
                JolCraftItemIds.REPUTATION_TABLET_2,
                Rarity.RARE
        );
    }

    public static DeferredItem<Item> registerReputationTablet3() {
        return registerReputationTablet(
                JolCraftItemIds.REPUTATION_TABLET_3,
                Rarity.EPIC
        );
    }

    public static DeferredItem<Item> registerReputationTablet4() {
        return registerReputationTablet(
                JolCraftItemIds.REPUTATION_TABLET_4,
                JolCraftEnumExtensions.Rarity.LEGENDARY.getValue()
        );
    }

    private static DeferredItem<Item> registerReputationTablet(String id, Rarity rarity) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                ReputationTabletItem::new,
                new Item.Properties()
                        .stacksTo(1)
                        .rarity(rarity)
        );
    }
}