package net.sievert.jolcraft.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.sievert.jolcraft.data.JolCraftTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(CropBlock.class)
public abstract class CropBlockMixin {

    @Unique
    private static final float VERDANT_GROWTH_MULTIPLIER = 1.5F;

    @ModifyExpressionValue(
            method = "randomTick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getRawBrightness(Lnet/minecraft/core/BlockPos;I)I"
            )
    )
    private int jolcraft$bypassLightRequirementOnVerdantSoil(
            int brightness,
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        return level.getBlockState(pos.below()).is(JolCraftTags.Blocks.VERDANT)
                ? Math.max(brightness, 9)
                : brightness;
    }

    @ModifyReturnValue(
            method = "getGrowthSpeed",
            at = @At("RETURN")
    )
    private static float jolcraft$applyVerdantGrowthMultiplier(
            float original,
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return level.getBlockState(pos.below()).is(JolCraftTags.Blocks.VERDANT)
                ? original * VERDANT_GROWTH_MULTIPLIER
                : original;
    }
}