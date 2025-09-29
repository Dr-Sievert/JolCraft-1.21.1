package net.sievert.jolcraft.entity.client.util.dwarf.animation;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.world.entity.AnimationState;
import net.sievert.jolcraft.entity.client.model.dwarf.DwarfModel;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;

/**
 * Handles applying non-walk animations to a dwarf model.
 * This includes idle and active action animations.
 * Walking is handled separately in the model.
 */
public class DwarfAnimationHandler {

    /**
     * Applies all non-walk animations based on the current render state.
     *
     * @param state the current animation state for the dwarf
     * @param model the model to apply animations to
     */
    public static void animate(DwarfRenderState state, DwarfModel model) {
        AnimationDefinition def = DwarfAnimationHelper.resolve(
                state.currentActionType,
                state.currentActionSubtype
        );
        if (def == null) return;

        AnimationState anim = state.animationStates.get(state.currentActionType);
        if (anim != null) {
            model.forwardAnimation(anim, def, state.ageInTicks, 1.0f);
        }
    }


    /**
     * Updates all animation states for the current frame, starting/stopping as needed.
     * Call this from your renderer each frame.
     */
    public static void updateAnimationStates(DwarfRenderState s, DwarfActionType active, int ticks) {
        for (DwarfActionType t : DwarfActionType.values()) {
            AnimationState a = s.animationStates.get(t);
            if (t == active) {
                if (!a.isStarted()) a.start(ticks);
            } else {
                a.stop();
            }
        }
    }


}
