package net.sievert.jolcraft.entity.util.dwarf.action;

/**
 * Enumerates the top-level action/animation types that a dwarf can perform.
 * Each value represents a high-level action group, which can be mapped to
 * animation states and used for both logic and rendering.
 *
 * Not all actions have or need a subtype: most actions (e.g., WALK, DRINK)
 * are atomic. Subtypes are used only for complex action families (e.g., INSPECT).
 */
public enum DwarfActionType {
    IDLE,
    ATTACK,
    BLOCK,
    DRINK,
    INSPECT;

    /**
     * Enumerates specific sub-action types, each mapped to a parent DwarfActionType.
     * Subtypes are only used for action groups where more granularity is needed
     * (e.g., INSPECT, ATTACK). For actions with no subtypes, use null.
     */
    public enum Subtype {

        // INSPECT subtypes
        CONTRACT_SIGNING(INSPECT),
        PROFESSION_PROMOTION(INSPECT),
        ENDORSEMENT(INSPECT),
        GENERIC_INSPECTION(INSPECT),

        // ATTACK subtypes
        ATTACK_AXE(ATTACK);

        /** The parent action group for this subtype. */
        public final DwarfActionType parent;

        Subtype(DwarfActionType parent) {
            this.parent = parent;
        }

        /** Returns the parent action group for this subtype. */
        public DwarfActionType getParent() {
            return parent;
        }
    }
}
