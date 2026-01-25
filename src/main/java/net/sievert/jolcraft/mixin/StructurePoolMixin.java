package net.sievert.jolcraft.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = StructureTemplatePool.class, priority = 1200)
public class StructurePoolMixin {

    @Unique
    private static final int WEIGHT_MAX = 5000;

    @WrapOperation(
            method = { "lambda$static$0", "lambda$static$1" },
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/serialization/Codec;intRange(II)Lcom/mojang/serialization/Codec;"
            ),
            require = 0
    )
    private static Codec<Integer> jolcraft_increaseWeightLimit(
            int minRange,
            int maxRange,
            Operation<Codec<Integer>> original
    ) {
        if (minRange == 1 && maxRange == 150) {
            return original.call(minRange, WEIGHT_MAX);
        }
        return original.call(minRange, maxRange);
    }
}