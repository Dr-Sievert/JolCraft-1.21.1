package net.sievert.jolcraft.event.game;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.util.bounty.BountyData;
import net.sievert.jolcraft.world.sound.util.PlaySound;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftBountyEvents {

    private JolCraftBountyEvents() {}

    @SubscribeEvent
    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide) return;

        ServerPlayer player = resolveKiller(event);
        if (player == null) return;

        if (!hasAnyValidSlayBounty(player, target)) return;

        applySlayProgress(player, target);
    }

    private static ServerPlayer resolveKiller(LivingDeathEvent event) {
        Entity sourceEntity = event.getSource().getEntity();
        if (sourceEntity instanceof ServerPlayer sp) {
            return sp;
        }

        Entity direct = event.getSource().getDirectEntity();
        if (direct instanceof Projectile projectile) {
            Entity owner = projectile.getOwner();
            if (owner instanceof ServerPlayer sp) {
                return sp;
            }
        }

        return null;
    }

    private static boolean hasAnyValidSlayBounty(ServerPlayer player, LivingEntity target) {
        Inventory inv = player.getInventory();

        if (isValidSlayBountyStack(player.getMainHandItem(), target)) return true;
        if (isValidSlayBountyStack(player.getOffhandItem(), target)) return true;

        for (int i = 0; i < 9; i++) {
            if (isValidSlayBountyStack(inv.items.get(i), target)) return true;
        }

        for (int i = 9; i < inv.items.size(); i++) {
            if (isValidSlayBountyStack(inv.items.get(i), target)) return true;
        }

        return false;
    }

    private static boolean isValidSlayBountyStack(ItemStack stack, LivingEntity target) {
        if (stack.isEmpty()) return false;

        BountyData data = stack.get(JolCraftDataComponents.BOUNTY_DATA.get());
        if (data == null) return false;

        if (!(data.objective() instanceof BountyData.BountyObjective.EntityObjective(var targetEntity, int required))) return false;
        if (required <= 0) return false;

        if (Boolean.TRUE.equals(stack.get(JolCraftDataComponents.BOUNTY_COMPLETE.get()))) return false;

        return targetEntity.value() == target.getType();
    }

    private static void applySlayProgress(ServerPlayer player, LivingEntity target) {
        Inventory inv = player.getInventory();

        if (tryProgressOneStack(player, player.getMainHandItem(), target)) return;
        if (tryProgressOneStack(player, player.getOffhandItem(), target)) return;

        for (int i = 0; i < 9; i++) {
            if (tryProgressOneStack(player, inv.items.get(i), target)) return;
        }

        for (int i = 9; i < inv.items.size(); i++) {
            if (tryProgressOneStack(player, inv.items.get(i), target)) return;
        }
    }

    private static boolean tryProgressOneStack(Player player, ItemStack stack, LivingEntity target) {
        if (!isValidSlayBountyStack(stack, target)) return false;

        BountyData data = stack.get(JolCraftDataComponents.BOUNTY_DATA.get());
        if(data == null) return false;

        var obj = (BountyData.BountyObjective.EntityObjective) data.objective();
        int required = obj.amount();
        var targetEntity = obj.entity();

        int fill = stack.getOrDefault(JolCraftDataComponents.BOUNTY_FILL.get(), 0);

        int newFill = Math.min(required, fill + 1);
        stack.set(JolCraftDataComponents.BOUNTY_FILL.get(), newFill);

        if (newFill >= required) {
            stack.set(JolCraftDataComponents.BOUNTY_COMPLETE.get(), true);

            Component targetName = targetEntity.value().getDescription();
            Component label = Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_SLAY, targetName);

            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_BOUNTY_COMPLETED, label)
                            .withStyle(ChatFormatting.GREEN),
                    true
            );
            PlaySound.levelUp(player);
        }

        return true;
    }
}