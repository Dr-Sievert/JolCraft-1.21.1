package net.sievert.jolcraft.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.sievert.jolcraft.world.worldgen.structure.util.FeaturePlacementContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ConfiguredFeature.class)
public abstract class ConfiguredFeatureMixin {

    @WrapOperation(
            method = "place",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/feature/Feature;place(" +
                            "Lnet/minecraft/world/level/levelgen/feature/configurations/FeatureConfiguration;" +
                            "Lnet/minecraft/world/level/WorldGenLevel;" +
                            "Lnet/minecraft/world/level/chunk/ChunkGenerator;" +
                            "Lnet/minecraft/util/RandomSource;" +
                            "Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    @SuppressWarnings({"rawtypes"})
    private boolean jolcraft$trackFeaturePlacement(
            Feature instance,
            FeatureConfiguration configuration,
            WorldGenLevel level,
            ChunkGenerator chunkGenerator,
            RandomSource random,
            BlockPos pos,
            Operation<Boolean> original
    ) {
        FeaturePlacementContext.enter();

        try {
            return original.call(
                    instance,
                    configuration,
                    level,
                    chunkGenerator,
                    random,
                    pos
            );
        } finally {
            FeaturePlacementContext.exit();
        }
    }
}