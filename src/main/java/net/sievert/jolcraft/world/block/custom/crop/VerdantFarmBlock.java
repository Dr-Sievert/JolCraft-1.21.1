package net.sievert.jolcraft.world.block.custom.crop;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.sievert.jolcraft.world.block.JolCraftBlocks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VerdantFarmBlock extends FarmBlock {

    public static final MapCodec<FarmBlock> CODEC = simpleCodec(VerdantFarmBlock::new);

    public VerdantFarmBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(MOISTURE, MAX_MOISTURE));
    }

    @Override
    public MapCodec<FarmBlock> codec() {
        return CODEC;
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (state.getValue(MOISTURE) < MAX_MOISTURE) {
            level.setBlock(pos, state.setValue(MOISTURE, MAX_MOISTURE), 2);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(level, pos)) {
            turnToVerdantSoil(null, state, level, pos);
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

    public static void turnToVerdantSoil(
            @Nullable Entity entity,
            BlockState state,
            Level level,
            BlockPos pos
    ) {
        BlockState verdantSoil = pushEntitiesUp(
                state,
                JolCraftBlocks.VERDANT_SOIL.get().defaultBlockState(),
                level,
                pos
        );

        level.setBlockAndUpdate(pos, verdantSoil);
        level.gameEvent(
                GameEvent.BLOCK_CHANGE,
                pos,
                GameEvent.Context.of(entity, verdantSoil)
        );
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (random.nextInt(100) == 0) {
            level.addParticle(
                    ParticleTypes.HAPPY_VILLAGER,
                    pos.getX() + random.nextDouble(),
                    pos.getY() + 1.1,
                    pos.getZ() + random.nextDouble(),
                    0.0,
                    0.0,
                    0.0
            );
        }
    }
}