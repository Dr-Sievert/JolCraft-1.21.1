package net.sievert.jolcraft.event.game;


import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.data.JolCraftAttachments;
import net.sievert.jolcraft.data.custom.attachment.block.Hearth;
import net.sievert.jolcraft.util.attachment.AttachmentSyncHelper;
import net.sievert.jolcraft.util.attachment.DwarvenReputationHelper;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class JolCraftPlayerEvents {

    //General

    @SubscribeEvent
    public static void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        JolCraftCriteriaTriggers.HAS_ADVANCEMENT.trigger(player, event.getAdvancement().id());
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        AttachmentSyncHelper.syncAll(serverPlayer);
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        Hearth hearth = Hearth.get(player);
        if (hearth.hasLitThisDay()) {
            hearth.setLitThisDay(false);
        }
    }

}
