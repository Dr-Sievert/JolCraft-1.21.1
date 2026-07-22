package net.sievert.jolcraft.world.item.equipment;

import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;

public record JolCraftArmorItemSet(
        DeferredItem<Item> helmet,
        DeferredItem<Item> chestplate,
        DeferredItem<Item> leggings,
        DeferredItem<Item> boots
) {

    public static JolCraftArmorItemSet of(
            DeferredItem<Item> helmet,
            DeferredItem<Item> chestplate,
            DeferredItem<Item> leggings,
            DeferredItem<Item> boots
    ) {
        return new JolCraftArmorItemSet(helmet, chestplate, leggings, boots);
    }

    public DeferredItem<Item> get(ArmorItem.Type type) {
        return switch (type) {
            case HELMET -> helmet;
            case CHESTPLATE -> chestplate;
            case LEGGINGS -> leggings;
            case BOOTS -> boots;
            case BODY -> throw new IllegalArgumentException("Unsupported armor type: " + type);
        };
    }
}