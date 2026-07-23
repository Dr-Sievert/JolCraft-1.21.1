package net.sievert.jolcraft.world.block.custom.crop;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.util.TriState;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class JolCraftMushroomBlock extends BushBlock {

    protected JolCraftMushroomBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos soilPos = pos.below();
        BlockState soil = level.getBlockState(soilPos);

        if (soil.is(BlockTags.MUSHROOM_GROW_BLOCK)) {
            return true;
        }

        TriState soilDecision = soil.canSustainPlant(level, soilPos, Direction.UP, state);

        if (!soilDecision.isDefault()) {
            return soilDecision.isTrue();
        }

        return level.getRawBrightness(pos, 0) < 8 && this.mayPlaceOn(soil, level, soilPos);
    }

    @Override
    protected void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        if (random.nextInt(25) != 0) {
            return;
        }

        int remainingNearbyMushrooms = 5;

        for (BlockPos nearbyPos : BlockPos.betweenClosed(
                pos.offset(-4, -1, -4),
                pos.offset(4, 1, 4)
        )) {
            if (level.getBlockState(nearbyPos).is(this)
                    && --remainingNearbyMushrooms <= 0) {
                return;
            }
        }

        BlockPos origin = pos;
        BlockPos target = offsetRandomly(origin, random);

        for (int attempt = 0; attempt < 4; attempt++) {
            if (level.isEmptyBlock(target) && state.canSurvive(level, target)) {
                origin = target;
            }

            target = offsetRandomly(origin, random);
        }

        if (level.isEmptyBlock(target) && state.canSurvive(level, target)) {
            level.setBlock(target, state, 2);
        }
    }

    private static BlockPos offsetRandomly(BlockPos origin, RandomSource random) {
        return origin.offset(
                random.nextInt(3) - 1,
                random.nextInt(2) - random.nextInt(2),
                random.nextInt(3) - 1
        );
    }
}