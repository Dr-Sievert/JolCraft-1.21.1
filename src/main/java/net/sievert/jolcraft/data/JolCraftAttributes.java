package net.sievert.jolcraft.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.JolCraft;

public final class JolCraftAttributes {

    private JolCraftAttributes(){}

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, JolCraft.MOD_ID);

    public static final DeferredHolder<Attribute, Attribute> XP_BOOST =
            ATTRIBUTES.register("xp_boost", () ->
                    new PercentageAttribute("attribute.jolcraft.xp_boost", 0.0D, 0.0D, 10.0D)
                            .setSyncable(true)
            );

    public static final DeferredHolder<Attribute, Attribute> SLOW_RESISTANCE =
            ATTRIBUTES.register("slow_resistance", () ->
                    new PercentageAttribute("attribute.jolcraft.slow_resistance", 0.0D, 0.0D, 1.0D)
                            .setSyncable(true)
            );

    public static final DeferredHolder<Attribute, Attribute> EXTRA_CROP =
            ATTRIBUTES.register("extra_crop", () ->
                    new PercentageAttribute("attribute.jolcraft.extra_crop", 0.0D, 0.0D, 1.0D)
                            .setSyncable(true)
            );

    public static final DeferredHolder<Attribute, Attribute> EXTRA_CHEST_LOOT =
            ATTRIBUTES.register("extra_chest_loot", () ->
                    new PercentageAttribute("attribute.jolcraft.extra_chest_loot", 0.0D, 0.0D, 1.0D)
                            .setSyncable(true)
            );

    public static final DeferredHolder<Attribute, Attribute> RADIANT =
            ATTRIBUTES.register("radiant", () ->
                    new PercentageAttribute("attribute.jolcraft.radiant", 0.0D, 0.0D, 1.0D)
                            .setSyncable(true)
            );

    public static final DeferredHolder<Attribute, Attribute> ARMOR_UNBREAKING =
            ATTRIBUTES.register("armor_unbreaking", () ->
                    new PercentageAttribute("attribute.jolcraft.armor_unbreaking", 0.0D, 0.0D, 1.0D)
                            .setSyncable(true)
            );

    public static final DeferredHolder<Attribute, Attribute> MAGIC_RESISTANCE =
            ATTRIBUTES.register("magic_resistance", () ->
                    new PercentageAttribute("attribute.jolcraft.magic_resistance", 0.0D, 0.0D, 1.0D)
                            .setSyncable(true)
            );

    public static final DeferredHolder<Attribute, Attribute> ARMOR_INCREASE =
            ATTRIBUTES.register("armor_increase", () ->
                    new PercentageAttribute("attribute.jolcraft.armor_increase", 0.0D, 0.0D, 1.0D)
                            .setSyncable(true)
            );

    public static final DeferredHolder<Attribute, Attribute> ATTACK_DAMAGE_INCREASE =
            ATTRIBUTES.register("attack_damage_increase", () ->
                    new PercentageAttribute("attribute.jolcraft.attack_damage_increase", 0.0D, 0.0D, 1.0D)
                            .setSyncable(true)
            );

    public static final DeferredHolder<Attribute, Attribute> MOVEMENT_SPEED_BOOST_DAY =
            ATTRIBUTES.register("movement_speed_boost_day", () ->
                    new PercentageAttribute("attribute.jolcraft.movement_speed_boost_day", 0.0D, 0.0D, 1.0D)
                            .setSyncable(true)
            );

    public static final DeferredHolder<Attribute, Attribute> MOVEMENT_SPEED_BOOST_NIGHT =
            ATTRIBUTES.register("movement_speed_boost_night", () ->
                    new PercentageAttribute("attribute.jolcraft.movement_speed_boost_night", 0.0D, 0.0D, 1.0D)
                            .setSyncable(true)
            );

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
