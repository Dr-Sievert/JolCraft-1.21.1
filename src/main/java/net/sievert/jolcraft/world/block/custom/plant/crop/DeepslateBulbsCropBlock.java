package net.sievert.jolcraft.world.block.custom.plant.crop;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
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
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.common.util.TriState;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DeepslateBulbsCropBlock extends CropBlock {

    public static final int MAX_AGE = 9;
    public static final IntegerProperty AGE = IntegerProperty.create(JolCraftDictionary.AGE, 0, MAX_AGE);

    private static final VoxelShape[] SHAPE_BY_AGE = {
            Block.box(0.0, 0.0, 0.0, 16.0, 1.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 5.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 6.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 10.0, 16.0),
            Block.box(0.0, 0.0, 0.0, 16.0, 11.0, 16.0)
    };

    public DeepslateBulbsCropBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(
                this.stateDefinition.any().setValue(this.getAgeProperty(), 0)
        );
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
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder
    ) {
        builder.add(AGE);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(JolCraftTags.Blocks.DEEPSLATE_BULBS_PLANTABLE);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockPos soilPos = pos.below();
        BlockState soil = level.getBlockState(soilPos);

        if (soil.is(JolCraftBlocks.VERDANT_SOIL.get())) {
            return true;
        }

        if (pos.getY() > 0 || !hasSufficientDarkness(level, pos)) {
            return false;
        }

        TriState soilDecision = soil.canSustainPlant(level, soilPos, Direction.UP, state);

        if (!soilDecision.isDefault()) {
            return soilDecision.isTrue();
        }

        return this.mayPlaceOn(soil, level, soilPos);
    }

    @Override
    protected void entityInside(
            BlockState state,
            Level level,
            BlockPos pos,
            Entity entity
    ) {
        // Deepslate bulbs cannot be trampled.
    }

    public static boolean hasSufficientDarkness(LevelReader level, BlockPos pos) {
        return level.getRawBrightness(pos, 0) <= 8;
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

        if (!this.canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }

        BlockState soil = level.getBlockState(pos.below());
        if (!soil.is(JolCraftBlocks.VERDANT_SOIL.get()) && !hasSufficientDarkness(level, pos)) {
            return;
        }

        int age = this.getAge(state);
        if (age >= this.getMaxAge()) {
            return;
        }

        float growthSpeed = getGrowthSpeed(state, level, pos);
        boolean shouldGrow =
                random.nextInt((int) (25.0F / growthSpeed) + 1) == 0;

        if (CommonHooks.canCropGrow(level, pos, state, shouldGrow)) {
            level.setBlock(pos, this.getStateForAge(age + 1), 2);
            CommonHooks.fireCropGrowPost(level, pos, state);
        }
    }

    @Override
    public boolean isValidBonemealTarget(
            LevelReader level,
            BlockPos pos,
            BlockState state
    ) {
        return false;
    }
}