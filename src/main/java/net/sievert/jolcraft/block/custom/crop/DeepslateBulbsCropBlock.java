package net.sievert.jolcraft.block.custom.crop;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.TriState;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.data.JolCraftTags;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DeepslateBulbsCropBlock extends CropBlock {
    public static final int MAX_AGE = 9;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, MAX_AGE);

    // Stage 0–6: height = 0–6
    // Stage 7–8: height = 10
    // Stage 9:   height = 11
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),  // age 0
            Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),  // age 1
            Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),  // age 2
            Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),  // age 3
            Block.box(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),  // age 4
            Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),  // age 5
            Block.box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0),  // age 6
            Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), // age 7
            Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0), // age 8
            Block.box(0.0, 0.0, 0.0, 16.0, 11.0, 16.0)  // age 9 (mature)
    };

    public DeepslateBulbsCropBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(this.getAgeProperty(), 0));
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_BY_AGE[state.getValue(this.getAgeProperty())];
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return JolCraftItems.DEEPSLATE_BULBS.get();
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(JolCraftTags.Blocks.DEEPSLATE_BULBS_PLANTABLE);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());

        if (below.is(JolCraftBlocks.VERDANT_SOIL.get())) {
            return true;
        }

        if (pos.getY() > 0) return false;

        TriState soilDecision = below.canSustainPlant(level, pos.below(), Direction.UP, state);
        if (!soilDecision.isDefault()) {
            return soilDecision.isTrue();
        }

        boolean darkOk = hasSufficientDarkness(level, pos);

        return below.is(JolCraftTags.Blocks.DEEPSLATE_BULBS_PLANTABLE) && darkOk;
    }


    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, net.minecraft.world.entity.Entity entity) {
        // No interaction — Players/Ravagers don't trample this crop
    }

    public static boolean hasSufficientDarkness(LevelReader level, BlockPos pos) {
        return level.getRawBrightness(pos, 0) <= 8;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!level.isAreaLoaded(pos, 1)) return;

        if (!this.canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }

        BlockState below = level.getBlockState(pos.below());
        boolean onVerdant = below.is(JolCraftBlocks.VERDANT_SOIL.get());

        if (!onVerdant && !hasSufficientDarkness(level, pos)) return;

        int age = this.getAge(state);
        if (age >= this.getMaxAge()) return;

        float growthSpeed = getGrowthSpeed(state, level, pos);
        if (net.neoforged.neoforge.common.CommonHooks.canCropGrow(level, pos, state, random.nextInt((int)(25.0F / growthSpeed) + 1) == 0)) {
            level.setBlock(pos, this.getStateForAge(age + 1), 2);
            net.neoforged.neoforge.common.CommonHooks.fireCropGrowPost(level, pos, state);
        }
    }

    protected static float getGrowthSpeed(BlockState blockState, BlockGetter level, BlockPos pos) {
        float base = CropBlock.getGrowthSpeed(blockState, level, pos);
        BlockState soil = level.getBlockState(pos.below());
        if (soil.is(JolCraftBlocks.VERDANT_SOIL.get())) {
            return base * 1.5F;
        }
        return base;
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state) {
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
