//package tests;
//
//import net.minecraft.core.BlockPos;
//import net.minecraft.gametest.framework.GameTest;
//import net.minecraft.gametest.framework.GameTestHelper;
//import net.minecraft.server.level.ServerLevel;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.GameType;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.entity.ChestBlockEntity;
//import net.minecraft.world.level.storage.loot.BuiltInLootTables;
//import net.neoforged.neoforge.gametest.GameTestHolder;
//import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
//import net.sievert.jolcraft.JolCraft;
//import net.sievert.jolcraft.util.log.JolCraftLogTags;
//import net.sievert.jolcraft.util.log.JolCraftLogs;
//import net.sievert.jolcraft.world.item.JolCraftItems;
//
//@GameTestHolder(JolCraft.MOD_ID)
//public final class ChestLootGameTest {
//
//    @PrefixGameTestTemplate(false)
//    @GameTest(template = "gametest/empty")
//    public void testChestLootChance(GameTestHelper helper) {
//        ServerLevel level = helper.getLevel();
//        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
//
//        BlockPos chestPos = new BlockPos(1, 0, 0);
//        helper.setBlock(chestPos, Blocks.CHEST);
//
//        int runs = 100000;
//        int hits = 0;
//
//        for (int i = 0; i < runs; i++) {
//            ChestBlockEntity chest = helper.getBlockEntity(chestPos);
//
//            chest.clearContent();
//            chest.setLootTable(BuiltInLootTables.ABANDONED_MINESHAFT, level.random.nextLong());
//            chest.setChanged();
//
//            helper.useBlock(chestPos, player);
//
//            boolean found = false;
//            for (int slot = 0; slot < chest.getContainerSize(); slot++) {
//                ItemStack stack = chest.getItem(slot);
//                if (stack.is(JolCraftItems.DWARVEN_LEXICON.get())) {
//                    found = true;
//                    break;
//                }
//            }
//
//            if (found) {
//                hits++;
//            }
//
//            chest.clearContent();
//            chest.setChanged();
//        }
//
//        float rate = (float) hits / runs;
//
//        JolCraftLogs.info(
//                JolCraftLogTags.DATAGEN,
//                "Chest loot test: runs={}, hits={}, rate={}%",
//                runs,
//                hits,
//                JolCraftLogs.pct1(rate)
//        );
//
//        helper.assertTrue(hits > 0, "Expected at least one lexicon hit across all runs");
//        helper.assertTrue(hits < runs, "Expected at least one miss across all runs");
//
//        helper.succeed();
//    }
//}