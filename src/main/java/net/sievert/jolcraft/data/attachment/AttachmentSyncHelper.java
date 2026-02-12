package net.sievert.jolcraft.data.attachment;


import net.minecraft.server.level.ServerPlayer;
import net.sievert.jolcraft.data.attachment.custom.language.ancient.AncientDwarvenLanguageHelper;
import net.sievert.jolcraft.data.attachment.custom.language.DwarvenLanguageHelper;
import net.sievert.jolcraft.data.attachment.custom.lore.DwarfLoreUnlockHelper;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.*;

import java.util.Set;

/**
 * Handles initial sync of all JolCraft attachment data for a joining player.
 */
public class AttachmentSyncHelper {

    public static void syncAll(ServerPlayer player) {

        // Dwarvish language
        boolean knowsLang = DwarvenLanguageHelper.knowsDwarvishBypassCreative(player);
        JolCraftNetworking.sendToClient(player, new ClientboundDwarvenLanguagePacket(knowsLang));

        // Ancient Dwarvish language
        boolean knowsAncient = AncientDwarvenLanguageHelper.knowsAncientDwarvishBypassCreative(player);
        JolCraftNetworking.sendToClient(player, new ClientboundAncientDwarvenLanguagePacket(knowsAncient));

        // Reputation tier
        int tier = DwarvenReputationHelper.getTier(player);
        JolCraftNetworking.sendToClient(player, new ClientboundDwarvenReputationPacket(tier));

        // Endorsements
        Set<DwarfProfession> endorsements = DwarvenReputationHelper.getAllEndorsements(player);
        JolCraftNetworking.sendToClient(player, new ClientboundDwarvenEndorsementsPacket(endorsements));

        // Tome Unlocks
        Set<DwarfLoreKey> dwarfTomeUnlocks = DwarfLoreUnlockHelper.getAllUnlocks(player);
        JolCraftNetworking.sendToClient(player, ClientboundDwarfTomeUnlocksPacket.fromEnumSet(dwarfTomeUnlocks));

        JolCraftLogs.debug(JolCraftLogTags.NETWORK, "Synced attachments for {}", player.getGameProfile().getName());
    }
}