package net.sievert.jolcraft.data.util.attachment;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.S2C.*;

import java.util.Set;

/**
 * Handles initial sync of all JolCraft attachment data for a joining player.
 */
public class AttachmentSyncHelper {

    public static void syncAll(ServerPlayer player) {

        // Dwarvish language
        boolean knowsLang = net.sievert.jolcraft.data.util.attachment.DwarvenLanguageHelper.knowsDwarvishBypassCreative(player);
        JolCraftNetworking.sendToClient(player, new ClientboundLanguagePacket(knowsLang));

        // Ancient Dwarvish language
        boolean knowsAncient = net.sievert.jolcraft.data.util.attachment.AncientDwarvenLanguageHelper.knowsAncientDwarvishBypassCreative(player);
        JolCraftNetworking.sendToClient(player, new ClientboundAncientLanguagePacket(knowsAncient));

        // Reputation tier
        int tier = net.sievert.jolcraft.data.util.attachment.DwarvenReputationHelper.getTierBypassCreative(player);
        JolCraftNetworking.sendToClient(player, new ClientboundReputationPacket(tier));

        // Endorsements
        Set<DwarfProfession> endorsements = net.sievert.jolcraft.data.util.attachment.DwarvenReputationHelper.getAllEndorsementsBypassCreative(player);
        JolCraftNetworking.sendToClient(player, new ClientboundEndorsementsPacket(endorsements));

        // Tome Unlocks
        Set<String> unlocks = net.sievert.jolcraft.data.util.attachment.TomeUnlockHelper.getAllUnlocks(player);
        JolCraftNetworking.sendToClient(player, new ClientboundTomeUnlocksPacket(unlocks));
    }
}