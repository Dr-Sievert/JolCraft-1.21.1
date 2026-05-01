package net.sievert.jolcraft.world.item.custom.tool;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;

public class ToolItem extends Item {

    protected final Tier tier;

    public ToolItem(Tier tier, Item.Properties properties) {
        super(properties.durability(tier.getUses()));
        this.tier = tier;
    }

    public Tier tier() {
        return this.tier;
    }
}