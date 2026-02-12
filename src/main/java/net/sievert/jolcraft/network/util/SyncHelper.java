package net.sievert.jolcraft.network.util;

import net.minecraft.server.level.ServerPlayer;
import net.sievert.jolcraft.data.attachment.custom.language.ancient.AncientDwarvenLanguageHelper;
import net.sievert.jolcraft.data.attachment.custom.language.DwarvenLanguageHelper;
import net.sievert.jolcraft.data.attachment.custom.lore.DwarfTomeUnlockHelper;
import net.sievert.jolcraft.data.attachment.custom.reputation.DwarvenReputationHelper;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundAncientDwarvenLanguagePacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDeliriumCursePacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDwarfTomeUnlocksPacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDwarvenEndorsementsPacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDwarvenLanguagePacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDwarvenReputationPacket;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.effect.custom.curse.DeliriumCurseEffect;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;

import java.util.Set;

/**
 * Handles initial sync of all JolCraft data for a joining player.
 */
public class SyncHelper {

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
        Set<DwarfLoreKey> dwarfTomeUnlocks = DwarfTomeUnlockHelper.getAllUnlocks(player);
        JolCraftNetworking.sendToClient(player, ClientboundDwarfTomeUnlocksPacket.fromEnumSet(dwarfTomeUnlocks));

        // Delirium episode window (muffle) relog safety
        int deliriumRemaining = DeliriumCurseEffect.getRemainingEpisodeTicks(player);
        if (deliriumRemaining > 0) {
            JolCraftNetworking.sendToClient(player, new ClientboundDeliriumCursePacket(deliriumRemaining));
        }

        JolCraftLogs.debug(JolCraftLogTags.NETWORK, "Synced data for {}", player.getGameProfile().getName());
    }
}
