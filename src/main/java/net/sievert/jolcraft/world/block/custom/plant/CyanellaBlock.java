package net.sievert.jolcraft.world.block.custom.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FlowerBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class CyanellaBlock extends FlowerBlock {

    public CyanellaBlock(Holder<MobEffect> effect, float seconds, BlockBehaviour.Properties properties) {
        super(effect, seconds, properties);
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return state.is(Blocks.WARPED_NYLIUM);
    }
}