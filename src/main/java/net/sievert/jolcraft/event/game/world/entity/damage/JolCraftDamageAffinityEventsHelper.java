package net.sievert.jolcraft.event.game.world.entity.damage;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;

import java.util.List;

public final class JolCraftDamageAffinityEventsHelper {

    private static final double IMMUNE_RESISTANCE = 1.0D;
    private static final double RESISTANT_RESISTANCE = 0.5D;
    private static final double VULNERABLE_VULNERABILITY = 0.5D;

    private static final List<DamageAffinity> AFFINITIES = List.of(
            affinity(
                    DamageTypeTags.IS_EXPLOSION,
                    JolCraftAttributes.EXPLOSION_RESISTANCE,
                    JolCraftAttributes.EXPLOSION_VULNERABILITY,
                    JolCraftTags.EntityTypes.EXPLOSION_IMMUNE,
                    JolCraftTags.EntityTypes.EXPLOSION_RESISTANT,
                    JolCraftTags.EntityTypes.EXPLOSION_VULNERABLE
            ),
            affinity(
                    DamageTypeTags.IS_FIRE,
                    JolCraftAttributes.FIRE_RESISTANCE,
                    JolCraftAttributes.FIRE_VULNERABILITY,
                    JolCraftTags.EntityTypes.FIRE_IMMUNE,
                    JolCraftTags.EntityTypes.FIRE_RESISTANT,
                    JolCraftTags.EntityTypes.FIRE_VULNERABLE
            ),
            affinity(
                    DamageTypeTags.IS_FREEZING,
                    JolCraftAttributes.FROST_RESISTANCE,
                    JolCraftAttributes.FROST_VULNERABILITY,
                    JolCraftTags.EntityTypes.FROST_IMMUNE,
                    JolCraftTags.EntityTypes.FROST_RESISTANT,
                    JolCraftTags.EntityTypes.FROST_VULNERABLE
            ),
            affinity(
                    Tags.DamageTypes.IS_MAGIC,
                    JolCraftAttributes.MAGIC_RESISTANCE,
                    JolCraftAttributes.MAGIC_VULNERABILITY,
                    JolCraftTags.EntityTypes.MAGIC_IMMUNE,
                    JolCraftTags.EntityTypes.MAGIC_RESISTANT,
                    JolCraftTags.EntityTypes.MAGIC_VULNERABLE,
                    Tags.DamageTypes.IS_POISON
            ),
            affinity(
                    Tags.DamageTypes.IS_POISON,
                    JolCraftAttributes.POISON_RESISTANCE,
                    JolCraftAttributes.POISON_VULNERABILITY,
                    JolCraftTags.EntityTypes.POISON_IMMUNE,
                    JolCraftTags.EntityTypes.POISON_RESISTANT,
                    JolCraftTags.EntityTypes.POISON_VULNERABLE
            ),
            affinity(
                    Tags.DamageTypes.IS_WITHER,
                    JolCraftAttributes.WITHER_RESISTANCE,
                    JolCraftAttributes.WITHER_VULNERABILITY,
                    JolCraftTags.EntityTypes.WITHER_IMMUNE,
                    JolCraftTags.EntityTypes.WITHER_RESISTANT,
                    JolCraftTags.EntityTypes.WITHER_VULNERABLE
            )
    );

    private JolCraftDamageAffinityEventsHelper() {}

    public static void applyEntityTypeAffinityModifiers(LivingEntity entity) {
        EntityType<?> type = entity.getType();

        for (DamageAffinity affinity : AFFINITIES) {
            double resistance = type.is(affinity.immuneTag())
                    ? IMMUNE_RESISTANCE
                    : type.is(affinity.resistantTag())
                    ? RESISTANT_RESISTANCE
                    : 0.0D;

            double vulnerability = type.is(affinity.vulnerableTag())
                    ? VULNERABLE_VULNERABILITY
                    : 0.0D;

            updateModifier(
                    entity,
                    affinity.resistance(),
                    affinity.resistanceModifierId(),
                    resistance
            );

            updateModifier(
                    entity,
                    affinity.vulnerability(),
                    affinity.vulnerabilityModifierId(),
                    vulnerability
            );
        }
    }

