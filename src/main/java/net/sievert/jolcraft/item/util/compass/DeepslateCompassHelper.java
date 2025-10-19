package net.sievert.jolcraft.item.util.compass;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

import java.util.Map;

public class DeepslateCompassHelper {

    public static TagKey<Structure> getStructureTagForGroup(String groupId) {
        StructureGroup group = StructureGroup.fromId(groupId);
        return group != null ? group.tag() : null;
    }

    private static final Map<StructureGroup, Integer> GROUP_COLORS = Map.of(
            StructureGroup.DWARVEN, 0x505050,
            StructureGroup.ANCIENT, 0x009295
    );

    public static int getColor(StructureGroup group) {
        return GROUP_COLORS.getOrDefault(group, 0xAAAAAA);
    }

    public static int getColor(String groupId) {
        StructureGroup group = StructureGroup.fromId(groupId);
        return getColor(group);
    }
}
