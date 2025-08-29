package net.sievert.jolcraft.block.custom.crop;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JolCraftMushroomBlock extends BushBlock implements BonemealableBlock {

    public JolCraftMushroomBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    protected static final VoxelShape SHAPE = Block.box(5.0, 0.0, 5.0, 11.0, 6.0, 11.0);


    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    public static final MapCodec<JolCraftMushroomBlock> CODEC = RecordCodecBuilder.mapCodec(
            builder -> builder.group(
                    BlockBehaviour.Properties.CODEC.fieldOf("properties").forGetter(block -> block.properties)
            ).apply(builder, JolCraftMushroomBlock::new)
    );

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.isSolidRender();
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos blockpos = pos.below();
        BlockState blockstate = level.getBlockState(blockpos);
        net.neoforged.neoforge.common.util.TriState soilDecision = blockstate.canSustainPlant(level, blockpos, net.minecraft.core.Direction.UP, state);
        return blockstate.is(BlockTags.MUSHROOM_GROW_BLOCK) || (soilDecision.isDefault() ? (level.getRawBrightness(pos, 0) < 8 && this.mayPlaceOn(blockstate, level, blockpos)) : soilDecision.isTrue());
    }

    @Override
    protected void randomTick(BlockState p_221784_, ServerLevel p_221785_, BlockPos p_221786_, RandomSource p_221787_) {
        if (p_221787_.nextInt(25) == 0) {
            int i = 5;
            int j = 4;

            for (BlockPos blockpos : BlockPos.betweenClosed(p_221786_.offset(-4, -1, -4), p_221786_.offset(4, 1, 4))) {
                if (p_221785_.getBlockState(blockpos).is(this)) {
                    if (--i <= 0) {
                        return;
                    }
                }
            }

            BlockPos blockpos1 = p_221786_.offset(p_221787_.nextInt(3) - 1, p_221787_.nextInt(2) - p_221787_.nextInt(2), p_221787_.nextInt(3) - 1);

            for (int k = 0; k < 4; k++) {
                if (p_221785_.isEmptyBlock(blockpos1) && p_221784_.canSurvive(p_221785_, blockpos1)) {
                    p_221786_ = blockpos1;
                }

                blockpos1 = p_221786_.offset(p_221787_.nextInt(3) - 1, p_221787_.nextInt(2) - p_221787_.nextInt(2), p_221787_.nextInt(3) - 1);
            }

            if (p_221785_.isEmptyBlock(blockpos1) && p_221784_.canSurvive(p_221785_, blockpos1)) {
                p_221785_.setBlock(blockpos1, p_221784_, 2);
            }
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader levelReader, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return false;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {

    }
}
