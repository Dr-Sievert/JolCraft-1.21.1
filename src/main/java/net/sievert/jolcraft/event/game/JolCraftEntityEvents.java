package net.sievert.jolcraft.event.game;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.WanderingTrader;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityInvulnerabilityCheckEvent;
import net.neoforged.neoforge.event.entity.living.FinalizeSpawnEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.ai.goal.dwarf.DwarfBlockGoal;
import net.sievert.jolcraft.entity.custom.dwarf.AbstractDwarfEntity;
import net.sievert.jolcraft.entity.custom.dwarf.DwarfGuardEntity;
import net.sievert.jolcraft.entity.util.dwarf.action.type.combat.BlockDwarfAction;
import net.sievert.jolcraft.entity.util.dwarf.interaction.DwarfInteractionHelper;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfMerchantOffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class JolCraftEntityEvents {

    //Dwarf

    @SubscribeEvent
    public static void onInvulnerabilityCheck(EntityInvulnerabilityCheckEvent event) {
        if (event.getEntity() instanceof AbstractDwarfEntity dwarf && dwarf.canBlock() && event.getSource().getEntity() instanceof Monster monster) {
            if (event.getSource().getDirectEntity() instanceof Projectile) {
                dwarf.shouldBlock = true;
                dwarf.blockCooldownTicks = 75;
                event.setInvulnerable(true);
                return;
            }
            if (monster.isWithinMeleeAttackRange(dwarf)) {
                event.setInvulnerable(true);
                dwarf.shouldBlock = true;
                dwarf.blockCooldownTicks = 75;
            }
        }
    }

    @SubscribeEvent
    public static void onMonsterTarget(FinalizeSpawnEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity instanceof Monster mob && !(entity instanceof Creeper) && !(entity instanceof EnderMan)) {
            mob.goalSelector.addGoal(3, new NearestAttackableTargetGoal<>(mob, AbstractDwarfEntity.class, true));
        }
    }

    @SubscribeEvent
    public static void onCrateInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        Player player = event.getEntity();
        Entity target = event.getTarget();
        ItemStack stack = event.getItemStack();

        if (player.getCooldowns().isOnCooldown(stack)) {
            player.displayClientMessage(
                    Component.translatable("tooltip.jolcraft.crate.cooldown").withStyle(ChatFormatting.GRAY),
                    true
            );
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        // Dwarf logic
        if (target instanceof AbstractDwarfEntity dwarf && !dwarf.isBaby() && dwarf.canTrade()) {

            // --- Language Check (block event if player can't interact) ---
            InteractionResult langFilter = DwarfInteractionHelper.languageCheck(dwarf, player);
            if (langFilter != InteractionResult.SUCCESS) {
                event.setCancellationResult(langFilter);
                event.setCanceled(true);
                return;
            }

            // Restock Crate
            if (stack.is(JolCraftItems.RESTOCK_CRATE.get())) {

                //Prevent clientside crash
                if (player.level().isClientSide) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                //Needs to actually have offers
                if (dwarf.getOffers().isEmpty()) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.crate.no_offers_dwarf").withStyle(ChatFormatting.RED),
                            true
                    );
                    JolCraftSoundHelper.playDwarfNo(dwarf);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                boolean needsRestock = dwarf.getOffers().stream().anyMatch(DwarfMerchantOffer::isOutOfStock);

                if (!needsRestock && !dwarf.hasRandomTrades()) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.restock_crate.no_need").withStyle(ChatFormatting.RED),
                            true
                    );
                    JolCraftSoundHelper.playDwarfNo(dwarf);
                } else {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.restock_crate.success").withStyle(ChatFormatting.GREEN),
                            true
                    );
                    dwarf.crateRestock();
                    JolCraftSoundHelper.playDwarfYes(dwarf);
                    if (!player.isCreative()) stack.shrink(1);
                    player.getCooldowns().addCooldown(stack, 60);
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }

            // Reroll Crate
            if (stack.is(JolCraftItems.REROLL_CRATE.get())) {

                //Prevent clientside crash
                if (player.level().isClientSide) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                //Needs to actually have offers
                if (dwarf.getOffers().isEmpty()) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.crate.no_offers_dwarf").withStyle(ChatFormatting.RED),
                            true
                    );
                    JolCraftSoundHelper.playDwarfNo(dwarf);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                if (!dwarf.canReroll()) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.reroll_crate.fail").withStyle(ChatFormatting.RED),
                            true
                    );
                    JolCraftSoundHelper.playDwarfNo(dwarf);
                } else {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.reroll_crate.success").withStyle(ChatFormatting.GREEN),
                            true
                    );
                    dwarf.rerollTrades();
                    JolCraftSoundHelper.playDwarfYes(dwarf);
                    if (!player.isCreative()) stack.shrink(1);
                    player.getCooldowns().addCooldown(stack, 60);
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }
        }

        // Villager logic
        if (target instanceof Villager villager) {


            // Restock Crate
            if (stack.is(JolCraftItems.RESTOCK_CRATE.get()) && !villager.isBaby() && villager.canRestock()) {

                //Prevent clientside crash
                if (player.level().isClientSide) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                //Needs to actually have offers
                if (villager.getOffers().isEmpty()) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.crate.no_offers_villager").withStyle(ChatFormatting.RED),
                            true
                    );
                    JolCraftSoundHelper.playVillagerNo(villager);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                boolean needsRestock = villager.getOffers().stream().anyMatch(MerchantOffer::isOutOfStock);

                if (!needsRestock) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.restock_crate.no_need").withStyle(ChatFormatting.GRAY),
                            true
                    );
                    JolCraftSoundHelper.playVillagerNo(villager);
                } else {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.restock_crate.success").withStyle(ChatFormatting.GREEN),
                            true
                    );
                    JolCraftSoundHelper.playVillagerFisherman(villager);
                    JolCraftSoundHelper.playVillagerYes(villager);
                    villager.restock();
                    if (!player.isCreative()) stack.shrink(1);
                    player.getCooldowns().addCooldown(stack, 60);
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }

            // Reroll Crate
            if (stack.is(JolCraftItems.REROLL_CRATE.get()) && !villager.isBaby()) {

                //Prevent clientside crash
                if (player.level().isClientSide) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                //Needs to actually have offers
                if (villager.getOffers().isEmpty()) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.crate.no_offers_villager").withStyle(ChatFormatting.RED),
                            true
                    );
                    JolCraftSoundHelper.playVillagerNo(villager);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                VillagerData data = villager.getVillagerData();
                int currentLevel = data.getLevel();

                MerchantOffers accumulated = new MerchantOffers();

                for (int level = 1; level <= currentLevel; level++) {
                    villager.setVillagerData(data.setLevel(level));
                    villager.setOffers(null);
                    MerchantOffers thisLevelOffers = villager.getOffers();

                    accumulated.addAll(thisLevelOffers);
                }

                villager.setOffers(accumulated);
                villager.setVillagerData(data.setLevel(currentLevel)); // restore

                JolCraftSoundHelper.playVillagerFisherman(villager);
                JolCraftSoundHelper.playVillagerYes(villager);
                player.displayClientMessage(
                        Component.translatable("tooltip.jolcraft.reroll_crate.success").withStyle(ChatFormatting.GREEN),
                        true
                );
                if (!player.isCreative()) stack.shrink(1);
                player.getCooldowns().addCooldown(stack, 60);
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }

        }

        // Wandering Trader logic
        if (target instanceof WanderingTrader trader) {


            // Restock Crate
            if (stack.is(JolCraftItems.RESTOCK_CRATE.get())) {

                //Prevent clientside crash
                if (player.level().isClientSide) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                //Needs to actually have offers
                if (trader.getOffers().isEmpty()) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.crate.no_offers_villager").withStyle(ChatFormatting.RED),
                            true
                    );
                    JolCraftSoundHelper.playVillagerNo(trader);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                boolean needsRestock = trader.getOffers().stream().anyMatch(MerchantOffer::isOutOfStock);

                if (!needsRestock) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.restock_crate.no_need").withStyle(ChatFormatting.GRAY),
                            true
                    );
                    JolCraftSoundHelper.playVillagerNo(trader);
                } else {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.restock_crate.success").withStyle(ChatFormatting.GREEN),
                            true
                    );
                    JolCraftSoundHelper.playVillagerFisherman(trader);
                    JolCraftSoundHelper.playVillagerYes(trader);
                    for (MerchantOffer merchantoffer : trader.getOffers()) {
                        merchantoffer.resetUses();
                    }
                    if (!player.isCreative()) stack.shrink(1);
                    player.getCooldowns().addCooldown(stack, 60);
                }
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }

            // Reroll Crate
            if (stack.is(JolCraftItems.REROLL_CRATE.get())) {

                //Prevent clientside crash
                if (player.level().isClientSide) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                //Needs to actually have offers
                if (trader.getOffers().isEmpty()) {
                    player.displayClientMessage(
                            Component.translatable("tooltip.jolcraft.crate.no_offers_villager").withStyle(ChatFormatting.RED),
                            true
                    );
                    JolCraftSoundHelper.playVillagerNo(trader);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }
                trader.getOffers().clear();

                VillagerTrades.ItemListing[] pool1 = VillagerTrades.WANDERING_TRADER_TRADES.get(1);
                if (pool1 != null) {
                    List<VillagerTrades.ItemListing> genericTrades = new ArrayList<>(Arrays.asList(pool1));
                    int toAdd = Math.min(5, genericTrades.size());
                    for (int i = 0; i < toAdd && !genericTrades.isEmpty(); i++) {
                        VillagerTrades.ItemListing picked = genericTrades.remove(trader.getRandom().nextInt(genericTrades.size()));
                        MerchantOffer offer = picked.getOffer(trader, trader.getRandom());
                        if (offer != null) {
                            trader.getOffers().add(offer);
                        }
                    }
                }

                VillagerTrades.ItemListing[] pool2 = VillagerTrades.WANDERING_TRADER_TRADES.get(2);
                if (pool2 != null && pool2.length > 0) {
                    int rareIndex = trader.getRandom().nextInt(pool2.length);
                    VillagerTrades.ItemListing rareListing = pool2[rareIndex];
                    MerchantOffer rareOffer = rareListing.getOffer(trader, trader.getRandom());
                    if (rareOffer != null) {
                        trader.getOffers().add(rareOffer);
                    }
                }

                JolCraftSoundHelper.playVillagerFisherman(trader);
                JolCraftSoundHelper.playVillagerYes(trader);
                player.displayClientMessage(
                        Component.translatable("tooltip.jolcraft.reroll_crate.success").withStyle(ChatFormatting.GREEN),
                        true
                );
                if (!player.isCreative()) stack.shrink(1);
                player.getCooldowns().addCooldown(stack, 60);
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }

    }


}
