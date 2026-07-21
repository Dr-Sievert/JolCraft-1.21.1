package net.sievert.jolcraft.world.entity.custom.dwarf.action;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.*;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.bounty.BountyRewardAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.bounty.BountyTaskAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.combat.AttackDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.combat.AttackHeavyDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.combat.BlockDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.combat.DrinkDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.profession.GuardEquipDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.profession.PromoteDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.profession.SignDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.reputation.EndorseDwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.type.reputation.ReputationGainDwarfAction;

import javax.annotation.Nullable;
import java.util.UUID;

/**
 * Handles all action logic for a dwarf entity, including tracking the current action,
 * ticking, transitions, and exposing type/subtype info for animation/rendering.
 * Also provides static helpers for action sync via EntityData.
 */
public class DwarfActionHelper {

    private static final String NBT_INTERRUPTED_ACTION = "interrupted_action";
    private static final String NBT_PLAYER = "player";
    private static final String NBT_ACTION_INPUT = "action_input";
    private static final String NBT_PREVIOUS_MAIN_HAND = "previous_main_hand";
    private static final String NBT_REFUND_INPUT = "refund_input";

    /** The currently active action (never null; defaults to Idle). */
    private DwarfAction activeAction = IdleDwarfAction.INSTANCE;

    /**
     * Delayed inspect actions are player-bound transactions and are therefore
     * rolled back instead of resumed after an unload. This state survives the
     * entity save and is consumed on the first server tick after loading.
     */
    @Nullable
    private InterruptedAction interruptedAction;

    private record InterruptedAction(
            @Nullable UUID playerId,
            ItemStack actionInput,
            ItemStack previousMainHandItem,
            boolean refundInput
    ) {
        private InterruptedAction {
            actionInput = actionInput.copy();
            previousMainHandItem = previousMainHandItem.copy();
        }
    }

