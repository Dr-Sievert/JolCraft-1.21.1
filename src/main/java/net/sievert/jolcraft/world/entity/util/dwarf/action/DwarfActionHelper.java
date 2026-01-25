package net.sievert.jolcraft.world.entity.util.dwarf.action;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.util.dwarf.action.type.*;
import net.sievert.jolcraft.world.entity.util.dwarf.action.type.bounty.BountyCrateDwarfAction;
import net.sievert.jolcraft.world.entity.util.dwarf.action.type.bounty.BountyDwarfAction;
import net.sievert.jolcraft.world.entity.util.dwarf.action.type.combat.AttackDwarfAction;
import net.sievert.jolcraft.world.entity.util.dwarf.action.type.combat.AttackHeavyDwarfAction;
import net.sievert.jolcraft.world.entity.util.dwarf.action.type.combat.BlockDwarfAction;
import net.sievert.jolcraft.world.entity.util.dwarf.action.type.combat.DrinkDwarfAction;
import net.sievert.jolcraft.world.entity.util.dwarf.action.type.profession.GuardEquipDwarfAction;
import net.sievert.jolcraft.world.entity.util.dwarf.action.type.profession.PromoteDwarfAction;
import net.sievert.jolcraft.world.entity.util.dwarf.action.type.profession.SignDwarfAction;
import net.sievert.jolcraft.world.entity.util.dwarf.action.type.reputation.EndorseDwarfAction;
import net.sievert.jolcraft.world.entity.util.dwarf.action.type.reputation.ReputationGainDwarfAction;

import javax.annotation.Nullable;

/**
 * Handles all action logic for a dwarf entity, including tracking the current action,
 * ticking, transitions, and exposing type/subtype info for animation/rendering.
 * Also provides static helpers for action sync via EntityData.
 */
public class DwarfActionHelper {

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
        if(activeAction.getType() != DwarfActionType.IDLE){
            activeAction.tick();
            if (activeAction.isStopped()) {
                activeAction.stop();
                stopAction(dwarf);
            }
        }
    }

    /** Sets a new active action. Null is treated as Idle. Calls start() on the new action. */
    public void setAction(
            AbstractDwarfEntity dwarf,
            @Nullable DwarfActionType type,
            @Nullable DwarfActionType.Subtype subtype,
            @Nullable Player player,
            @Nullable InteractionHand hand,
            @Nullable ItemStack itemstack
    ) {
        if (activeAction.getType() != DwarfActionType.IDLE) return;
        DwarfAction newAction;
        if (subtype != null) {
            newAction = switch (subtype) {
                case CONTRACT_SIGNING -> new SignDwarfAction(dwarf, player, hand, itemstack);
                case PROMOTE -> new PromoteDwarfAction(dwarf, player, hand, itemstack);
                case ENDORSE -> new EndorseDwarfAction(dwarf, player, hand, itemstack);
                case REPUTATION_GAIN -> new ReputationGainDwarfAction(dwarf, player, hand, itemstack);
                case BOUNTY -> new BountyDwarfAction(dwarf, player, hand, itemstack);
                case BOUNTY_CRATE -> new BountyCrateDwarfAction(dwarf, player, hand, itemstack);
                case GUARD_EQUIP -> new GuardEquipDwarfAction(dwarf, player, hand, itemstack);
                case ATTACK_HEAVY ->  new AttackHeavyDwarfAction(dwarf);
            };
        } else {
            assert type != null;
            newAction = switch (type) {
                case INSPECT -> new InspectDwarfAction(dwarf, player, hand, itemstack);
                case DRINK   -> new DrinkDwarfAction();
                case BLOCK   -> new BlockDwarfAction(dwarf);
                case ATTACK  -> new AttackDwarfAction(dwarf);
                case IDLE    -> IdleDwarfAction.INSTANCE;
            };
        }
        this.activeAction = newAction;
        this.activeAction.start();
        setCurrentAction(dwarf, newAction.getType(), newAction.getSubtype());
    }

    public void setAction(
            AbstractDwarfEntity dwarf,
            DwarfActionType type
    ) {
        setAction(dwarf, type, null, null, null, null);
    }

    public void setAction(
            AbstractDwarfEntity dwarf,
            DwarfActionType.Subtype subtype
    ) {
        setAction(dwarf, null, subtype, null, null, null);
    }

    public void setAction(
            AbstractDwarfEntity dwarf,
            @Nullable DwarfActionType.Subtype subtype,
            @Nullable Player player,
            @Nullable InteractionHand hand,
            @Nullable ItemStack itemstack
    ) {
        setAction(dwarf, null, subtype, player, hand, itemstack);
    }

    /**
     * Sets the current action type value into the entity's data.
     */
    public static void setCurrentAction(AbstractDwarfEntity entity, DwarfActionType type, @Nullable DwarfActionType.Subtype subtype) {
        if (subtype != null){
            entity.getEntityData().set(AbstractDwarfEntity.CURRENT_ACTION_SUBTYPE, subtype.ordinal());
            entity.getEntityData().set(AbstractDwarfEntity.CURRENT_ACTION, subtype.getParent().ordinal());
        }else{
            entity.getEntityData().set(AbstractDwarfEntity.CURRENT_ACTION, type.ordinal());
            entity.getEntityData().set(AbstractDwarfEntity.CURRENT_ACTION_SUBTYPE, -1);
        }
    }

    /** Stops the current action and returns the dwarf to Idle. */
    public void stopAction(AbstractDwarfEntity dwarf) {
        setCurrentAction(dwarf, DwarfActionType.IDLE, null);
        activeAction = IdleDwarfAction.INSTANCE;
    }


    /**
     * Gets the current action type from the entity's data.
     */
    public static DwarfActionType getCurrentActionType(AbstractDwarfEntity entity) {
        int idx = entity.getEntityData().get(AbstractDwarfEntity.CURRENT_ACTION);
        return DwarfActionType.values()[idx];
    }

    /**
     * Gets the current action type from the entity's data.
     */
    public static DwarfActionType.Subtype getCurrentActionSubType(AbstractDwarfEntity entity) {
        int idx = entity.getEntityData().get(AbstractDwarfEntity.CURRENT_ACTION_SUBTYPE);
        return idx < 0 ? null : DwarfActionType.Subtype.values()[idx];
    }

    /**
     * Checks if the entity's current action matches the given type.
     */
    public static boolean isActionType(AbstractDwarfEntity entity, DwarfActionType type) {
        int stored = entity.getEntityData().get(AbstractDwarfEntity.CURRENT_ACTION);
        return stored == type.ordinal();
    }

    /**
     * Checks if the entity's current action matches the given type.
     */
    public static boolean isActionSubType(AbstractDwarfEntity entity, DwarfActionType.Subtype subtype) {
        int stored = entity.getEntityData().get(AbstractDwarfEntity.CURRENT_ACTION_SUBTYPE);
        return stored == subtype.ordinal();
    }
}