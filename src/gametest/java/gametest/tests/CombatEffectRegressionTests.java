package gametest.tests;

import gametest.GameTestGroup;
import gametest.util.TestPlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingEntityUseItemEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.sievert.jolcraft.event.game.world.entity.attribute.JolCraftEntityAttributeEventsHelper;
import net.sievert.jolcraft.event.game.world.entity.effect.util.harmful.JolCraftCrowdControlEventsHelper;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;

@GameTestGroup
public final class CombatEffectRegressionTests {

    @GameTest(template = "basic")
    public static void disarmedServerRejectsAttack(GameTestHelper helper) {
        ServerPlayer player = TestPlayerHelper.createPlayer(helper.getLevel(), "attack_test");
        try {
            var target = helper.spawn(EntityType.COW, new BlockPos(1, 1, 1));
            float initialHealth = target.getHealth();
            player.addEffect(new MobEffectInstance(JolCraftEffects.DISARMED, 200));
            player.attack(target);
            helper.assertTrue(target.getHealth() == initialHealth,
                    "The server accepted an attack while Disarmed");
            player.removeEffect(JolCraftEffects.DISARMED);
            player.attack(target);
            helper.assertTrue(target.getHealth() < initialHealth,
                    "Removing Disarmed did not restore attacks");
            helper.succeed();
        } finally {
            TestPlayerHelper.disconnect(player);
        }
    }

    @GameTest(template = "basic")
    public static void crowdControlServerRejectsUse(GameTestHelper helper) {
        ServerPlayer player = TestPlayerHelper.createPlayer(helper.getLevel(), "use_test");
        try {
            ItemStack bow = new ItemStack(Items.BOW);
            player.setItemInHand(InteractionHand.OFF_HAND, bow);
            player.addEffect(new MobEffectInstance(JolCraftEffects.STUNNED, 200));
            var stunnedUse = new LivingEntityUseItemEvent.Start(
                    player, bow, InteractionHand.OFF_HAND, 72000);
            NeoForge.EVENT_BUS.post(stunnedUse);
            helper.assertTrue(stunnedUse.isCanceled(), "Stunned permitted offhand bow use");
            player.removeEffect(JolCraftEffects.STUNNED);

            player.addEffect(new MobEffectInstance(JolCraftEffects.DISARMED, 200));
            var disarmedUse = new LivingEntityUseItemEvent.Start(
                    player, bow, InteractionHand.OFF_HAND, 72000);
            NeoForge.EVENT_BUS.post(disarmedUse);
            helper.assertTrue(disarmedUse.isCanceled(), "Disarmed permitted ranged item use");
            player.removeEffect(JolCraftEffects.DISARMED);

            player.addEffect(new MobEffectInstance(JolCraftEffects.SUPPRESSED, 200));
            var rangedUse = new LivingEntityUseItemEvent.Start(
                    player, bow, InteractionHand.OFF_HAND, 72000);
            NeoForge.EVENT_BUS.post(rangedUse);
            helper.assertTrue(!rangedUse.isCanceled(), "Suppressed incorrectly blocked ranged use");
            player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.APPLE));
            var foodUse = new PlayerInteractEvent.RightClickItem(player, InteractionHand.MAIN_HAND);
            NeoForge.EVENT_BUS.post(foodUse);
            helper.assertTrue(foodUse.isCanceled(), "Suppressed permitted a non-ranged interaction");

            var creative = helper.makeMockPlayer(GameType.CREATIVE);
            for (var effect : java.util.List.of(JolCraftEffects.STUNNED,
                    JolCraftEffects.DISARMED, JolCraftEffects.SUPPRESSED)) {
                ItemStack item = new ItemStack(effect == JolCraftEffects.SUPPRESSED ? Items.APPLE : Items.BOW);
                creative.setItemInHand(InteractionHand.MAIN_HAND, item);
                creative.addEffect(new MobEffectInstance(effect, 200));
                creative.startUsingItem(InteractionHand.MAIN_HAND);
                JolCraftCrowdControlEventsHelper.onEntityTick(new EntityTickEvent.Post(creative));
                helper.assertTrue(creative.isUsingItem(), "Creative item use was blocked by " + effect);
                creative.stopUsingItem();
                creative.removeEffect(effect);
            }
            helper.succeed();
        } finally {
            TestPlayerHelper.disconnect(player);
        }
    }

    @GameTest(template = "basic")
    public static void slowVulnerabilityAppliesNegativeCorrection(GameTestHelper helper) {
        ServerPlayer player = TestPlayerHelper.createPlayer(helper.getLevel(), "slow_test");
        try {
            double baseSpeed = player.getAttributeValue(Attributes.MOVEMENT_SPEED);
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 200));
            player.addEffect(new MobEffectInstance(JolCraftEffects.SLOW_VULNERABILITY, 200));
            JolCraftEntityAttributeEventsHelper.tickAttributes(player);
            helper.assertTrue(Math.abs(player.getAttributeValue(Attributes.MOVEMENT_SPEED)
                    - baseSpeed * 0.8125D) < 0.000001D,
                    "Slowness I with 25% vulnerability did not slow by 18.75%");
            player.removeEffect(JolCraftEffects.SLOW_VULNERABILITY);
            JolCraftEntityAttributeEventsHelper.tickAttributes(player);
            helper.assertTrue(Math.abs(player.getAttributeValue(Attributes.MOVEMENT_SPEED)
                    - baseSpeed * 0.85D) < 0.000001D,
                    "Removing vulnerability left a stale speed correction");
            helper.succeed();
        } finally {
            TestPlayerHelper.disconnect(player);
        }
    }

    @GameTest(template = "basic")
    public static void vitalityAmplificationSurvivesReconnect(GameTestHelper helper) {
        ServerPlayer player = TestPlayerHelper.createPlayer(helper.getLevel(), "curse_test");
        try {
            player.addEffect(new MobEffectInstance(JolCraftEffects.VITALITY_CURSE, 200));
            player.addEffect(new MobEffectInstance(JolCraftEffects.HEX, 200));
            helper.assertTrue(Math.abs(player.getMaxHealth() - 12.0F) < 0.0001F,
                    "Hex did not amplify Vitality Curse before reconnect");
            TestPlayerHelper.disconnect(player);
            player = TestPlayerHelper.reconnect(helper.getLevel(), player);
            helper.assertTrue(player.hasEffect(JolCraftEffects.VITALITY_CURSE)
                    && player.hasEffect(JolCraftEffects.HEX), "Reconnect did not restore both curses");
            helper.assertTrue(Math.abs(player.getMaxHealth() - 12.0F) < 0.0001F,
                    "Reconnect lost Hex's derived Vitality Curse modifier");
            helper.succeed();
        } finally {
            TestPlayerHelper.disconnect(player);
        }
    }
}
