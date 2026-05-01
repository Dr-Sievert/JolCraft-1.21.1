package net.sievert.jolcraft.world.item.registry;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.world.item.custom.scrapper.SalvageItem;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

public final class JolCraftSalvageItems {

    private JolCraftSalvageItems() {}

    public static DeferredItem<Item> registerScrap() {
        return JolCraftItemRegistryHelper.registerSimpleItem(JolCraftItemIds.SCRAP);
    }

    public static DeferredItem<Item> registerScrapHeap() {
        return JolCraftItemRegistryHelper.registerSimpleItem(JolCraftItemIds.SCRAP_HEAP);
    }

    public static DeferredItem<Item> registerBrokenPickaxe() {
        return registerSalvage(JolCraftItemIds.BROKEN_PICKAXE, new Item.Properties().stacksTo(1));
    }

    public static DeferredItem<Item> registerBrokenAmulet() {
        return registerSalvage(JolCraftItemIds.BROKEN_AMULET, new Item.Properties().stacksTo(1));
    }

    public static DeferredItem<Item> registerBrokenBelt() {
        return registerSalvage(JolCraftItemIds.BROKEN_BELT, new Item.Properties().stacksTo(1));
    }

    public static DeferredItem<Item> registerBrokenCoins() {
        return registerSalvage(JolCraftItemIds.BROKEN_COINS, new Item.Properties());
    }

    public static DeferredItem<Item> registerDeepslateMug() {
        return registerSalvage(JolCraftItemIds.DEEPSLATE_MUG, new Item.Properties().stacksTo(16));
    }

    public static DeferredItem<Item> registerExpiredPotion() {
        return registerSalvage(JolCraftItemIds.EXPIRED_POTION, new Item.Properties().stacksTo(16));
    }

    public static DeferredItem<Item> registerIngotMould() {
        return registerSalvage(JolCraftItemIds.INGOT_MOULD, new Item.Properties().stacksTo(16));
    }

    public static DeferredItem<Item> registerMithrilScrap() {
        return registerSalvage(JolCraftItemIds.MITHRIL_SCRAP, JolCraftItemRegistryHelper.mithrilProperties(new Item.Properties()));
    }

    public static DeferredItem<Item> registerOldFabric() {
        return registerSalvage(JolCraftItemIds.OLD_FABRIC, new Item.Properties());
    }

    public static DeferredItem<Item> registerRustyTongs() {
        return registerSalvage(JolCraftItemIds.RUSTY_TONGS, new Item.Properties().stacksTo(1));
    }

    public static DeferredItem<Item> registerBrokenMithrilSword() {
        return registerSalvage(
                JolCraftItemIds.BROKEN_MITHRIL_SWORD,
                JolCraftItemRegistryHelper.mithrilProperties(new Item.Properties().stacksTo(1))
        );
    }

    public static DeferredItem<Item> registerBrokenTablet() {
        return registerSalvage(JolCraftItemIds.BROKEN_TABLET, new Item.Properties().stacksTo(16));
    }

    public static DeferredItem<Item> registerBrokenDeepslatePlates() {
        return registerSalvage(JolCraftItemIds.BROKEN_DEEPSLATE_PLATES, new Item.Properties());
    }

    public static DeferredItem<Item> registerBrokenMithrilPlate() {
        return registerSalvage(
                JolCraftItemIds.BROKEN_MITHRIL_PLATE,
                JolCraftItemRegistryHelper.mithrilProperties(new Item.Properties())
        );
    }

    public static DeferredItem<Item> registerBrokenDeepslateGear() {
        return registerSalvage(JolCraftItemIds.BROKEN_DEEPSLATE_GEAR, new Item.Properties());
    }

    public static DeferredItem<Item> registerBrokenDeepslatePickaxeHead() {
        return registerSalvage(JolCraftItemIds.BROKEN_DEEPSLATE_PICKAXE_HEAD, new Item.Properties());
    }

    private static DeferredItem<Item> registerSalvage(String id, Item.Properties properties) {
        return JolCraftItemRegistryHelper.registerItem(id, SalvageItem::new, properties);
    }
}