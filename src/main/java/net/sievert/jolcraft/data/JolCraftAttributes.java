package net.sievert.jolcraft.data;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.attribute.JolCraftAttributeIds;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;

public final class JolCraftAttributes {

    private JolCraftAttributes() {}

    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(Registries.ATTRIBUTE, JolCraft.MOD_ID);

    private static DeferredHolder<Attribute, Attribute> registerPercent(String id, double max) {
        return ATTRIBUTES.register(id, () ->
                new PercentageAttribute(AbstractLanguageKeys.attribute(id), 0.0D, 0.0D, max)
                        .setSyncable(true)
        );
    }

    public static final DeferredHolder<Attribute, Attribute> XP_INCREASE =
            registerPercent(JolCraftAttributeIds.XP_INCREASE, 10.0D);

    public static final DeferredHolder<Attribute, Attribute> SLOW_RESISTANCE =
            registerPercent(JolCraftAttributeIds.SLOW_RESISTANCE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> CROP_LOOT_INCREASE =
            registerPercent(JolCraftAttributeIds.CROP_LOOT_INCREASE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> CHEST_LOOT_INCREASE =
            registerPercent(JolCraftAttributeIds.CHEST_LOOT_INCREASE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> RADIANT =
            registerPercent(JolCraftAttributeIds.RADIANT, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> ARMOR_UNBREAKING =
            registerPercent(JolCraftAttributeIds.ARMOR_UNBREAKING, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> MAGIC_RESISTANCE =
            registerPercent(JolCraftAttributeIds.MAGIC_RESISTANCE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> ARMOR_INCREASE =
            registerPercent(JolCraftAttributeIds.ARMOR_INCREASE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> ATTACK_DAMAGE_INCREASE =
            registerPercent(JolCraftAttributeIds.ATTACK_DAMAGE_INCREASE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> MOVEMENT_SPEED_DAY_INCREASE =
            registerPercent(JolCraftAttributeIds.MOVEMENT_SPEED_DAY_INCREASE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> MOVEMENT_SPEED_NIGHT_INCREASE =
            registerPercent(JolCraftAttributeIds.MOVEMENT_SPEED_NIGHT_INCREASE, 1.0D);

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);
    }
}
