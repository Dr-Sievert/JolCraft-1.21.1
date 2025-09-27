package net.sievert.jolcraft.block.custom;

import com.mojang.serialization.MapCodec;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.sievert.jolcraft.gui.custom.lapidary_bench.LapidaryBenchMenu;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LapidaryBenchBlock extends Block {

    private static final Component CONTAINER_TITLE = Component.translatable("container.jolcraft.lapidary_bench");

    public LapidaryBenchBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    public static final MapCodec<LapidaryBenchBlock> CODEC = simpleCodec(LapidaryBenchBlock::new);

    @Override
    protected MapCodec<? extends LapidaryBenchBlock> codec() {
        return CODEC;
    }

    @Override
    protected MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider(
                (windowId, playerInv, player) ->
                        new LapidaryBenchMenu(windowId, playerInv, ContainerLevelAccess.create(level, pos)),
                CONTAINER_TITLE
        );
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            player.openMenu(this.getMenuProvider(state, level, pos), pos);
        }
        return InteractionResult.SUCCESS;
    }

}
