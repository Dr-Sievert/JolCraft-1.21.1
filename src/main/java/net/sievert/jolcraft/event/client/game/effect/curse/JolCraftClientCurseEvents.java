package net.sievert.jolcraft.event.client.game.effect.curse;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.sievert.jolcraft.JolCraft;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public final class JolCraftClientCurseEvents {

    private JolCraftClientCurseEvents() {}

    @SubscribeEvent
    public static void onAtaxiaCurseInput(InputEvent.Key event) {
        JolCraftClientAtaxiaHelper.onKey(event);
    }

    @SubscribeEvent
    public static void onAtaxiaMovementInput(MovementInputUpdateEvent event) {
        JolCraftClientAtaxiaHelper.onMovement(event);
    }

    @SubscribeEvent
    public static void onDeliriumCurseEpisode(PlaySoundEvent event) {
        JolCraftClientDeliriumHelper.onSound(event);
    }
}