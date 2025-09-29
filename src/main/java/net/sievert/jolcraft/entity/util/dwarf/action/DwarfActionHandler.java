package net.sievert.jolcraft.entity.util.dwarf.action;

import net.sievert.jolcraft.entity.custom.dwarf.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.type.*;

import javax.annotation.Nullable;

/**
 * Handles all action logic for a dwarf entity, including tracking the current action,
 * ticking, transitions, and exposing type/subtype info for animation/rendering.
 * Also provides static helpers for action sync via EntityData.
 */
public class DwarfActionHandler {

    /** The currently active action (never null; defaults to Idle). */
    private DwarfAction activeAction = IdleDwarfAction.INSTANCE;

    /**
     * Ticks the current action. If stopped, calls stop() and transitions to Idle.
     * Should be called every tick from the owning entity.
     *
     * @param dwarf The owning entity (passed for callbacks, sound, etc).
     */
    public void tick(AbstractDwarfEntity dwarf) {
        if (activeAction == null) {
            activeAction = IdleDwarfAction.INSTANCE;
        }
        activeAction.tick(dwarf);
        if (activeAction.isStopped()) {
            activeAction.stop(dwarf);
            stopAction(dwarf);
        }
    }

    /** Returns the currently active action object. */
    public DwarfAction getCurrentAction() {
        return activeAction;
    }

    /** Returns the top-level action type (e.g., IDLE, ATTACK, etc). */
    public DwarfActionType getCurrentActionType() {
        return activeAction.getType();
    }

    /** Returns the current action subtype if present, else null. */
    public DwarfActionType.Subtype getCurrentActionSubtype() {
        return activeAction.getSubtype();
    }

    /** Sets a new active action. Null is treated as Idle. Calls start() on the new action. */
    public void setAction(@Nullable DwarfActionType type, AbstractDwarfEntity dwarf) {
        if (activeAction != null && !activeAction.isStopped() && activeAction.getType() != DwarfActionType.IDLE) {
            return;
        }
        DwarfAction newAction = switch (type) {
            case INSPECT -> new InspectDwarfAction();
            case DRINK   -> new DrinkDwarfAction();
            case BLOCK   -> new BlockDwarfAction();
            case ATTACK  -> new AttackDwarfAction();
            case null, default -> IdleDwarfAction.INSTANCE;
        };
        this.activeAction = newAction;
        this.activeAction.start(dwarf);
        setCurrentActionType(dwarf, newAction.getType());
    }


    /** Stops the current action and returns the dwarf to Idle. */
    public void stopAction(AbstractDwarfEntity dwarf) {
        setAction(DwarfActionType.IDLE, dwarf);
    }

    /**
     * Sets the current action type value into the entity's data.
     */
    public static void setCurrentActionType(AbstractDwarfEntity entity, DwarfActionType type) {
        entity.getEntityData().set(AbstractDwarfEntity.CURRENT_ACTION, type.ordinal());
    }

    /**
     * Gets the current action type from the entity's data.
     */
    public static DwarfActionType getCurrentActionType(AbstractDwarfEntity entity) {
        int idx = entity.getEntityData().get(AbstractDwarfEntity.CURRENT_ACTION);
        return DwarfActionType.values()[idx];
    }

    /**
     * Checks if the entity's current action matches the given type.
     */
    public boolean isAction(AbstractDwarfEntity entity, DwarfActionType type) {
        int stored = entity.getEntityData().get(AbstractDwarfEntity.CURRENT_ACTION);
        return stored == type.ordinal();
    }
}
