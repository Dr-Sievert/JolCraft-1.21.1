package net.sievert.jolcraft.world.block.custom.crop;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sievert.jolcraft.world.worldgen.feature.JolCraftConfiguredFeatures;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DuskcapBlock extends JolCraftMushroomBlock {

    public static final MapCodec<DuskcapBlock> CODEC =
            simpleCodec(DuskcapBlock::new);

    private static final VoxelShape SHAPE =
            Block.box(
                    5.0,
                    0.0,
                    5.0,
                    11.0,
                    12.0,
                    11.0
            );

    public DuskcapBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BushBlock> codec() {
        return CODEC;
    }

    @Override
    protected ResourceKey<ConfiguredFeature<?, ?>> hugeFeature() {
        return JolCraftConfiguredFeatures.HUGE_DUSKCAP_KEY;
    }

    @Override
    protected VoxelShape getShape(
            BlockState state,
            BlockGetter level,
            BlockPos pos,
            CollisionContext context
    ) {
        return SHAPE;
    }
}