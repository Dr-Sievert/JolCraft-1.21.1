package net.sievert.jolcraft.world.worldgen.feature;

import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.*;

import java.util.List;

public class JolCraftOreReplacement {

    public static List<PlacementModifier> orePlacement(
            PlacementModifier countPlacement,
            PlacementModifier heightRange
    ) {
        return List.of(
                countPlacement,
                InSquarePlacement.spread(),
                heightRange,
                BiomeFilter.biome()
        );
    }

    public static List<PlacementModifier> commonOrePlacement(
            int count,
            PlacementModifier heightRange
    ) {
        return orePlacement(
                CountPlacement.of(count),
                heightRange
        );
    }

    public static List<PlacementModifier> rareOrePlacement(
            int chance,
            PlacementModifier heightRange
    ) {
        return orePlacement(
                RarityFilter.onAverageOnceEvery(chance),
                heightRange
        );
    }

    public static List<PlacementModifier> commonNetherOrePlacement(int count) {
        return commonOrePlacement(
                count,
                HeightRangePlacement.uniform(
                        VerticalAnchor.BOTTOM,
                        VerticalAnchor.TOP
                )
        );
    }

    public static List<PlacementModifier> rareNetherOrePlacement(int chance) {
        return rareOrePlacement(
                chance,
                HeightRangePlacement.uniform(
                        VerticalAnchor.BOTTOM,
                        VerticalAnchor.TOP
                )
        );
    }
}