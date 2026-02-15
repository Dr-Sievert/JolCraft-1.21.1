package net.sievert.jolcraft.world.item.util.compass;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.util.JolCraftEnumHelper;

import javax.annotation.Nullable;

public enum StructureGroup implements JolCraftEnumHelper.StringId {

    DWARVEN(JolCraftTags.Structures.DWARVEN_STRUCTURES),
    ANCIENT(JolCraftTags.Structures.ANCIENT_STRUCTURES);

    private final TagKey<Structure> tag;

    StructureGroup(TagKey<Structure> tag) {
        this.tag = tag;
    }

    /** The id string (path) that matches JolCraftTagIds.* exactly (e.g. "dwarven_structures"). */
    @Override
    public String getId() {
        return tag.location().getPath();
    }

    /** Canonical TagKey singleton from JolCraftTags.Structures. */
    public TagKey<Structure> tag() {
        return tag;
    }

    public static @Nullable StructureGroup fromId(String id) {
        return JolCraftEnumHelper.byStringId(StructureGroup.class, id, null);
    }
}