    public static void apply(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        float damage = event.getNewDamage();

        for (DamageAffinity affinity : AFFINITIES) {
            if (!affinity.matches(source)) {
                continue;
            }

            damage = applyAffinity(
                    entity,
                    source,
                    affinity,
                    damage
            );
        }

        event.setNewDamage(
                Math.max(0.0F, damage)
        );
    }

    private static void updateModifier(
            LivingEntity entity,
            Holder<Attribute> attribute,
            ResourceLocation modifierId,
            double amount
    ) {
        AttributeInstance instance = entity.getAttribute(attribute);

        if (instance == null) {
            return;
        }

        if (amount <= 0.0D) {
            instance.removeModifier(modifierId);
            return;
        }

        instance.addOrUpdateTransientModifier(
                new AttributeModifier(
                        modifierId,
                        amount,
                        AttributeModifier.Operation.ADD_VALUE
                )
        );
    }

    private static float applyAffinity(
            LivingEntity entity,
            DamageSource source,
            DamageAffinity affinity,
            float damage
    ) {
        double resistance = attributeValue(
                entity,
                affinity.resistance(),
                1.0D
        );

        double vulnerability = attributeValue(
                entity,
                affinity.vulnerability(),
                Double.MAX_VALUE
        );

        if (resistance <= 0.0D && vulnerability <= 0.0D) {
            return damage;
        }

        float modified = (float) (
                damage
                        * (1.0D + vulnerability)
                        * (1.0D - resistance)
        );

        Object damageTypeId = source.typeHolder()
                .unwrapKey()
                .map(ResourceKey::location)
                .orElse(null);

        Object resistanceId = affinity.resistance()
                .unwrapKey()
                .map(ResourceKey::location)
                .orElse(null);

        Object vulnerabilityId = affinity.vulnerability()
                .unwrapKey()
                .map(ResourceKey::location)
                .orElse(null);

        JolCraftLogs.debug(
                JolCraftLogTags.ENTITY,
                "Entity {} received modified {} damage from tag {} through resistance {} at {}% and vulnerability {} at {}% from {} to {} damage",
                entity.getName().getString(),
                damageTypeId,
                affinity.damageTypeTag().location(),
                resistanceId,
                JolCraftLogs.pct1(resistance),
                vulnerabilityId,
                JolCraftLogs.pct1(vulnerability),
                damage,
                modified
        );

        return modified;
    }

    private static double attributeValue(
            LivingEntity entity,
            Holder<Attribute> attribute,
            double max
    ) {
        return Mth.clamp(
                entity.getAttributeValue(attribute),
                0.0D,
                max
        );
    }

    @SafeVarargs
    private static DamageAffinity affinity(
            TagKey<DamageType> damageTypeTag,
            Holder<Attribute> resistance,
            Holder<Attribute> vulnerability,
            TagKey<EntityType<?>> immuneTag,
            TagKey<EntityType<?>> resistantTag,
            TagKey<EntityType<?>> vulnerableTag,
            TagKey<DamageType>... excludedTags
    ) {
        String path = damageTypeTag.location().getPath();

        return new DamageAffinity(
                damageTypeTag,
                List.of(excludedTags),
                resistance,
                vulnerability,
                immuneTag,
                resistantTag,
                vulnerableTag,
                JolCraft.location("entity_affinity/" + path + "_resistance"),
                JolCraft.location("entity_affinity/" + path + "_vulnerability")
        );
    }

    private record DamageAffinity(
            TagKey<DamageType> damageTypeTag,
            List<TagKey<DamageType>> excludedTags,
            Holder<Attribute> resistance,
            Holder<Attribute> vulnerability,
            TagKey<EntityType<?>> immuneTag,
            TagKey<EntityType<?>> resistantTag,
            TagKey<EntityType<?>> vulnerableTag,
            ResourceLocation resistanceModifierId,
            ResourceLocation vulnerabilityModifierId
    ) {

        private boolean matches(DamageSource source) {
            if (!source.is(damageTypeTag)) {
                return false;
            }

            for (TagKey<DamageType> excludedTag : excludedTags) {
                if (source.is(excludedTag)) {
                    return false;
                }
            }

            return true;
        }
    }
}