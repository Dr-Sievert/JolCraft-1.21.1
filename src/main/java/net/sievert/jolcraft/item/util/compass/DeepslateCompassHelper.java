package net.sievert.jolcraft.item.util.compass;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;

public class DeepslateCompassHelper {

    public static TagKey<Structure> getStructureTagForGroup(String groupId) {
        StructureGroup group = StructureGroup.fromId(groupId);
        return group != null ? group.tag() : null;
    }
}
