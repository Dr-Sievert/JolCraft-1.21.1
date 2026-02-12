package net.sievert.jolcraft.network.handler;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sievert.jolcraft.network.packet.s2c.*;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

public final class JolCraftClientPayloadHandlers {

    private JolCraftClientPayloadHandlers() {}

    public static void handleClientboundDwarfMerchantOffers(ClientboundDwarfMerchantOffersPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    public static void handleClientboundLoreUnlocks(ClientboundDwarfTomeUnlocksPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    public static void handleClientboundDelirium(ClientboundDeliriumCursePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    public static void handleClientboundLanguage(ClientboundDwarvenLanguagePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    public static void handleClientboundAncientLanguage(ClientboundAncientDwarvenLanguagePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    public static void handleClientboundReputation(ClientboundDwarvenReputationPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    public static void handleClientboundEndorsements(ClientboundDwarvenEndorsementsPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }
}