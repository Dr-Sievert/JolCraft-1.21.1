package net.sievert.jolcraft.entity.client.util.dwarf.animation;

import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.world.entity.AnimationState;
import net.sievert.jolcraft.entity.client.model.dwarf.DwarfModel;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.entity.util.dwarf.action.DwarfActionType;

import java.util.EnumMap;
import java.util.Map;

/**
 * Central animation lookup. Both DwarfActionType and Subtype
 * can be passed to resolve the final animation.
 */
public class DwarfAnimationHelper {

    private static final Map<DwarfActionType, AnimationDefinition> BY_TYPE = new EnumMap<>(DwarfActionType.class);
    private static final Map<DwarfActionType.Subtype, AnimationDefinition> BY_SUBTYPE = new EnumMap<>(DwarfActionType.Subtype.class);

    static {
        BY_TYPE.put(DwarfActionType.IDLE,    DwarfAnimations.IDLE);
        BY_TYPE.put(DwarfActionType.ATTACK,  DwarfAnimations.ATTACK);
        BY_TYPE.put(DwarfActionType.BLOCK,   DwarfAnimations.BLOCK);
        BY_TYPE.put(DwarfActionType.DRINK,   DwarfAnimations.DRINK);
        BY_TYPE.put(DwarfActionType.INSPECT, DwarfAnimations.INSPECT);

        BY_SUBTYPE.put(DwarfActionType.Subtype.ATTACK_HEAVY, DwarfAnimations.ATTACK_HEAVY);
    }

    /**
     * Unified lookup: caller can pass either a type or a subtype.
     */
    public static AnimationDefinition resolve(DwarfActionType type, DwarfActionType.Subtype subtype) {
        if (subtype != null) {
            AnimationDefinition def = BY_SUBTYPE.get(subtype);
            if (def != null) return def;
        }
        return BY_TYPE.get(type);
    }

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
