package net.sievert.jolcraft.item.util.compass;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.sievert.jolcraft.data.JolCraftTags;

public enum StructureGroup {
    DWARVEN("dwarven_structures", JolCraftTags.Structures.DWARVEN_STRUCTURES),
    ANCIENT("ancient_structures", JolCraftTags.Structures.ANCIENT_STRUCTURES);

    private final String id;
    private final TagKey<Structure> tag;

    StructureGroup(String id, TagKey<Structure> tag) {
        this.id = id;
        this.tag = tag;
    }

    public String id() {
        return id;
    }

    public TagKey<Structure> tag() {
        return tag;
    }

    public static StructureGroup fromId(String id) {
        for (StructureGroup group : values()) {
            if (group.id.equals(id)) return group;
        }
        return null;
    }
}
