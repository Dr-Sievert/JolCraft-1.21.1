package net.sievert.jolcraft.data.attachment.custom.reputation;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundEndorsementsPacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundReputationPacket;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;

import java.util.Set;
import java.util.stream.Collectors;

public final class DwarvenReputationHelper {

    private DwarvenReputationHelper() {}

    private static DwarvenReputation repOrNull(Player player) {
        return player == null ? null : player.getData(JolCraftAttachments.DWARVEN_REP.get());
    }

    /**
     * Checks if player is creative OR has at least the specified tier.
     */
    public static boolean hasTier(Player player, int minTier) {
        if (player == null) return false;
        if (player.isCreative()) return true;
        return hasTierBypassCreative(player, minTier);
    }

    /**
     * Checks if player has at least the specified tier (does NOT bypass creative).
     */
    public static boolean hasTierBypassCreative(Player player, int minTier) {
        DwarvenReputation rep = repOrNull(player);
        return rep != null && rep.getTierId() >= minTier;
    }

    /**
     * Checks if player is creative OR has the specified profession endorsement.
     */
    public static boolean hasEndorsement(Player player, DwarfProfession profession) {
        if (player == null) return false;
        if (player.isCreative()) return true;
        return hasEndorsementBypassCreative(player, profession);
    }

    /**
     * Checks if player has the specified profession endorsement (does NOT bypass creative).
     */
    public static boolean hasEndorsementBypassCreative(Player player, DwarfProfession profession) {
        DwarvenReputation rep = repOrNull(player);
        return rep != null && rep.hasEndorsement(profession);
    }

    /**
     * Gets the count of profession endorsements (returns 0 if null).
     * Creative bypass is irrelevant for a numeric query; use hasEndorsement(...) for gating.
     */
    public static int getEndorsementCount(Player player) {
        DwarvenReputation rep = repOrNull(player);
        return rep != null ? rep.getEndorsementCount() : 0;
    }

    /**
     * Returns the full set of endorsed dwarf professions for the given player.
     */
    public static Set<DwarfProfession> getAllEndorsements(Player player) {
        DwarvenReputation rep = repOrNull(player);
        return rep != null ? toProfessions(rep) : Set.of();
    }

    /**
     * Adds a profession endorsement to the player.
     * - Server-side only.
     * - Syncs the client only if the endorsement was newly added.
     */
    public static void addEndorsement(Player player, DwarfProfession profession) {
        if (player == null || profession == null || profession == DwarfProfession.NONE) return;
        if (player.level().isClientSide()) return;

        DwarvenReputation rep = player.getData(JolCraftAttachments.DWARVEN_REP.get());

        if (!rep.addEndorsement(profession)) return;

        if (player instanceof ServerPlayer serverPlayer) {
            JolCraftNetworking.sendToClient(serverPlayer, new ClientboundEndorsementsPacket(toProfessions(rep)));
        }
    }

    /**
     * Gets the player's Dwarven Reputation tier (returns 0 if null).
     * Creative bypass is irrelevant for a numeric query; use hasTier(...) for gating.
     */
    public static int getTier(Player player) {
        DwarvenReputation rep = repOrNull(player);
        return rep != null ? rep.getTierId() : 0;
    }

    /**
     * Sets the reputation tier for the player.
     * - Server-side only.
     * - Syncs the client view if the player is a ServerPlayer.
     */
    public static void setReputationTier(Player player, int tier) {
        if (player == null) return;
        if (player.level().isClientSide()) return;

        DwarvenReputation rep = player.getData(JolCraftAttachments.DWARVEN_REP.get());
        rep.setTierId(tier);

        if (player instanceof ServerPlayer serverPlayer) {
            JolCraftNetworking.sendToClient(serverPlayer, new ClientboundReputationPacket(tier));
        }
    }

    /**
     * Returns the language key representing the given reputation tier getId.
     * Intended for logging and other server-side diagnostics.
     */
    public static String getTierLangKey(int tier) {
        return DwarvenReputationTier.fromId(tier).langKey();
    }

    private static Set<DwarfProfession> toProfessions(DwarvenReputation rep) {
        return rep.getEndorsements().stream()
                .map(id -> DwarfProfession.byId(id.getPath()))
                .filter(prof -> prof != DwarfProfession.NONE)
                .collect(Collectors.toUnmodifiableSet());
    }
}