package gametest.tests;

import gametest.GameTestGroup;
import gametest.util.TestPlayerHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.entity.attachment.player.custom.reputation.DwarvenReputationAttachmentHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfAction;
import net.sievert.jolcraft.world.entity.custom.dwarf.action.DwarfActionType;
import net.sievert.jolcraft.world.entity.custom.dwarf.ai.goal.dwarf.DwarfUseItemGoal;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.BountyData;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRecipe;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRecipeInput;

@GameTestGroup
public class DwarfTransactionTests {

    @GameTest(template = "basic")
    public static void bountyDeathBeforeCompletion(GameTestHelper helper) {
        assertDeathCancelsBounty(helper, 5);
    }

    @GameTest(template = "basic")
    public static void bountyDeathNearCompletion(GameTestHelper helper) {
        assertDeathCancelsBounty(helper, 30);
    }

    private static void assertDeathCancelsBounty(GameTestHelper helper, int elapsedTicks) {
        AbstractDwarfEntity dwarf = dwarf(helper, DwarfProfession.MERCHANT, 1);
        ServerPlayer player = player(helper);
        try {
            start(helper, dwarf, player, DwarfActionType.Subtype.BOUNTY_REWARD, completedBounty(), true);
            tick(dwarf, elapsedTicks);
            dwarf.setHealth(0);
            dwarf.die(helper.getLevel().damageSources().generic());
            // The action helper still runs during the dwarf's death animation.
            tick(dwarf, 45);
            dwarf.getActionHelper().stopAction(dwarf);
            helper.assertTrue(count(helper, dwarf, player, JolCraftItems.BOUNTY.get()) == 1,
                    "A cancelled bounty must return exactly one input");
            helper.assertTrue(count(helper, dwarf, player, JolCraftItems.REWARD_CRATE.get()) == 0,
                    "A dead dwarf must not complete the cancelled bounty");
            helper.succeed();
        } finally {
            TestPlayerHelper.disconnect(player);
        }
    }

    @GameTest(template = "basic")
    public static void liveBountyCompletesOnce(GameTestHelper helper) {
        AbstractDwarfEntity dwarf = dwarf(helper, DwarfProfession.MERCHANT, 1);
        ServerPlayer player = player(helper);
        try {
            start(helper, dwarf, player, DwarfActionType.Subtype.BOUNTY_REWARD, completedBounty(), true);
            tick(dwarf, 80);
            helper.assertTrue(count(helper, dwarf, player, JolCraftItems.BOUNTY.get()) == 0,
                    "A completed bounty must consume its input");
            helper.assertTrue(count(helper, dwarf, player, JolCraftItems.REWARD_CRATE.get()) == 1,
                    "A live dwarf must produce exactly one reward");
            helper.succeed();
        } finally {
            TestPlayerHelper.disconnect(player);
        }
    }

    @GameTest(template = "basic")
    public static void unspentBountyIsNotRefunded(GameTestHelper helper) {
        AbstractDwarfEntity dwarf = dwarf(helper, DwarfProfession.MERCHANT, 1);
        ServerPlayer player = player(helper);
        try {
            start(helper, dwarf, player, DwarfActionType.Subtype.BOUNTY_REWARD, completedBounty(), false);
            dwarf.setHealth(0);
            dwarf.die(helper.getLevel().damageSources().generic());
            tick(dwarf, 45);
            helper.assertTrue(count(helper, dwarf, player, JolCraftItems.BOUNTY.get()) == 0,
                    "An unspent creative input must not be duplicated by death loot");
            helper.succeed();
        } finally {
            TestPlayerHelper.disconnect(player);
        }
    }

