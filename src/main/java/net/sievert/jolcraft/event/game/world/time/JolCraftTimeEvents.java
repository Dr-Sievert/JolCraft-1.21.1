package net.sievert.jolcraft.event.game.world.time;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.event.game.world.recipe.brewing.BrewingSleepHandler;
import net.sievert.jolcraft.world.entity.attachment.player.custom.hearth.HearthAttachmentHelper;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftTimeEvents {

    private JolCraftTimeEvents() {}

    @SubscribeEvent
    public static void onSleepFinished(
            SleepFinishedTimeEvent event
    ) {
        if (!(event.getLevel() instanceof ServerLevel level)) {
            return;
        }

        BrewingSleepHandler.handleSleepFinished(
                level,
                event.getNewTime()
        );

        for (ServerPlayer player : level.players()) {
            if (HearthAttachmentHelper.lastLitDay(player) == -1L) {
                continue;
            }

            HearthAttachmentHelper.clearLastLitDay(
                    player
            );
        }
    }
}