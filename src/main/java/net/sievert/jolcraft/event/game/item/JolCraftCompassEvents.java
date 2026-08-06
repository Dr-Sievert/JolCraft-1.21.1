package net.sievert.jolcraft.event.game.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.util.JolCraftRuntime;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassDialColor;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassStructureGroup;
import net.sievert.jolcraft.world.item.inventory.JolCraftItemInsertionHelper;
import net.sievert.jolcraft.world.player.attachment.custom.compass.DiscoveredStructuresAttachmentHelper;
import net.sievert.jolcraft.world.sound.util.JolCraftSoundHelper;
import net.sievert.jolcraft.world.sound.util.PlaySound;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftCompassEvents {

    private record Compass(@NotNull ItemStack stack, @NotNull InteractionHand hand, @NotNull CompassData data) {}

    private record CompassData(
            @NotNull GlobalPos target,
            @NotNull ResourceLocation structureKey,
            @NotNull DeepslateCompassStructureGroup structureGroup
    ) {}

    private static final JolCraftRuntime.StateCache<BlockPos> LAST_PLAYER_POS = new JolCraftRuntime.StateCache<>();

    public static void cleanupPlayer(ServerPlayer player) {
        LAST_PLAYER_POS.clear(player);
    }

    @SubscribeEvent
    public static void onCompassTick(PlayerTickEvent.Post event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if (player.tickCount % 20 != 0) return;

        ItemStack main = player.getItemInHand(InteractionHand.MAIN_HAND);
        ItemStack off = player.getItemInHand(InteractionHand.OFF_HAND);
        Item compassItem = JolCraftItems.DEEPSLATE_COMPASS.asItem();

        if (!main.is(compassItem) && !off.is(compassItem)) {
            cleanupPlayer(player);
            return;
        }

        Compass compass = findActiveCompass(player);
        if (compass == null) {
            cleanupPlayer(player);
            return;
        }

        BlockPos playerPos = player.blockPosition();

        if (!LAST_PLAYER_POS.hasChanged(player, playerPos)) return;
        LAST_PLAYER_POS.set(player, playerPos);

        if (isInsidePlayerStructure(player, compass)) {
            completeDiscovery(player, compass);
        }
    }

    @Nullable
    private static Compass findActiveCompass(ServerPlayer player) {
        ItemStack main = player.getMainHandItem();
        CompassData data = getCompassData(main);
        if (data != null) {
            return new Compass(main, InteractionHand.MAIN_HAND, data);
        }

        ItemStack off = player.getOffhandItem();
        data = getCompassData(off);
        if (data != null) {
            return new Compass(off, InteractionHand.OFF_HAND, data);
        }

        return null;
    }

    @Nullable
    private static CompassData getCompassData(@NotNull ItemStack stack) {
        if (!stack.is(JolCraftItems.DEEPSLATE_COMPASS.get())) return null;

        GlobalPos target = stack.get(JolCraftDataComponents.DEEPSLATE_COMPASS_TARGET);
        String structureId = stack.get(JolCraftDataComponents.STRUCTURE_GROUP);
        DeepslateCompassDialColor dialColor = stack.get(JolCraftDataComponents.DEEPSLATE_COMPASS_DIAL_COLOR);

        if (target == null || structureId == null || structureId.isEmpty() || dialColor == null) return null;

        ResourceLocation structureKey = ResourceLocation.tryParse(structureId);
        DeepslateCompassStructureGroup structureGroup =
                DeepslateCompassStructureGroup.byColor(dialColor.color());

        if (structureKey == null || structureGroup == null) return null;

        return new CompassData(target, structureKey, structureGroup);
    }

    private static boolean isInsidePlayerStructure(
            ServerPlayer player,
            @NotNull Compass compass
    ) {
        if (!(player.level() instanceof ServerLevel level)) return false;

        CompassData data = compass.data();
        GlobalPos target = data.target();
        ResourceLocation structureKey = data.structureKey();

        if (!level.dimension().equals(target.dimension())) return false;

        var registry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
        var holder = registry.get(ResourceKey.create(Registries.STRUCTURE, structureKey)).orElse(null);
        if (holder == null) return false;

        var manager = level.structureManager();
        var structure = holder.value();

        StructureStart playerStart = manager.getStructureWithPieceAt(
                player.blockPosition(),
                structure
        );

        if (!playerStart.isValid()) {
            return false;
        }

        ChunkPos targetChunkPos = new ChunkPos(target.pos());
        ChunkAccess targetChunk = level.getChunk(
                targetChunkPos.x,
                targetChunkPos.z,
                ChunkStatus.STRUCTURE_STARTS
        );

        StructureStart targetStart = manager.getStartForStructure(
                SectionPos.bottomOf(targetChunk),
                structure,
                targetChunk
        );

        return targetStart != null
                && targetStart.isValid()
                && playerStart.getChunkPos().equals(targetStart.getChunkPos());
    }

    private static void completeDiscovery(ServerPlayer player, @NotNull Compass compass) {
        CompassData data = compass.data();
        GlobalPos target = data.target();
        ResourceLocation structureKey = data.structureKey();

        if (!DiscoveredStructuresAttachmentHelper.addDiscoveredStructureServer(player, target)) {
            player.displayClientMessage(
                    Component.translatable(JolCraftLanguageKeys.TOOLTIP_STRUCTURE_ALREADY_DISCOVERED)
                            .withStyle(ChatFormatting.RED),
                    true
            );
            replaceWithEmptyCompass(player, compass);
            JolCraftSoundHelper.player(player, SoundEvents.ITEM_BREAK, 1.0F, 1.5F);
            return;
        }

        int dustCount = data.structureGroup().discoveryDust(structureKey);
        replaceWithEmptyCompass(player, compass);
        JolCraftItemInsertionHelper.tryInsertIntoInventoryOrDrop(
                player,
                new ItemStack(JolCraftItems.DIAL_DUST.get(), dustCount)
        );

        player.displayClientMessage(
                Component.translatable(
                        JolCraftLanguageKeys.TOOLTIP_STRUCTURE_DISCOVERED,
                        Component.translatable(JolCraftLanguageKeys.tooltipStructure(structureKey.toString()))
                                .withStyle(ChatFormatting.BLUE)
                ).withStyle(ChatFormatting.GRAY),
                true
        );

        JolCraftLogs.info(
                JolCraftLogTags.PLAYER,
                "Structure discovered: player={}, structure={}, dial={}, dial_dust={}, pos={}, dimension={}",
                player.getDisplayName(),
                structureKey,
                data.structureGroup().getId(),
                dustCount,
                JolCraftLogs.roundedPos(target.pos()),
                target.dimension().location()
        );

        JolCraftSoundHelper.player(player, SoundEvents.ITEM_BREAK, 1.0F, 1.5F);
        PlaySound.levelUp(player);
    }

    private static void replaceWithEmptyCompass(ServerPlayer player, Compass compass) {
        ItemStack original = compass.stack();
        ItemStack empty = new ItemStack(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get());

        var dye = original.get(DataComponents.DYED_COLOR);
        if (dye != null) {
            empty.set(DataComponents.DYED_COLOR, dye);
        }

        player.setItemInHand(compass.hand(), empty);
    }
}
