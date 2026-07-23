package net.sievert.jolcraft.world.block.custom.crop;

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
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.particle.util.JolCraftParticleHelper;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FesterlingCropBlock extends BushBlock implements BonemealableBlock {

    protected ItemLike getBaseSeedId() {
        return Items.ROTTEN_FLESH;
    }
    public static final int MAX_AGE = 3;
    public static final IntegerProperty AGE = IntegerProperty.create(JolCraftDictionary.AGE, 0, MAX_AGE);

    public static final MapCodec<FesterlingCropBlock> CODEC = simpleCodec(FesterlingCropBlock::new);

    private static final VoxelShape[] SHAPE_BY_AGE = {
            Block.box(2.0, 0.0, 2.0, 14.0, 3.0, 14.0),
            Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0),
            Block.box(6.0, 0.0, 6.0, 10.0, 6.0, 10.0),
            Block.box(6.0, 0.0, 6.0, 10.0, 8.0, 10.0)
    };

    public FesterlingCropBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

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
        BlockState substrate = level.getBlockState(pos.below());
        return substrate.is(JolCraftBlocks.VERDANT_SOIL.get()) || isUprightLog(substrate);
    }

    private static boolean isUprightLog(BlockState state) {
        return state.is(BlockTags.LOGS) && state.hasProperty(BlockStateProperties.AXIS) && state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y;
    }

    @Override
    protected void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        if (!level.isAreaLoaded(pos, 1)) {
            return;
        }

        int age = state.getValue(AGE);
        if (age >= MAX_AGE) {
            mature(level, pos, random);
            return;
        }

        float growthSpeed = getGrowthSpeed(level, pos);
        if (random.nextInt((int) (25.0F / growthSpeed) + 1) == 0) {
            level.setBlock(pos, state.setValue(AGE, age + 1), 2);
        }
    }

    private static float getGrowthSpeed(BlockGetter level, BlockPos pos) {
        float speed = 1.0F;
        BlockPos substratePos = pos.below();

        if (level.getBlockState(substratePos).is(JolCraftBlocks.VERDANT_SOIL.get())) {
            speed *= 1.5F;
        }

        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int zOffset = -1; zOffset <= 1; zOffset++) {
                if (xOffset == 0 && zOffset == 0) {
                    continue;
                }

                BlockState neighboringSubstrate =
                        level.getBlockState(substratePos.offset(xOffset, 0, zOffset));

                if (neighboringSubstrate.is(JolCraftBlocks.VERDANT_SOIL.get())) {
                    speed += 2.0F;
                }
            }
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            if (isUprightLog(level.getBlockState(pos.relative(direction)))) {
                speed += 0.5F;
            }
        }

        return speed;
    }

    private static void mature(
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        level.setBlock(
                pos,
                JolCraftBlocks.FESTERLING.get().defaultBlockState(),
                2
        );

        for (int particle = 0; particle < 5; particle++) {
            double x = pos.getX() + 0.5
                    + (random.nextDouble() - 0.5) * 0.7;
            double y = pos.getY() + 0.7
                    + random.nextDouble() * 0.3;
            double z = pos.getZ() + 0.5
                    + (random.nextDouble() - 0.5) * 0.7;

            JolCraftParticleHelper.spawn(
                    level,
                    ParticleTypes.HAPPY_VILLAGER,
                    x,
                    y,
                    z,
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.0
            );
        }
    }

    @Override
    public boolean isValidBonemealTarget(
            LevelReader level,
            BlockPos pos,
            BlockState state
    ) {
        return state.getValue(AGE) < MAX_AGE;
    }

    @Override
    public boolean isBonemealSuccess(
            Level level,
            RandomSource random,
            BlockPos pos,
            BlockState state
    ) {
        return random.nextFloat() < 0.4F;
    }

    @Override
    public void performBonemeal(
            ServerLevel level,
            RandomSource random,
            BlockPos pos,
            BlockState state
    ) {
        int newAge = Math.min(
                state.getValue(AGE) + Mth.nextInt(random, 1, 2),
                MAX_AGE
        );

        level.setBlock(pos, state.setValue(AGE, newAge), 2);
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(AGE);
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE_BY_AGE[state.getValue(AGE)];
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getCloneItemStack(
            LevelReader level,
            BlockPos pos,
            BlockState state
    ) {
        if (state.getValue(AGE) == MAX_AGE) return new ItemStack(JolCraftBlocks.FESTERLING);
        return new ItemStack(this.getBaseSeedId());
    }
}