package net.sievert.jolcraft.world.worldgen.placement.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.sievert.jolcraft.data.id.worldgen.JolCraftPlacementModifierTypeIds;
import net.sievert.jolcraft.world.worldgen.placement.JolCraftPlacementModifierTypes;
import org.jetbrains.annotations.NotNull;

public final class MaxYPlacementFilter extends PlacementFilter {

    public static final MapCodec<MaxYPlacementFilter> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.INT.fieldOf(JolCraftPlacementModifierTypeIds.MAX_Y).forGetter(filter -> filter.maxY)
            ).apply(instance, MaxYPlacementFilter::new)
    );

    private final int maxY;

    private MaxYPlacementFilter(int maxY) {
        this.maxY = maxY;
    }

    public static MaxYPlacementFilter of(int maxY) {
        return new MaxYPlacementFilter(maxY);
    }

    @Override
    protected boolean shouldPlace(@NotNull PlacementContext context, @NotNull RandomSource random, BlockPos pos) {
        return pos.getY() <= maxY;
    }

    @Override
    public @NotNull PlacementModifierType<?> type() {
        return JolCraftPlacementModifierTypes.MAX_Y.get();
    }
}