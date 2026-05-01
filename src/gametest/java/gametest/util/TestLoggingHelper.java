package gametest.util;

import gametest.JolCraftTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public final class TestLoggingHelper {

    private final JolCraftTestHelper helper;

    public TestLoggingHelper(JolCraftTestHelper helper) {
        this.helper = helper;
    }

    /**
     * Helper for starting a test.
     */
    public void startTest(String testName, String description) {
        System.out.println("[GAMETEST START] " + testName);
        System.out.println("[GAMETEST DESCRIPTION] " + description);
    }

    /**
     * Helper for ending a test successfully.
     */
    public void endTest(String testName) {
        System.out.println("[GAMETEST PASS] " + testName);
        helper.succeed();
    }

    /**
     * Runs an assertion, logs the outcome, and fails the test if it throws.
     */
    public void check(Runnable check, String description) {
        try {
            check.run();
            System.out.println("[CHECK PASS] " + description);
        } catch (Throwable throwable) {
            helper.fail("[CHECK FAIL] " + description + " | " + throwable.getMessage());
        }
    }

    public void checkItem(ItemStack stack, Runnable check, String description) {
        String itemDesc = "[%s x%d]".formatted(
                stack.getHoverName().getString(),
                stack.getCount()
        );
        check(check, itemDesc + " " + description);
    }

    public void checkBlock(Block block, Runnable check, String description) {
        String blockDesc = "[%s]".formatted(block.getName().getString());
        check(check, blockDesc + " " + description);
    }

    public void checkEntity(EntityType<?> type, Runnable check, String description) {
        String entityDesc = "[%s]".formatted(type.getDescription().getString());
        check(check, entityDesc + " " + description);
    }

    public void checkPlayer(ServerPlayer player, Runnable check, String description) {
        String playerDesc = "[%s]".formatted(player.getName().getString());
        check(check, playerDesc + " " + description);
    }
}
