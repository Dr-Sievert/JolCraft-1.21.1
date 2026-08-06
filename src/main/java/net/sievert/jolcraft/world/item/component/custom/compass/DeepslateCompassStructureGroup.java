package net.sievert.jolcraft.world.item.component.custom.compass;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import net.sievert.jolcraft.world.worldgen.structure.JolCraftStructures;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public enum DeepslateCompassStructureGroup implements JolCraftEnumHelper.StringId {

    DWARVEN(
            JolCraftTags.Structures.DWARVEN,
            JolCraftStructures.DWARVEN_FORTRESS.key(),
            0xFF242424,
            6
    ),
    VILLAGES(
            JolCraftTags.Structures.VILLAGES,
            BuiltinStructures.VILLAGE_PLAINS,
            0xFFB37B62,
            2
    ),
    PILLAGERS(
            JolCraftTags.Structures.PILLAGERS,
            BuiltinStructures.PILLAGER_OUTPOST,
            0xFF8E9393,
            3
    ),
    NETHER_PORTALS(
            JolCraftTags.Structures.NETHER_PORTALS,
            BuiltinStructures.RUINED_PORTAL_STANDARD,
            0xFF271E3D,
            2
    ),
    SURFACE(
            JolCraftTags.Structures.SURFACE,
            BuiltinStructures.JUNGLE_TEMPLE,
            0xFF61A137,
            1
    ),
    RUINS(
            JolCraftTags.Structures.RUINS,
            BuiltinStructures.TRAIL_RUINS,
            0xFFA54926,
            3
    ),
    OCEAN(
            JolCraftTags.Structures.OCEAN,
            BuiltinStructures.SHIPWRECK,
            0xFF2332C3,
            3
    ),
    UNDERGROUND(
            JolCraftTags.Structures.UNDERGROUND,
            BuiltinStructures.MINESHAFT,
            0xFF111B21,
            4
    );

    private static final Map<ResourceLocation, Integer> STRUCTURE_DUST_BONUSES = Map.ofEntries(
            Map.entry(BuiltinStructures.WOODLAND_MANSION.location(), 2),
            Map.entry(BuiltinStructures.TRAIL_RUINS.location(), 1),
            Map.entry(BuiltinStructures.OCEAN_MONUMENT.location(), 2),
            Map.entry(BuiltinStructures.TRIAL_CHAMBERS.location(), 1),
            Map.entry(BuiltinStructures.STRONGHOLD.location(), 2),
            Map.entry(BuiltinStructures.ANCIENT_CITY.location(), 3),
            Map.entry(JolCraftStructures.DWARVEN_FORTRESS.id(), 4)
    );

    private final TagKey<Structure> structureTag;
    private final ResourceKey<Structure> displayStructure;
    private final int color;
    private final int discoveryDust;

    DeepslateCompassStructureGroup(
            TagKey<Structure> structureTag,
            ResourceKey<Structure> displayStructure,
            int color,
            int discoveryDust
    ) {
        this.structureTag = structureTag;
        this.displayStructure = displayStructure;
        this.color = color;
        this.discoveryDust = discoveryDust;
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

    public int discoveryDust(ResourceLocation structureId) {
        return this.discoveryDust + STRUCTURE_DUST_BONUSES.getOrDefault(structureId, 0);
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

    public static @Nullable DeepslateCompassStructureGroup byColor(int color) {
        for (DeepslateCompassStructureGroup group : values()) {
            if (group.color == color) {
                return group;
            }
        }

        return null;
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
