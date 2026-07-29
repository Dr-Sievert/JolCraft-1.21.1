package net.sievert.jolcraft.world.entity.custom.dwarf.interaction;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.core.*;
import net.sievert.jolcraft.world.entity.custom.dwarf.interaction.handler.profession.*;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfInteractions {

    private static final Map<
            DwarfProfession,
            ProfessionInteraction
            > PROFESSION_HANDLERS =
            new EnumMap<>(DwarfProfession.class);

    private static final List<CoreInteraction> CORE_PIPELINE =
            new ArrayList<>();

    private DwarfInteractions() {}

    // -------------------------------------------------------------------------
    // Registration
    // -------------------------------------------------------------------------

    private static boolean isProfessionRegistered(
            DwarfProfession profession
    ) {
        return PROFESSION_HANDLERS.containsKey(profession);
    }

    public static void registerCore(
            CoreInteraction handler
    ) {
        CORE_PIPELINE.add(handler);
    }

    public static void register(
            DwarfProfession profession,
            ProfessionInteraction handler
    ) {
        PROFESSION_HANDLERS.put(
                profession,
                handler
        );
    }

    public static void registerAll() {
        CORE_PIPELINE.clear();
        PROFESSION_HANDLERS.clear();

        registerCore(new IgnoreInteractionHandler());
        registerCore(new LanguageGateInteractionHandler());
        registerCore(new ReputationGateInteractionHandler());
        registerCore(new BusyGateInteractionHandler());
        registerCore(new BreedInteractionHandler());
        registerCore(new PayCoinInteractionHandler());
        registerCore(new SignContractInteractionHandler());
        registerCore(new PromoteInteractionHandler());
        registerCore(new EndorseInteractionHandler());
        registerCore(new BountyInteractionHandler());
        registerCore(new TradeCrateInteractionHandler());
        registerCore(new TradeInteractionHandler());

        register(
                DwarfProfession.GUARD,
                new GuardInteractionHandler()
        );

        register(
                DwarfProfession.EXPLORER,
                new ExplorerInteractionHandler()
        );

        register(
                DwarfProfession.GUILDMASTER,
                new GuildmasterInteractionHandler()
        );

        ProfessionInteraction defaultHandler =
                new DefaultProfessionInteractionHandler();

        for (DwarfProfession profession : DwarfProfession.values()) {
            if (!isProfessionRegistered(profession)) {
                register(
                        profession,
                        defaultHandler
                );
            }
        }

        validateComplete();
    }

    private static void validateComplete() {
        if (CORE_PIPELINE.isEmpty()) {
            throw new IllegalStateException(
                    "Missing dwarf CORE interaction pipeline"
            );
        }

        for (DwarfProfession profession : DwarfProfession.values()) {
            if (!PROFESSION_HANDLERS.containsKey(profession)) {
                throw new IllegalStateException(
                        "Missing dwarf interaction handler for profession: "
                                + profession
                );
            }
        }
    }

    // -------------------------------------------------------------------------
    // Dispatch
    // -------------------------------------------------------------------------

    public static InteractionResult dispatch(
            DwarfInteractionContext ctx
    ) {
        if (ctx.isClient()) {
            return InteractionResult.SUCCESS;
        }

        /*
         * Preserve the interacted item before any handler or action can mutate
         * the player's actual stack.
         */
        ItemStack inputSnapshot =
                ctx.stack().copyWithCount(1);

        DwarfProfession profession =
                ctx.dwarf().getProfession();

        ProfessionInteraction professionHandler =
                PROFESSION_HANDLERS.get(profession);

        if (professionHandler == null) {
            throw new IllegalStateException(
                    "No dwarf interaction handler registered for profession: "
                            + profession
            );
        }

        if (professionHandler instanceof DwarfInteractionHooks hooks) {
            hooks.preCore(ctx);
        }

        for (CoreInteraction handler : CORE_PIPELINE) {
            DwarfInteractionOutcome outcome =
                    handler.handle(ctx);

            if (!outcome.isPass()) {
                return commit(
                        ctx,
                        inputSnapshot,
                        outcome
                );
            }
        }

        if (professionHandler instanceof DwarfInteractionHooks hooks) {
            hooks.postCore(ctx);
            hooks.preProfession(ctx);
        }

        DwarfInteractionOutcome professionOutcome =
                professionHandler.handle(ctx);

        if (!professionOutcome.isPass()) {
            return commit(
                    ctx,
                    inputSnapshot,
                    professionOutcome
            );
        }

        if (professionHandler instanceof DwarfInteractionHooks hooks) {
            hooks.postProfession(ctx);
        }

        return InteractionResult.FAIL;
    }

    /**
     * The single commit point for dwarf interactions.
     *
     * An action is started before consuming the player's item. If the action
     * cannot start, the item remains untouched.
     */
    private static InteractionResult commit(
            DwarfInteractionContext ctx,
            ItemStack inputSnapshot,
            DwarfInteractionOutcome outcome
    ) {
        DwarfActionType.Subtype actionSubtype =
                outcome.actionSubtype();

        if (actionSubtype != null) {
            boolean started =
                    ctx.dwarf()
                            .getActionHelper()
                            .trySetAction(
                                    ctx.dwarf(),
                                    null,
                                    actionSubtype,
                                    ctx.player(),
                                    ctx.hand(),
                                    inputSnapshot
                            );

            if (!started) {
                return InteractionResult.FAIL;
            }
        }

        if (outcome.itemUse()
                == DwarfInteractionOutcome.HeldItemUse.CONSUME_ONE) {

            /*
             * This is the only normal usePlayerItem call in the dwarf
             * interaction system.
             */
            int countBefore = ctx.stack().getCount();

            ctx.dwarf().usePlayerItem(
                    ctx.player(),
                    ctx.hand(),
                    ctx.stack()
            );

            if (ctx.stack().getCount() < countBefore) {
                ctx.dwarf()
                        .getActionHelper()
                        .markActionInputConsumed();
            }
        }

        return outcome.result();
    }

    // -------------------------------------------------------------------------
    // Interaction types
    // -------------------------------------------------------------------------

    public interface DwarfInteraction {

        DwarfInteractionOutcome handle(
                DwarfInteractionContext ctx
        );
    }

    public interface CoreInteraction
            extends DwarfInteraction {}

    public interface ProfessionInteraction
            extends DwarfInteraction {}

    public interface DwarfInteractionHooks {

        default void preCore(
                DwarfInteractionContext ctx
        ) {}

        default void postCore(
                DwarfInteractionContext ctx
        ) {}

        default void preProfession(
                DwarfInteractionContext ctx
        ) {}

        default void postProfession(
                DwarfInteractionContext ctx
        ) {}
    }

    public record DwarfInteractionContext(
            AbstractDwarfEntity dwarf,
            Player player,
            InteractionHand hand,
            ItemStack stack,
            Level level,
            boolean isClient
    ) {}
}
