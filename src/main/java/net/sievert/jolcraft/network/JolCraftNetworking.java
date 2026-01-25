package net.sievert.jolcraft.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.gui.custom.dwarf.DwarfMerchantMenu;
import net.sievert.jolcraft.network.packet.C2S.ServerboundDwarfSelectTradePacket;
import net.sievert.jolcraft.network.packet.S2C.*;

import java.util.EnumSet;
import java.util.List;

public class JolCraftNetworking {

    public static void register(RegisterPayloadHandlersEvent event) {
        var registrar = event.registrar(JolCraft.MOD_ID).versioned("1.0");

        registrar.playToServer(
                ServerboundDwarfSelectTradePacket.TYPE,
                ServerboundDwarfSelectTradePacket.CODEC,
                JolCraftNetworking::handleServerboundDwarfSelectTrade
        );

        final boolean isClient = FMLEnvironment.dist.isClient();

        if (isClient) {
            registrar
                    .playToClient(ClientboundDeliriumPacket.TYPE, ClientboundDeliriumPacket.CODEC, ClientHandlers::handleDelirium)
                    .playToClient(ClientboundLanguagePacket.TYPE, ClientboundLanguagePacket.CODEC, ClientHandlers::handleSyncLanguage)
                    .playToClient(ClientboundAncientLanguagePacket.TYPE, ClientboundAncientLanguagePacket.CODEC, ClientHandlers::handleSyncAncientLanguage)
                    .playToClient(ClientboundReputationPacket.TYPE, ClientboundReputationPacket.CODEC, ClientHandlers::handleSyncReputation)
                    .playToClient(ClientboundEndorsementsPacket.TYPE, ClientboundEndorsementsPacket.CODEC, ClientHandlers::handleSyncEndorsements)
                    .playToClient(ClientboundLoreUnlocksPacket.TYPE, ClientboundLoreUnlocksPacket.CODEC, ClientHandlers::handleSyncTomeUnlocks)
                    .playToClient(ClientboundDwarfMerchantOffersPacket.TYPE, ClientboundDwarfMerchantOffersPacket.CODEC, ClientHandlers::handleDwarfMerchantOffers)
                    .playToClient(ClientboundPlaySoundPacket.TYPE, ClientboundPlaySoundPacket.CODEC, ClientHandlers::handlePlaySound)
                    .playToClient(ClientboundParticlePacket.TYPE, ClientboundParticlePacket.CODEC, ClientHandlers::handleParticle);
        } else {
            registrar
                    .playToClient(ClientboundDeliriumPacket.TYPE, ClientboundDeliriumPacket.CODEC, JolCraftNetworking::handleNoopClientbound)
                    .playToClient(ClientboundLanguagePacket.TYPE, ClientboundLanguagePacket.CODEC, JolCraftNetworking::handleNoopClientbound)
                    .playToClient(ClientboundAncientLanguagePacket.TYPE, ClientboundAncientLanguagePacket.CODEC, JolCraftNetworking::handleNoopClientbound)
                    .playToClient(ClientboundReputationPacket.TYPE, ClientboundReputationPacket.CODEC, JolCraftNetworking::handleNoopClientbound)
                    .playToClient(ClientboundEndorsementsPacket.TYPE, ClientboundEndorsementsPacket.CODEC, JolCraftNetworking::handleNoopClientbound)
                    .playToClient(ClientboundLoreUnlocksPacket.TYPE, ClientboundLoreUnlocksPacket.CODEC, JolCraftNetworking::handleNoopClientbound)
                    .playToClient(ClientboundDwarfMerchantOffersPacket.TYPE, ClientboundDwarfMerchantOffersPacket.CODEC, JolCraftNetworking::handleNoopClientbound)
                    .playToClient(ClientboundPlaySoundPacket.TYPE, ClientboundPlaySoundPacket.CODEC, JolCraftNetworking::handleNoopClientbound)
                    .playToClient(ClientboundParticlePacket.TYPE, ClientboundParticlePacket.CODEC, JolCraftNetworking::handleNoopClientbound);
        }
    }

    private static <T extends CustomPacketPayload> void handleNoopClientbound(T payload, IPayloadContext ctx) {}

    // ----------------------------
    // Serverbound handlers (safe on dedicated server)
    // ----------------------------

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

    // ----------------------------
    // Sending helpers (server-side)
    // ----------------------------

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

    // ----------------------------
    // Clientbound handlers (isolated so the server never loads client classes)
    // ----------------------------

    private static final class ClientHandlers {

        private static void handleParticle(ClientboundParticlePacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                var mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.level instanceof net.minecraft.client.multiplayer.ClientLevel clientLevel) {
                    clientLevel.addParticle(
                            packet.particle(),
                            packet.overrideLimiter(),
                            packet.alwaysShow(),
                            packet.x(), packet.y(), packet.z(),
                            packet.vx(), packet.vy(), packet.vz()
                    );
                }
            });
        }

        private static void handlePlaySound(ClientboundPlaySoundPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                var mc = net.minecraft.client.Minecraft.getInstance();
                var player = mc.player;
                if (player == null) return;

                var optHolder = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.get(packet.soundId());
                if (optHolder.isEmpty()) return;

                var sound = optHolder.get().value();
                player.level().playLocalSound(
                        packet.x(), packet.y(), packet.z(),
                        sound,
                        packet.source(),
                        packet.volume(),
                        packet.pitch(),
                        false
                );
            });
        }

        private static void handleDwarfMerchantOffers(ClientboundDwarfMerchantOffersPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {
                var mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.player == null) return;

                var menu = mc.player.containerMenu;
                if (packet.containerId() == menu.containerId && menu instanceof DwarfMerchantMenu dwarfMenu) {
                    dwarfMenu.setOffers(packet.offers());
                    dwarfMenu.setXp(packet.dwarfXp());
                    dwarfMenu.setMerchantLevel(packet.dwarfLevel());
                    dwarfMenu.setShowProgressBar(packet.showProgress());
                    dwarfMenu.setshowLevel(packet.showLevel());
                    dwarfMenu.setCanRestock(packet.canRestock());
                }
            });
        }

        private static void handleSyncTomeUnlocks(ClientboundLoreUnlocksPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> {net.sievert.jolcraft.network.client.data.ClientTomeUnlocksData.setUnlocks(List.copyOf(packet.unlocks()));});
        }

        private static void handleDelirium(ClientboundDeliriumPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> net.sievert.jolcraft.network.client.data.ClientDeliriumData.setMuffleTicks(packet.durationTicks()));
        }

        private static void handleSyncLanguage(ClientboundLanguagePacket packet, IPayloadContext context) {
            context.enqueueWork(() -> net.sievert.jolcraft.network.client.data.ClientLanguageData.setKnows(packet.knowsLanguage()));
        }

        private static void handleSyncAncientLanguage(ClientboundAncientLanguagePacket packet, IPayloadContext context) {
            context.enqueueWork(() -> net.sievert.jolcraft.network.client.data.ClientAncientLanguageData.setKnows(packet.knowsLanguage()));
        }

        private static void handleSyncReputation(ClientboundReputationPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> net.sievert.jolcraft.network.client.data.ClientReputationData.setTier(packet.tier()));
        }

        private static void handleSyncEndorsements(ClientboundEndorsementsPacket packet, IPayloadContext context) {
            context.enqueueWork(() -> net.sievert.jolcraft.network.client.data.ClientReputationData.setEndorsements(EnumSet.copyOf(packet.endorsements())));
        }

    }
}