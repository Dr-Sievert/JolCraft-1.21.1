package net.sievert.jolcraft.event.game.world.recipe;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.BountyData;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.sound.util.PlaySound;

public final class JolCraftBountyEvents {

    private JolCraftBountyEvents() {}

    public static void onLivingDeath(LivingDeathEvent event) {
        LivingEntity target = event.getEntity();
        if (target.level().isClientSide) return;

        ServerPlayer player = resolveKiller(event);
        if (player == null) return;

        tryProgressFirstMatchingSlayBounty(player, target);
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

    private static void tryProgressFirstMatchingSlayBounty(ServerPlayer player, LivingEntity target) {
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

    @SuppressWarnings("DeconstructionCanBeUsed")
    private static BountyData.BountyObjective.EntityObjective getValidSlayObjective(ItemStack stack, LivingEntity target) {
        if (stack.isEmpty()) return null;

        if (Boolean.TRUE.equals(stack.get(JolCraftDataComponents.BOUNTY_COMPLETE.get()))) return null;

        BountyData data = stack.get(JolCraftDataComponents.BOUNTY_DATA.get());
        if (data == null) return null;

        if (!(data.objective() instanceof BountyData.BountyObjective.EntityObjective obj)) {
            return null;
        }

        Holder<EntityType<?>> entity = obj.entity();
        int amount = obj.amount();


        if (amount <= 0) return null;

        return (entity.value() == target.getType()) ? obj : null;
    }

    private static boolean tryProgressOneStack(Player player, ItemStack stack, LivingEntity target) {
        BountyData.BountyObjective.EntityObjective obj = getValidSlayObjective(stack, target);
        if (obj == null) return false;

        int required = obj.amount();

        int fill = stack.getOrDefault(JolCraftDataComponents.BOUNTY_FILL.get(), 0);
        int newFill = Math.min(required, fill + 1);

        stack.set(JolCraftDataComponents.BOUNTY_FILL.get(), newFill);

        if (newFill >= required) {
            stack.set(JolCraftDataComponents.BOUNTY_COMPLETE.get(), true);

            Component targetName = obj.entity().value().getDescription();
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