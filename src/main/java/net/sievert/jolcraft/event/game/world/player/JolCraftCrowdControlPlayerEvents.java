package net.sievert.jolcraft.event.game.world.player;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.item.equipment.JolCraftEquipmentHelper;

/** Server authorization for actions which the client also suppresses in its UI. */
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftCrowdControlPlayerEvents {

    private JolCraftCrowdControlPlayerEvents() {}

    private static boolean blocksAttack(Player player) {
        return appliesTo(player) && (player.hasEffect(JolCraftEffects.STUNNED)
                || player.hasEffect(JolCraftEffects.DISARMED));
    }

    private static boolean blocksUse(Player player, ItemStack item) {
        if (!appliesTo(player)) return false;
        boolean ranged = JolCraftEquipmentHelper.isRangedWeapon(item);
        return player.hasEffect(JolCraftEffects.STUNNED)
                || (player.hasEffect(JolCraftEffects.DISARMED) && ranged)
                || (player.hasEffect(JolCraftEffects.SUPPRESSED) && !ranged);
    }

    private static boolean appliesTo(Player player) {
        return !player.level().isClientSide() && !player.isCreative();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onAttack(AttackEntityEvent event) {
        if (blocksAttack(event.getEntity())) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (blocksAttack(event.getEntity())) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (blocksAttack(event.getPlayer())) event.setCanceled(true);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (blocksUse(event.getEntity(), event.getItemStack())) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (blocksUse(event.getEntity(), event.getItemStack())) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (blocksUse(event.getEntity(), event.getItemStack())) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityInteractSpecific(PlayerInteractEvent.EntityInteractSpecific event) {
        if (blocksUse(event.getEntity(), event.getItemStack())) {
            event.setCancellationResult(InteractionResult.FAIL);
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseStart(LivingEntityUseItemEvent.Start event) {
        if (event.getEntity() instanceof Player player && blocksUse(player, event.getItem())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseTick(LivingEntityUseItemEvent.Tick event) {
        if (event.getEntity() instanceof Player player && blocksUse(player, event.getItem())) {
            event.setCanceled(true);
            player.stopUsingItem();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onUseStop(LivingEntityUseItemEvent.Stop event) {
        if (event.getEntity() instanceof Player player && blocksUse(player, event.getItem())) {
            event.setCanceled(true);
        }
    }
}
