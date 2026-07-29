package net.sievert.jolcraft.event.client.game.effect;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CalculatePlayerTurnEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.MovementInputUpdateEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.item.equipment.JolCraftEquipmentHelper;


@SuppressWarnings("removal")
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class JolCraftClientEffectEvents {

    @SubscribeEvent
    public static void onCrowdControlledAction(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || player.isCreative()) return;

        boolean disarmed = player.hasEffect(JolCraftEffects.DISARMED);
        boolean stunned = player.hasEffect(JolCraftEffects.STUNNED);
        boolean suppressed = player.hasEffect(JolCraftEffects.SUPPRESSED);

        if (!disarmed && !suppressed && !stunned) return;

        ItemStack mainhand = player.getMainHandItem();

        if (stunned) {
            event.setCanceled(true);
            event.setSwingHand(false);
            return;
        }

        if (disarmed && (event.isAttack() || JolCraftEquipmentHelper.isRangedWeapon(mainhand))) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }

        if (suppressed && !JolCraftEquipmentHelper.isRangedWeapon(mainhand) && (event.isUseItem() || event.isPickBlock())) {
            event.setCanceled(true);
            event.setSwingHand(false);
        }
    }

    @SubscribeEvent
    public static void onCrowdControlledMovement(MovementInputUpdateEvent event) {
        if (!(event.getEntity() instanceof LocalPlayer player)) return;
        if (player.isCreative()) return;
        if (!player.hasEffect(JolCraftEffects.STUNNED) && !player.hasEffect(JolCraftEffects.ROOTED)) return;

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

    @SubscribeEvent
    public static void onStunnedTurn(CalculatePlayerTurnEvent event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        if (player == null || player.isCreative()) return;
        if (!player.hasEffect(JolCraftEffects.STUNNED)) return;
        event.setMouseSensitivity(0.0D);
    }
}
