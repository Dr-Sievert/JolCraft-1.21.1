package gametest.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.gametest.framework.GameTestInfo;

@Mixin(GameTestHelper.class)
public interface GameTestHelperAccessor {
    @Accessor
    GameTestInfo getTestInfo();
}