    /**
     * Ticks the current action. If stopped, calls stop() and transitions to Idle.
     * Should be called every tick from the owning entity.
     *
     * @param dwarf The owning entity (passed for callbacks, sound, etc).
     */
    public void tick(AbstractDwarfEntity dwarf) {
        recoverInterruptedAction(dwarf);

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

    public boolean trySetAction(
            AbstractDwarfEntity dwarf,
            @Nullable DwarfActionType type,
            @Nullable DwarfActionType.Subtype subtype,
            @Nullable Player player,
            @Nullable InteractionHand hand,
            @Nullable ItemStack itemstack
    ) {
        if (interruptedAction != null
                || activeAction.getType() != DwarfActionType.IDLE) {
            return false;
        }

        ItemStack actionInput =
                itemstack == null
                        ? ItemStack.EMPTY
                        : itemstack.copyWithCount(1);

        DwarfAction newAction;

        if (subtype != null) {
            newAction = switch (subtype) {
                case CONTRACT_SIGNING ->
                        new SignDwarfAction(
                                dwarf,
                                player,
                                hand,
                                actionInput
                        );

                case PROMOTE ->
                        new PromoteDwarfAction(
                                dwarf,
                                player,
                                hand,
                                actionInput
                        );

                case ENDORSE ->
                        new EndorseDwarfAction(
                                dwarf,
                                player,
                                hand,
                                actionInput
                        );

                case REPUTATION_GAIN ->
                        new ReputationGainDwarfAction(
                                dwarf,
                                player,
                                hand,
                                actionInput
                        );

                case BOUNTY ->
                        new BountyTaskAction(
                                dwarf,
                                player,
                                hand,
                                actionInput
                        );

                case BOUNTY_REWARD ->
                        new BountyRewardAction(
                                dwarf,
                                player,
                                hand,
                                actionInput
                        );

                case GUARD_EQUIP ->
                        new GuardEquipDwarfAction(
                                dwarf,
                                player,
                                hand,
                                actionInput
                        );

                case ATTACK_HEAVY ->
                        new AttackHeavyDwarfAction(dwarf);
            };
        } else {
            if (type == null) {
                return false;
            }

            newAction = switch (type) {
                case INSPECT ->
                        new InspectDwarfAction(
                                dwarf,
                                player,
                                hand,
                                actionInput
                        );

                case DRINK ->
                        new DrinkDwarfAction();

                case BLOCK ->
                        new BlockDwarfAction(dwarf);

                case ATTACK ->
                        new AttackDwarfAction(dwarf);

                case IDLE ->
                        IdleDwarfAction.INSTANCE;
            };
        }

        activeAction = newAction;
        activeAction.start();

        setCurrentAction(
                dwarf,
                newAction.getType(),
                newAction.getSubtype()
        );

        return true;
    }

    /**
     * Records that the current inspect action's input was truly consumed.
     * This is called after usePlayerItem so creative-mode interactions are not
     * refunded an item they never spent.
     */
    public void markActionInputConsumed() {
        if (activeAction instanceof InspectDwarfAction inspectAction) {
            inspectAction.markInputConsumed();
        }
    }

    /**
     * Saves only the rollback data needed for a delayed inspect transaction.
     * Synced animation ordinals are intentionally not persistent action state.
     */
    public void addAdditionalSaveData(
            AbstractDwarfEntity dwarf,
            CompoundTag compound
    ) {
        InterruptedAction state = interruptedAction;

        if (activeAction instanceof InspectDwarfAction inspectAction) {
            state = new InterruptedAction(
                    inspectAction.getPlayerId(),
                    inspectAction.getActionInput(),
                    inspectAction.getPreviousMainHandItem(),
                    inspectAction.wasInputConsumed()
            );
        }

        if (state == null) {
            return;
        }

        compound.put(
                NBT_INTERRUPTED_ACTION,
                saveInterruptedAction(
                        state,
                        dwarf.level().registryAccess()
                )
        );
    }

    /**
     * Queues an interrupted inspect transaction for rollback on the first
     * server tick. The legacy flag migrates saves made before rollback data
     * was written; in that case the visible hand item is returned.
     */
    public void readAdditionalSaveData(
            AbstractDwarfEntity dwarf,
            CompoundTag compound,
            boolean legacyInterruptedInspect
    ) {
        activeAction = IdleDwarfAction.INSTANCE;
        interruptedAction = null;

        if (compound.contains(NBT_INTERRUPTED_ACTION, Tag.TAG_COMPOUND)) {
            interruptedAction = loadInterruptedAction(
                    compound.getCompound(NBT_INTERRUPTED_ACTION),
                    dwarf.level().registryAccess()
            );
        } else if (legacyInterruptedInspect) {
            interruptedAction = new InterruptedAction(
                    null,
                    dwarf.getMainHandItem(),
                    ItemStack.EMPTY,
                    true
            );
        }

        setCurrentAction(dwarf, DwarfActionType.IDLE, null);
    }

    private static CompoundTag saveInterruptedAction(
            InterruptedAction state,
            HolderLookup.Provider registries
    ) {
        CompoundTag tag = new CompoundTag();

        if (state.playerId() != null) {
            tag.putUUID(NBT_PLAYER, state.playerId());
        }

        putStack(
                tag,
                NBT_ACTION_INPUT,
                state.actionInput(),
                registries
        );

        putStack(
                tag,
                NBT_PREVIOUS_MAIN_HAND,
                state.previousMainHandItem(),
                registries
        );

        tag.putBoolean(NBT_REFUND_INPUT, state.refundInput());
        return tag;
    }

    private static InterruptedAction loadInterruptedAction(
            CompoundTag tag,
            HolderLookup.Provider registries
    ) {
        UUID playerId = tag.hasUUID(NBT_PLAYER)
                ? tag.getUUID(NBT_PLAYER)
                : null;

        return new InterruptedAction(
                playerId,
                getStack(tag, NBT_ACTION_INPUT, registries),
                getStack(tag, NBT_PREVIOUS_MAIN_HAND, registries),
                tag.getBoolean(NBT_REFUND_INPUT)
        );
    }

    private static void putStack(
            CompoundTag tag,
            String key,
            ItemStack stack,
            HolderLookup.Provider registries
    ) {
        if (!stack.isEmpty()) {
            tag.put(
                    key,
                    stack.save(registries, new CompoundTag())
            );
        }
    }

    private static ItemStack getStack(
            CompoundTag tag,
            String key,
            HolderLookup.Provider registries
    ) {
        if (!tag.contains(key, Tag.TAG_COMPOUND)) {
            return ItemStack.EMPTY;
        }

        return ItemStack.parseOptional(
                registries,
                tag.getCompound(key)
        );
    }

    private void recoverInterruptedAction(AbstractDwarfEntity dwarf) {
        InterruptedAction state = interruptedAction;

        if (state == null
                || !(dwarf.level() instanceof ServerLevel level)) {
            return;
        }

        interruptedAction = null;
        activeAction = IdleDwarfAction.INSTANCE;
        setCurrentAction(dwarf, DwarfActionType.IDLE, null);

        dwarf.setItemSlot(
                EquipmentSlot.MAINHAND,
                state.previousMainHandItem().copy()
        );

        if (!state.refundInput()
                || state.actionInput().isEmpty()) {
            return;
        }

        ItemStack refund = state.actionInput().copy();
        ServerPlayer player = state.playerId() == null
                ? null
                : level.getServer()
                .getPlayerList()
                .getPlayer(state.playerId());

        if (player != null) {
            player.getInventory().add(refund);

            if (!refund.isEmpty()) {
                player.drop(refund, false);
            }
        } else {
            dwarf.spawnAtLocation(refund);
        }
    }

    /**
     * Compatibility wrapper for existing non-interaction callers.
     */
    public void setAction(
            AbstractDwarfEntity dwarf,
            @Nullable DwarfActionType type,
            @Nullable DwarfActionType.Subtype subtype,
            @Nullable Player player,
            @Nullable InteractionHand hand,
            @Nullable ItemStack itemstack
    ) {
        trySetAction(
                dwarf,
                type,
                subtype,
                player,
                hand,
                itemstack
        );
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
        DwarfActionType[] values = DwarfActionType.values();
        return idx >= 0 && idx < values.length
                ? values[idx]
                : DwarfActionType.IDLE;
    }

    /**
     * Gets the current action type from the entity's data.
     */
    public static DwarfActionType.Subtype getCurrentActionSubType(AbstractDwarfEntity entity) {
        int idx = entity.getEntityData().get(AbstractDwarfEntity.CURRENT_ACTION_SUBTYPE);
        DwarfActionType.Subtype[] values = DwarfActionType.Subtype.values();
        return idx >= 0 && idx < values.length
                ? values[idx]
                : null;
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

    public boolean blocksMovement() {
        return interruptedAction != null
                || activeAction != null && activeAction.blocksMovement();
    }
}
