package net.sievert.jolcraft.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.sievert.jolcraft.world.worldgen.structure.util.JolCraftStructureContext;
import net.sievert.jolcraft.world.worldgen.structure.util.SinglePlacementPart;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

@Mixin(targets =
        "net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement$Placer")
public abstract class JigsawPlacementPlacerMixin {

    @Unique
    private Set<SinglePlacementPart> jolcraft$singlePlacementParts;

    @Unique
    private Set<SinglePlacementPart> jolcraft$consumedSinglePlacementParts;

    @Unique
    private Set<StructurePoolElement> jolcraft$currentPrimaryCandidates;

    @Unique
    private ResourceKey<StructureTemplatePool> jolcraft$currentPool;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void jolcraft$initializeSinglePlacementParts(
            Registry<StructureTemplatePool> pools,
            int maxDepth,
            ChunkGenerator chunkGenerator,
            StructureTemplateManager structureTemplateManager,
            List<? super PoolElementStructurePiece> pieces,
            RandomSource random,
            CallbackInfo ci
    ) {
        this.jolcraft$singlePlacementParts =
                Set.copyOf(
                        JolCraftStructureContext.getSinglePlacementParts()
                );

        this.jolcraft$consumedSinglePlacementParts = new HashSet<>();
        this.jolcraft$currentPrimaryCandidates = jolcraft$newIdentitySet();
    }

    /**
     * Captures the pool belonging to the connector currently being processed.
     *
     * Only ordinal zero is captured. Later readPoolKey calls are used by
     * vanilla's expansion calculation and must not replace the connector pool.
     */
    @ModifyExpressionValue(
            method = "tryPlacingChildren",
            at = @At(
                    value = "INVOKE",
                    target =
                            "Lnet/minecraft/world/level/levelgen/structure/pools/JigsawPlacement$Placer;" +
                                    "readPoolKey(" +
                                    "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureTemplate$StructureBlockInfo;" +
                                    "Lnet/minecraft/world/level/levelgen/structure/pools/alias/PoolAliasLookup;" +
                                    ")Lnet/minecraft/resources/ResourceKey;",
                    ordinal = 0
            )
    )
    private ResourceKey<StructureTemplatePool> jolcraft$captureCurrentPool(
            ResourceKey<StructureTemplatePool> original
    ) {
        if (jolcraft$isSinglePlacementEnabled()) {
            this.jolcraft$currentPool = original;
            this.jolcraft$currentPrimaryCandidates =
                    jolcraft$newIdentitySet();
        }

        return original;
    }

    /**
     * Filters consumed single-placement templates from the primary pool.
     *
     * Weighted pools can contain the same element multiple times. Filtering
     * the returned list removes every weighted copy after the first successful
     * placement.
     */
    @ModifyExpressionValue(
            method = "tryPlacingChildren",
            at = @At(
                    value = "INVOKE",
                    target =
                            "Lnet/minecraft/world/level/levelgen/structure/pools/StructureTemplatePool;" +
                                    "getShuffledTemplates(" +
                                    "Lnet/minecraft/util/RandomSource;" +
                                    ")Ljava/util/List;",
                    ordinal = 0
            )
    )
    private List<StructurePoolElement> jolcraft$filterConsumedParts(
            List<StructurePoolElement> original
    ) {
        if (!jolcraft$isSinglePlacementEnabled()
                || this.jolcraft$currentPool == null) {
            return original;
        }

        this.jolcraft$currentPrimaryCandidates.addAll(original);

        return original.stream()
                .filter(candidate ->
                        !jolcraft$isConsumed(
                                this.jolcraft$currentPool,
                                candidate
                        )
                )
                .toList();
    }

