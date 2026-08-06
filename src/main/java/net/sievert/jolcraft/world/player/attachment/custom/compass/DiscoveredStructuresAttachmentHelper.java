package net.sievert.jolcraft.world.player.attachment.custom.compass;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureCheckResult;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.ConcentricRingsStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.player.JolCraftStats;
import net.sievert.jolcraft.world.player.attachment.JolCraftAttachments;
import net.sievert.jolcraft.world.player.attachment.base.JolCraftAttachmentHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class DiscoveredStructuresAttachmentHelper extends JolCraftAttachmentHelper<DiscoveredStructuresAttachment> {

    private static final DiscoveredStructuresAttachmentHelper INSTANCE = new DiscoveredStructuresAttachmentHelper();

    private DiscoveredStructuresAttachmentHelper() {}

    @Override
    protected @NotNull AttachmentType<DiscoveredStructuresAttachment> type() {
        return JolCraftAttachments.DISCOVERED_STRUCTURES.get();
    }

    public static DiscoveredStructuresAttachment get(ServerPlayer player) {
        return INSTANCE.read(player);
    }

    public static void set(ServerPlayer player, DiscoveredStructuresAttachment value) {
        INSTANCE.write(player, value);
    }

    public static void remove(ServerPlayer player) {
        INSTANCE.clear(player);
    }

    public record LocatedStructure(
            @NotNull GlobalPos pos,
            @NotNull ResourceLocation structureId
    ) {}

    /**
     * SERVER-SIDE: Returns the discovered structures for this player (snapshot).
     */
    public static Set<GlobalPos> getDiscoveredStructures(ServerPlayer player) {
        if (player == null) {
            return Set.of();
        }
        return get(player).getDiscovered();
    }

    /**
     * SERVER-SIDE: Returns true if the player has discovered the given GlobalPos.
     */
    public static boolean hasDiscovered(ServerPlayer player, GlobalPos pos) {
        return player != null && pos != null && get(player).isDiscovered(pos);
    }

    /**
     * SERVER-SIDE: Returns true if the player has discovered any structure in the given dimension.
     */
    public static boolean hasDiscoveredInDimension(ServerPlayer player, String dimId) {
        if (player == null || dimId == null || dimId.isEmpty()) {
            return false;
        }

        for (GlobalPos gp : get(player).getDiscovered()) {
            if (gp.dimension().location().toString().equals(dimId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * SERVER-SIDE: Adds a newly discovered structure location.
     */
    public static boolean addDiscoveredStructureServer(ServerPlayer player, GlobalPos pos) {
        if (player == null || pos == null) {
            return false;
        }

        DiscoveredStructuresAttachment current = get(player);
        DiscoveredStructuresAttachment updated = current.withDiscovered(pos);

        if (updated == current) {
            return false;
        }

        set(player, updated);
        player.awardStat(JolCraftStats.STRUCTURES_DISCOVERED.get());
        return true;
    }

    private record StructurePlacements(
            Map<RandomSpreadStructurePlacement, Set<Holder<Structure>>> randomSpread,
            Map<ConcentricRingsStructurePlacement, Set<Holder<Structure>>> concentric
    ) {
        private boolean isEmpty() {
            return randomSpread.isEmpty() && concentric.isEmpty();
        }
    }

    @Nullable
    public static LocatedStructure findNearestUndiscoveredStructure(
            ServerPlayer player,
            @NotNull TagKey<Structure> structureTag,
            int radius
    ) {
        if (player == null || !(player.level() instanceof ServerLevel level)) return null;
        if (radius < 0 || !level.getServer().getWorldData().worldGenOptions().generateStructures()) return null;

        StructurePlacements placements = getStructurePlacements(level, structureTag);
        if (placements.isEmpty()) return null;

        BlockPos origin = player.blockPosition();

        LocatedStructure randomSpread = findNearestRandomSpreadStructure(
                level,
                player,
                origin,
                placements.randomSpread(),
                radius
        );

        LocatedStructure concentric = findNearestConcentricStructure(
                level,
                player,
                origin,
                placements.concentric()
        );

        return nearest(origin, randomSpread, concentric);
    }

    private static StructurePlacements getStructurePlacements(
            ServerLevel level,
            @NotNull TagKey<Structure> structureTag
    ) {
        var registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
        var tag = registry.getTag(structureTag).orElse(null);

        Map<RandomSpreadStructurePlacement, Set<Holder<Structure>>> randomSpread = new Object2ObjectArrayMap<>();
        Map<ConcentricRingsStructurePlacement, Set<Holder<Structure>>> concentric = new Object2ObjectArrayMap<>();

        if (tag == null) {
            return new StructurePlacements(randomSpread, concentric);
        }

        var structureState = level.getChunkSource().getGeneratorState();

        for (Holder<Structure> holder : tag) {
            for (StructurePlacement placement : structureState.getPlacementsForStructure(holder)) {
                if (placement instanceof RandomSpreadStructurePlacement spread) {
                    randomSpread.computeIfAbsent(spread, ignored -> new ObjectArraySet<>()).add(holder);
                } else if (placement instanceof ConcentricRingsStructurePlacement rings) {
                    concentric.computeIfAbsent(rings, ignored -> new ObjectArraySet<>()).add(holder);
                }
            }
        }

        return new StructurePlacements(randomSpread, concentric);
    }

    @Nullable
    private static LocatedStructure findNearestRandomSpreadStructure(
            ServerLevel level,
            ServerPlayer player,
            BlockPos origin,
            Map<RandomSpreadStructurePlacement, Set<Holder<Structure>>> placements,
            int radius
    ) {
        if (placements.isEmpty()) return null;

        int sectionX = SectionPos.blockToSectionCoord(origin.getX());
        int sectionZ = SectionPos.blockToSectionCoord(origin.getZ());
        long seed = level.getChunkSource().getGeneratorState().getLevelSeed();

        for (int ring = 0; ring <= radius; ring++) {
            LocatedStructure best = null;
            double bestDistanceSqr = Double.MAX_VALUE;

            for (var entry : placements.entrySet()) {
                RandomSpreadStructurePlacement placement = entry.getKey();
                Set<Holder<Structure>> structures = entry.getValue();
                int spacing = placement.spacing();

                for (int dz = -ring; dz <= ring; dz++) {
                    boolean edgeZ = dz == -ring || dz == ring;

                    for (int dx = -ring; dx <= ring; dx++) {
                        if (!edgeZ && dx != -ring && dx != ring) continue;

                        ChunkPos chunkPos = placement.getPotentialStructureChunk(
                                seed,
                                sectionX + spacing * dx,
                                sectionZ + spacing * dz
                        );

                        LocatedStructure found = tryFindStructureAtChunk(
                                level,
                                chunkPos,
                                placement,
                                structures,
                                player
                        );

                        if (found == null) continue;

                        double distanceSqr = origin.distSqr(found.pos().pos());
                        if (distanceSqr < bestDistanceSqr) {
                            best = found;
                            bestDistanceSqr = distanceSqr;
                        }
                    }
                }
            }

            if (best != null) return best;
        }

        return null;
    }

    @Nullable
    private static LocatedStructure findNearestConcentricStructure(
            ServerLevel level,
            ServerPlayer player,
            BlockPos origin,
            Map<ConcentricRingsStructurePlacement, Set<Holder<Structure>>> placements
    ) {
        if (placements.isEmpty()) return null;

        var structureState = level.getChunkSource().getGeneratorState();

        LocatedStructure best = null;
        double bestDistanceSqr = Double.MAX_VALUE;

        for (var entry : placements.entrySet()) {
            ConcentricRingsStructurePlacement placement = entry.getKey();
            Set<Holder<Structure>> structures = entry.getValue();

            List<ChunkPos> ringPositions = structureState.getRingPositionsFor(placement);
            if (ringPositions == null) continue;

            for (ChunkPos chunkPos : ringPositions) {
                LocatedStructure found = tryFindStructureAtChunk(
                        level,
                        chunkPos,
                        placement,
                        structures,
                        player
                );

                if (found == null) continue;

                double distanceSqr = origin.distSqr(found.pos().pos());
                if (distanceSqr < bestDistanceSqr) {
                    best = found;
                    bestDistanceSqr = distanceSqr;
                }
            }
        }

        return best;
    }

    @Nullable
    private static LocatedStructure tryFindStructureAtChunk(
            ServerLevel level,
            ChunkPos chunkPos,
            StructurePlacement placement,
            Set<Holder<Structure>> structures,
            ServerPlayer player
    ) {
        StructureManager structureManager = level.structureManager();
        var structureRegistry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);

        for (Holder<Structure> holder : structures) {
            Structure structure = holder.value();

            StructureCheckResult result = structureManager.checkStructurePresence(
                    chunkPos,
                    structure,
                    placement,
                    false
            );

            if (result == StructureCheckResult.START_NOT_PRESENT) {
                continue;
            }

            BlockPos locatePos = resolveLocatePos(
                    level,
                    structureManager,
                    chunkPos,
                    structure,
                    placement,
                    result
            );

            if (locatePos == null) {
                continue;
            }

            if (hasDiscoveredStructureTarget(level, player, locatePos)
                    || hasTrackedStructureTarget(level, player, locatePos)) {
                continue;
            }

            ResourceLocation structureId = structureRegistry.getKey(structure);
            if (structureId == null) {
                continue;
            }

            return new LocatedStructure(
                    GlobalPos.of(level.dimension(), locatePos),
                    structureId
            );
        }

        return null;
    }

    @Nullable
    private static BlockPos resolveLocatePos(
            ServerLevel level,
            StructureManager structureManager,
            ChunkPos chunkPos,
            Structure structure,
            StructurePlacement placement,
            StructureCheckResult result
    ) {
        if (result == StructureCheckResult.START_PRESENT) {
            return placement.getLocatePos(chunkPos);
        }

        ChunkAccess chunk = level.getChunk(chunkPos.x, chunkPos.z, ChunkStatus.STRUCTURE_STARTS);

        StructureStart start = structureManager.getStartForStructure(
                SectionPos.bottomOf(chunk),
                structure,
                chunk
        );

        if (start == null || !start.isValid()) {
            return null;
        }

        return placement.getLocatePos(start.getChunkPos());
    }

    private static boolean hasDiscoveredStructureTarget(
            ServerLevel level,
            ServerPlayer player,
            BlockPos candidate
    ) {
        for (GlobalPos discovered : getDiscoveredStructures(player)) {
            if (!discovered.dimension().equals(level.dimension())) continue;

            BlockPos pos = discovered.pos();
            if (pos.getX() == candidate.getX() && pos.getZ() == candidate.getZ()) {
                return true;
            }
        }

        return false;
    }


    private static boolean hasTrackedStructureTarget(
            ServerLevel level,
            ServerPlayer player,
            BlockPos candidate
    ) {
        for (int slot = 0; slot < player.getInventory().getContainerSize(); slot++) {
            var stack = player.getInventory().getItem(slot);
            if (!stack.is(JolCraftItems.DEEPSLATE_COMPASS.get())) continue;

            GlobalPos target = stack.get(JolCraftDataComponents.DEEPSLATE_COMPASS_TARGET);
            if (target == null || !target.dimension().equals(level.dimension())) continue;

            BlockPos pos = target.pos();
            if (pos.getX() == candidate.getX() && pos.getZ() == candidate.getZ()) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    private static LocatedStructure nearest(
            BlockPos origin,
            @Nullable LocatedStructure first,
            @Nullable LocatedStructure second
    ) {
        if (first == null) return second;
        if (second == null) return first;

        double firstDistance = origin.distSqr(first.pos().pos());
        double secondDistance = origin.distSqr(second.pos().pos());

        return firstDistance <= secondDistance ? first : second;
    }
}