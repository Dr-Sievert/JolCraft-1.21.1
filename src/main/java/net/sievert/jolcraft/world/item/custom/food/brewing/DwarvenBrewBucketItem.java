package net.sievert.jolcraft.world.item.custom.food.brewing;

import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.material.Fluid;

public final class DwarvenBrewBucketItem extends BucketItem {

    public DwarvenBrewBucketItem(
            Fluid fluid,
            Item.Properties properties
    ) {
        super(
                fluid,
                properties
        );
    }
}