package net.sievert.jolcraft.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.sievert.jolcraft.data.JolCraftTags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ConfiguredFeature.class)
public abstract class ConfiguredFeatureMixin {

    @SuppressWarnings("deprecation")
    @Inject(
            method = "place",
            at = @At("HEAD"),
            cancellable = true
    )
    private void jolcraft$preventPlacementInsideProtectedStructure(
            WorldGenLevel level,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BlockPos pos,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (!(level instanceof WorldGenRegion region)) {
            return;
        }

        if (region.getLevel()
                .structureManager()
                .forWorldGenRegion(region)
                .getStructureWithPieceAt(
                        pos,
                        JolCraftTags.Structures.FEATURE_PROTECTED
                )
                .isValid()) {
            cir.setReturnValue(false);
        }
    }
}