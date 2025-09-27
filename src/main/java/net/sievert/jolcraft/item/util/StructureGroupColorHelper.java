package net.sievert.jolcraft.item.util;

import java.util.Map;

public class StructureGroupColorHelper {

    public static final Map<String, Integer> GROUP_COLORS = Map.of(
            "dwarven_structures", 0x505050,
            "ancient_structures", 0x009295
    );

    public static int getColor(String group) {
        return GROUP_COLORS.getOrDefault(group, 0xAAAAAA);
    }
}
