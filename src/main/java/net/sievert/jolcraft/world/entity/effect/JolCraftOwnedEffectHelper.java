package net.sievert.jolcraft.world.entity.effect;

import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

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

            if (owned && !matches(current, amplifier)) {
                clearOwned(
                        entity,
                        ownershipRoot,
                        ownershipId
                );
            }

            return;
        }

        if (owned
                && current != null
                && matches(current, amplifier)) {
            entity.removeEffect(effect);
        }

        clearOwned(
                entity,
                ownershipRoot,
                ownershipId
        );
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
