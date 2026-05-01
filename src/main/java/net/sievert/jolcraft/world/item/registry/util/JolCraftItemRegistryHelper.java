package net.sievert.jolcraft.world.item.registry.util;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.world.item.JolCraftItems;

import java.util.function.Function;

public final class JolCraftItemRegistryHelper {

    private JolCraftItemRegistryHelper() {}

    public static <I extends Item> DeferredItem<I> registerItem(
            String name,
            Function<Item.Properties, ? extends I> factory
    ) {
        return JolCraftItems.ITEMS.registerItem(name, factory);
    }

    public static <I extends Item> DeferredItem<I> registerItem(
            String name,
            Function<Item.Properties, ? extends I> factory,
            Item.Properties properties
    ) {
        return JolCraftItems.ITEMS.registerItem(name, factory, properties);
    }

    public static DeferredItem<Item> registerItem(
            String name,
            Item.Properties properties
    ) {
        return JolCraftItems.ITEMS.registerItem(name, Item::new, properties);
    }

    public static DeferredItem<Item> registerSimpleItem(String name) {
        return JolCraftItems.ITEMS.registerSimpleItem(name);
    }

    public static Item.Properties mithrilProperties(Item.Properties props) {
        return props.fireResistant().rarity(Rarity.RARE);
    }

    public static <I extends Item> DeferredItem<I> registerMithrilItem(
            String name,
            Function<Item.Properties, ? extends I> factory
    ) {
        return registerItem(
                name,
                props -> factory.apply(mithrilProperties(props))
        );
    }
}