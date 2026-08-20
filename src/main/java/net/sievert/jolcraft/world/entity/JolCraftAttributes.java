package net.sievert.jolcraft.world.entity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.PercentageAttribute;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.attribute.JolCraftAttributeIds;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;

@SuppressWarnings({"unused", "SameParameterValue"})
public final class JolCraftAttributes {

    private JolCraftAttributes() {}

    public static final DeferredRegister<Attribute> ATTRIBUTES = DeferredRegister.create(Registries.ATTRIBUTE, JolCraft.MOD_ID);

    // Positive

    public static final DeferredHolder<Attribute, Attribute> EXPERIENCE_INCREASE =
            registerPositivePercentage(JolCraftAttributeIds.EXPERIENCE_INCREASE, 2048.0D);

    public static final DeferredHolder<Attribute, Attribute> SLOW_RESISTANCE =
            registerPositivePercentage(JolCraftAttributeIds.SLOW_RESISTANCE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> CROP_LOOT_INCREASE =
            registerPositivePercentage(JolCraftAttributeIds.CROP_LOOT_INCREASE, 2048.0D);

    public static final DeferredHolder<Attribute, Attribute> CONTAINER_LOOT_INCREASE =
            registerPositivePercentage(JolCraftAttributeIds.CONTAINER_LOOT_INCREASE, 2048.0D);

    public static final DeferredHolder<Attribute, Attribute> LUMINANCE =
            registerPositiveDouble(JolCraftAttributeIds.LUMINANCE, 6);

    public static final DeferredHolder<Attribute, Attribute> ARMOR_PENETRATION =
            registerPositivePercentage(JolCraftAttributeIds.ARMOR_PENETRATION, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> MAGIC_RESISTANCE =
            registerPositivePercentage(JolCraftAttributeIds.MAGIC_RESISTANCE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> FIRE_RESISTANCE =
            registerPositivePercentage(JolCraftAttributeIds.FIRE_RESISTANCE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> EXPLOSION_RESISTANCE =
            registerPositivePercentage(JolCraftAttributeIds.EXPLOSION_RESISTANCE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> POISON_RESISTANCE =
            registerPositivePercentage(JolCraftAttributeIds.POISON_RESISTANCE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> FROST_RESISTANCE =
            registerPositivePercentage(JolCraftAttributeIds.FROST_RESISTANCE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> WITHER_RESISTANCE =
            registerPositivePercentage(JolCraftAttributeIds.WITHER_RESISTANCE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> CROWD_CONTROL_REDUCTION =
            registerPositivePercentage(JolCraftAttributeIds.CROWD_CONTROL_REDUCTION, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> FOCUS =
            registerPositivePercentage(JolCraftAttributeIds.FOCUS, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> ITEM_USE_SPEED =
            registerPositivePercentage(JolCraftAttributeIds.ITEM_USE_SPEED, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> MOON_SHIELD =
            registerPositiveDouble(JolCraftAttributeIds.MOON_SHIELD, 6);

    public static final DeferredHolder<Attribute, Attribute> PROJECTILE_DAMAGE =
            registerPositiveDouble(JolCraftAttributeIds.PROJECTILE_DAMAGE, 2048.0D);

    public static final DeferredHolder<Attribute, Attribute> LOCKPICKING =
            registerPositiveDouble(JolCraftAttributeIds.LOCKPICKING, 2048.0D);

    public static final DeferredHolder<Attribute, Attribute> SUN_FIRE_DAMAGE =
            registerPositiveDouble(JolCraftAttributeIds.SUN_FIRE_DAMAGE, 2048.0D);

    public static final DeferredHolder<Attribute, Attribute> MAX_OVERHEAL =
            registerPositivePercentage(JolCraftAttributeIds.MAX_OVERHEAL, 1.0D);

    // Negative

    public static final DeferredHolder<Attribute, Attribute> CURSE_VULNERABILITY =
            registerNegativeDouble(JolCraftAttributeIds.CURSE_VULNERABILITY, 2048.0D);

    public static final DeferredHolder<Attribute, Attribute> EXPLOSION_VULNERABILITY =
            registerNegativePercentage(JolCraftAttributeIds.EXPLOSION_VULNERABILITY, 2048.0D);

    public static final DeferredHolder<Attribute, Attribute> FIRE_VULNERABILITY =
            registerNegativePercentage(JolCraftAttributeIds.FIRE_VULNERABILITY, 2048.0D);

    public static final DeferredHolder<Attribute, Attribute> FROST_VULNERABILITY =
            registerNegativePercentage(JolCraftAttributeIds.FROST_VULNERABILITY, 2048.0D);

    public static final DeferredHolder<Attribute, Attribute> MAGIC_VULNERABILITY =
            registerNegativePercentage(JolCraftAttributeIds.MAGIC_VULNERABILITY, 2048.0D);

    public static final DeferredHolder<Attribute, Attribute> POISON_VULNERABILITY =
            registerNegativePercentage(JolCraftAttributeIds.POISON_VULNERABILITY, 2048.0D);

    public static final DeferredHolder<Attribute, Attribute> SLOW_VULNERABILITY =
            registerNegativePercentage(JolCraftAttributeIds.SLOW_VULNERABILITY, 2048.0D);

    public static final DeferredHolder<Attribute, Attribute> WITHER_VULNERABILITY =
            registerNegativePercentage(JolCraftAttributeIds.WITHER_VULNERABILITY, 2048.0D);

    // Helpers

    private static DeferredHolder<Attribute, Attribute> registerPositivePercentage(String id, double max) {
        return ATTRIBUTES.register(id, () ->
                new PercentageAttribute(AbstractLanguageKeys.attribute(id), 0.0D, 0.0D, max)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.POSITIVE)
        );
    }

    private static DeferredHolder<Attribute, Attribute> registerPositiveDouble(String id, double max) {
        return ATTRIBUTES.register(id, () ->
                new RangedAttribute(AbstractLanguageKeys.attribute(id), 0.0D, 0.0D, max)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.POSITIVE)
        );
    }

    private static DeferredHolder<Attribute, Attribute> registerNegativePercentage(String id, double max) {
        return ATTRIBUTES.register(id, () ->
                new PercentageAttribute(AbstractLanguageKeys.attribute(id), 0.0D, 0.0D, max)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.NEGATIVE)
        );
    }

    @SuppressWarnings("SameParameterValue")
    private static DeferredHolder<Attribute, Attribute> registerNegativeDouble(String id, double max) {
        return ATTRIBUTES.register(id, () ->
                new RangedAttribute(AbstractLanguageKeys.attribute(id), 0.0D, 0.0D, max)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.NEGATIVE)
        );
    }

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued {} attributes",
                ATTRIBUTES.getEntries().size()
        );
    }
}
