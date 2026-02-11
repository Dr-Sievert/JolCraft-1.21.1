package net.sievert.jolcraft.data.attachment.custom.compass;

import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.sievert.jolcraft.data.JolCraftStats;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.worldgen.structure.JolCraftStructures;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

public class DiscoveredStructuresHelper {

    /**
     * SERVER-SIDE: Returns the discovered structures for this player (snapshot).
     */
    public static Set<GlobalPos> getDiscoveredStructures(Player player) {
        if (player == null) return Set.of();
        return DiscoveredStructures.get(player).getDiscovered();
    }

    /**
     * SERVER-SIDE: Returns true if the player has discovered the given GlobalPos.
     */
    public static boolean hasDiscovered(Player player, GlobalPos pos) {
        if (player == null || pos == null) return false;
        return DiscoveredStructures.get(player).isDiscovered(pos);
    }

    /**
     * SERVER-SIDE: Returns true if the player has discovered any structure in the given dimension.
     */
    public static boolean hasDiscoveredInDimension(Player player, String dimId) {
        if (player == null || dimId == null || dimId.isEmpty()) return false;

        for (GlobalPos gp : DiscoveredStructures.get(player).getDiscovered()) {
            if (gp.dimension().location().toString().equals(dimId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * SERVER-SIDE: Add a structure location and structure-based discovery score.
     */
    public static void addDiscoveredStructureServer(Player player, GlobalPos pos, ResourceLocation structureId) {
        if (player == null || pos == null || structureId == null) return;

        DiscoveredStructures ds = DiscoveredStructures.get(player);

        if (ds.addDiscovered(pos)) {
            JolCraftStats.awardStructureDiscovery(player);

            int score = STRUCTURE_SCORES.getOrDefault(structureId, 50);
            ds.addScore(score);
        }
    }

    private static final Map<ResourceLocation, Integer> STRUCTURE_SCORES = Map.of(
            BuiltinStructures.TRAIL_RUINS.location(), 25,
            BuiltinStructures.ANCIENT_CITY.location(), 100,
            JolCraftStructures.DWARVEN_TRAIL_RUIN.id(), 25,
            JolCraftStructures.FORGE.id(), 100
    );

    /**
     * Get the player's current structure discovery score (for Explorer dwarf leveling).
     */
    public static int getDiscoveryScore(Player player) {
        if (player == null) return 0;
        return DiscoveredStructures.get(player).getScore();
    }

    @Nullable
    public static GlobalPos findNearestUndiscoveredStructure(
            ServerLevel level,
            TagKey<Structure> structureTag,
            BlockPos origin,
            int radius,
            Player player
    ) {
        if (level == null || structureTag == null || origin == null || player == null) return null;

        BlockPos pos = level.findNearestMapStructure(structureTag, origin, radius, true);
        if (pos == null) return null;

        var structureManager = level.structureManager();
        var structureRegistry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);

        if (!structureRegistry.getTagOrEmpty(structureTag).iterator().hasNext()) {
            JolCraftLogs.debug(
                    JolCraftLogTags.ATTACHMENT,
                    "findNearestUndiscoveredStructure: empty structure tag {}",
                    structureTag.location()
            );
            return null;
        }

        Structure matchedStructure = null;
        for (Structure structure : structureManager.getAllStructuresAt(pos).keySet()) {
            for (Holder<Structure> holder : structureRegistry.getTagOrEmpty(structureTag)) {
                if (holder.value() == structure) {
                    matchedStructure = structure;
                    break;
                }
            }
            if (matchedStructure != null) break;
        }

        int spacing = 0;
        if (matchedStructure != null) {
            Registry<StructureSet> setRegistry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE_SET);
            for (StructureSet set : setRegistry) {
                for (StructureSet.StructureSelectionEntry entry : set.structures()) {
                    if (entry.structure().value() == matchedStructure) {
                        var placement = set.placement();
                        if (placement instanceof RandomSpreadStructurePlacement randomSpread) {
                            spacing = randomSpread.spacing();
                        }
                        break;
                    }
                }
                if (spacing > 0) break;
            }
        }

        if (matchedStructure == null) {
            JolCraftLogs.debug(
                    JolCraftLogTags.ATTACHMENT,
                    "findNearestUndiscoveredStructure: nearest pos {} for tag {} but no matching structure found",
                    pos,
                    structureTag.location()
            );
            return null;
        }

        int exclusionRadius = Math.max(96, spacing > 0 ? (spacing / 2 + 64) : 96);
        int exclusionRadiusSqr = exclusionRadius * exclusionRadius;

        for (GlobalPos gp : DiscoveredStructures.get(player).getDiscovered()) {
            if (!gp.dimension().equals(level.dimension())) continue;
            if (gp.pos().distSqr(pos) < exclusionRadiusSqr) return null;
        }

        BlockPos patchedPos = pos;
        StructureStart start = structureManager.getStructureAt(pos, matchedStructure);
        if (start.isValid()) {
            var box = start.getBoundingBox();
            int centerY = box.getCenter().getY();
            if (box.isInside(pos)) {
                if (pos.getY() == 0 || pos.getY() < box.minY() || pos.getY() > box.maxY()) {
                    patchedPos = BlockPos.containing(pos.getX(), centerY, pos.getZ());
                }
            }
        }

        return GlobalPos.of(level.dimension(), patchedPos);
    }
}