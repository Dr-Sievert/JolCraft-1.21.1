package net.sievert.jolcraft.world.item.custom.food.brewing;

import net.minecraft.world.item.Item;

/**
 * A capability-backed brew container. It intentionally does not extend
 * BucketItem because brew components cannot survive placement as a world fluid.
 */
public final class DwarvenBrewBucketItem extends Item {

    public DwarvenBrewBucketItem(
            Properties properties
    ) {
        super(
                properties
        );
    }
}
