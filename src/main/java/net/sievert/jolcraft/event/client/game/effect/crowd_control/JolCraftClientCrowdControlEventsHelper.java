package net.sievert.jolcraft.event.client.game.effect.crowd_control;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.CalculatePlayerTurnEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.item.equipment.JolCraftEquipmentHelper;

public final class JolCraftClientCrowdControlEventsHelper {

    private JolCraftClientCrowdControlEventsHelper() {}

    public static void onInteraction(
            InputEvent.InteractionKeyMappingTriggered event
    ) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null || player.isCreative()) return;

        boolean stunned = player.hasEffect(JolCraftEffects.STUNNED);
        boolean disarmed = player.hasEffect(JolCraftEffects.DISARMED);
        boolean suppressed = player.hasEffect(JolCraftEffects.SUPPRESSED);

        if (!stunned && !disarmed && !suppressed) return;

        if (stunned) {
            cancelInteraction(event);
            return;
        }

        ItemStack mainHand = player.getMainHandItem();
        boolean rangedWeapon =
                JolCraftEquipmentHelper.isRangedWeapon(mainHand);

        if (disarmed
                && (event.isAttack()
                || (event.isUseItem() && rangedWeapon))) {
            cancelInteraction(event);
            return;
        }

        if (suppressed
                && !rangedWeapon
                && (event.isUseItem() || event.isPickBlock())) {
            cancelInteraction(event);
        }
    }

    public static void onMovement(
            MovementInputUpdateEvent event
    ) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;
        if (player.isCreative()) return;

        boolean stunned = player.hasEffect(JolCraftEffects.STUNNED);
        boolean rooted = player.hasEffect(JolCraftEffects.ROOTED);

        if (!stunned && !rooted) return;

        var input = event.getInput();

        input.up = false;
        input.down = false;
        input.left = false;
        input.right = false;
        input.jumping = false;
        input.shiftKeyDown = false;

        input.forwardImpulse = 0.0F;
        input.leftImpulse = 0.0F;
    }

    public static void onTurn(
            CalculatePlayerTurnEvent event
    ) {
        LocalPlayer player = Minecraft.getInstance().player;

        if (player == null
                || player.isCreative()
                || !player.hasEffect(JolCraftEffects.STUNNED)) {
            return;
        }

        event.setMouseSensitivity(0.0D);
    }

    private static void cancelInteraction(
            InputEvent.InteractionKeyMappingTriggered event
    ) {
        event.setCanceled(true);
        event.setSwingHand(false);
    }
}