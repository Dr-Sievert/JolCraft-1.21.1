package net.sievert.jolcraft.world.block.custom.crop;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FesterlingBlock extends JolCraftMushroomBlock{

    public FesterlingBlock(Properties properties) {
        super(properties);
    }

    protected static final VoxelShape SHAPE = Block.box(6, 0, 6, 10, 6, 10);

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public static final MapCodec<FesterlingBlock> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    BlockBehaviour.Properties.CODEC.fieldOf(JolCraftDictionary.PROPERTIES).forGetter(block -> block.properties)
            ).apply(builder, FesterlingBlock::new)
    );

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return isValidFesterlingSubstrate(level.getBlockState(pos.below()));
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return isValidFesterlingSubstrate(state);
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (random.nextInt(25) == 0) {
            int i = 5;
            for (BlockPos checkPos : BlockPos.betweenClosed(pos.offset(-4, -1, -4), pos.offset(4, 1, 4))) {
                if (level.getBlockState(checkPos).is(this)) {
                    if (--i <= 0) return;
                }
            }

            BlockPos tryPos = pos.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            for (int k = 0; k < 4; ++k) {
                if (level.isEmptyBlock(tryPos) && isValidFesterlingSubstrate(level.getBlockState(tryPos.below()))) {
                    level.setBlock(tryPos, state, 2);
                    return;
                }
                tryPos = pos.offset(random.nextInt(3) - 1, random.nextInt(2) - random.nextInt(2), random.nextInt(3) - 1);
            }
        }
    }

    private boolean isValidFesterlingSubstrate(BlockState state) {
        if (state.is(BlockTags.MUSHROOM_GROW_BLOCK)) return true;
        if (state.is(BlockTags.LOGS)) {
            return !state.hasProperty(BlockStateProperties.AXIS)
                    || state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y;
        }
        return false;
    }

    @Override
    public BlockState updateShape(
            BlockState state, LevelReader level, ScheduledTickAccess scheduledTick,
            BlockPos pos, Direction dir, BlockPos pos2, BlockState state2, RandomSource random
    ) {
        return this.canSurvive(state, level, pos)
                ? state
                : Blocks.AIR.defaultBlockState();
    }


}
