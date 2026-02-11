package net.sievert.jolcraft.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.sievert.jolcraft.data.id.network.JolCraftNetworkIds;
import net.sievert.jolcraft.network.handler.JolCraftClientPayloadHandlers;
import net.sievert.jolcraft.network.handler.JolCraftServerPayloadHandlers;
import net.sievert.jolcraft.network.packet.c2s.ServerboundDwarfSelectTradePacket;
import net.sievert.jolcraft.network.packet.c2s.ServerboundPlaySoundPacket;
import net.sievert.jolcraft.network.packet.c2s.ServerboundSpawnParticlePacket;
import net.sievert.jolcraft.network.packet.s2c.*;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.JolCraft;

public final class JolCraftNetworking {

    public static final String PROTOCOL = JolCraftNetworkIds.PROTOCOL;

    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(JolCraft.MOD_ID).versioned(PROTOCOL);

        JolCraftLogs.info(JolCraftLogTags.INIT, "Registering JolCraft networking payloads");

        registrar
                .playToServer(ServerboundDwarfSelectTradePacket.TYPE, ServerboundDwarfSelectTradePacket.CODEC, JolCraftServerPayloadHandlers::handleServerboundDwarfSelectTrade)
                .playToServer(ServerboundPlaySoundPacket.TYPE, ServerboundPlaySoundPacket.CODEC, JolCraftServerPayloadHandlers::handleServerboundPlayWorldSound)
                .playToServer(ServerboundSpawnParticlePacket.TYPE, ServerboundSpawnParticlePacket.CODEC, JolCraftServerPayloadHandlers::handleServerboundSpawnWorldParticle);

        registrar
                .playToClient(ClientboundDeliriumCursePacket.TYPE, ClientboundDeliriumCursePacket.CODEC, JolCraftClientPayloadHandlers::handleClientboundDelirium)
                .playToClient(ClientboundDwarvenLanguagePacket.TYPE, ClientboundDwarvenLanguagePacket.CODEC, JolCraftClientPayloadHandlers::handleClientboundLanguage)
                .playToClient(ClientboundAncientDwarvenLanguagePacket.TYPE, ClientboundAncientDwarvenLanguagePacket.CODEC, JolCraftClientPayloadHandlers::handleClientboundAncientLanguage)
                .playToClient(ClientboundReputationPacket.TYPE, ClientboundReputationPacket.CODEC, JolCraftClientPayloadHandlers::handleClientboundReputation)
                .playToClient(ClientboundEndorsementsPacket.TYPE, ClientboundEndorsementsPacket.CODEC, JolCraftClientPayloadHandlers::handleClientboundEndorsements)
                .playToClient(ClientboundLoreUnlocksPacket.TYPE, ClientboundLoreUnlocksPacket.CODEC, JolCraftClientPayloadHandlers::handleClientboundLoreUnlocks)
                .playToClient(ClientboundDwarfMerchantOffersPacket.TYPE, ClientboundDwarfMerchantOffersPacket.CODEC, JolCraftClientPayloadHandlers::handleClientboundDwarfMerchantOffers);
        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Registered networking payloads (protocol version {})",
                PROTOCOL
        );
    }

    public static final double DEFAULT_RADIUS = 32.0D;

    public static void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }

    public static void sendToClient(ServerPlayer player, CustomPacketPayload payload) {
        player.connection.send(payload);
        JolCraftLogs.debug(
                JolCraftLogTags.NETWORK,
                "Sent {} to {}",
                payload.type().id(),
                player.getGameProfile().getName()
        );
    }

    public static void sendToNearbyClients(Level world, BlockPos pos, CustomPacketPayload payload) {
        sendToNearbyClients(world, pos, DEFAULT_RADIUS, payload);
    }

    public static void sendToNearbyClients(Level world, BlockPos pos, double radius, CustomPacketPayload payload) {
        if (!(world instanceof ServerLevel serverLevel)) return;

        double radiusSq = radius * radius;
        int sent = 0;

        for (ServerPlayer player : serverLevel.players()) {
            if (player.blockPosition().distSqr(pos) <= radiusSq) {
                sendToClient(player, payload);
                sent++;
            }
        }
        JolCraftLogs.debug(
                JolCraftLogTags.NETWORK,
                "Sent {} to {} nearby client(s) at {} (r={})",
                payload.type().id(),
                sent,
                pos,
                radius
        );
    }
}