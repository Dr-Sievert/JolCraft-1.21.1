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
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("SameParameterValue")
public final class JolCraftDamageAffinityEventsHelper {

    private static final List<DamageAffinity> AFFINITIES = List.of(
            resistance(
                    Tags.DamageTypes.IS_MAGIC,
                    JolCraftAttributes.MAGIC_RESISTANCE,
                    Tags.DamageTypes.IS_POISON
            ),
            resistance(
                    Tags.DamageTypes.IS_POISON,
                    JolCraftAttributes.POISON_RESISTANCE
            ),
            resistance(
                    DamageTypeTags.IS_FREEZING,
                    JolCraftAttributes.FROST_RESISTANCE
            ),
            resistance(
                    Tags.DamageTypes.IS_WITHER,
                    JolCraftAttributes.WITHER_RESISTANCE
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
        double resistance =
                attributeValue(
                        entity,
                        affinity.resistance(),
                        1.0D
                );

        double vulnerability =
                attributeValue(
                        entity,
                        affinity.vulnerability(),
                        Double.MAX_VALUE
                );

        if (resistance <= 0.0D
                && vulnerability <= 0.0D) {
            return damage;
        }

        float modified =
                (float) (
                        damage
                                * (1.0D + vulnerability)
                                * (1.0D - resistance)
                );

        Object damageTypeId =
                source.typeHolder()
                        .unwrapKey()
                        .map(ResourceKey::location)
                        .orElse(null);

        Object resistanceId =
                affinity.resistance() != null
                        ? affinity.resistance()
                        .unwrapKey()
                        .map(ResourceKey::location)
                        .orElse(null)
                        : null;

        Object vulnerabilityId =
                affinity.vulnerability() != null
                        ? affinity.vulnerability()
                        .unwrapKey()
                        .map(ResourceKey::location)
                        .orElse(null)
                        : null;

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
            @Nullable Holder<Attribute> attribute,
            double max
    ) {
        if (attribute == null) {
            return 0.0D;
        }

        return Mth.clamp(
                entity.getAttributeValue(attribute),
                0.0D,
                max
        );
    }

    @SafeVarargs
    private static DamageAffinity resistance(
            TagKey<DamageType> damageTypeTag,
            Holder<Attribute> resistance,
            TagKey<DamageType>... excludedTags
    ) {
        return new DamageAffinity(
                damageTypeTag,
                List.of(excludedTags),
                resistance,
                null
        );
    }

    @SafeVarargs
    @SuppressWarnings("unused")
    private static DamageAffinity vulnerability(
            TagKey<DamageType> damageTypeTag,
            Holder<Attribute> vulnerability,
            TagKey<DamageType>... excludedTags
    ) {
        return new DamageAffinity(
                damageTypeTag,
                List.of(excludedTags),
                null,
                vulnerability
        );
    }

    @SafeVarargs
    @SuppressWarnings("unused")
    private static DamageAffinity affinity(
            TagKey<DamageType> damageTypeTag,
            @Nullable Holder<Attribute> resistance,
            @Nullable Holder<Attribute> vulnerability,
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
            @Nullable Holder<Attribute> resistance,
            @Nullable Holder<Attribute> vulnerability
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