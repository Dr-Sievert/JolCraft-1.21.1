package gametest;

import gametest.util.TestLoggingHelper;
import gametest.util.TestNetworkHelper;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;
import gametest.mixin.GameTestHelperAccessor;

public final class JolCraftTestHelper extends GameTestHelper {

    private final TestLoggingHelper logHelper;
    private final TestNetworkHelper networkHelper;

    public static JolCraftTestHelper of(GameTestHelper original) {
        GameTestHelperAccessor access = (GameTestHelperAccessor) original;
        return new JolCraftTestHelper(access.getTestInfo());
    }

    public JolCraftTestHelper(GameTestInfo info) {
        super(info);
        this.logHelper = new TestLoggingHelper(this);
        this.networkHelper = new TestNetworkHelper();
    }

    public TestLoggingHelper log() {
        return this.logHelper;
    }

    public TestNetworkHelper network() {
        return this.networkHelper;
    }
}