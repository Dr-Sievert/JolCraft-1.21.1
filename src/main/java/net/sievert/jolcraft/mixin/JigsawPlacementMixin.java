package net.sievert.jolcraft.mixin;

import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.sievert.jolcraft.world.worldgen.structure.util.JolCraftStructureContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(JigsawPlacement.class)
public class JigsawPlacementMixin {

    @ModifyVariable(
            method = "addPieces(Lnet/minecraft/world/level/levelgen/structure/Structure$GenerationContext;Lnet/minecraft/core/Holder;Ljava/util/Optional;ILnet/minecraft/core/BlockPos;ZLjava/util/Optional;ILnet/minecraft/world/level/levelgen/structure/pools/alias/PoolAliasLookup;Lnet/minecraft/world/level/levelgen/structure/pools/DimensionPadding;Lnet/minecraft/world/level/levelgen/structure/templatesystem/LiquidSettings;)Ljava/util/Optional;",
            at = @At("STORE"),
            ordinal = 0
    )
    private static Rotation jolcraft$modifyStartRotation(Rotation original) {
        Rotation forced = JolCraftStructureContext.getRotation();

        if (forced != null) {
            return forced;
        }

        return original;
    }
}