    @GameTest(template = "basic")
    public static void parallelGuildmastersRevalidatePromotion(GameTestHelper helper) {
        AbstractDwarfEntity first = dwarf(helper, DwarfProfession.GUILDMASTER, 1);
        AbstractDwarfEntity second = dwarf(helper, DwarfProfession.GUILDMASTER, 2);
        ServerPlayer player = player(helper);
        try {
            DwarvenReputationAttachmentHelper.addEndorsement(player, DwarfProfession.MERCHANT);
            first.setPaid(player);
            second.setPaid(player);
            ItemStack tablet = new ItemStack(JolCraftItems.REPUTATION_TABLET_0.get());
            start(helper, first, player, DwarfActionType.Subtype.REPUTATION_GAIN, tablet, true);
            start(helper, second, player, DwarfActionType.Subtype.REPUTATION_GAIN, tablet, true);
            for (int i = 0; i < 45; i++) {
                tick(first, 1);
                tick(second, 1);
            }
            helper.assertTrue(DwarvenReputationAttachmentHelper.getTier(player) == 1,
                    "One endorsement must not allow two concurrent promotions");
            helper.assertTrue(count(helper, second, player, JolCraftItems.REPUTATION_TABLET_0.get()) == 1,
                    "The stale promotion must return its original tablet");
            helper.assertTrue(first.needsPay() && !second.needsPay(),
                    "Only the successful guildmaster should consume its payment");
            helper.succeed();
        } finally {
            TestPlayerHelper.disconnect(player);
        }
    }

    @GameTest(template = "basic")
    public static void cancelledInspectionRefundsOnce(GameTestHelper helper) {
        AbstractDwarfEntity dwarf = dwarf(helper, DwarfProfession.MERCHANT, 1);
        ServerPlayer player = player(helper);
        try {
            ItemStack weapon = new ItemStack(Items.IRON_AXE);
            dwarf.setItemSlot(EquipmentSlot.MAINHAND, weapon.copy());
            start(helper, dwarf, player, DwarfActionType.Subtype.BOUNTY_REWARD, completedBounty(), true);
            dwarf.getActionHelper().stopAction(dwarf);
            dwarf.getActionHelper().stopAction(dwarf);
            tick(dwarf, 45);
            helper.assertTrue(ItemStack.matches(dwarf.getMainHandItem(), weapon),
                    "Cancelling an inspection must restore its previous equipment");
            helper.assertTrue(count(helper, dwarf, player, JolCraftItems.BOUNTY.get()) == 1,
                    "Repeated cancellation must refund once");
            helper.assertTrue(count(helper, dwarf, player, JolCraftItems.REWARD_CRATE.get()) == 0,
                    "A cancelled inspection must not produce a reward");
            helper.succeed();
        } finally {
            TestPlayerHelper.disconnect(player);
        }
    }

    @GameTest(template = "basic")
    public static void healingCannotReplaceInspection(GameTestHelper helper) {
        AbstractDwarfEntity dwarf = dwarf(helper, DwarfProfession.GUILDMASTER, 1);
        ServerPlayer player = player(helper);
        try {
            start(helper, dwarf, player, DwarfActionType.Subtype.CONTRACT_SIGNING,
                    new ItemStack(JolCraftItems.CONTRACT_WRITTEN.get()), true);
            dwarf.setHealth(dwarf.getMaxHealth() - 1);
            DwarfAction inspection = dwarf.getActionHelper().getActiveAction();
            ItemStack input = dwarf.getMainHandItem().copy();
            DwarfUseItemGoal<AbstractDwarfEntity> drink = new DwarfUseItemGoal<>(dwarf,
                    PotionContents.createItemStack(Items.POTION, Potions.STRONG_HEALING),
                    null, mob -> mob.getHealth() < mob.getMaxHealth(), 300);
            helper.assertTrue(!drink.canUse(), "Healing must wait for inspection completion");
            // Also cover a rejected start and its eventual goal cleanup.
            drink.start();
            drink.stop();
            helper.assertTrue(dwarf.getActionHelper().getActiveAction() == inspection,
                    "Rejected drinking must not clear the inspection action");
            helper.assertTrue(ItemStack.matches(dwarf.getMainHandItem(), input),
                    "Rejected drinking must not overwrite the submitted item");
            helper.assertTrue(!dwarf.isUsingItem(), "Rejected drinking must not use a potion");
            dwarf.getActionHelper().stopAction(dwarf);
            helper.succeed();
        } finally {
            TestPlayerHelper.disconnect(player);
        }
    }

