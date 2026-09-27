package net.sievert.jolcraft.event.game.world.player;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.entity.attachment.player.custom.hearth.HearthAttachmentHelper;
import net.sievert.jolcraft.world.item.custom.armor.ArmorSetItem;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class HearthEquipmentEvents {
    private HearthEquipmentEvents() {}

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        HearthAttachmentHelper.maintainHomesteadEffect(player);
        ArmorSetItem.maintainSetEffects(player);
    }
}
