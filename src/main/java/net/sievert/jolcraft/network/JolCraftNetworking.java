package net.sievert.jolcraft.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.network.client.data.*;
import net.sievert.jolcraft.network.packet.C2S.ServerboundDwarfSelectTradePacket;
import net.sievert.jolcraft.network.packet.S2C.*;
import net.sievert.jolcraft.gui.custom.dwarf.DwarfMerchantMenu;

import java.util.Set;

public class JolCraftNetworking {

    public static void register(RegisterPayloadHandlersEvent event) {
        event.registrar(JolCraft.MOD_ID)
                .versioned("1.0")
                .playToClient(
                        ClientboundDeliriumPacket.TYPE,
                        ClientboundDeliriumPacket.CODEC,
                        JolCraftNetworking::handleDelirium
                )
                .playToClient(
                        ClientboundLanguagePacket.TYPE,
                        ClientboundLanguagePacket.CODEC,
                        JolCraftNetworking::handleSyncLanguage
                )
                .playToClient(
                        ClientboundAncientLanguagePacket.TYPE,
                        ClientboundAncientLanguagePacket.CODEC,
                        JolCraftNetworking::handleSyncAncientLanguage
                )
                .playToClient(
                        ClientboundReputationPacket.TYPE,
                        ClientboundReputationPacket.CODEC,
                        JolCraftNetworking::handleSyncReputation
                )
                .playToClient(
                        ClientboundEndorsementsPacket.TYPE,
                        ClientboundEndorsementsPacket.CODEC,
                        JolCraftNetworking::handleSyncEndorsements
                )
                .playToClient(
                        ClientboundLoreUnlocksPacket.TYPE,
                        ClientboundLoreUnlocksPacket.CODEC,
                        JolCraftNetworking::handleSyncTomeUnlocks
                )
                .playToClient(
                        ClientboundDwarfMerchantOffersPacket.TYPE,
                        ClientboundDwarfMerchantOffersPacket.CODEC,
                        JolCraftNetworking::handleDwarfMerchantOffers
                )
                .playToServer(
                        ServerboundDwarfSelectTradePacket.TYPE,
                        ServerboundDwarfSelectTradePacket.CODEC,
                        JolCraftNetworking::handleServerboundDwarfSelectTrade
                )
                .playToClient(
                        ClientboundPlaySoundPacket.TYPE,
                        ClientboundPlaySoundPacket.CODEC,
                        JolCraftNetworking::handlePlaySound
                )
                .playToClient(
                        ClientboundParticlePacket.TYPE,
                        ClientboundParticlePacket.CODEC,
                        JolCraftNetworking::handleParticle
                );


    }

    public static void handleParticle(ClientboundParticlePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var mc = Minecraft.getInstance();
            if (mc.level instanceof ClientLevel clientLevel) {
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

    public static void handlePlaySound(ClientboundPlaySoundPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            var mc = Minecraft.getInstance();
            var player = mc.player;
            if (player == null) return;

            var optHolder = BuiltInRegistries.SOUND_EVENT.get(packet.soundId());
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


    public static void handleServerboundDwarfSelectTrade(ServerboundDwarfSelectTradePacket packet, net.neoforged.neoforge.network.handling.IPayloadContext context) {
        context.enqueueWork(() -> {
            var player = context.player();

            if (player.containerMenu instanceof DwarfMerchantMenu menu) {
                menu.setSelectionHint(packet.getItem());
                menu.tryMoveItems(packet.getItem());
            }
        });
    }

    public static void handleDwarfMerchantOffers(ClientboundDwarfMerchantOffersPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            assert mc.player != null;
            AbstractContainerMenu abstractContainerMenu = mc.player.containerMenu;
            if (packet.containerId() == abstractContainerMenu.containerId && abstractContainerMenu instanceof DwarfMerchantMenu dwarfMenu) {
                dwarfMenu.setOffers(packet.offers());
                dwarfMenu.setXp(packet.dwarfXp());
                dwarfMenu.setMerchantLevel(packet.dwarfLevel());
                dwarfMenu.setShowProgressBar(packet.showProgress());
                dwarfMenu.setshowLevel(packet.showLevel());
                dwarfMenu.setCanRestock(packet.canRestock());
            }
        });
    }

    public static void handleSyncTomeUnlocks(ClientboundLoreUnlocksPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientTomeUnlocksData.setUnlocks(packet.unlocks());
        });
    }

    public static void handleDelirium(ClientboundDeliriumPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientDeliriumData.setMuffleTicks(packet.durationTicks()));
    }

    public static void handleSyncLanguage(ClientboundLanguagePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientLanguageData.setKnows(packet.knowsLanguage()));
    }

    public static void handleSyncAncientLanguage(ClientboundAncientLanguagePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientAncientLanguageData.setKnows(packet.knowsLanguage()));
    }

    public static void handleSyncReputation(ClientboundReputationPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientReputationData.setTier(packet.tier()));
    }

    public static void handleSyncEndorsements(ClientboundEndorsementsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Set<DwarfProfession> set = packet.endorsements();
            ClientReputationData.setEndorsements(set);
        });
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
