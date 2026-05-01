package gametest;

import net.minecraft.gametest.framework.GameTestGenerator;
import net.minecraft.gametest.framework.TestFunction;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterGameTestsEvent;
import gametest.tests.*;


import java.util.Collection;

@EventBusSubscriber
public class JolCraftGameTests {
    private static final Class<?>[] TEST_HOLDERS = {
            ExperimentalTests.class
    };

    @SubscribeEvent
    public static void registerTests(RegisterGameTestsEvent event) {
        event.register(JolCraftGameTests.class);
    }

    @GameTestGenerator
    public static Collection<TestFunction> generateTests() {
        return JolCraftTestFunction.getTestsFrom(TEST_HOLDERS);
    }
}
