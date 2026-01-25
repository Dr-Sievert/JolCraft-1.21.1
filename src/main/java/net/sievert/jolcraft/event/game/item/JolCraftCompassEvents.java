package net.sievert.jolcraft.event.game.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftComponents;
import net.sievert.jolcraft.data.custom.attachment.compass.DiscoveredStructuresHelper;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.item.util.compass.DeepslateCompassHelper;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.network.packet.S2C.ClientboundPlaySoundPacket;
import net.sievert.jolcraft.sound.JolCraftSounds;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class JolCraftCompassEvents {

    private static final Map<UUID, BlockPos> LAST_COMPASS_POS = new HashMap<>();
    private static final Map<java.util.UUID, Integer> LAST_COMPASS_SLOT = new java.util.HashMap<>();
    private static final Map<UUID, Integer> NEXT_FULL_SCAN_TICK = new java.util.HashMap<>();

    @SubscribeEvent
    public static void onCompassCrafted(PlayerEvent.ItemCraftedEvent event) {
        ItemStack output = event.getCrafting();
        if (output.is(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())) {
            ItemStack input = ItemStack.EMPTY;
            for (int i = 0; i < event.getInventory().getContainerSize(); i++) {
                ItemStack stack = event.getInventory().getItem(i);
                if (stack.is(JolCraftItems.DEEPSLATE_COMPASS.get())) {
                    input = stack;
                    break;
                }
            }
            if (!input.isEmpty()) {
                var dye = input.get(DataComponents.DYED_COLOR);
                if (dye != null) {
                    output.set(DataComponents.DYED_COLOR, dye);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onDialCombine(PlayerInteractEvent.RightClickItem event) {
        Player player = event.getEntity();
        Level level = event.getLevel();
        ItemStack main = event.getItemStack();
        ItemStack offhand = player.getOffhandItem();

        boolean mainIsDial = main.is(JolCraftItems.DEEPSLATE_COMPASS_DIAL.get());
        boolean offIsDial = offhand.is(JolCraftItems.DEEPSLATE_COMPASS_DIAL.get());
        boolean mainIsEmpty = main.is(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get());
        boolean offIsEmpty = offhand.is(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get());

        if (!((mainIsDial && offIsEmpty) || (offIsDial && mainIsEmpty))) return;

        if (!level.isClientSide) {
            ItemStack dial = mainIsDial ? main : offhand;
            ItemStack empty = mainIsEmpty ? main : offhand;
            InteractionHand swingHand = mainIsDial ? event.getHand()
                    : (event.getHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);

            String group = dial.get(JolCraftComponents.STRUCTURE_GROUP);
            if (group == null) return;

            TagKey<Structure> structureTag = DeepslateCompassHelper.getStructureTagForGroup(group);

            GlobalPos targetPos = null;
            String foundStructureFullId = "unknown";

            if (structureTag != null && player.level() instanceof ServerLevel serverLevel) {
                try {
                    targetPos = DiscoveredStructuresHelper.findNearestUndiscoveredStructure(
                            serverLevel,
                            structureTag,
                            player.blockPosition(),
                            100,
                            player
                    );

                    if (targetPos != null) {
                        var registry = serverLevel.registryAccess().lookupOrThrow(Registries.STRUCTURE);
                        var allRefs = serverLevel.structureManager().getAllStructuresAt(targetPos.pos());

                        for (Structure structure : allRefs.keySet()) {
                            for (Holder<Structure> holder : registry.getTagOrEmpty(structureTag)) {
                                if (holder.value() == structure) {
                                    ResourceLocation id = registry.getKey(structure);
                                    if (id != null) {
                                        foundStructureFullId = id.toString();
                                    }
                                    break;
                                }
                            }
                            if (!foundStructureFullId.equals("unknown")) break;
                        }

                        if (foundStructureFullId.equals("unknown")) {
                            foundStructureFullId = group;
                        }
                    }
                } catch (Exception e) {
                    targetPos = null;
                }
            }

            if (targetPos == null) {
                return;
            }

            ItemStack result = new ItemStack(JolCraftItems.DEEPSLATE_COMPASS.get());

            var dyeColor = empty.get(DataComponents.DYED_COLOR);
            if (dyeColor != null) {
                result.set(DataComponents.DYED_COLOR, dyeColor);
            }
            result.set(JolCraftComponents.STRUCTURE_GROUP, foundStructureFullId);

            var dialColor = dial.get(JolCraftComponents.DIAL_COLOR.get());
            if (dialColor != null) {
                result.set(JolCraftComponents.DIAL_COLOR, dialColor);
            }

            result.set(JolCraftComponents.DEEPSLATE_COMPASS_TARGET, targetPos);

            dial.shrink(1);
            empty.shrink(1);

            if (!player.addItem(result)) {
                player.drop(result, false);
            }

            player.swing(swingHand, true);
            level.playSound(null, player.blockPosition(), SoundEvents.METAL_HIT, SoundSource.PLAYERS, 1.0F, 1.4F);
        }

        event.setCancellationResult(InteractionResult.SUCCESS);
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onCompassTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (!(player.level() instanceof ServerLevel serverLevel)) return;
        if (player.tickCount % 5 != 0) return;


        BlockPos currentPos = player.blockPosition();
        BlockPos lastPos = LAST_COMPASS_POS.get(player.getUUID());
        if (currentPos.equals(lastPos)) return;
        LAST_COMPASS_POS.put(player.getUUID(), currentPos);

        var inv = player.getInventory();
        var items = inv.items;

        UUID id = player.getUUID();

        int nextScan = NEXT_FULL_SCAN_TICK.getOrDefault(id, 0);
        int slot = LAST_COMPASS_SLOT.getOrDefault(id, -1);

        boolean needScan = player.tickCount >= nextScan;

        if (!needScan) {
            if (slot < 0 || slot >= items.size() || !items.get(slot).is(JolCraftItems.DEEPSLATE_COMPASS.get())) {
                needScan = true;
            }
        }

        if (needScan) {
            int found = -1;
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).is(JolCraftItems.DEEPSLATE_COMPASS.get())) {
                    found = i;
                    break;
                }
            }
            if (found < 0) {
                LAST_COMPASS_SLOT.remove(id);
                NEXT_FULL_SCAN_TICK.put(id, player.tickCount + 20);
                return;
            }
            slot = found;
            LAST_COMPASS_SLOT.put(id, slot);
            NEXT_FULL_SCAN_TICK.put(id, player.tickCount + 20);
        }

        // From here: only evaluate ONE compass (the first one we care about).
        ItemStack stack = items.get(slot);
        if (!stack.is(JolCraftItems.DEEPSLATE_COMPASS.get())) return;

        GlobalPos tracked = stack.get(JolCraftComponents.DEEPSLATE_COMPASS_TARGET);
        String trackedStructureId = stack.get(JolCraftComponents.STRUCTURE_GROUP);
        if (tracked == null || trackedStructureId == null || trackedStructureId.isEmpty()) return;
        if (!player.level().dimension().equals(tracked.dimension())) return;

        var structureRegistry = serverLevel.registryAccess().lookupOrThrow(Registries.STRUCTURE);
        ResourceLocation trackedStructureKey = ResourceLocation.tryParse(trackedStructureId);
        if (trackedStructureKey == null) return;
        var trackedStructureHolder = structureRegistry.get(trackedStructureKey).orElse(null);
        if (trackedStructureHolder == null) return;

        BlockPos trackedPosXZ = BlockPos.containing(tracked.pos().getX(), player.blockPosition().getY(), tracked.pos().getZ());
        StructureManager structureManager = serverLevel.structureManager();
        StructureStart start = structureManager.getStructureAt(trackedPosXZ, trackedStructureHolder.value());
        if (!start.isValid()) return;

        BoundingBox box = start.getBoundingBox();
        if (!box.isInside(player.blockPosition())) return;

        BlockPos patchedEntrance = BlockPos.containing(tracked.pos().getX(), box.getCenter().getY(), tracked.pos().getZ());

        // Avoid stream allocs every tick: plain loop
        boolean alreadyDiscovered = false;
        for (GlobalPos gp : DiscoveredStructuresHelper.getDiscoveredStructures(player)) {
            if (gp.dimension().equals(tracked.dimension()) && gp.pos().equals(patchedEntrance)) {
                alreadyDiscovered = true;
                break;
            }
        }
        if (alreadyDiscovered) return;

        // --- Discovery action ---
        DiscoveredStructuresHelper.addDiscoveredStructureServer(
                player,
                GlobalPos.of(tracked.dimension(), patchedEntrance),
                trackedStructureKey
        );

        ItemStack emptyCompass = new ItemStack(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get());
        var dye = stack.get(DataComponents.DYED_COLOR);
        if (dye != null) emptyCompass.set(DataComponents.DYED_COLOR, dye);

        items.set(slot, emptyCompass);
        inv.setChanged();

        player.displayClientMessage(
                Component.translatable("tooltip.jolcraft.structure.discovered")
                        .withStyle(ChatFormatting.GRAY)
                        .append(Component.translatable("tooltip.jolcraft.structure." + trackedStructureId)
                                .withStyle(ChatFormatting.BLUE)),
                true
        );

        if (player instanceof ServerPlayer serverPlayer) {
            JolCraftNetworking.sendToClient(serverPlayer,
                    new ClientboundPlaySoundPacket(
                            SoundEvents.ITEM_BREAK.location(),
                            player.getX(), player.getY(), player.getZ(),
                            SoundSource.PLAYERS, 1.0F, 1.5F
                    ));
            JolCraftNetworking.sendToClient(serverPlayer,
                    new ClientboundPlaySoundPacket(
                            JolCraftSounds.LEVEL_UP.get().location(),
                            player.getX(), player.getY(), player.getZ(),
                            SoundSource.PLAYERS, 1.0F, 1.0F
                    ));
        }
    }
}
