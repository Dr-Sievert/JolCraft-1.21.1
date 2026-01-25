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
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftStats;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DiscoveredStructuresHelper {

    /**
     * SERVER-SIDE: Returns the discovered structures for this player.
     */
    public static List<GlobalPos> getDiscoveredStructures(Player player) {
        if (player == null) return List.of();
        DiscoveredStructures ds = DiscoveredStructures.get(player);
        return ds.getDiscovered();
    }

    /**
     * SERVER-SIDE: Returns true if the player has discovered the given GlobalPos.
     */
    public static boolean hasDiscovered(Player player, GlobalPos pos) {
        return getDiscoveredStructures(player).contains(pos);
    }

    /**
     * SERVER-SIDE: Returns true if the player has discovered any structure in the given dimension.
     */
    public static boolean hasDiscoveredInDimension(Player player, String dimId) {
        return getDiscoveredStructures(player).stream()
                .anyMatch(gp -> gp.dimension().location().toString().equals(dimId));
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
            if (ds instanceof DiscoveredStructuresImpl impl) {
                impl.addScore(score);
            }
        }
    }

    private static final Map<ResourceLocation, Integer> STRUCTURE_SCORES = Map.of(
            ResourceLocation.withDefaultNamespace("trail_ruins"),          25,
            ResourceLocation.withDefaultNamespace("ancient_city"),         100,
            JolCraft.location("dwarven_trail_ruin"),                         25,
            JolCraft.location("forge"),                                      100
    );

    /**
     * Get the player's current structure discovery score (for Explorer dwarf leveling).
     */
    public static int getDiscoveryScore(Player player) {
        DiscoveredStructures ds = DiscoveredStructures.get(player);
        if (ds instanceof DiscoveredStructuresImpl impl) {
            return impl.getScore();
        }
        return 0;
    }

    @Nullable
    public static GlobalPos findNearestUndiscoveredStructure(
            ServerLevel level,
            TagKey<Structure> structureTag,
            BlockPos origin,
            int radius,
            Player player
    ) {
        Set<BlockPos> discovered = DiscoveredStructures.get(player).getDiscovered().stream()
                .filter(gp -> gp.dimension().equals(level.dimension()))
                .map(GlobalPos::pos)
                .collect(Collectors.toSet());

        BlockPos pos = level.findNearestMapStructure(structureTag, origin, radius, true);
        if (pos == null) return null;

        var structureManager = level.structureManager();
        var registry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE);
        Structure matchedStructure = null;
        int maxDistanceFromCenter = 80;
        for (Structure structure : structureManager.getAllStructuresAt(pos).keySet()) {
            for (Holder<Structure> holder : registry.getTagOrEmpty(structureTag)) {
                if (holder.value() == structure) {
                    matchedStructure = structure;
                    try {
                        var field = structure.getClass().getDeclaredField("maxDistanceFromCenter");
                        field.setAccessible(true);
                        maxDistanceFromCenter = field.getInt(structure);
                    } catch (Exception ignored) {}
                    break;
                }
            }
            if (matchedStructure != null) break;
        }

        Registry<StructureSet> setRegistry = level.registryAccess().lookupOrThrow(Registries.STRUCTURE_SET);

        int spacing = 0;
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

        int exclusionRadius = Math.max(64, maxDistanceFromCenter + spacing / 2);

        boolean foundNear = discovered.stream()
                .anyMatch(center -> center.distSqr(pos) < exclusionRadius * exclusionRadius);
        if (foundNear) return null;

        BlockPos patchedPos = pos;
        if (matchedStructure != null) {
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
        }
        return GlobalPos.of(level.dimension(), patchedPos);
    }
}
