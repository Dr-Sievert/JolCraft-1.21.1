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

public final class MinYPlacementFilter extends PlacementFilter {

    public static final MapCodec<MinYPlacementFilter> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    Codec.INT.fieldOf(JolCraftPlacementModifierTypeIds.MIN_Y).forGetter(filter -> filter.minY)
            ).apply(instance, MinYPlacementFilter::new)
    );

    private final int minY;

    private MinYPlacementFilter(int minY) {
        this.minY = minY;
    }

    public static MinYPlacementFilter of(int minY) {
        return new MinYPlacementFilter(minY);
    }

    @Override
    protected boolean shouldPlace(@NotNull PlacementContext context, @NotNull RandomSource random, BlockPos pos) {
        return pos.getY() >= minY;
    }

    @Override
    public @NotNull PlacementModifierType<?> type() {
        return JolCraftPlacementModifierTypes.MIN_Y.get();
    }
}