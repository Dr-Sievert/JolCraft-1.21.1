package net.sievert.jolcraft.world.block.custom.crop;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.neoforged.neoforge.common.util.TriState;
import net.sievert.jolcraft.world.block.JolCraftBlocks;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public abstract class JolCraftMushroomBlock
        extends BushBlock
        implements BonemealableBlock {

    protected JolCraftMushroomBlock(
            BlockBehaviour.Properties properties
    ) {
        super(properties);
    }

    protected abstract ResourceKey<ConfiguredFeature<?, ?>> hugeFeature();

    @Override
    protected boolean mayPlaceOn(
            BlockState state,
            BlockGetter level,
            BlockPos pos
    ) {
        return state.isSolidRender(
                level,
                pos
        );
    }

    @Override
    protected boolean canSurvive(
            BlockState state,
            LevelReader level,
            BlockPos pos
    ) {
        BlockPos soilPos = pos.below();
        BlockState soil = level.getBlockState(soilPos);

        TriState soilDecision = soil.canSustainPlant(
                level,
                soilPos,
                Direction.UP,
                state
        );

        return soil.is(BlockTags.MUSHROOM_GROW_BLOCK)
                || (soilDecision.isDefault()
                ? level.getRawBrightness(
                pos,
                0
        ) < 13
                && this.mayPlaceOn(
                soil,
                level,
                soilPos
        )
                : soilDecision.isTrue());
    }

    @Override
    protected void randomTick(
            BlockState state,
            ServerLevel level,
            BlockPos pos,
            RandomSource random
    ) {
        boolean onVerdantSoil = level.getBlockState(pos.below())
                .is(JolCraftBlocks.VERDANT_SOIL.get());

        int successfulRolls =
                onVerdantSoil
                        ? 3
                        : 2;

        if (random.nextInt(50) >= successfulRolls) {
            return;
        }

        int remainingNearbyMushrooms = 5;

        for (BlockPos nearbyPos : BlockPos.betweenClosed(
                pos.offset(
                        -4,
                        -1,
                        -4
                ),
                pos.offset(
                        4,
                        1,
                        4
                )
        )) {
            if (level.getBlockState(nearbyPos).is(this)
                    && --remainingNearbyMushrooms <= 0) {
                return;
            }
        }

        BlockPos origin = pos;
        BlockPos target = offsetRandomly(
                origin,
                random
        );

        for (int attempt = 0; attempt < 4; attempt++) {
            if (canSpreadTo(
                    state,
                    level,
                    target
            )) {
                origin = target;
            }

            target = offsetRandomly(
                    origin,
                    random
            );
        }

        if (canSpreadTo(
                state,
                level,
                target
        )) {
            level.setBlock(
                    target,
                    state,
                    Block.UPDATE_CLIENTS
            );
        }
    }

    public boolean growMushroom(
            ServerLevel level,
            BlockPos pos,
            BlockState state,
            RandomSource random
    ) {
        Optional<Holder.Reference<ConfiguredFeature<?, ?>>> configuredFeature =
                level.registryAccess()
                        .registryOrThrow(Registries.CONFIGURED_FEATURE)
                        .getHolder(this.hugeFeature());

        if (configuredFeature.isEmpty()) {
            return false;
        }

        level.removeBlock(
                pos,
                false
        );

        boolean placed = configuredFeature.get()
                .value()
                .place(
                        level,
                        level.getChunkSource().getGenerator(),
                        random,
                        pos
                );

        if (!placed) {
            level.setBlock(
                    pos,
                    state,
                    Block.UPDATE_CLIENTS
            );
        }

        return placed;
    }

    @Override
    public boolean isValidBonemealTarget(
            LevelReader level,
            BlockPos pos,
            BlockState state
    ) {
        return true;
    }

    @Override
    public boolean isBonemealSuccess(
            Level level,
            RandomSource random,
            BlockPos pos,
            BlockState state
    ) {
        boolean verdant = level.getBlockState(pos.below())
                .is(JolCraftBlocks.VERDANT_SOIL.get());

        return random.nextFloat() < (verdant ? 0.6F : 0.4F);
    }

    @Override
    public void performBonemeal(
            ServerLevel level,
            RandomSource random,
            BlockPos pos,
            BlockState state
    ) {
        this.growMushroom(
                level,
                pos,
                state,
                random
        );
    }

    private static boolean canSpreadTo(
            BlockState mushroom,
            ServerLevel level,
            BlockPos pos
    ) {
        if (!level.isEmptyBlock(pos)) {
            return false;
        }

        BlockPos soilPos = pos.below();
        BlockState soil = level.getBlockState(soilPos);

        if (soil.is(BlockTags.MUSHROOM_GROW_BLOCK)) {
            return true;
        }

        TriState soilDecision = soil.canSustainPlant(
                level,
                soilPos,
                Direction.UP,
                mushroom
        );

        return soilDecision.isTrue();
    }

    private static BlockPos offsetRandomly(
            BlockPos origin,
            RandomSource random
    ) {
        return origin.offset(
                random.nextInt(3) - 1,
                random.nextInt(2) - random.nextInt(2),
                random.nextInt(3) - 1
        );
    }
}