package net.sievert.jolcraft.world.item.food;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

public class JolCraftFoodProperties {

    //Edibles
    public static final FoodProperties DWARVEN_BREW = new FoodProperties.Builder().alwaysEdible().nutrition(3).saturationModifier(0.25f).build();

    //Effects
    public static final Consumable DWARVEN_BREW_EFFECT = Consumables.defaultDrink()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 1.0f))
            .build();

    public static final Consumable DEEPSLATE_BULBS_EFFECT = Consumables.defaultFood()
            .onConsume(new ApplyStatusEffectsConsumeEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 100, 0), 1.0f))
            .build();


}
