package net.sievert.jolcraft.world.item.food;

import net.minecraft.world.food.FoodProperties;

public final class JolCraftFoodProperties {

    private JolCraftFoodProperties() {
    }

    public static final FoodProperties DWARVEN_BREW = new FoodProperties.Builder()
            .alwaysEdible()
            .nutrition(3)
            .saturationModifier(0.25f)
            .build();
}