    /**
     * Vanilla reaches List.add only after the candidate has passed its
     * attachment, rotation, height and collision checks.
     *
     * Therefore, the rule is consumed only after actual successful placement.
     */
    @WrapOperation(
            method = "tryPlacingChildren",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;add(Ljava/lang/Object;)Z"
            )
    )
    private boolean jolcraft$consumeAfterSuccessfulPlacement(
            List<Object> pieces,
            Object addedPiece,
            Operation<Boolean> original
    ) {
        boolean added = original.call(pieces, addedPiece);

        if (!added
                || !jolcraft$isSinglePlacementEnabled()
                || this.jolcraft$currentPool == null
                || !(addedPiece instanceof PoolElementStructurePiece child)
                || !this.jolcraft$currentPrimaryCandidates.contains(
                child.getElement()
        )) {
            return added;
        }

        jolcraft$ruleFor(
                this.jolcraft$currentPool,
                child.getElement()
        ).ifPresent(this.jolcraft$consumedSinglePlacementParts::add);

        return added;
    }

    /**
     * Captures vanilla's internal-jigsaw check:
     *
     * boundingBox.isInside(positionInFrontOfJigsaw)
     */
    @ModifyExpressionValue(
            method = "tryPlacingChildren",
            at = @At(
                    value = "INVOKE",
                    target =
                            "Lnet/minecraft/world/level/levelgen/structure/BoundingBox;" +
                                    "isInside(Lnet/minecraft/core/Vec3i;)Z"
            )
    )
    private boolean jolcraft$captureInternalJigsaw(
            boolean original,
            @Share("jolcraft$internalJigsaw")
            LocalBooleanRef internalJigsaw
    ) {
        internalJigsaw.set(original);
        return original;
    }

    /**
     * Allows an internal jigsaw to use its pool when its parent has reached
     * maxDepth.
     */
    @ModifyExpressionValue(
            method = "tryPlacingChildren",
            at = @At(
                    value = "FIELD",
                    target =
                            "Lnet/minecraft/world/level/levelgen/structure/pools/JigsawPlacement$Placer;" +
                                    "maxDepth:I",
                    ordinal = 0,
                    opcode = Opcodes.GETFIELD
            )
    )
    private int jolcraft$allowInternalPoolAtMaxDepth(
            int originalMaxDepth,
            @Share("jolcraft$internalJigsaw")
            LocalBooleanRef internalJigsaw
    ) {
        return jolcraft$ignoresDepth(internalJigsaw)
                ? originalMaxDepth + 1
                : originalMaxDepth;
    }

    /**
     * Allows a successfully placed internal child to enter the placement
     * queue when its parent has reached maxDepth.
     */
    @ModifyExpressionValue(
            method = "tryPlacingChildren",
            at = @At(
                    value = "FIELD",
                    target =
                            "Lnet/minecraft/world/level/levelgen/structure/pools/JigsawPlacement$Placer;" +
                                    "maxDepth:I",
                    ordinal = 1,
                    opcode = Opcodes.GETFIELD
            )
    )
    private int jolcraft$allowInternalChildAtMaxDepth(
            int originalMaxDepth,
            @Share("jolcraft$internalJigsaw")
            LocalBooleanRef internalJigsaw
    ) {
        return jolcraft$ignoresDepth(internalJigsaw)
                ? originalMaxDepth + 1
                : originalMaxDepth;
    }

    /**
     * Keeps an internal child at its parent's depth instead of increasing the
     * external structure depth.
     */
    @ModifyArg(
            method = "tryPlacingChildren",
            at = @At(
                    value = "INVOKE",
                    target =
                            "Lnet/minecraft/world/level/levelgen/structure/pools/JigsawPlacement$PieceState;" +
                                    "<init>(" +
                                    "Lnet/minecraft/world/level/levelgen/structure/PoolElementStructurePiece;" +
                                    "Lorg/apache/commons/lang3/mutable/MutableObject;" +
                                    "I)V"
            ),
            index = 2
    )
    private int jolcraft$preserveInternalDepth(
            int nextDepth,
            @Local(argsOnly = true) int depth,
            @Share("jolcraft$internalJigsaw")
            LocalBooleanRef internalJigsaw
    ) {
        return jolcraft$ignoresDepth(internalJigsaw)
                ? depth
                : nextDepth;
    }

    @Unique
    private boolean jolcraft$isConsumed(
            ResourceKey<StructureTemplatePool> pool,
            StructurePoolElement element
    ) {
        return jolcraft$ruleFor(pool, element)
                .map(this.jolcraft$consumedSinglePlacementParts::contains)
                .orElse(false);
    }

    @Unique
    private java.util.Optional<SinglePlacementPart> jolcraft$ruleFor(
            ResourceKey<StructureTemplatePool> pool,
            StructurePoolElement element
    ) {
        ResourceLocation template = jolcraft$getTemplateId(element);

        if (template == null) {
            return java.util.Optional.empty();
        }

        return this.jolcraft$singlePlacementParts.stream()
                .filter(rule ->
                        rule.pool().equals(pool)
                                && rule.template().equals(template)
                )
                .findFirst();
    }

    @Unique
    private static ResourceLocation jolcraft$getTemplateId(
            StructurePoolElement element
    ) {
        if (!(element instanceof SinglePoolElement singlePoolElement)) {
            return null;
        }

        return ((SinglePoolElementAccessor) singlePoolElement)
                .jolcraft$getTemplate()
                .left()
                .orElse(null);
    }

    @Unique
    private boolean jolcraft$isSinglePlacementEnabled() {
        return JolCraftStructureContext.isActive()
                && !this.jolcraft$singlePlacementParts.isEmpty();
    }

    @Unique
    private static boolean jolcraft$ignoresDepth(
            LocalBooleanRef internalJigsaw
    ) {
        return JolCraftStructureContext.isActive()
                && internalJigsaw.get();
    }

    @Unique
    private static <T> Set<T> jolcraft$newIdentitySet() {
        return Collections.newSetFromMap(new IdentityHashMap<>());
    }
}