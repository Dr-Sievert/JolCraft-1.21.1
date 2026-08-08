package net.sievert.jolcraft.world.entity.effect.custom.neutral;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.phys.Vec3;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnchorEffect extends MobEffect {

    private static final double WATER_SINK_FORCE = 0.075D;

    public AnchorEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        return true;
    }

    private static void breakBoat(LivingEntity entity, Boat boat) {
        boat.hurt(entity.damageSources().generic(), Float.MAX_VALUE);
        JolCraftSoundHelper.entity(entity, SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR);
    }

    @Override
    public boolean applyEffectTick(@NotNull LivingEntity entity, int amplifier) {
        if (entity instanceof Player player && player.isCreative()) return true;

        boolean aboveWater = entity.level()
                .getBlockState(entity.blockPosition().below())
                .getFluidState()
                .is(FluidTags.WATER);

        List<Boat> boats = entity.level().getEntitiesOfClass(Boat.class, entity.getBoundingBox().move(0.0D, -0.1D, 0.0D));

        if (!boats.isEmpty()) {
            breakBoat(entity, boats.getFirst());
            return true;
        }

        if (entity.getVehicle() instanceof Boat boat && aboveWater) {
            breakBoat(entity, boat);
            return true;
        }

        if (!entity.isInWater()) return true;

        Vec3 movement = entity.getDeltaMovement();

        boolean shallowWater =
                entity.level()
                        .getBlockState(entity.blockPosition().above())
                        .isAir()
                        && !entity.level()
                        .getBlockState(entity.blockPosition().below())
                        .getCollisionShape(
                                entity.level(),
                                entity.blockPosition().below()
                        )
                        .isEmpty();

        if (shallowWater) return true;

        if (entity.isSwimming()) {
            entity.setDeltaMovement(
                    movement.x,
                    movement.y - WATER_SINK_FORCE / 2.0D,
                    movement.z
            );

            return true;
        }

        if (!entity.onGround()) {
            entity.setDeltaMovement(
                    movement.x,
                    movement.y - WATER_SINK_FORCE,
                    movement.z
            );
        }

        return true;
    }
}