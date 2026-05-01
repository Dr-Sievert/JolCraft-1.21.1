package gametest.tests;

import gametest.GameTestGroup;
import gametest.JolCraftTestHelper;
import gametest.util.TestPlayerHelper;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.protocol.game.ClientboundSystemChatPacket;
import net.minecraft.server.level.ServerPlayer;

@GameTestGroup
public class ExperimentalTests {

    @GameTest(template = "basic")
    public static void experimental(GameTestHelper vanillaHelper) {
        JolCraftTestHelper helper = JolCraftTestHelper.of(vanillaHelper);
        String testName = "Experimental";

        helper.log().startTest(
                testName,
                "Does whatever you want!"
        );

        helper.network().enablePacketRecording();

        System.out.println("===========================");

        ServerPlayer player = TestPlayerHelper.createPlayer(helper.getLevel());

        TestPlayerHelper.assertPlayerPresent(helper.log(), helper.getLevel(), player.getUUID());

        helper.network().getSentPackets(player, ClientboundSystemChatPacket.class).forEach(packet ->
                System.out.println("System chat: " + packet.content().getString()));

        helper.network().clearAllPackets();

        TestPlayerHelper.disconnect(player);

        TestPlayerHelper.assertPlayerNotPresent(helper.log(), helper.getLevel(), player.getUUID());

        player = TestPlayerHelper.reconnect(helper.getLevel(), player);

        TestPlayerHelper.assertPlayerPresent(helper.log(), helper.getLevel(), player.getUUID());

        helper.network().getSentPackets(player, ClientboundSystemChatPacket.class).forEach(packet ->
                System.out.println("System chat: " + packet.content().getString()));

        System.out.println("===========================");

        helper.network().disablePacketRecording();

        helper.log().endTest(testName);
    }
}