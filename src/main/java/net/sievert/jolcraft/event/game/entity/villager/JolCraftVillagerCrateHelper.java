package net.sievert.jolcraft.event.game.entity.villager;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class JolCraftVillagerCrateHelper {

    private static final int COOLDOWN_TICKS = 60;

    private JolCraftVillagerCrateHelper() {}

    public static void onInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        Player player = event.getEntity();
        Entity target = event.getTarget();
        ItemStack stack = event.getItemStack();

        if (!stack.is(JolCraftItems.RESTOCK_CRATE.get()) && !stack.is(JolCraftItems.REROLL_CRATE.get())) return;

        if (player.getCooldowns().isOnCooldown(stack.getItem())) {
            if (!player.level().isClientSide) {
                message(player, JolCraftLanguageKeys.TOOLTIP_CRATE_COOLDOWN, ChatFormatting.GRAY);
            }
            cancel(event);
            return;
        }

        if (target instanceof Villager villager) {
            handleVillager(event, player, stack, villager);
            return;
        }

        if (target instanceof WanderingTrader trader) {
            handleTrader(event, player, stack, trader);
        }
    }

    private static void handleVillager(
            PlayerInteractEvent.EntityInteractSpecific event,
            Player player,
            ItemStack stack,
            Villager villager
    ) {
        if (villager.isBaby()) return;

        if (stack.is(JolCraftItems.RESTOCK_CRATE.get())) {
            if (!villager.canRestock()) return;
            if (clientCancel(event, player)) return;
            if (!hasOffers(event, player, villager)) return;

            if (!needsRestock(villager.getOffers())) {
                message(player, JolCraftLanguageKeys.TOOLTIP_RESTOCK_CRATE_NO_NEED, ChatFormatting.GRAY);
                PlaySound.villagerNo(villager);
                cancel(event);
                return;
            }

            message(player, JolCraftLanguageKeys.TOOLTIP_RESTOCK_CRATE_SUCCESS, ChatFormatting.GREEN);
            workSound(villager);
            PlaySound.villagerYes(villager);
            villager.restock();

            boolean consumed = consume(player, stack);
            log(player, "Villager", "restock", consumed);
            cancel(event);
            return;
        }

        if (stack.is(JolCraftItems.REROLL_CRATE.get())) {
            if (clientCancel(event, player)) return;
            if (!hasOffers(event, player, villager)) return;

            rerollVillager(villager);

            message(player, JolCraftLanguageKeys.TOOLTIP_REROLL_CRATE_SUCCESS, ChatFormatting.GREEN);
            workSound(villager);
            PlaySound.villagerYes(villager);

            boolean consumed = consume(player, stack);
            log(player, "Villager", "reroll", consumed);
            cancel(event);
        }
    }

    private static void handleTrader(
            PlayerInteractEvent.EntityInteractSpecific event,
            Player player,
            ItemStack stack,
            WanderingTrader trader
    ) {
        if (stack.is(JolCraftItems.RESTOCK_CRATE.get())) {
            if (clientCancel(event, player)) return;
            if (!hasOffers(event, player, trader)) return;

            if (!needsRestock(trader.getOffers())) {
                message(player, JolCraftLanguageKeys.TOOLTIP_RESTOCK_CRATE_NO_NEED, ChatFormatting.GRAY);
                PlaySound.villagerNo(trader);
                cancel(event);
                return;
            }

            message(player, JolCraftLanguageKeys.TOOLTIP_RESTOCK_CRATE_SUCCESS, ChatFormatting.GREEN);
            workSound(trader);
            PlaySound.villagerYes(trader);
            trader.getOffers().forEach(MerchantOffer::resetUses);

            boolean consumed = consume(player, stack);
            log(player, "WanderingTrader", "restock", consumed);
            cancel(event);
            return;
        }

        if (stack.is(JolCraftItems.REROLL_CRATE.get())) {
            if (clientCancel(event, player)) return;
            if (!hasOffers(event, player, trader)) return;

            rerollTrader(trader);

            message(player, JolCraftLanguageKeys.TOOLTIP_REROLL_CRATE_SUCCESS, ChatFormatting.GREEN);
            workSound(trader);
            PlaySound.villagerYes(trader);

            boolean consumed = consume(player, stack);
            log(player, "WanderingTrader", "reroll", consumed);
            cancel(event);
        }
    }

    private static void rerollVillager(Villager villager) {
        VillagerData data = villager.getVillagerData();
        int currentLevel = data.getLevel();

        MerchantOffers accumulated = new MerchantOffers();

        for (int level = 1; level <= currentLevel; level++) {
            villager.setVillagerData(data.setLevel(level));
            //noinspection DataFlowIssue
            villager.setOffers(null);
            accumulated.addAll(villager.getOffers());
        }

        villager.setOffers(accumulated);
        villager.setVillagerData(data.setLevel(currentLevel));
    }

    private static void rerollTrader(WanderingTrader trader) {
        trader.getOffers().clear();

        VillagerTrades.ItemListing[] genericPool = VillagerTrades.WANDERING_TRADER_TRADES.get(1);
        if (genericPool != null) {
            List<VillagerTrades.ItemListing> genericTrades = new ArrayList<>(Arrays.asList(genericPool));
            int toAdd = Math.min(5, genericTrades.size());

            for (int i = 0; i < toAdd && !genericTrades.isEmpty(); i++) {
                VillagerTrades.ItemListing picked = genericTrades.remove(trader.getRandom().nextInt(genericTrades.size()));
                MerchantOffer offer = picked.getOffer(trader, trader.getRandom());
                if (offer != null) trader.getOffers().add(offer);
            }
        }

        VillagerTrades.ItemListing[] rarePool = VillagerTrades.WANDERING_TRADER_TRADES.get(2);
        if (rarePool != null && rarePool.length > 0) {
            VillagerTrades.ItemListing picked = rarePool[trader.getRandom().nextInt(rarePool.length)];
            MerchantOffer offer = picked.getOffer(trader, trader.getRandom());
            if (offer != null) trader.getOffers().add(offer);
        }
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean hasOffers(PlayerInteractEvent.EntityInteractSpecific event, Player player, LivingEntity target) {
        MerchantOffers offers = target instanceof Villager villager
                ? villager.getOffers()
                : ((WanderingTrader) target).getOffers();

        if (!offers.isEmpty()) return true;

        message(player, JolCraftLanguageKeys.TOOLTIP_CRATE_NO_OFFERS_VILLAGER, ChatFormatting.RED);
        PlaySound.villagerNo(target);
        cancel(event);
        return false;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private static boolean needsRestock(MerchantOffers offers) {
        return offers.stream().anyMatch(MerchantOffer::isOutOfStock);
    }

    private static boolean consume(Player player, ItemStack stack) {
        player.getCooldowns().addCooldown(stack.getItem(), COOLDOWN_TICKS);

        if (player.isCreative()) return false;

        stack.shrink(1);
        return true;
    }

    private static boolean clientCancel(PlayerInteractEvent.EntityInteractSpecific event, Player player) {
        if (!player.level().isClientSide) return false;

        cancel(event);
        return true;
    }

    private static void cancel(PlayerInteractEvent.EntityInteractSpecific event) {
        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    private static void message(Player player, String key, ChatFormatting color) {
        player.displayClientMessage(Component.translatable(key).withStyle(color), true);
    }

    private static void workSound(LivingEntity entity) {
        JolCraftSoundHelper.entity(entity, SoundEvents.VILLAGER_WORK_FISHERMAN);
    }

    private static void log(Player player, String target, String crate, boolean consumed) {
        JolCraftLogs.debug(
                JolCraftLogTags.ENTITY,
                "Crate applied: player={}, target={}, crate={}, creative={}, consumed={}",
                player.getUUID(),
                target,
                crate,
                player.isCreative(),
                consumed
        );
    }
}