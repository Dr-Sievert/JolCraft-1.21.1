package net.sievert.jolcraft.block.custom.crop;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sievert.jolcraft.block.JolCraftBlocks;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FesterlingCropBlock extends BushBlock implements BonemealableBlock {

    public FesterlingCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(2, 0, 2, 14, 3, 14), // age 0
            Block.box(2, 0, 2, 14, 4, 14), // age 1
            Block.box(6, 0, 6, 10, 6, 10), // age 2
            Block.box(6, 0, 6, 10, 8, 10)  // age 3 (mature)
    };

    public static final MapCodec<FesterlingCropBlock> CODEC = simpleCodec(FesterlingCropBlock::new);

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return isUprightLog(state);
    }

    @Override
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());
        if (below.is(JolCraftBlocks.VERDANT_SOIL.get())) {
            return true;
        }

        return isUprightLog(below);
    }

    private boolean isUprightLog(BlockState state) {
        return state.is(BlockTags.LOGS)
                && state.hasProperty(BlockStateProperties.AXIS)
                && state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;
        if (!this.canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }
        int age = state.getValue(AGE);

        BlockState below = level.getBlockState(pos.below());
        boolean isOnLog = isUprightLog(below);
        boolean isOnVerdant = below.is(JolCraftBlocks.VERDANT_SOIL.get());

        if (!(isOnLog || isOnVerdant)) return;

        if (age < MAX_AGE) {
            float growthChance = getGrowthSpeed(state, level, pos);
            if (random.nextInt((int) (25.0F / growthChance) + 1) == 0) {
                level.setBlock(pos, state.setValue(AGE, age + 1), 2);
            }
        }
        else if (age == MAX_AGE) {
            level.setBlock(pos, JolCraftBlocks.FESTERLING.get().defaultBlockState(), 2);
            for (int i = 0; i < 5; ++i) {
                double dx = pos.getX() + 0.5 + (random.nextDouble() - 0.5) * 0.7;
                double dy = pos.getY() + 0.7 + (random.nextDouble() * 0.3);
                double dz = pos.getZ() + 0.5 + (random.nextDouble() - 0.5) * 0.7;
                level.sendParticles(
                        ParticleTypes.HAPPY_VILLAGER,
                        dx, dy, dz,
                        1, 0.0D, 0.0D, 0.0D, 0.0D
                );
            }
        }
    }


    protected static float getGrowthSpeed(BlockState state, BlockGetter level, BlockPos pos) {
        float speed = 1.0F;
        BlockPos below = pos.below();

        BlockState soil = level.getBlockState(below);
        if (soil.is(JolCraftBlocks.VERDANT_SOIL.get()) || soil.is(JolCraftBlocks.VERDANT_FARMLAND.get())) {
            speed *= 1.5F;
        }

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                BlockState neighborSoil = level.getBlockState(below.offset(dx, 0, dz));
                if (neighborSoil.is(JolCraftBlocks.VERDANT_SOIL.get()) || neighborSoil.is(JolCraftBlocks.VERDANT_FARMLAND.get())) {
                    speed += 2.0F;
                }
            }
        }

        for (Direction dir : Direction.Plane.HORIZONTAL) {
            BlockState neighbor = level.getBlockState(pos.relative(dir));
            if (neighbor.is(BlockTags.LOGS)
                    && neighbor.hasProperty(BlockStateProperties.AXIS)
                    && neighbor.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y) {
                speed += 0.5F;
            }
        }

        return speed;
    }

    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
        return state.getValue(AGE) < MAX_AGE;
    }

    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return random.nextFloat() < 0.4F;
    }

    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        int age = state.getValue(AGE);
        int growBy = Mth.nextInt(random, 1, 2);
        int newAge = Math.min(age + growBy, MAX_AGE);
        level.setBlock(pos, state.setValue(AGE, newAge), 2);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE_BY_AGE[state.getValue(AGE)];
    }

    protected ItemLike getBaseSeedId() {
        return Items.ROTTEN_FLESH;
    }

    @Override
    protected ItemStack getCloneItemStack(LevelReader p_304482_, BlockPos p_52255_, BlockState p_52256_, boolean p_387989_) {
        return new ItemStack(this.getBaseSeedId());
    }
}
