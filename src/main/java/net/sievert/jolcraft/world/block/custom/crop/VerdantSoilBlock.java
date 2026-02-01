package net.sievert.jolcraft.world.block.custom.crop;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.particle.util.JolCraftParticleHelper;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class VerdantSoilBlock extends Block {
    public VerdantSoilBlock(Properties properties) {
        super(properties);
    }

    @Override
    @Nullable
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ItemAbility ability, boolean simulate) {
        if (ability == ItemAbilities.HOE_TILL) {
            if (context.getLevel().getBlockState(context.getClickedPos().above()).isAir()) {
                return JolCraftBlocks.VERDANT_FARMLAND.get().defaultBlockState();
            }
        }
        return null;
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
                    0.0, 0.0, 0.0
            );
        }
    }
}
