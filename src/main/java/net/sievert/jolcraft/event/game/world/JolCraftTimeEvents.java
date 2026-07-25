package net.sievert.jolcraft.event.game.world;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingBarrelBlockEntity;
import net.sievert.jolcraft.world.player.attachment.custom.hearth.HearthAttachmentHelper;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingCauldronBlockEntity;

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

        FermentingCauldronBlockEntity.handleSleepFinished(
                level,
                event.getNewTime()
        );

        FermentingBarrelBlockEntity.handleSleepFinished(
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