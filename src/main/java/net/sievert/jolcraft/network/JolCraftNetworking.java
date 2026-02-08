package net.sievert.jolcraft.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.network.handler.JolCraftClientPayloadHandlers;
import net.sievert.jolcraft.network.handler.JolCraftServerPayloadHandlers;
import net.sievert.jolcraft.network.packet.c2s.ServerboundDwarfSelectTradePacket;
import net.sievert.jolcraft.network.packet.c2s.ServerboundPlaySoundPacket;
import net.sievert.jolcraft.network.packet.c2s.ServerboundSpawnParticlePacket;
import net.sievert.jolcraft.network.packet.s2c.*;

public final class JolCraftNetworking {

    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(JolCraft.MOD_ID).versioned("1.0");

        registrar
                .playToServer(ServerboundDwarfSelectTradePacket.TYPE, ServerboundDwarfSelectTradePacket.CODEC, JolCraftServerPayloadHandlers::handleServerboundDwarfSelectTrade)
                .playToServer(ServerboundPlaySoundPacket.TYPE, ServerboundPlaySoundPacket.CODEC, JolCraftServerPayloadHandlers::handleServerboundPlayWorldSound)
                .playToServer(ServerboundSpawnParticlePacket.TYPE, ServerboundSpawnParticlePacket.CODEC, JolCraftServerPayloadHandlers::handleServerboundSpawnWorldParticle);

        registrar
                .playToClient(ClientboundDeliriumPacket.TYPE, ClientboundDeliriumPacket.CODEC, JolCraftClientPayloadHandlers::handleClientboundDelirium)
                .playToClient(ClientboundLanguagePacket.TYPE, ClientboundLanguagePacket.CODEC, JolCraftClientPayloadHandlers::handleClientboundLanguage)
                .playToClient(ClientboundAncientLanguagePacket.TYPE, ClientboundAncientLanguagePacket.CODEC, JolCraftClientPayloadHandlers::handleClientboundAncientLanguage)
                .playToClient(ClientboundReputationPacket.TYPE, ClientboundReputationPacket.CODEC, JolCraftClientPayloadHandlers::handleClientboundReputation)
                .playToClient(ClientboundEndorsementsPacket.TYPE, ClientboundEndorsementsPacket.CODEC, JolCraftClientPayloadHandlers::handleClientboundEndorsements)
                .playToClient(ClientboundLoreUnlocksPacket.TYPE, ClientboundLoreUnlocksPacket.CODEC, JolCraftClientPayloadHandlers::handleClientboundLoreUnlocks)
                .playToClient(ClientboundDwarfMerchantOffersPacket.TYPE, ClientboundDwarfMerchantOffersPacket.CODEC, JolCraftClientPayloadHandlers::handleClientboundDwarfMerchantOffers);
    }

    public static final double DEFAULT_RADIUS = 32.0D;

    public static void sendToServer(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
    }

    public static void sendToClient(ServerPlayer player, CustomPacketPayload payload) {
        player.connection.send(payload);
    }

    public static void sendToNearbyClients(Level world, BlockPos pos, CustomPacketPayload payload) {
        sendToNearbyClients(world, pos, DEFAULT_RADIUS, payload);
    }

    public static void sendToNearbyClients(Level world, BlockPos pos, double radius, CustomPacketPayload payload) {
        if (!(world instanceof ServerLevel serverLevel)) return;

        double radiusSq = radius * radius;
        for (ServerPlayer player : serverLevel.players()) {
            if (player.blockPosition().distSqr(pos) <= radiusSq) {
                sendToClient(player, payload);
            }
        }
    }
}