package net.sievert.jolcraft.network.util;


import net.minecraft.server.level.ServerPlayer;
import net.sievert.jolcraft.data.custom.attachment.language.AncientDwarvenLanguageHelper;
import net.sievert.jolcraft.data.custom.attachment.language.DwarvenLanguageHelper;
import net.sievert.jolcraft.data.custom.attachment.lore.DwarfLoreUnlockHelper;
import net.sievert.jolcraft.data.custom.attachment.reputation.DwarvenReputationHelper;
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
        int tier = DwarvenReputationHelper.getTier(player);
        JolCraftNetworking.sendToClient(player, new ClientboundReputationPacket(tier));

        // Endorsements
        Set<DwarfProfession> endorsements = DwarvenReputationHelper.getAllEndorsements(player);
        JolCraftNetworking.sendToClient(player, new ClientboundEndorsementsPacket(endorsements));

        // Tome Unlocks
        Set<DwarfLoreKey> dwarfTomeUnlocks = DwarfLoreUnlockHelper.getAllUnlocks(player);
        JolCraftNetworking.sendToClient(player, ClientboundLoreUnlocksPacket.fromEnumSet(dwarfTomeUnlocks));

    }
}