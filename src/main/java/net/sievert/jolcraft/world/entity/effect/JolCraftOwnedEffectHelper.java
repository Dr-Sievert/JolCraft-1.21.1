package net.sievert.jolcraft.world.entity.effect;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.sievert.jolcraft.mixin.MobEffectInstanceAccessor;

import javax.annotation.Nullable;

public final class JolCraftOwnedEffectHelper {

    private JolCraftOwnedEffectHelper() {}

    public static void syncInfinite(
            LivingEntity entity,
            Holder<MobEffect> effect,
            int amplifier,
            String ownershipRoot,
            String ownershipId,
            boolean active,
            boolean ambient,
            boolean visible,
            boolean showIcon
    ) {
        MobEffectInstance current = entity.getEffect(effect);
        boolean owned = isOwned(
                entity,
                ownershipRoot,
                ownershipId
        );

        if (active) {
            if (current == null) {
                boolean applied = entity.addEffect(
                        new MobEffectInstance(
                                effect,
                                MobEffectInstance.INFINITE_DURATION,
                                amplifier,
                                ambient,
                                visible,
                                showIcon
                        )
                );

                if (applied) {
                    setOwned(
                            entity,
                            ownershipRoot,
                            ownershipId
                    );
                }

                return;
            }

            if (owned && !containsOwnedEffect(current, amplifier)) {
                clearOwned(
                        entity,
                        ownershipRoot,
                        ownershipId
                );
            }

            return;
        }

        if (owned && current != null) {
            MobEffectInstance remaining = removeOwnedEffect(current, amplifier);
            if (remaining != current) {
                entity.removeEffect(effect);
                if (remaining != null) {
                    entity.addEffect(remaining);
                }
            }
        }

        clearOwned(
                entity,
                ownershipRoot,
                ownershipId
        );
    }

    private static boolean containsOwnedEffect(MobEffectInstance current, int amplifier) {
        for (MobEffectInstance candidate = current; candidate != null;
             candidate = ((MobEffectInstanceAccessor) candidate).jolcraft$getHiddenEffect()) {
            if (matches(candidate, amplifier)) return true;
        }
        return false;
    }

    /** Unlink an owned hidden bonus without replacing the active potion effect. */
    private static @Nullable MobEffectInstance removeOwnedEffect(MobEffectInstance current, int amplifier) {
        MobEffectInstanceAccessor accessor = (MobEffectInstanceAccessor) current;
        MobEffectInstance hidden = accessor.jolcraft$getHiddenEffect();
        if (hidden != null) {
            hidden = removeOwnedEffect(hidden, amplifier);
            accessor.jolcraft$setHiddenEffect(hidden);
        }
        return matches(current, amplifier) ? hidden : current;
    }

    private static boolean matches(
            MobEffectInstance effect,
            int amplifier
    ) {
        return effect.isInfiniteDuration()
                && effect.getAmplifier() == amplifier;
    }

    private static boolean isOwned(
            LivingEntity entity,
            String root,
            String id
    ) {
        CompoundTag data = entity.getPersistentData();

        if (!data.contains(root, Tag.TAG_COMPOUND)) {
            return false;
        }

        return data.getCompound(root).getBoolean(id);
    }

    private static void setOwned(
            LivingEntity entity,
            String root,
            String id
    ) {
        CompoundTag data = entity.getPersistentData();

        if (!data.contains(root, Tag.TAG_COMPOUND)) {
            data.put(root, new CompoundTag());
        }

        data.getCompound(root).putBoolean(id, true);
    }

    private static void clearOwned(
            LivingEntity entity,
            String root,
            String id
    ) {
        CompoundTag data = entity.getPersistentData();

        if (!data.contains(root, Tag.TAG_COMPOUND)) {
            data.remove(root);
            return;
        }

        CompoundTag ownedEffects = data.getCompound(root);
        ownedEffects.remove(id);

        if (ownedEffects.isEmpty()) {
            data.remove(root);
        }
    }
}
