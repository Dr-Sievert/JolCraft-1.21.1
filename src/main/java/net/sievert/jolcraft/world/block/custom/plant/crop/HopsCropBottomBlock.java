package net.sievert.jolcraft.world.block.custom.plant.crop;

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
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.TriState;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.block.JolCraftBlocks;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HopsCropBottomBlock extends CropBlock {

    public static final int MAX_AGE = 9;
    public static final IntegerProperty AGE = IntegerProperty.create(JolCraftDictionary.AGE, 0, MAX_AGE);

    private static final VoxelShape[] SHAPE_BY_AGE = {
            Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 9.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 12.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 16.0)
    };
    private final Supplier<? extends ItemLike> seedItem;
    private final Supplier<? extends Block> topBlock;

    public HopsCropBottomBlock(
            Properties properties,
            Supplier<? extends ItemLike> seedItem,
            Supplier<? extends Block> topBlock
    ) {
        super(properties);
        this.seedItem = seedItem;
        this.topBlock = topBlock;
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return this.seedItem.get();
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
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE_BY_AGE[this.getAge(state)];
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(AGE);
    }

    @Override
    protected boolean isRandomlyTicking(BlockState state) {
        return true;
    }

    @Override
    protected void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        int age = this.getAge(state);

        if (age < MAX_AGE
                && canGrowInto(level.getBlockState(pos.above()))
                && canGrowAt(level, pos)) {
            float growthSpeed = CropBlock.getGrowthSpeed(state, level, pos);
            boolean shouldGrow = random.nextInt((int) (25.0F / growthSpeed) + 1) == 0;

            if (CommonHooks.canCropGrow(level, pos, state, shouldGrow)) {
                age++;
                level.setBlock(pos, this.getStateForAge(age), 2);
                CommonHooks.fireCropGrowPost(level, pos, state);
            }
        }

        this.syncTopBlock(level, pos, age);
    }

    private static boolean canGrowInto(BlockState state) {
        return state.isAir() || state.is(JolCraftTags.Blocks.HOPS_TOP);
    }

    private static boolean canGrowAt(LevelReader level, BlockPos pos) {
        return level.getBlockState(pos.below()).is(JolCraftBlocks.VERDANT_FARMLAND.get())
                || hasSufficientDarkness(level, pos);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos soilPos = pos.below();
        BlockState soilState = level.getBlockState(soilPos);

        if (soilState.is(JolCraftBlocks.VERDANT_FARMLAND.get())) {
            return true;
        }

        TriState soilDecision = soilState.canSustainPlant(level, soilPos, Direction.UP, state);
        boolean validSoil = soilDecision.isDefault()
                ? soilState.getBlock() instanceof FarmBlock
                : soilDecision.isTrue();

        return validSoil && hasSufficientDarkness(level, pos);
    }

    private static boolean hasSufficientDarkness(LevelReader level, BlockPos pos) {
        return level.getRawBrightness(pos, 0) <= 8;
    }

    @Override
    public void growCrops(Level level, BlockPos pos, BlockState state) {
        super.growCrops(level, pos, state);
        this.syncTopBlock(level, pos, this.getAge(level.getBlockState(pos)));
    }

    private void syncTopBlock(Level level, BlockPos pos, int age) {
        BlockPos topPos = pos.above();
        BlockState topState = level.getBlockState(topPos);

        if (age < 5) {
            if (topState.is(JolCraftTags.Blocks.HOPS_TOP)) {
                level.removeBlock(topPos, false);
            }
            return;
        }

        int topAge = Math.min(age - 5, HopsCropTopBlock.MAX_AGE);

        if (topState.isAir()) {
            level.setBlock(
                    topPos,
                    this.topBlock.get()
                            .defaultBlockState()
                            .setValue(HopsCropTopBlock.AGE, topAge),
                    2
            );
        } else if (topState.is(JolCraftTags.Blocks.HOPS_TOP)
                && topState.getValue(HopsCropTopBlock.AGE) != topAge) {
            level.setBlock(
                    topPos,
                    topState.setValue(HopsCropTopBlock.AGE, topAge),
                    2
            );
        }
    }

    @Override
    protected void onRemove(
            BlockState state,
            Level level,
            BlockPos pos,
            BlockState newState,
            boolean isMoving
    ) {
        if (!state.is(newState.getBlock())
                && !(this instanceof HopsCropTopBlock)
                && level.getBlockState(pos.above()).is(JolCraftTags.Blocks.HOPS_TOP)) {
            level.removeBlock(pos.above(), false);
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }
}