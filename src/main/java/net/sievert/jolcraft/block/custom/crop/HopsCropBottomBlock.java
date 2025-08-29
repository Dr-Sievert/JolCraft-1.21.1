package net.sievert.jolcraft.block.custom.crop;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Ravager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.TriState;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.data.JolCraftTags;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class HopsCropBottomBlock extends CropBlock {
    public static final int MAX_AGE = 9;
    public static final IntegerProperty AGE = IntegerProperty.create("age", 0, 9);
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(0, 0, 0, 16, 2, 16),
            Block.box(0, 0, 0, 16, 4, 16),
            Block.box(0, 0, 0, 16, 6, 16),
            Block.box(0, 0, 0, 16, 8, 16),
            Block.box(0, 0, 0, 16, 10, 16),
            Block.box(0, 0, 0, 16, 12, 16),
            Block.box(0, 0, 0, 16, 14, 16),
            Block.box(0, 0, 0, 16, 16, 16),
            Block.box(0, 0, 0, 16, 16, 16),
            Block.box(0, 0, 0, 16, 16, 16)
    };

    private final Supplier<? extends ItemLike> seedItem;
    private final @NotNull Supplier<? extends Block> topBlock;

    public HopsCropBottomBlock(Properties props, Supplier<? extends ItemLike> seedItem, Supplier<? extends Block> topBlock) {
        super(props);
        this.seedItem = seedItem;
        this.topBlock = topBlock;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx) {
        return SHAPE_BY_AGE[state.getValue(AGE)];
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return seedItem.get();
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
    protected boolean isRandomlyTicking(BlockState state) {
        return true; // Always tick, for enforcement & fixes
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        int age = state.getValue(AGE);
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);

        // Survival check
        if (!this.canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
            return;
        }

        BlockState soil = level.getBlockState(pos.below());

        // Growth logic (only if not max age and not blocked above)
        if (age < MAX_AGE) {
            if (aboveState.isAir() || aboveState.is(JolCraftTags.Blocks.HOPS_TOP)) {
                // If on Verdant Farmland: ignore darkness check, always allow growth
                boolean canGrow = soil.is(JolCraftBlocks.VERDANT_FARMLAND.get()) || hasSufficientDarkness(level, pos);
                if (canGrow) {
                    float growthSpeed = getGrowthSpeed(state, level, pos);
                    if (random.nextInt((int) (25.0F / growthSpeed) + 1) == 0) {
                        int newAge = age + 1;
                        BlockState newState = this.getStateForAge(newAge);
                        level.setBlock(pos, newState, 2);
                        age = newAge; // update age so sync is correct
                    }
                }
            }
        }
        syncTopBlock(level, pos, age);
    }

    protected static float getGrowthSpeed(BlockState blockState, BlockGetter level, BlockPos pos) {
        float base = CropBlock.getGrowthSpeed(blockState, level, pos);
        BlockState soil = level.getBlockState(pos.below());
        if (soil.is(JolCraftBlocks.VERDANT_SOIL.get())) {
            return base * 1.5F; // 20% faster
        }
        return base;
    }


    @Override
    public void growCrops(Level level, BlockPos pos, BlockState state) {
        super.growCrops(level, pos, state);
        int newAge = level.getBlockState(pos).getValue(this.getAgeProperty());
        syncTopBlock(level, pos, newAge);
    }

    private void syncTopBlock(Level level, BlockPos pos, int age) {
        BlockPos above = pos.above();
        BlockState aboveState = level.getBlockState(above);

        if (age >= 5) {
            int topAge = Math.min(age - 5, HopsCropTopBlock.MAX_AGE);
            if (!aboveState.is(JolCraftTags.Blocks.HOPS_TOP)) {
                BlockState topState = topBlock.get().defaultBlockState().setValue(HopsCropTopBlock.TOP_AGE, topAge);
                level.setBlock(above, topState, 2);
            } else if (aboveState.getValue(HopsCropTopBlock.TOP_AGE) != topAge) {
                level.setBlock(above, aboveState.setValue(HopsCropTopBlock.TOP_AGE, topAge), 2);
            }
        } else if (aboveState.is(JolCraftTags.Blocks.HOPS_TOP)) {
            level.destroyBlock(above, true);
        }
    }


    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        BlockState soil = level.getBlockState(pos.below());

        // If on Verdant Farmland, always survive (ignore light and darkness)
        if (soil.is(JolCraftBlocks.VERDANT_FARMLAND.get())) {
            return true;
        }

        // NeoForge/vanilla's TriState for plant support
        TriState soilDecision = soil.canSustainPlant(level, pos.below(), Direction.UP, state);
        boolean soilOk;
        if (!soilDecision.isDefault()) {
            soilOk = soilDecision.isTrue();
        } else {
            soilOk = soil.getBlock() instanceof FarmBlock;
        }

        boolean darkOk = hasSufficientDarkness(level, pos);


        // Must have both good soil and darkness if not on Verdant Farmland
        return soilOk && darkOk;
    }


    public static boolean hasSufficientDarkness(LevelReader level, BlockPos pos) {
        return level.getRawBrightness(pos, 0) <= 8;
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (level instanceof ServerLevel && entity instanceof Ravager) {
            level.destroyBlock(pos, true, entity);
        }
        super.entityInside(state, level, pos, entity);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (newState.getBlock() != this) {
            // Remove the top half if it exists (never drops)
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);
            if (aboveState.is(JolCraftTags.Blocks.HOPS_TOP)) {
                level.setBlock(abovePos, Blocks.AIR.defaultBlockState(), 35);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void playerDestroy(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack tool) {
        if (!player.isCreative()) {
            // Drop loot as normal (vanilla does this)
            super.playerDestroy(level, player, pos, state, blockEntity, tool);
        } else {
            // In creative, just remove block with no drops
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 35);
        }
        // Remove the other half (never drops)
        BlockPos otherHalf = pos.above();
        BlockState otherState = level.getBlockState(otherHalf);
        if (otherState.is(JolCraftTags.Blocks.HOPS_TOP)) {
            level.setBlock(otherHalf, Blocks.AIR.defaultBlockState(), 35);
        }
    }








}
