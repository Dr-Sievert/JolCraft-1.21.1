package net.sievert.jolcraft.util.attachment;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftAttachments;
import net.sievert.jolcraft.data.custom.attachment.rep.DwarvenReputation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.S2C.ClientboundEndorsementsPacket;
import net.sievert.jolcraft.network.packet.S2C.ClientboundReputationPacket;

import java.util.Set;

/**
 * Helper for Dwarven Reputation attachment, using the JolCraftProxy system for server/client safety.
 */
public class DwarvenReputationHelper {

    // --- Universal access (safe both sides) ---

    /**
     * Checks if player is creative OR has at least the specified tier.
     */
    public static boolean hasTier(Player player, int minTier) {
        if (player == null) return false;
        if (player.isCreative()) return true;
        DwarvenReputation rep = JolCraft.PROXY.getAttachment(JolCraftAttachments.DWARVEN_REP.get(), player);
        return rep != null && rep.getTier() >= minTier;
    }

    /**
     * Checks if player has at least the specified tier (does NOT bypass creative).
     */
    public static boolean hasTierBypassCreative(Player player, int minTier) {
        if (player == null) return false;
        DwarvenReputation rep = JolCraft.PROXY.getAttachment(JolCraftAttachments.DWARVEN_REP.get(), player);
        return rep != null && rep.getTier() >= minTier;
    }

    /**
     * Checks if player is creative OR has the specified profession endorsement.
     */
    public static boolean hasEndorsement(Player player, ResourceLocation professionId) {
        if (player == null) return false;
        if (player.isCreative()) return true;
        DwarvenReputation rep = JolCraft.PROXY.getAttachment(JolCraftAttachments.DWARVEN_REP.get(), player);
        return rep != null && rep.hasEndorsement(professionId);
    }

    /**
     * Checks if player has the specified profession endorsement (does NOT bypass creative).
     */
    public static boolean hasEndorsementBypassCreative(Player player, ResourceLocation professionId) {
        if (player == null) return false;
        DwarvenReputation rep = JolCraft.PROXY.getAttachment(JolCraftAttachments.DWARVEN_REP.get(), player);
        return rep != null && rep.hasEndorsement(professionId);
    }

    /**
     * Gets the count of profession endorsements (bypasses creative, returns 0 if null).
     */
    public static int getEndorsementCount(Player player) {
        if (player == null) return 0;
        DwarvenReputation rep = JolCraft.PROXY.getAttachment(JolCraftAttachments.DWARVEN_REP.get(), player);
        return rep != null ? rep.getEndorsementCount() : 0;
    }

    /**
     * Gets the count of profession endorsements (bypasses creative, returns 0 if null).
     */
    public static int getEndorsementCountBypassCreative(Player player) {
        if (player == null) return 0;
        DwarvenReputation rep = JolCraft.PROXY.getAttachment(JolCraftAttachments.DWARVEN_REP.get(), player);
        return rep != null ? rep.getEndorsementCount() : 0;
    }

    /**
     * Gets the full set of profession endorsements (bypasses creative, returns empty if null).
     */
    public static Set<ResourceLocation> getAllEndorsements(Player player) {
        if (player == null) return Set.of();
        DwarvenReputation rep = JolCraft.PROXY.getAttachment(JolCraftAttachments.DWARVEN_REP.get(), player);
        return rep != null ? rep.getEndorsements() : Set.of();
    }

    /**
     * Gets the full set of profession endorsements (bypasses creative, returns empty if null).
     */
    public static Set<ResourceLocation> getAllEndorsementsBypassCreative(Player player) {
        if (player == null) return Set.of();
        DwarvenReputation rep = JolCraft.PROXY.getAttachment(JolCraftAttachments.DWARVEN_REP.get(), player);
        return rep != null ? rep.getEndorsements() : Set.of();
    }

    /**
     * Adds a profession endorsement to the player.
     * Only call this on the server. Also syncs the client view.
     */
    public static void addEndorsement(Player player, ResourceLocation professionId) {
        if (player == null) return;
        DwarvenReputation rep = player.getData(JolCraftAttachments.DWARVEN_REP.get());
        rep.addEndorsement(professionId);
        if (player instanceof ServerPlayer serverPlayer) {
            JolCraftNetworking.sendToClient(serverPlayer,
                    new ClientboundEndorsementsPacket(rep.getEndorsements()));
        }
    }

    /**
     * Gets the player's Dwarven Reputation tier (bypasses creative, returns 0 if null).
     */
    public static int getTier(Player player) {
        if (player == null) return 0;
        DwarvenReputation rep = JolCraft.PROXY.getAttachment(JolCraftAttachments.DWARVEN_REP.get(), player);
        return rep != null ? rep.getTier() : 0;
    }

    /**
     * Gets the player's Dwarven Reputation tier (bypasses creative mode).
     */
    public static int getTierBypassCreative(Player player) {
        if (player == null) return 0;
        DwarvenReputation rep = JolCraft.PROXY.getAttachment(JolCraftAttachments.DWARVEN_REP.get(), player);
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
            JolCraftNetworking.sendToClient(serverPlayer,
                    new ClientboundReputationPacket(tier));
        }
    }

    // --- CLIENT utility methods (for local player only) ---

    @OnlyIn(Dist.CLIENT)
    public static boolean hasClientTier(int minTier) {
        Player player = Minecraft.getInstance().player;
        return hasTier(player, minTier);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean hasClientTierBypassCreative(int minTier) {
        Player player = Minecraft.getInstance().player;
        return hasTierBypassCreative(player, minTier);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean hasClientEndorsement(ResourceLocation professionId) {
        Player player = Minecraft.getInstance().player;
        return hasEndorsement(player, professionId);
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean hasClientEndorsementBypassCreative(ResourceLocation professionId) {
        Player player = Minecraft.getInstance().player;
        return hasEndorsementBypassCreative(player, professionId);
    }

    @OnlyIn(Dist.CLIENT)
    public static int getClientTier() {
        Player player = Minecraft.getInstance().player;
        return getTier(player);
    }

    @OnlyIn(Dist.CLIENT)
    public static int getClientEndorsementCount() {
        Player player = Minecraft.getInstance().player;
        return getEndorsementCount(player);
    }

    @OnlyIn(Dist.CLIENT)
    public static Set<ResourceLocation> getAllClientEndorsements() {
        Player player = Minecraft.getInstance().player;
        return getAllEndorsements(player);
    }
}
