package net.sievert.jolcraft.world.item.component.custom.compass;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import net.sievert.jolcraft.util.client.JolCraftColors;
import net.sievert.jolcraft.world.worldgen.structure.JolCraftStructures;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public enum DeepslateCompassStructureGroup implements JolCraftEnumHelper.StringId {

    DWARVEN(
            JolCraftTags.Structures.DWARVEN,
            List.of(
                    JolCraftStructures.DWARVEN_FORTRESS.key()
            ),
            "242424",
            6
    ),
    VILLAGES(
            JolCraftTags.Structures.VILLAGES,
            List.of(
                    BuiltinStructures.VILLAGE_PLAINS,
                    BuiltinStructures.VILLAGE_DESERT,
                    BuiltinStructures.VILLAGE_SAVANNA,
                    BuiltinStructures.VILLAGE_SNOWY,
                    BuiltinStructures.VILLAGE_TAIGA
            ),
            "B37B62",
            2
    ),
    PILLAGERS(
            JolCraftTags.Structures.PILLAGERS,
            List.of(
                    BuiltinStructures.PILLAGER_OUTPOST,
                    BuiltinStructures.WOODLAND_MANSION
            ),
            "8E9393",
            3
    ),
    NETHER_PORTALS(
            JolCraftTags.Structures.NETHER_PORTALS,
            List.of(
                    BuiltinStructures.RUINED_PORTAL_STANDARD,
                    BuiltinStructures.RUINED_PORTAL_DESERT,
                    BuiltinStructures.RUINED_PORTAL_MOUNTAIN,
                    BuiltinStructures.RUINED_PORTAL_JUNGLE,
                    BuiltinStructures.RUINED_PORTAL_SWAMP,
                    BuiltinStructures.RUINED_PORTAL_OCEAN
            ),
            "271E3D",
            2
    ),
    SURFACE(
            JolCraftTags.Structures.SURFACE,
            List.of(
                    BuiltinStructures.VILLAGE_PLAINS,
                    BuiltinStructures.VILLAGE_DESERT,
                    BuiltinStructures.VILLAGE_SAVANNA,
                    BuiltinStructures.VILLAGE_SNOWY,
                    BuiltinStructures.VILLAGE_TAIGA,
                    BuiltinStructures.PILLAGER_OUTPOST,
                    BuiltinStructures.WOODLAND_MANSION,
                    BuiltinStructures.RUINED_PORTAL_STANDARD,
                    BuiltinStructures.RUINED_PORTAL_DESERT,
                    BuiltinStructures.RUINED_PORTAL_MOUNTAIN,
                    BuiltinStructures.RUINED_PORTAL_JUNGLE,
                    BuiltinStructures.RUINED_PORTAL_SWAMP,
                    BuiltinStructures.RUINED_PORTAL_OCEAN,
                    BuiltinStructures.MINESHAFT_MESA,
                    BuiltinStructures.JUNGLE_TEMPLE,
                    BuiltinStructures.DESERT_PYRAMID,
                    BuiltinStructures.IGLOO,
                    BuiltinStructures.SWAMP_HUT
            ),
            "61A137",
            1
    ),
    RUINS(
            JolCraftTags.Structures.RUINS,
            List.of(
                    BuiltinStructures.TRAIL_RUINS,
                    BuiltinStructures.OCEAN_RUIN_COLD,
                    BuiltinStructures.OCEAN_RUIN_WARM
            ),
            "A54926",
            3
    ),
    OCEAN(
            JolCraftTags.Structures.OCEAN,
            List.of(
                    BuiltinStructures.BURIED_TREASURE,
                    BuiltinStructures.SHIPWRECK,
                    BuiltinStructures.SHIPWRECK_BEACHED,
                    BuiltinStructures.OCEAN_RUIN_COLD,
                    BuiltinStructures.OCEAN_RUIN_WARM,
                    BuiltinStructures.OCEAN_MONUMENT
            ),
            "2332C3",
            3
    ),
    UNDERGROUND(
            JolCraftTags.Structures.UNDERGROUND,
            List.of(
                    BuiltinStructures.MINESHAFT,
                    BuiltinStructures.ANCIENT_CITY,
                    BuiltinStructures.TRIAL_CHAMBERS,
                    BuiltinStructures.STRONGHOLD,
                    JolCraftStructures.DWARVEN_FORTRESS.key()
            ),
            "111B21",
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
    private final List<ResourceKey<Structure>> structures;
    private final String color;
    private final int discoveryDust;

    DeepslateCompassStructureGroup(
            TagKey<Structure> structureTag,
            List<ResourceKey<Structure>> structures,
            String color,
            int discoveryDust
    ) {
        this.structureTag = structureTag;
        this.structures = List.copyOf(structures);
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

    public List<ResourceKey<Structure>> structures() {
        return this.structures;
    }

    public String color() {
        return this.color;
    }

    public int discoveryDust(ResourceLocation structureId) {
        return this.discoveryDust
                + STRUCTURE_DUST_BONUSES.getOrDefault(structureId, 0);
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

    public static @Nullable DeepslateCompassStructureGroup byColor(String color) {
        for (DeepslateCompassStructureGroup group : values()) {
            if (group.color.equals(color)) {
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
                ? JolCraftColors.argb(group.color())
                : fallback;
    }
}
