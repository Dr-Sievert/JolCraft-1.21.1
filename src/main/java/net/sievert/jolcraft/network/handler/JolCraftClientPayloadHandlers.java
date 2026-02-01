package net.sievert.jolcraft.network.handler;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sievert.jolcraft.network.packet.s2c.*;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

public final class JolCraftClientPayloadHandlers {

    private JolCraftClientPayloadHandlers() {}

    public static void handleClientboundDwarfMerchantOffers(ClientboundDwarfMerchantOffersPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    public static void handleClientboundLoreUnlocks(ClientboundLoreUnlocksPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    public static void handleClientboundDelirium(ClientboundDeliriumPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    public static void handleClientboundLanguage(ClientboundLanguagePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    public static void handleClientboundAncientLanguage(ClientboundAncientLanguagePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    public static void handleClientboundReputation(ClientboundReputationPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    public static void handleClientboundEndorsements(ClientboundEndorsementsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }
}