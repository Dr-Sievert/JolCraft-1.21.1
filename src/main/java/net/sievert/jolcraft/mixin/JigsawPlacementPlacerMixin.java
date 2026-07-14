package net.sievert.jolcraft.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.sievert.jolcraft.world.worldgen.structure.util.JolCraftStructureContext;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(targets = "net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement$Placer")
public abstract class JigsawPlacementPlacerMixin {

    /**
     * Captures vanilla's exact internal-jigsaw check:
     *
     * boundingBox.isInside(positionInFrontOfJigsaw)
     */
    @ModifyExpressionValue(
            method = "tryPlacingChildren",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;isInside(Lnet/minecraft/core/Vec3i;)Z"
            )
    )
    private boolean jolcraft$captureInternalJigsaw(
            boolean original,
            @Share("jolcraft$internalJigsaw") LocalBooleanRef internalJigsaw
    ) {
        internalJigsaw.set(original);
        return original;
    }

    /**
     * Allows an internal jigsaw to use its normal pool when the current
     * piece has already reached maxDepth.
     *
     * Targets:
     * if (depth != this.maxDepth)
     */
    @ModifyExpressionValue(
            method = "tryPlacingChildren",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/levelgen/structure/pools/JigsawPlacement$Placer;maxDepth:I",
                    ordinal = 0,
                    opcode = Opcodes.GETFIELD)
    )
    private int jolcraft$allowInternalPoolAtMaxDepth(
            int originalMaxDepth,
            @Share("jolcraft$internalJigsaw") LocalBooleanRef internalJigsaw
    ) {
        return jolcraft$ignoresDepth(internalJigsaw)
                ? originalMaxDepth + 1
                : originalMaxDepth;
    }

    /**
     * Allows the successfully placed internal child to be queued even when
     * the current external depth has reached maxDepth.
     *
     * Targets:
     * if (depth + 1 <= this.maxDepth)
     */
    @ModifyExpressionValue(
            method = "tryPlacingChildren",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/level/levelgen/structure/pools/JigsawPlacement$Placer;maxDepth:I",
                    ordinal = 1,
                    opcode = Opcodes.GETFIELD)
    )
    private int jolcraft$allowInternalChildAtMaxDepth(
            int originalMaxDepth,
            @Share("jolcraft$internalJigsaw") LocalBooleanRef internalJigsaw
    ) {
        return jolcraft$ignoresDepth(internalJigsaw)
                ? originalMaxDepth + 1
                : originalMaxDepth;
    }

    /**
     * Keeps an internal child at its parent's depth instead of assigning
     * depth + 1.
     */
    @ModifyArg(
            method = "tryPlacingChildren",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/level/levelgen/structure/pools/JigsawPlacement$PieceState;<init>(Lnet/minecraft/world/level/levelgen/structure/PoolElementStructurePiece;Lorg/apache/commons/lang3/mutable/MutableObject;I)V"
            ),
            index = 2
    )
    private int jolcraft$preserveInternalDepth(
            int nextDepth,
            @Local(argsOnly = true) int depth,
            @Share("jolcraft$internalJigsaw") LocalBooleanRef internalJigsaw
    ) {
        return jolcraft$ignoresDepth(internalJigsaw)
                ? depth
                : nextDepth;
    }

    @Unique
    private static boolean jolcraft$ignoresDepth(LocalBooleanRef internalJigsaw) {
        return JolCraftStructureContext.isActive() && internalJigsaw.get();
    }
}