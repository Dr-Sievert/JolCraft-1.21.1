package net.sievert.jolcraft.world.block.custom.crop;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.particle.util.JolCraftParticleHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VerdantFarmBlock extends FarmBlock {
    public static final MapCodec<FarmBlock> CODEC = simpleCodec(VerdantFarmBlock::new);
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 15.0, 16.0);

    public VerdantFarmBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MOISTURE, 7));
    }

    @Override
    public MapCodec<FarmBlock> codec() {
        return CODEC;
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(MOISTURE) != MAX_MOISTURE) {
            level.setBlock(pos, state.setValue(MOISTURE, MAX_MOISTURE), 2);
        }
    }

    @Override
    protected void tick(BlockState p_221134_, ServerLevel p_221135_, BlockPos p_221136_, RandomSource p_221137_) {
        if (!p_221134_.canSurvive(p_221135_, p_221136_)) {
            turnToDirt(null, p_221134_, p_221135_, p_221136_);
        }
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        entity.causeFallDamage(fallDistance, 1.0F, entity.damageSources().fall());
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return !this.defaultBlockState().canSurvive(context.getLevel(), context.getClickedPos())
                ? JolCraftBlocks.VERDANT_SOIL.get().defaultBlockState()
                : super.getStateForPlacement(context);
    }

    public static void turnToDirt(@Nullable Entity entity, BlockState state, Level level, BlockPos pos) {
        BlockState blockstate = pushEntitiesUp(state, JolCraftBlocks.VERDANT_SOIL.get().defaultBlockState(), level, pos);
        level.setBlockAndUpdate(pos, blockstate);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(entity, blockstate));
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);
        if (random.nextInt(100) == 0) {
            JolCraftParticleHelper.spawn(
                    level,
                    ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + random.nextDouble(),
                    pos.getY() + 1.1,
                    pos.getZ() + random.nextDouble(),
                    0.0, 0.0, 0.0
            );
        }
    }

}
