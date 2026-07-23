package net.sievert.jolcraft.mixin;

import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.world.worldgen.structure.util.FeaturePlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(WorldGenRegion.class)
public abstract class WorldGenRegionMixin {

    @Inject(
            method = "setBlock",
            at = @At("HEAD"),
            cancellable = true
    )
    private void jolcraft$preventFeaturePlacementInsideProtectedStructure(
            BlockPos pos,
            BlockState state,
            int flags,
            int recursionLeft,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!FeaturePlacementContext.isPlacingFeature()) {
            return;
        }

        WorldGenRegion region = (WorldGenRegion) (Object) this;

        if (!region.ensureCanWrite(pos)) {
            cir.setReturnValue(false);
            return;
        }

        if (jolcraft$isInsideProtectedStructure(region, pos)) {
            cir.setReturnValue(false);
        }
    }

    @Unique
    private static boolean jolcraft$isInsideProtectedStructure(
            WorldGenRegion region,
            BlockPos pos
    ) {
        ChunkAccess targetChunk = region.getChunk(pos);

        if (targetChunk.getPersistedStatus().isBefore(
                ChunkStatus.STRUCTURE_REFERENCES
        )) {
            return true;
        }

        Registry<Structure> structures = region.registryAccess()
                .registryOrThrow(Registries.STRUCTURE);

        for (Map.Entry<Structure, LongSet> entry
                : targetChunk.getAllReferences().entrySet()) {
            Structure structure = entry.getKey();

            if (!structures.wrapAsHolder(structure).is(
                    JolCraftTags.Structures.FEATURE_PROTECTED
            )) {
                continue;
            }

            for (long reference : entry.getValue()) {
                ChunkPos startPos = new ChunkPos(reference);

                if (!region.hasChunk(startPos.x, startPos.z)) {
                    return true;
                }

                ChunkAccess startChunk = region.getChunk(
                        startPos.x,
                        startPos.z,
                        ChunkStatus.STRUCTURE_STARTS
                );
                StructureStart start = startChunk.getStartForStructure(
                        structure
                );

                if (start != null && start.isValid()) {
                    for (StructurePiece piece : start.getPieces()) {
                        if (piece.getBoundingBox().isInside(pos)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }
}