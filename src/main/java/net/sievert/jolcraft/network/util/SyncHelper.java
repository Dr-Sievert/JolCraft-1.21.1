package net.sievert.jolcraft.network.util;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.sievert.jolcraft.world.entity.attachment.player.custom.hearth.HearthAttachmentHelper;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDeliriumCursePacket;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.custom.HearthBlock;
import net.sievert.jolcraft.world.block.entity.custom.HearthBlockEntity;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.entity.effect.custom.harmful.curse.DeliriumCurseEffect;

/**
 * Handles initial sync of all JolCraft data for a joining player.
 */
public class SyncHelper {

    public static void syncAll(ServerPlayer player) {

        syncDelirium(player);
        syncHearth(player);

        JolCraftLogs.debug(JolCraftLogTags.NETWORK, "Synced data for {}", player.getGameProfile().getName());
    }

    private static void syncDelirium(ServerPlayer player) {
        var effect = player.getEffect(JolCraftEffects.DELIRIUM_CURSE);
        if (effect == null) return;

        int remaining = DeliriumCurseEffect.getRemainingEpisodeTicks(player);
        if (remaining <= 0) return;

        JolCraftNetworking.sendToClient(player, new ClientboundDeliriumCursePacket(remaining));
    }

    private static void syncHearth(ServerPlayer player) {
        if (!HearthAttachmentHelper.hasActiveHearth(player)) return;

        BlockPos hearthPos = HearthAttachmentHelper.activeHearthPos(player);
        if (hearthPos == null) return;

        var state = player.level().getBlockState(hearthPos);
        if (!(state.getBlock() instanceof HearthBlock)) {
            HearthAttachmentHelper.clearActiveHearthPos(player);
            return;
        }

        if (state.getValue(HearthBlock.HALF) != DoubleBlockHalf.LOWER) {
            HearthAttachmentHelper.clearActiveHearthPos(player);
            return;
        }

        BlockEntity blockEntity = player.level().getBlockEntity(hearthPos);
        if (!(blockEntity instanceof HearthBlockEntity hearth)) {
            HearthAttachmentHelper.clearActiveHearthPos(player);
            return;
        }

        if (!player.getUUID().equals(hearth.getOwner())) {
            HearthAttachmentHelper.clearActiveHearthPos(player);
        }
    }
}
