package net.sievert.jolcraft.network.handler;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.sievert.jolcraft.network.packet.s2c.*;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

public final class JolCraftClientPayloadHandlers {

    private JolCraftClientPayloadHandlers() {}

    public static void handleClientboundDwarfMerchantOffers(ClientboundDwarfMerchantOffersPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    public static void handleClientboundDelirium(ClientboundDeliriumCursePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }

    public static void handleClientboundRewardLootTables(ClientboundRewardLootTablesPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> JolCraftProxy.access().apply(packet));
    }
}