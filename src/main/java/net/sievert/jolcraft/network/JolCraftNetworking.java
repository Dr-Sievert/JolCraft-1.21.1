package net.sievert.jolcraft.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.gui.custom.menu.DwarfMerchantMenu;
import net.sievert.jolcraft.network.packet.c2s.ServerboundDwarfSelectTradePacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundAncientLanguagePacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDeliriumPacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundDwarfMerchantOffersPacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundEndorsementsPacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundLanguagePacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundLoreUnlocksPacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundParticlePacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundPlaySoundPacket;
import net.sievert.jolcraft.network.packet.s2c.ClientboundReputationPacket;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

public class JolCraftNetworking {

    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(JolCraft.MOD_ID).versioned("1.0");

        registrar.playToServer(
                ServerboundDwarfSelectTradePacket.TYPE,
                ServerboundDwarfSelectTradePacket.CODEC,
                JolCraftNetworking::handleServerboundDwarfSelectTrade
        );

        registrar
                .playToClient(ClientboundDeliriumPacket.TYPE, ClientboundDeliriumPacket.CODEC, JolCraftNetworking::handleClientboundDelirium)
                .playToClient(ClientboundLanguagePacket.TYPE, ClientboundLanguagePacket.CODEC, JolCraftNetworking::handleClientboundLanguage)
                .playToClient(ClientboundAncientLanguagePacket.TYPE, ClientboundAncientLanguagePacket.CODEC, JolCraftNetworking::handleClientboundAncientLanguage)
                .playToClient(ClientboundReputationPacket.TYPE, ClientboundReputationPacket.CODEC, JolCraftNetworking::handleClientboundReputation)
                .playToClient(ClientboundEndorsementsPacket.TYPE, ClientboundEndorsementsPacket.CODEC, JolCraftNetworking::handleClientboundEndorsements)
                .playToClient(ClientboundLoreUnlocksPacket.TYPE, ClientboundLoreUnlocksPacket.CODEC, JolCraftNetworking::handleClientboundLoreUnlocks)
                .playToClient(ClientboundDwarfMerchantOffersPacket.TYPE, ClientboundDwarfMerchantOffersPacket.CODEC, JolCraftNetworking::handleClientboundDwarfMerchantOffers)
                .playToClient(ClientboundPlaySoundPacket.TYPE, ClientboundPlaySoundPacket.CODEC, JolCraftNetworking::handleClientboundPlaySound)
                .playToClient(ClientboundParticlePacket.TYPE, ClientboundParticlePacket.CODEC, JolCraftNetworking::handleClientboundParticle);
    }

    public static void handleServerboundDwarfSelectTrade(
            ServerboundDwarfSelectTradePacket packet,
            IPayloadContext context
    ) {
        context.enqueueWork(() -> {
            var player = context.player();
            if (!(player instanceof ServerPlayer sp)) return;

            if (!(sp.containerMenu instanceof DwarfMerchantMenu menu)) return;

            int selected = packet.item();

            var offers = menu.getOffers();
            if (selected < 0 || selected >= offers.size()) return;

            if (!menu.stillValid(sp)) return;

            var trader = menu.getTrader();
            var tradingPlayer = trader.getTradingPlayer();
            if (tradingPlayer != null && tradingPlayer != sp) return;

            menu.setSelectionHint(selected);
            menu.tryMoveItems(selected);
        });
    }

    private static void handleClientboundParticle(ClientboundParticlePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    private static void handleClientboundPlaySound(ClientboundPlaySoundPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    private static void handleClientboundDwarfMerchantOffers(ClientboundDwarfMerchantOffersPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    private static void handleClientboundLoreUnlocks(ClientboundLoreUnlocksPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    private static void handleClientboundDelirium(ClientboundDeliriumPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    private static void handleClientboundLanguage(ClientboundLanguagePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    private static void handleClientboundAncientLanguage(ClientboundAncientLanguagePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    private static void handleClientboundReputation(ClientboundReputationPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    private static void handleClientboundEndorsements(ClientboundEndorsementsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    public static void sendToClient(ServerPlayer player, CustomPacketPayload payload) {
        player.connection.send(payload);
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