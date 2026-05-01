package net.sievert.jolcraft.event.game.player;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.event.game.item.JolCraftCompassEvents;
import net.sievert.jolcraft.event.game.player.attribute.JolCraftPlayerAttributeHelper;
import net.sievert.jolcraft.world.player.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.world.recipe.param.level.WorldContext;
import net.sievert.jolcraft.network.handler.JolCraftServerPayloadHandlers;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.custom.FermentingCauldronBlock;
import net.sievert.jolcraft.world.block.entity.custom.FermentingCauldronBlockEntity;
import net.sievert.jolcraft.world.effect.custom.curse.DeliriumCurseEffect;
import net.sievert.jolcraft.world.gui.menu.DwarfMerchantMenu;
import net.sievert.jolcraft.network.util.SyncHelper;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron.FermentingCauldronRecipeInput;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftPlayerEvents {

    @SubscribeEvent
    public static void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        JolCraftCriteriaTriggers.HAS_ADVANCEMENT.trigger(player, event.getAdvancement().id());
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        SyncHelper.syncAll(serverPlayer);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            cleanupPlayer(player);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            cleanupPlayer(player);
        }
    }

    private static void cleanupPlayer(ServerPlayer player) {
        if (player.containerMenu instanceof DwarfMerchantMenu menu) {
            menu.getTrader().setTradingPlayer(null);
        }

        DeliriumCurseEffect.cleanupRuntime(player);
        JolCraftServerPayloadHandlers.cleanupPlayer(player);
        JolCraftPlayerAttributeHelper.clearPlayerTracking(player.getUUID());
        JolCraftCompassEvents.cleanupPlayer(player);
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        var player = event.getEntity();
        var pos = event.getPos();
        var state = serverLevel.getBlockState(pos);
        var used = event.getItemStack();

        if (used.is(Items.ROTTEN_FLESH)) {
            BlockPos above = pos.above();

            boolean onLog = (event.getFace() == Direction.UP
                    && state.is(BlockTags.LOGS)
                    && state.hasProperty(BlockStateProperties.AXIS)
                    && state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y);

            boolean onSoil = (event.getFace() == Direction.UP && (state.is(JolCraftBlocks.VERDANT_SOIL.get())));

            boolean canPlant = onLog || onSoil;

            if (canPlant && serverLevel.getBlockState(above).isAir()) {

                serverLevel.setBlock(above, JolCraftBlocks.FESTERLING_CROP.get().defaultBlockState(), 3);

                JolCraftLogs.debug(JolCraftLogTags.PLAYER,
                        "Planted festerling. player={} pos={} on={} face={} item={}",
                        player.getUUID(),
                        JolCraftLogs.roundedPos(above),
                        state.getBlock().builtInRegistryHolder().key().location(),
                        event.getFace(),
                        used.getItem().builtInRegistryHolder().key().location());

                JolCraftSoundHelper.block(serverLevel, above, SoundEvents.CROP_PLANTED, 1.0F, 1.0F);

                if (!player.isCreative()) used.shrink(1);

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }

        if (!serverLevel.isClientSide()
                && state.is(Blocks.WATER_CAULDRON)
                && state.getValue(LayeredCauldronBlock.LEVEL) == 3) {

            WorldContext ctx = new WorldContext(serverLevel, player, player);
            var input = new FermentingCauldronRecipeInput(
                    ctx,
                    used.copyWithCount(1),
                    ItemStack.EMPTY
            );

            boolean hasRecipe = serverLevel.getServer()
                    .getRecipeManager()
                    .getRecipeFor(JolCraftRecipes.FERMENTING_CAULDRON_TYPE.get(), input, serverLevel)
                    .isPresent();

            if (!hasRecipe) {
                return;
            }

            JolCraftLogs.debug(
                    JolCraftLogTags.PLAYER,
                    "Converting water cauldron -> fermenting cauldron player={} pos={} item={}",
                    player.getUUID(),
                    JolCraftLogs.roundedPos(pos),
                    used.getItem().builtInRegistryHolder().key().location()
            );

            BlockState newState = JolCraftBlocks.FERMENTING_CAULDRON.get()
                    .defaultBlockState()
                    .setValue(FermentingCauldronBlock.LEVEL, 3);

            serverLevel.setBlock(pos, newState, 3);

            if (serverLevel.getBlockEntity(pos) instanceof FermentingCauldronBlockEntity be) {
                ItemInteractionResult result = be.handleInteraction(player, event.getHand(), used);

                JolCraftLogs.debug(
                        JolCraftLogTags.PLAYER,
                        "Fermenting cauldron interaction handled player={} pos={} result={}",
                        player.getUUID(),
                        JolCraftLogs.roundedPos(pos),
                        result
                );

                event.setCancellationResult(result.result());
                event.setCanceled(true);
                return;
            }

            JolCraftLogs.warn(
                    JolCraftLogTags.PLAYER,
                    "Fermenting cauldron conversion failed (missing BE) reverting player={} pos={}",
                    player.getUUID(),
                    JolCraftLogs.roundedPos(pos)
            );

            serverLevel.setBlock(pos, state, 3);
        }
    }
}
