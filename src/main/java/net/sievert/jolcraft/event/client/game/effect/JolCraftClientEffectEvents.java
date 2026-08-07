package net.sievert.jolcraft.event.client.game.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CalculatePlayerTurnEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.neoforged.neoforge.client.event.sound.PlaySoundEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.event.client.game.effect.crowd_control.JolCraftClientCrowdControlEventsHelper;
import net.sievert.jolcraft.event.client.game.effect.curse.JolCraftClientAtaxiaEventsHelper;
import net.sievert.jolcraft.event.client.game.effect.curse.JolCraftClientDeliriumEventsHelper;

@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(
        modid = JolCraft.MOD_ID,
        bus = EventBusSubscriber.Bus.GAME,
        value = Dist.CLIENT
)
public final class JolCraftClientEffectEvents {

    private JolCraftClientEffectEvents() {}

    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (isCreative()) return;

        JolCraftClientAtaxiaEventsHelper.onKey(event);
    }

    @SubscribeEvent
    public static void onInteractionInput(InputEvent.InteractionKeyMappingTriggered event) {
        if (isCreative()) return;

        JolCraftClientCrowdControlEventsHelper.onInteraction(event);
    }

    @SubscribeEvent
    public static void onMovementInput(MovementInputUpdateEvent event) {
        if (isCreative()) return;

        JolCraftClientAtaxiaEventsHelper.onMovement(event);
        JolCraftClientCrowdControlEventsHelper.onMovement(event);
    }

    @SubscribeEvent
    public static void onPlayerTurn(CalculatePlayerTurnEvent event) {
        if (isCreative()) return;

        JolCraftClientCrowdControlEventsHelper.onTurn(event);
    }

    @SubscribeEvent
    public static void onSound(PlaySoundEvent event) {
        if (isCreative()) return;

        JolCraftClientDeliriumEventsHelper.onSound(event);
    }

    private static boolean isCreative() {
        LocalPlayer player = Minecraft.getInstance().player;
        return player != null && player.isCreative();
    }
}