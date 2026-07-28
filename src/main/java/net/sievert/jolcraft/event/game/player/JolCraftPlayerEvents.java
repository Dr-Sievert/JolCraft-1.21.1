package net.sievert.jolcraft.event.game.player;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.event.game.item.JolCraftCompassEvents;
import net.sievert.jolcraft.event.game.player.attribute.JolCraftPlayerAttributeHelper;
import net.sievert.jolcraft.event.game.player.util.BrewingBlockConversionHelper;
import net.sievert.jolcraft.network.handler.JolCraftServerPayloadHandlers;
import net.sievert.jolcraft.network.util.SyncHelper;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.effect.custom.curse.DeliriumCurseEffect;
import net.sievert.jolcraft.world.gui.menu.DwarfMerchantMenu;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.custom.container.CoinPouchItem;
import net.sievert.jolcraft.world.player.advancement.JolCraftCriteriaTriggers;
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

        if (event.isCanceled()) {
            return;
        }

        BrewingBlockConversionHelper.tryHandle(
                event
        );
    }

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        ItemEntity itemEntity = event.getItemEntity();
        ItemStack groundStack = itemEntity.getItem();
        Player player = event.getPlayer();

        if (!groundStack.is(JolCraftItems.GOLD_COIN.get()) || itemEntity.hasPickUpDelay()) {
            return;
        }

        int remaining = groundStack.getCount();

        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            ItemStack inventoryStack = player.getInventory().getItem(slot);

            if (!inventoryStack.is(JolCraftItems.COIN_POUCH.get())) {
                continue;
            }

            remaining -= CoinPouchItem.insertCoins(
                    inventoryStack,
                    remaining,
                    player
            );

            if (remaining <= 0) {
                break;
            }
        }

        if (remaining == groundStack.getCount()) {
            return;
        }

        groundStack.setCount(remaining);
        JolCraftSoundHelper.player(player, SoundEvents.ITEM_PICKUP);
    }
}