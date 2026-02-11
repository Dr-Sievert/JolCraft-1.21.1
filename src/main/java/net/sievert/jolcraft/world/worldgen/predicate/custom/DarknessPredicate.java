package net.sievert.jolcraft.world.worldgen.predicate.custom;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.sievert.jolcraft.world.worldgen.predicate.JolCraftBlockPredicateTypes;
import org.jetbrains.annotations.NotNull;

public class DarknessPredicate implements BlockPredicate {

    private static final String MAX_BRIGHTNESS = "max_brightness";

    public static final MapCodec<DarknessPredicate> CODEC =
            Codec.INT.fieldOf(MAX_BRIGHTNESS)
                    .xmap(DarknessPredicate::new, p -> p.maxBrightness);

    private final int maxBrightness;
    public DarknessPredicate(int maxBrightness) { this.maxBrightness = maxBrightness; }

    @Override
    public boolean test(WorldGenLevel level, BlockPos pos) {
        return level.getRawBrightness(pos, 0) <= maxBrightness;
    }

    @Override
    public @NotNull BlockPredicateType<?> type() {
        return JolCraftBlockPredicateTypes.DARKNESS.value();
    }
}
