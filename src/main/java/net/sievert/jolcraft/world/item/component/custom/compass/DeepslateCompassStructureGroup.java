package net.sievert.jolcraft.world.item.component.custom.compass;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import org.jetbrains.annotations.Nullable;

public enum DeepslateCompassStructureGroup implements JolCraftEnumHelper.StringId {

    DWARVEN(JolCraftTags.Structures.DWARVEN, 0xFF242424),
    VILLAGES(JolCraftTags.Structures.VILLAGES, 0xFFb37b62),
    PILLAGERS(JolCraftTags.Structures.PILLAGERS, 0xFF8e9393),
    NETHER_PORTALS(JolCraftTags.Structures.NETHER_PORTALS, 0xFF271e3d),
    SURFACE(JolCraftTags.Structures.SURFACE, 0xFF61a137),
    RUINS(JolCraftTags.Structures.RUINS, 0xFFa54926),
    OCEAN(JolCraftTags.Structures.OCEAN, 0xFF2332c3),
    UNDERGROUND(JolCraftTags.Structures.UNDERGROUND, 0xFF111b21);

    private final TagKey<Structure> structureTag;
    private final int color;

    DeepslateCompassStructureGroup(TagKey<Structure> structureTag, int color) {
        this.structureTag = structureTag;
        this.color = color;
    }

    @Override
    public String getId() {
        return this.structureTag.location().getPath();
    }

    public TagKey<Structure> structureTag() {
        return this.structureTag;
    }

    public int color() {
        return this.color;
    }

    public static @Nullable DeepslateCompassStructureGroup byId(@Nullable String id) {
        return JolCraftEnumHelper.byStringIdNullable(DeepslateCompassStructureGroup.class, id, null);
    }

    public static @Nullable TagKey<Structure> structureTag(@Nullable String id) {
        DeepslateCompassStructureGroup group = byId(id);
        return group != null ? group.structureTag() : null;
    }

    public static int color(@Nullable String id, int fallback) {
        DeepslateCompassStructureGroup group = byId(id);
        return group != null ? group.color() : fallback;
    }
}