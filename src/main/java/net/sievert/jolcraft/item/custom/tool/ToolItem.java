package net.sievert.jolcraft.item.custom.tool;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ToolMaterial;

public class ToolItem extends Item {

    public ToolItem(ToolMaterial material, Item.Properties properties) {
        super(properties
                .durability(material.durability())
                .enchantable(material.enchantmentValue())
                .repairable(material.repairItems())
        );
    }
}
