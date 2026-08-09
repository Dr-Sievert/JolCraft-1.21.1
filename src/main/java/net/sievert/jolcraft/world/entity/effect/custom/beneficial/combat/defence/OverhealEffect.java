package net.sievert.jolcraft.world.entity.effect.custom.beneficial.combat.defence;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.sievert.jolcraft.world.entity.attachment.custom.overheal.OverhealAttachmentHelper;
import org.jetbrains.annotations.NotNull;

public class OverhealEffect extends MobEffect {

    public OverhealEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void onEffectStarted(@NotNull LivingEntity entity, int amplifier) {
        super.onEffectStarted(entity, amplifier);

        OverhealAttachmentHelper.setAmount(
                entity,
                Math.max(
                        OverhealAttachmentHelper.getAmount(entity),
                        OverhealAttachmentHelper.getMaxAmount(entity)
                )
        );
    }
}