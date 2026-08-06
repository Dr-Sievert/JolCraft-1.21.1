package net.sievert.jolcraft.world.item.component.custom.compass;

import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import net.sievert.jolcraft.world.worldgen.structure.JolCraftStructures;
import org.jetbrains.annotations.Nullable;

public enum DeepslateCompassStructureGroup implements JolCraftEnumHelper.StringId {

    DWARVEN(
            JolCraftTags.Structures.DWARVEN,
            JolCraftStructures.DWARVEN_FORTRESS.key(),
            0xFF242424
    ),
    VILLAGES(
            JolCraftTags.Structures.VILLAGES,
            BuiltinStructures.VILLAGE_PLAINS,
            0xFFB37B62
    ),
    PILLAGERS(
            JolCraftTags.Structures.PILLAGERS,
            BuiltinStructures.PILLAGER_OUTPOST,
            0xFF8E9393
    ),
    NETHER_PORTALS(
            JolCraftTags.Structures.NETHER_PORTALS,
            BuiltinStructures.RUINED_PORTAL_STANDARD,
            0xFF271E3D
    ),
    SURFACE(
            JolCraftTags.Structures.SURFACE,
            BuiltinStructures.JUNGLE_TEMPLE,
            0xFF61A137
    ),
    RUINS(
            JolCraftTags.Structures.RUINS,
            BuiltinStructures.TRAIL_RUINS,
            0xFFA54926
    ),
    OCEAN(
            JolCraftTags.Structures.OCEAN,
            BuiltinStructures.SHIPWRECK,
            0xFF2332C3
    ),
    UNDERGROUND(
            JolCraftTags.Structures.UNDERGROUND,
            BuiltinStructures.MINESHAFT,
            0xFF111B21
    );

    private final TagKey<Structure> structureTag;
    private final ResourceKey<Structure> displayStructure;
    private final int color;

    DeepslateCompassStructureGroup(
            TagKey<Structure> structureTag,
            ResourceKey<Structure> displayStructure,
            int color
    ) {
        this.structureTag = structureTag;
        this.displayStructure = displayStructure;
        this.color = color;
    }

    @Override
    public String getId() {
        return this.structureTag.location().getPath();
    }

    public TagKey<Structure> structureTag() {
        return this.structureTag;
    }

    public ResourceKey<Structure> displayStructure() {
        return this.displayStructure;
    }

    public int color() {
        return this.color;
    }

    public static @Nullable DeepslateCompassStructureGroup byId(
            @Nullable String id
    ) {
        return JolCraftEnumHelper.byStringIdNullable(
                DeepslateCompassStructureGroup.class,
                id,
                null
        );
    }

    public static @Nullable TagKey<Structure> structureTag(
            @Nullable String id
    ) {
        DeepslateCompassStructureGroup group = byId(id);

        return group != null
                ? group.structureTag()
                : null;
    }

    public static int color(
            @Nullable String id,
            int fallback
    ) {
        DeepslateCompassStructureGroup group = byId(id);

        return group != null
                ? group.color()
                : fallback;
    }
}