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

public final class JolCraftAttributes {

    private JolCraftAttributes() {}

    public static final DeferredRegister<Attribute> ATTRIBUTES =
            DeferredRegister.create(Registries.ATTRIBUTE, JolCraft.MOD_ID);

    private static DeferredHolder<Attribute, Attribute> registerPercent(String id, double max) {
        return ATTRIBUTES.register(id, () ->
                new PercentageAttribute(AbstractLanguageKeys.attribute(id), 0.0D, 0.0D, max)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.POSITIVE)
        );
    }

    @SuppressWarnings("SameParameterValue")
    private static DeferredHolder<Attribute, Attribute> registerInt(String id, int max) {
        return ATTRIBUTES.register(id, () ->
                new RangedAttribute(AbstractLanguageKeys.attribute(id), 0.0D, 0.0D, max)
                        .setSyncable(true).setSentiment(Attribute.Sentiment.POSITIVE)
        );
    }

    public static final DeferredHolder<Attribute, Attribute> EXPERIENCE_INCREASE =
            registerPercent(JolCraftAttributeIds.EXPERIENCE_INCREASE, 10.0D);

    public static final DeferredHolder<Attribute, Attribute> SLOW_RESISTANCE =
            registerPercent(JolCraftAttributeIds.SLOW_RESISTANCE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> CROP_LOOT_INCREASE =
            registerPercent(JolCraftAttributeIds.CROP_LOOT_INCREASE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> CONTAINER_LOOT_INCREASE =
            registerPercent(JolCraftAttributeIds.CONTAINER_LOOT_INCREASE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> RADIANT =
            registerInt(JolCraftAttributeIds.RADIANT, 4);

    public static final DeferredHolder<Attribute, Attribute> ARMOR_PENETRATION =
            registerPercent(JolCraftAttributeIds.ARMOR_PENETRATION, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> MAGIC_RESISTANCE =
            registerPercent(JolCraftAttributeIds.MAGIC_RESISTANCE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> ARMOR_TOTAL =
            registerPercent(JolCraftAttributeIds.ARMOR_TOTAL, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> ATTACK_DAMAGE_INCREASE =
            registerPercent(JolCraftAttributeIds.ATTACK_DAMAGE_INCREASE, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> ITEM_USE_SPEED =
            registerPercent(JolCraftAttributeIds.ITEM_USE_SPEED, 1.0D);

    public static final DeferredHolder<Attribute, Attribute> MOON_SHIELD =
            registerInt(JolCraftAttributeIds.MOON_SHIELD, 4);

    public static void register(IEventBus eventBus) {
        ATTRIBUTES.register(eventBus);

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued {} attributes",
                ATTRIBUTES.getEntries().size()
        );
    }
}
