package net.sievert.jolcraft.data.attachment.custom.reputation;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;
import net.sievert.jolcraft.world.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundEndorsementsPacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundReputationPacket;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

import java.util.Set;

public final class DwarvenReputationHelper {

    private DwarvenReputationHelper() {}

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
        if (player == null) return false;
        DwarvenReputation rep = JolCraftProxy.access().getAttachment(JolCraftAttachments.DWARVEN_REP.get(), player);
        return rep != null && rep.getTier() >= minTier;
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
        if (player == null) return false;
        DwarvenReputation rep = JolCraftProxy.access().getAttachment(JolCraftAttachments.DWARVEN_REP.get(), player);
        return rep != null && rep.hasEndorsement(profession);
    }

    /**
     * Gets the count of profession endorsements (returns 0 if null).
     * (Creative bypass is irrelevant for a numeric query; use hasEndorsement(...) for gating.)
     */
    public static int getEndorsementCount(Player player) {
        if (player == null) return 0;
        DwarvenReputation rep = JolCraftProxy.access().getAttachment(JolCraftAttachments.DWARVEN_REP.get(), player);
        return rep != null ? rep.getEndorsementCount() : 0;
    }

    /**
     * Gets the full set of profession endorsements for the player (returns empty if null).
     * (Creative bypass is irrelevant for a set query; use hasEndorsement(...) for gating.)
     */
    public static Set<DwarfProfession> getAllEndorsements(Player player) {
        if (player == null) return Set.of();
        DwarvenReputation rep = JolCraftProxy.access().getAttachment(JolCraftAttachments.DWARVEN_REP.get(), player);
        return rep != null ? rep.getEndorsements() : Set.of();
    }


    /**
     * Adds a profession endorsement to the player.
     * Only call this on the server. Also syncs the client view.
     */
    public static void addEndorsement(Player player, DwarfProfession profession) {
        if (player == null || profession == null || profession == DwarfProfession.NONE) return;
        DwarvenReputation rep = player.getData(JolCraftAttachments.DWARVEN_REP.get());
        rep.addEndorsement(profession);
        if (player instanceof ServerPlayer serverPlayer) {
            JolCraftNetworking.sendToClient(serverPlayer, new ClientboundEndorsementsPacket(rep.getEndorsements()));
        }
    }

    /**
     * Gets the player's Dwarven Reputation tier (returns 0 if null).
     * (Creative bypass is irrelevant for a numeric query; use hasTier(...) for gating.)
     */
    public static int getTier(Player player) {
        if (player == null) return 0;
        DwarvenReputation rep = JolCraftProxy.access().getAttachment(JolCraftAttachments.DWARVEN_REP.get(), player);
        return rep != null ? rep.getTier() : 0;
    }

    /**
     * Sets the reputation tier for the player.
     * Only call this on the server. Also syncs the client view.
     */
    public static void setReputationTier(Player player, int tier) {
        if (player == null) return;
        DwarvenReputation rep = player.getData(JolCraftAttachments.DWARVEN_REP.get());
        rep.setTier(tier);
        if (player instanceof ServerPlayer serverPlayer) {
            JolCraftNetworking.sendToClient(serverPlayer, new ClientboundReputationPacket(tier));
        }
    }
}