package net.sievert.jolcraft.entity.client.util.dwarf.animation;

import net.minecraft.client.animation.AnimationDefinition;
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

        BY_SUBTYPE.put(DwarfActionType.Subtype.ATTACK_AXE, DwarfAnimations.ATTACK_AXE);
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
}
