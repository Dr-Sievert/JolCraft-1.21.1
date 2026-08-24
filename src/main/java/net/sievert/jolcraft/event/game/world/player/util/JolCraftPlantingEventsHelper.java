package net.sievert.jolcraft.event.game.world.player.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

public final class JolCraftPlantingEventsHelper {

    private JolCraftPlantingEventsHelper() {}

    @SuppressWarnings("deprecation")
    public static void tryHandle(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!event.getItemStack().is(Items.ROTTEN_FLESH)) return;

        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);

        boolean onLog =
                event.getFace() == Direction.UP
                        && state.is(BlockTags.LOGS)
                        && state.hasProperty(BlockStateProperties.AXIS)
                        && state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y;

        boolean onSoil =
                event.getFace() == Direction.UP
                        && state.is(JolCraftBlocks.VERDANT_SOIL.get());

        if ((!onLog && !onSoil)
                || !level.getBlockState(pos.above()).isAir()) {
            return;
        }

        BlockPos cropPos = pos.above();

        level.setBlock(
                cropPos,
                JolCraftBlocks.FESTERLING_CROP.get().defaultBlockState(),
                3
        );

        JolCraftLogs.debug(
                JolCraftLogTags.PLAYER,
                "Planted festerling. player={} pos={} on={} face={} item={}",
                event.getEntity().getUUID(),
                JolCraftLogs.roundedPos(cropPos),
                state.getBlock().builtInRegistryHolder().key().location(),
                event.getFace(),
                event.getItemStack().getItem()
                        .builtInRegistryHolder()
                        .key()
                        .location()
        );

        JolCraftSoundHelper.block(
                level,
                cropPos,
                SoundEvents.CROP_PLANTED,
                1.0F,
                1.0F
        );

        if (!event.getEntity().isCreative()) {
            event.getItemStack().shrink(1);
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }
}
