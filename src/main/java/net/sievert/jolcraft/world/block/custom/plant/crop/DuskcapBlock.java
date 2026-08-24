package net.sievert.jolcraft.world.block.custom.plant.crop;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.sievert.jolcraft.world.worldgen.feature.JolCraftConfiguredFeatures;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DuskcapBlock extends MushroomBlock {

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
        super(
                JolCraftConfiguredFeatures.HUGE_DUSKCAP_KEY,
                properties
        );
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