    @GameTest(template = "basic")
    public static void drinkingRestoresPreviousEquipment(GameTestHelper helper) {
        AbstractDwarfEntity dwarf = dwarf(helper, DwarfProfession.GUILDMASTER, 1);
        ServerPlayer player = player(helper);
        try {
            ItemStack weapon = new ItemStack(Items.IRON_AXE);
            dwarf.setItemSlot(EquipmentSlot.MAINHAND, weapon.copy());
            dwarf.setNoGravity(true);
            dwarf.setHealth(dwarf.getMaxHealth() - 2);
            DwarfUseItemGoal<AbstractDwarfEntity> drink = new DwarfUseItemGoal<>(dwarf,
                    PotionContents.createItemStack(Items.POTION, Potions.STRONG_HEALING),
                    null, mob -> true, 300);
            helper.assertTrue(drink.canUse(), "An idle dwarf must be able to drink");
            drink.start();
            helper.assertTrue(dwarf.getCurrentActionType() == DwarfActionType.DRINK,
                    "Successful drinking must acquire its action");
            int duration = dwarf.getUseItemRemainingTicks();
            for (int i = 0; i <= duration; i++) {
                dwarf.tick();
            }
            helper.assertTrue(!drink.canContinueToUse() && !dwarf.isUsingItem(),
                    "The potion must finish before the drinking goal cleans up");
            helper.assertTrue(dwarf.getHealth() == dwarf.getMaxHealth(),
                    "Completing the healing potion must heal the dwarf");
            drink.stop();
            helper.assertTrue(dwarf.getActionHelper().isIdle()
                            && ItemStack.matches(dwarf.getMainHandItem(), weapon),
                    "Normal drinking cleanup must restore the previous weapon");

            helper.succeed();
        } finally {
            TestPlayerHelper.disconnect(player);
        }
    }

    private static AbstractDwarfEntity dwarf(GameTestHelper helper, DwarfProfession profession, int x) {
        AbstractDwarfEntity dwarf = helper.spawn(JolCraftEntities.DWARF.get(), new BlockPos(x, 2, 1));
        dwarf.setProfession(profession);
        dwarf.setNoAi(true);
        return dwarf;
    }

    private static ServerPlayer player(GameTestHelper helper) {
        return TestPlayerHelper.createPlayer(helper.getLevel(), "transaction_test",
                helper.absolutePos(new BlockPos(1, 2, 2)));
    }

    private static ItemStack completedBounty() {
        ItemStack stack = new ItemStack(JolCraftItems.BOUNTY.get());
        BountyRecipe.setType(stack, DwarfProfession.MERCHANT);
        BountyRecipe.setTier(stack, DwarfMerchantData.Level.NOVICE);
        stack.set(JolCraftDataComponents.BOUNTY_DATA.get(),
                new BountyData(new BountyData.BountyObjective.ItemObjective(Items.DIAMOND.builtInRegistryHolder(), 1)));
        stack.set(JolCraftDataComponents.BOUNTY_FILL.get(), 1);
        stack.set(JolCraftDataComponents.BOUNTY_COMPLETE.get(), true);
        return stack;
    }

    private static void start(GameTestHelper helper, AbstractDwarfEntity dwarf, ServerPlayer player,
                              DwarfActionType.Subtype subtype, ItemStack input, boolean consumed) {
        if (subtype == DwarfActionType.Subtype.BOUNTY_REWARD) {
            helper.assertTrue(helper.getLevel().getRecipeManager().getRecipeFor(
                    JolCraftRecipes.BOUNTY_REWARD_TYPE.get(),
                    BountyRecipeInput.of(input).result().orElseThrow(), helper.getLevel()).isPresent(),
                    "Regression fixture must have a matching bounty reward recipe");
        }
        helper.assertTrue(dwarf.getActionHelper().trySetAction(dwarf, null, subtype,
                player, InteractionHand.MAIN_HAND, input), "Inspection must start");
        if (consumed) {
            dwarf.getActionHelper().markActionInputConsumed();
        }
    }

    private static void tick(AbstractDwarfEntity dwarf, int ticks) {
        for (int i = 0; i < ticks; i++) {
            dwarf.getActionHelper().tick(dwarf);
        }
    }

    private static int count(GameTestHelper helper, AbstractDwarfEntity dwarf, ServerPlayer player, Item item) {
        int count = player.getInventory().countItem(item);
        for (ItemEntity drop : helper.getLevel().getEntitiesOfClass(ItemEntity.class,
                dwarf.getBoundingBox().inflate(2))) {
            if (drop.getItem().is(item)) {
                count += drop.getItem().getCount();
            }
        }
        return count;
    }
}
