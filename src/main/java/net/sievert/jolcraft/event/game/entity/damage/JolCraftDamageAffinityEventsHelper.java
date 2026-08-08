package net.sievert.jolcraft.event.game.entity.damage;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;

import java.util.List;

public final class JolCraftDamageAffinityEventsHelper {

    private static final List<DamageAffinity> AFFINITIES = List.of(
            affinity(
                    Tags.DamageTypes.IS_MAGIC,
                    JolCraftAttributes.MAGIC_RESISTANCE,
                    JolCraftAttributes.MAGIC_VULNERABILITY,
                    Tags.DamageTypes.IS_POISON
            ),
            affinity(
                    DamageTypeTags.IS_FIRE,
                    JolCraftAttributes.FIRE_RESISTANCE,
                    JolCraftAttributes.FIRE_VULNERABILITY
            ),
            affinity(
                    DamageTypeTags.IS_EXPLOSION,
                    JolCraftAttributes.EXPLOSION_RESISTANCE,
                    JolCraftAttributes.EXPLOSION_VULNERABILITY
            ),
            affinity(
                    Tags.DamageTypes.IS_POISON,
                    JolCraftAttributes.POISON_RESISTANCE,
                    JolCraftAttributes.POISON_VULNERABILITY
            ),
            affinity(
                    DamageTypeTags.IS_FREEZING,
                    JolCraftAttributes.FROST_RESISTANCE,
                    JolCraftAttributes.FROST_VULNERABILITY
            ),
            affinity(
                    Tags.DamageTypes.IS_WITHER,
                    JolCraftAttributes.WITHER_RESISTANCE,
                    JolCraftAttributes.WITHER_VULNERABILITY
            )
    );

    private JolCraftDamageAffinityEventsHelper() {}

    public static void apply(LivingDamageEvent.Pre event) {
        LivingEntity entity = event.getEntity();
        DamageSource source = event.getSource();
        float damage = event.getNewDamage();

        for (DamageAffinity affinity : AFFINITIES) {
            if (!affinity.matches(source)) continue;

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
            TagKey<DamageType>... excludedTags
    ) {
        return new DamageAffinity(
                damageTypeTag,
                List.of(excludedTags),
                resistance,
                vulnerability
        );
    }

    private record DamageAffinity(
            TagKey<DamageType> damageTypeTag,
            List<TagKey<DamageType>> excludedTags,
            Holder<Attribute> resistance,
            Holder<Attribute> vulnerability
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