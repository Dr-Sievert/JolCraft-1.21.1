package net.sievert.jolcraft.world.entity.custom.util.dwarf.action;

/**
 * Represents a server-side action performed by a dwarf, such as contract signing,
 * profession promotion, endorsement, blocking, etc.
 * Actions are ticked every tick while active, and can handle their own
 * duration, start/stop detection, and cleanup logic.
 */
public interface DwarfAction {

    /**
     * Returns the top-level action type for this action.
     * Used to map to animation states or action groups (e.g., INSPECT, ATTACK).
     */
    DwarfActionType getType();

    /**
     * Returns the optional action subtype for this action (or null if none).
     * Only override if this action needs a specific subtype (e.g., ATTACK_AXE).
     */
    default DwarfActionType.Subtype getSubtype() {
        return null;
    }

    /**
     * Called every tick while this action is active.
     * Put all per-tick timers, sounds, particles, or state logic here.
     */
    default void tick() {}

    /**
     * Called once when this action becomes active.
     * Use for initialization, resetting timers, or spawning first effects.
     */
    default void start() {}

    /**
     * Returns true ONLY on the tick this action first starts.
     * Used for one-time effects (particles, sounds, etc).
     */
    default boolean isStarted() { return true; }

    /**
     * Called once when the action ends, after isStopped() returns true.
     * Put all one-time rewards, item handout, animation reset, or cleanup logic here.
     */
    default void stop() {}

    /**
     * Should return true when this action is stopped and ready to be cleaned up.
     * Use for timer expiration, completion conditions, etc.
     */
    default boolean isStopped() { return false; }
}
