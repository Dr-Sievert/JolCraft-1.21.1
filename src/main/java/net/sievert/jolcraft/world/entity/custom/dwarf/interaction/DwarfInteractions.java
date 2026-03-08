package net.sievert.jolcraft.world.entity.custom.dwarf.interaction;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
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

    // -------------------------------------------------------------------------
    // State
    // -------------------------------------------------------------------------

    private static final Map<DwarfProfession, ProfessionInteraction> PROFESSION_HANDLERS = new EnumMap<>(DwarfProfession.class);

    private static boolean isProfessionRegistered(DwarfProfession profession) {
        return PROFESSION_HANDLERS.containsKey(profession);
    }

    /**
     * Ordered core pipeline. First non-PASS wins.
     * Split CoreDwarfInteractionHandler into multiple CoreInteraction classes over time,
     * and register them here in the exact order you want them evaluated.
     */
    private static final List<CoreInteraction> CORE_PIPELINE = new ArrayList<>();

    // -------------------------------------------------------------------------
    // Registration
    // -------------------------------------------------------------------------

    public static void registerCore(CoreInteraction handler) {
        CORE_PIPELINE.add(handler);
    }

    public static void register(DwarfProfession profession, ProfessionInteraction handler) {
        PROFESSION_HANDLERS.put(profession, handler);
    }

    /**
     * Called once during common init.
     * Enforces that the interaction system is complete and deterministic.
     */
    public static void registerAll() {

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

        register(DwarfProfession.GUARD, new GuardInteractionHandler());
        register(DwarfProfession.EXPLORER, new ExplorerInteractionHandler());
        register(DwarfProfession.GUILDMASTER, new GuildmasterInteractionHandler());

        final ProfessionInteraction DEFAULT = new DefaultProfessionInteractionHandler();

        for (DwarfProfession prof : DwarfProfession.values()) {
            if (!isProfessionRegistered(prof)) {
                register(prof, DEFAULT);
            }
        }

        validateComplete();
    }

    private static void validateComplete() {
        if (CORE_PIPELINE.isEmpty()) {
            throw new IllegalStateException("Missing dwarf CORE interaction pipeline");
        }

        for (DwarfProfession profession : DwarfProfession.values()) {
            if (!PROFESSION_HANDLERS.containsKey(profession)) {
                throw new IllegalStateException(
                        "Missing dwarf interaction handler for profession: " + profession
                );
            }
        }
    }

    // -------------------------------------------------------------------------
    // Dispatch
    // -------------------------------------------------------------------------

    public static InteractionResult dispatch(DwarfInteractionContext ctx) {
        if (ctx.isClient()) {
            return InteractionResult.SUCCESS;
        }

        DwarfProfession profession = ctx.dwarf().getProfession();
        ProfessionInteraction professionHandler = PROFESSION_HANDLERS.get(profession);

        if (professionHandler == null) {
            throw new IllegalStateException(
                    "No dwarf interaction handler registered for profession: " + profession
            );
        }

        if (professionHandler instanceof DwarfInteractionHooks hooks) {
            hooks.preCore(ctx);
        }

        for (CoreInteraction core : CORE_PIPELINE) {
            InteractionResult coreResult = core.handle(ctx);
            if (coreResult != InteractionResult.PASS) {
                return coreResult;
            }
        }

        if (professionHandler instanceof DwarfInteractionHooks hooks) {
            hooks.postCore(ctx);
            hooks.preProfession(ctx);
        }

        InteractionResult profResult = professionHandler.handle(ctx);
        if (profResult != InteractionResult.PASS) {
            return profResult;
        }

        if (professionHandler instanceof DwarfInteractionHooks hooks) {
            hooks.postProfession(ctx);
        }

        return InteractionResult.FAIL;
    }

    // -------------------------------------------------------------------------
    // Types
    // -------------------------------------------------------------------------

    public interface DwarfInteraction {
        InteractionResult handle(DwarfInteractionContext ctx);
    }

    /**
     * Mandatory marker for CORE handlers.
     * These should be shared logic (gates + common interactions).
     */
    public interface CoreInteraction extends DwarfInteraction {
    }

    /**
     * Mandatory marker for PROFESSION handlers.
     * These should be profession-specific logic only.
     */
    public interface ProfessionInteraction extends DwarfInteraction {
    }

    /**
     * Optional timing hooks for profession handlers that need to run at precise points.
     * Having named phases makes this deterministic and very easy to read in code.
     */
    public interface DwarfInteractionHooks {
        default void preCore(DwarfInteractionContext ctx) {
        }

        default void postCore(DwarfInteractionContext ctx) {
        }

        default void preProfession(DwarfInteractionContext ctx) {
        }

        default void postProfession(DwarfInteractionContext ctx) {
        }
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