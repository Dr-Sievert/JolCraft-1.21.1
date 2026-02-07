package net.sievert.jolcraft.event.game;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
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
import net.sievert.jolcraft.datagen.language.subprovider.BountyLangSubProvider;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteractionHelper;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfMerchantOffer;
import net.sievert.jolcraft.world.sound.util.PlaySound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class JolCraftEntityEvents {

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
    public static void onVillagerCrateInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        Player player = event.getEntity();
        Entity target = event.getTarget();
        ItemStack stack = event.getItemStack();

        // Cooldowns are stack-keyed in 1.21.x, so use a stable 1-count copy.
        ItemStack cooldownStack = stack.copyWithCount(1);

        if (player.getCooldowns().isOnCooldown(cooldownStack)) {
            if (!player.level().isClientSide) {
                player.displayClientMessage(
                        Component.translatable(BountyLangSubProvider.TOOLTIP_CRATE_COOLDOWN).withStyle(ChatFormatting.GRAY),
                        true
                );
            }
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
            return;
        }

        // -------------------------------------------------------------------------
        // Villager logic
        // -------------------------------------------------------------------------
        if (target instanceof Villager villager) {

            // Restock Crate
            if (stack.is(JolCraftItems.RESTOCK_CRATE.get()) && !villager.isBaby() && villager.canRestock()) {

                // Prevent clientside crash
                if (player.level().isClientSide) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                // Needs to actually have offers
                if (villager.getOffers().isEmpty()) {
                    player.displayClientMessage(
                            Component.translatable(BountyLangSubProvider.TOOLTIP_CRATE_NO_OFFERS_VILLAGER).withStyle(ChatFormatting.RED),
                            true
                    );
                    PlaySound.villagerNo(villager);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                boolean needsRestock = villager.getOffers().stream().anyMatch(MerchantOffer::isOutOfStock);

                if (!needsRestock) {
                    player.displayClientMessage(
                            Component.translatable(BountyLangSubProvider.TOOLTIP_RESTOCK_CRATE_NO_NEED).withStyle(ChatFormatting.GRAY),
                            true
                    );
                    PlaySound.villagerNo(villager);
                } else {
                    player.displayClientMessage(
                            Component.translatable(BountyLangSubProvider.TOOLTIP_RESTOCK_CRATE_SUCCESS).withStyle(ChatFormatting.GREEN),
                            true
                    );
                    JolCraftSoundHelper.entity(
                            villager,
                            SoundEvents.VILLAGER_WORK_FISHERMAN
                    );
                    PlaySound.villagerYes(villager);
                    villager.restock();

                    // Apply cooldown BEFORE shrinking (stack may become empty)
                    player.getCooldowns().addCooldown(cooldownStack, 60);
                    if (!player.isCreative()) stack.shrink(1);
                }

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }

            // Reroll Crate
            if (stack.is(JolCraftItems.REROLL_CRATE.get()) && !villager.isBaby()) {

                // Prevent clientside crash
                if (player.level().isClientSide) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                // Needs to actually have offers
                if (villager.getOffers().isEmpty()) {
                    player.displayClientMessage(
                            Component.translatable(BountyLangSubProvider.TOOLTIP_CRATE_NO_OFFERS_VILLAGER).withStyle(ChatFormatting.RED),
                            true
                    );
                    PlaySound.villagerNo(villager);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                VillagerData data = villager.getVillagerData();
                int currentLevel = data.getLevel();

                MerchantOffers accumulated = new MerchantOffers();

                for (int level = 1; level <= currentLevel; level++) {
                    villager.setVillagerData(data.setLevel(level));
                    //noinspection DataFlowIssue
                    villager.setOffers(null);
                    MerchantOffers thisLevelOffers = villager.getOffers();
                    accumulated.addAll(thisLevelOffers);
                }

                villager.setOffers(accumulated);
                villager.setVillagerData(data.setLevel(currentLevel)); // restore

                JolCraftSoundHelper.entity(
                        villager,
                        SoundEvents.VILLAGER_WORK_FISHERMAN
                );
                PlaySound.villagerYes(villager);
                player.displayClientMessage(
                        Component.translatable(BountyLangSubProvider.TOOLTIP_REROLL_CRATE_SUCCESS).withStyle(ChatFormatting.GREEN),
                        true
                );

                // Apply cooldown BEFORE shrinking (stack may become empty)
                player.getCooldowns().addCooldown(cooldownStack, 60);
                if (!player.isCreative()) stack.shrink(1);

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }
        }

        // -------------------------------------------------------------------------
        // Wandering Trader logic
        // -------------------------------------------------------------------------
        if (target instanceof WanderingTrader trader) {

            // Restock Crate
            if (stack.is(JolCraftItems.RESTOCK_CRATE.get())) {

                // Prevent clientside crash
                if (player.level().isClientSide) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                // Needs to actually have offers
                if (trader.getOffers().isEmpty()) {
                    player.displayClientMessage(
                            Component.translatable(BountyLangSubProvider.TOOLTIP_CRATE_NO_OFFERS_VILLAGER).withStyle(ChatFormatting.RED),
                            true
                    );
                    PlaySound.villagerNo(trader);
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                boolean needsRestock = trader.getOffers().stream().anyMatch(MerchantOffer::isOutOfStock);

                if (!needsRestock) {
                    player.displayClientMessage(
                            Component.translatable(BountyLangSubProvider.TOOLTIP_RESTOCK_CRATE_NO_NEED).withStyle(ChatFormatting.GRAY),
                            true
                    );
                    PlaySound.villagerNo(trader);
                } else {
                    player.displayClientMessage(
                            Component.translatable(BountyLangSubProvider.TOOLTIP_RESTOCK_CRATE_SUCCESS).withStyle(ChatFormatting.GREEN),
                            true
                    );
                    JolCraftSoundHelper.entity(
                            trader,
                            SoundEvents.VILLAGER_WORK_FISHERMAN
                    );
                    PlaySound.villagerYes(trader);
                    for (MerchantOffer merchantoffer : trader.getOffers()) {
                        merchantoffer.resetUses();
                    }

                    // Apply cooldown BEFORE shrinking (stack may become empty)
                    player.getCooldowns().addCooldown(cooldownStack, 60);
                    if (!player.isCreative()) stack.shrink(1);
                }

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
                return;
            }

            // Reroll Crate
            if (stack.is(JolCraftItems.REROLL_CRATE.get())) {

                // Prevent clientside crash
                if (player.level().isClientSide) {
                    event.setCancellationResult(InteractionResult.SUCCESS);
                    event.setCanceled(true);
                    return;
                }

                // Needs to actually have offers
                if (trader.getOffers().isEmpty()) {
                    player.displayClientMessage(
                            Component.translatable(BountyLangSubProvider.TOOLTIP_CRATE_NO_OFFERS_VILLAGER).withStyle(ChatFormatting.RED),
                            true
                    );
                    PlaySound.villagerNo(trader);
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

                JolCraftSoundHelper.entity(
                        trader,
                        SoundEvents.VILLAGER_WORK_FISHERMAN
                );
                PlaySound.villagerYes(trader);
                player.displayClientMessage(
                        Component.translatable(BountyLangSubProvider.TOOLTIP_REROLL_CRATE_SUCCESS).withStyle(ChatFormatting.GREEN),
                        true
                );

                player.getCooldowns().addCooldown(cooldownStack, 60);
                if (!player.isCreative()) stack.shrink(1);

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }
    }
}
