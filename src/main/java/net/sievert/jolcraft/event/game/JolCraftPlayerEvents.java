package net.sievert.jolcraft.event.game;


import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.AdvancementEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.entity.player.PlayerWakeUpEvent;
import net.neoforged.neoforge.event.level.SleepFinishedTimeEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.block.custom.FermentingCauldronBlock;
import net.sievert.jolcraft.block.entity.custom.FermentingCauldronBlockEntity;
import net.sievert.jolcraft.data.custom.attachment.hearth.Hearth;
import net.sievert.jolcraft.effect.JolCraftEffects;
import net.sievert.jolcraft.gui.custom.dwarf.DwarfMerchantMenu;
import net.sievert.jolcraft.network.util.AttachmentSyncHelper;
import net.sievert.jolcraft.recipe.JolCraftRecipes;
import net.sievert.jolcraft.recipe.custom.input.FermentingCauldronRecipeInput;

import java.util.HashSet;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class JolCraftPlayerEvents {

    @SubscribeEvent
    public static void onAdvancementEarned(AdvancementEvent.AdvancementEarnEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        JolCraftCriteriaTriggers.HAS_ADVANCEMENT.trigger(player, event.getAdvancement().id());
    }

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer serverPlayer)) return;
        AttachmentSyncHelper.syncAll(serverPlayer);
    }

    @SubscribeEvent
    public static void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        if (player.containerMenu instanceof DwarfMerchantMenu menu) {
            menu.getTrader().setTradingPlayer(null);
        }
    }

    @SubscribeEvent
    public static void onPlayerWakeUp(PlayerWakeUpEvent event) {
        Player player = event.getEntity();
        Hearth hearth = Hearth.get(player);
        if (hearth.hasLitThisDay()) {
            hearth.setLitThisDay(false);
        }
    }

    @SubscribeEvent
    public static void onSleepFinished(SleepFinishedTimeEvent event) {
        ServerLevel level = (ServerLevel) event.getLevel();
        long skipped = event.getNewTime();
        if (skipped <= 0) return;

        final int chunkRadius = 4;
        HashSet<BlockPos> seen = new HashSet<>();

        for (var player : level.players()) {
            int centerChunkX = SectionPos.blockToSectionCoord(player.blockPosition().getX());
            int centerChunkZ = SectionPos.blockToSectionCoord(player.blockPosition().getZ());

            for (int dx = -chunkRadius; dx <= chunkRadius; dx++) {
                for (int dz = -chunkRadius; dz <= chunkRadius; dz++) {
                    int cx = centerChunkX + dx;
                    int cz = centerChunkZ + dz;

                    var chunk = level.getChunk(cx, cz, ChunkStatus.FULL, false);
                    if (!(chunk instanceof LevelChunk levelChunk)) continue;

                    for (var be : levelChunk.getBlockEntities().values()) {
                        if (be instanceof FermentingCauldronBlockEntity cauldron && cauldron.isBrewing() && seen.add(be.getBlockPos())) {
                            cauldron.fastForwardBrew(skipped);
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        Player player = event.getEntity();

        if (player.hasEffect(JolCraftEffects.DWARVEN_HASTE)) {
            MobEffectInstance effect = player.getEffect(JolCraftEffects.DWARVEN_HASTE);
            assert effect != null;
            int amplifier = effect.getAmplifier();

            float originalSpeed = event.getOriginalSpeed();
            float newSpeed = originalSpeed * (1.0F + 0.2F * (amplifier + 1));
            event.setNewSpeed(newSpeed);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        var player = event.getEntity();
        var pos = event.getPos();
        var state = serverLevel.getBlockState(pos);
        var mainHandStack = player.getMainHandItem();

        if (mainHandStack.is(Items.ROTTEN_FLESH)) {
            BlockPos above = pos.above();

            boolean onLog = (event.getFace() == Direction.UP
                    && state.is(BlockTags.LOGS)
                    && state.hasProperty(BlockStateProperties.AXIS)
                    && state.getValue(BlockStateProperties.AXIS) == Direction.Axis.Y);

            boolean onSoil = (event.getFace() == Direction.UP && (state.is(JolCraftBlocks.VERDANT_SOIL.get())));

            boolean canPlant = onLog || onSoil;

            if (canPlant && serverLevel.getBlockState(above).isAir()) {
                serverLevel.setBlock(above, JolCraftBlocks.FESTERLING_CROP.get().defaultBlockState(), 3);
                serverLevel.playSound(null, above, SoundEvents.CROP_PLANTED, SoundSource.BLOCKS, 1.0F, 1.0F);

                if (!player.isCreative()) mainHandStack.shrink(1);

                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
        }

        if (!serverLevel.isClientSide() && state.is(Blocks.WATER_CAULDRON) && state.getValue(LayeredCauldronBlock.LEVEL) == 3) {
            var input = new FermentingCauldronRecipeInput(mainHandStack.copyWithCount(1), ItemStack.EMPTY);

            boolean hasRecipe = serverLevel.getServer()
                    .getRecipeManager()
                    .getRecipeFor(JolCraftRecipes.FERMENTING_CAULDRON_TYPE.get(), input, serverLevel)
                    .isPresent();

            if (!hasRecipe) return;

            BlockState newState = JolCraftBlocks.FERMENTING_CAULDRON.get()
                    .defaultBlockState()
                    .setValue(FermentingCauldronBlock.LEVEL, 3);

            serverLevel.setBlock(pos, newState, 3);

            if (serverLevel.getBlockEntity(pos) instanceof FermentingCauldronBlockEntity be) {
                InteractionResult result = be.handleInteraction(player, event.getHand(), mainHandStack);
                event.setCancellationResult(result);
                event.setCanceled(true);
                return;
            }

            serverLevel.setBlock(pos, state, 3);
        }
    }
}
