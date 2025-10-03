package net.sievert.jolcraft.data.util.attachment;


import net.minecraft.server.level.ServerPlayer;
import net.sievert.jolcraft.data.util.attachment.language.AncientDwarvenLanguageHelper;
import net.sievert.jolcraft.data.util.attachment.language.DwarvenLanguageHelper;
import net.sievert.jolcraft.data.util.attachment.lore.DwarfTomeHelper;
import net.sievert.jolcraft.data.util.attachment.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.data.custom.lore.dwarf.DwarfLoreKey;
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
        boolean knowsLang = DwarvenLanguageHelper.knowsDwarvishBypassCreative(player);
        JolCraftNetworking.sendToClient(player, new ClientboundLanguagePacket(knowsLang));

        // Ancient Dwarvish language
        boolean knowsAncient = AncientDwarvenLanguageHelper.knowsAncientDwarvishBypassCreative(player);
        JolCraftNetworking.sendToClient(player, new ClientboundAncientLanguagePacket(knowsAncient));

        // Reputation tier
        int tier = DwarvenReputationHelper.getTierBypassCreative(player);
        JolCraftNetworking.sendToClient(player, new ClientboundReputationPacket(tier));

        // Endorsements
        Set<DwarfProfession> endorsements = DwarvenReputationHelper.getAllEndorsementsBypassCreative(player);
        JolCraftNetworking.sendToClient(player, new ClientboundEndorsementsPacket(endorsements));

        // Tome Unlocks
        Set<DwarfLoreKey> dwarfTomeUnlocks = DwarfTomeHelper.getAllUnlocks(player);
        JolCraftNetworking.sendToClient(player, ClientboundTomeUnlocksPacket.fromEnumSet(dwarfTomeUnlocks));

    }
}