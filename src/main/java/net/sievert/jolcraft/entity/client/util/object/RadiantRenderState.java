package net.sievert.jolcraft.entity.client.util.object;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.AnimationState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

/**
 * Custom render state for RadiantEntity.
 * Inherits body/head rotation, animation ticks, etc from LivingEntityRenderState.
 * Extend here for additional animation states (e.g. special actions) if needed.
 */
@OnlyIn(Dist.CLIENT)
public class RadiantRenderState extends LivingEntityRenderState {

    /** Idle animation (looped, always ticking if entity is alive). */
    public final AnimationState idleAnimationState = new AnimationState();
}
