package net.sievert.jolcraft.world.entity.custom.dwarf.interaction;

import net.minecraft.world.InteractionResult;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Describes what a dwarf interaction decided to do.
 *
 * Handlers return a decision. DwarfInteractions.dispatch() performs the
 * requested action and held-item consumption.
 */
public record DwarfInteractionOutcome(
        InteractionResult result,
        HeldItemUse itemUse,
        @Nullable DwarfActionType.Subtype actionSubtype
) {

    public DwarfInteractionOutcome {
        Objects.requireNonNull(result, "result");
        Objects.requireNonNull(itemUse, "itemUse");
    }

    /**
     * No handler accepted the interaction.
     */
    public static DwarfInteractionOutcome pass() {
        return new DwarfInteractionOutcome(
                InteractionResult.PASS,
                HeldItemUse.NONE,
                null
        );
    }

    /**
     * The interaction was handled successfully without consuming an item.
     */
    public static DwarfInteractionOutcome handled() {
        return new DwarfInteractionOutcome(
                InteractionResult.SUCCESS,
                HeldItemUse.NONE,
                null
        );
    }

    /**
     * The interaction was rejected without consuming an item.
     */
    public static DwarfInteractionOutcome failed() {
        return new DwarfInteractionOutcome(
                InteractionResult.FAIL,
                HeldItemUse.NONE,
                null
        );
    }

    /**
     * The interaction completed immediately and should consume one item.
     */
    public static DwarfInteractionOutcome consumeOne() {
        return new DwarfInteractionOutcome(
                InteractionResult.SUCCESS,
                HeldItemUse.CONSUME_ONE,
                null
        );
    }

    /**
     * Starts a delayed dwarf action.
     *
     * Consumption remains explicit so future actions may start without
     * consuming the held item.
     */
    public static DwarfInteractionOutcome startAction(
            DwarfActionType.Subtype subtype,
            HeldItemUse itemUse
    ) {
        return new DwarfInteractionOutcome(
                InteractionResult.SUCCESS,
                itemUse,
                Objects.requireNonNull(subtype, "subtype")
        );
    }

    public boolean isPass() {
        return result == InteractionResult.PASS;
    }

    public enum HeldItemUse {
        NONE,
        CONSUME_ONE
    }
}