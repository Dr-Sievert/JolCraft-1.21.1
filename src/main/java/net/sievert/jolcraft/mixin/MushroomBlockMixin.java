package net.sievert.jolcraft.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.Inject;

@Mixin(MushroomBlock.class)
public abstract class MushroomBlockMixin {

    @Redirect(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/RandomSource;nextInt(I)I",
                    ordinal = 0
            )
    )
    private int jolcraft$modifySpreadChance(
            RandomSource random,
            int bound,
            BlockState state,
            ServerLevel level,
            BlockPos pos
    ) {
        if (!level.getBlockState(pos.below())
                .is(JolCraftBlocks.VERDANT_SOIL.get())) {
            return random.nextInt(bound);
        }

        return random.nextInt(50) < 3
                ? 0
                : 1;
    }

    @Inject(
            method = "isBonemealSuccess",
            at = @At("HEAD"),
            cancellable = true
    )
    private void jolcraft$modifyBonemealSuccess(
            Level level,
            RandomSource random,
            BlockPos pos,
            BlockState state,
            CallbackInfoReturnable<Boolean> callback
    ) {
        if (level.getBlockState(pos.below())
                .is(JolCraftBlocks.VERDANT_SOIL.get())) {
            callback.setReturnValue(
                    random.nextFloat() < 0.6F
            );
        }
    }
}