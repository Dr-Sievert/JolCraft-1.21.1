package net.sievert.jolcraft.item.util;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.sievert.jolcraft.data.JolCraftTags;

public class DeepslateCompassHelper {

    public static TagKey<Structure> getStructureTagForGroup(String group) {
        return switch (group) {
            case "dwarven_structures" -> JolCraftTags.Structures.DWARVEN_STRUCTURES;
            case "ancient_structures" -> JolCraftTags.Structures.ANCIENT_STRUCTURES;
            default -> null;
        };
    }

}
