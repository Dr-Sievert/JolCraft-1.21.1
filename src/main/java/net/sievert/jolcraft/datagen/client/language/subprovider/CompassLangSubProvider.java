package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.datagen.client.language.util.JolCraftLanguageCategory;
import net.sievert.jolcraft.datagen.client.language.util.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.util.compass.StructureGroup;
import net.sievert.jolcraft.world.worldgen.structure.JolCraftStructures;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class CompassLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    // ---------------------------------------------------------------------
    // Structure tooltips
    // ---------------------------------------------------------------------

    public static final String UNKNOWN = "unknown";

    public static final String TOOLTIP_STRUCTURE_UNKNOWN = tooltipStructure(UNKNOWN);
    public static final String TOOLTIP_STRUCTURE_DISCOVERED = tooltipStructure("discovered");

    // tooltip.<modid>.structure.<namespace:path>
    public static String tooltipStructure(ResourceLocation structureId) {
        return JolCraftLanguageKeys.tooltip("structure", structureId.toString());
    }

    public static String tooltipStructure(String structureId) {
        return JolCraftLanguageKeys.tooltip("structure", structureId);
    }

    // ---------------------------------------------------------------------
    // Deepslate Compass tooltips
    // ---------------------------------------------------------------------

    public static final String DEEPSLATE_COMPASS = "deepslate_compass";
    public static final String DEEPSLATE_COMPASS_DIAL = "deepslate_compass_dial";

    public static final String TOOLTIP_DEEPSLATE_COMPASS_TRACKING = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, DEEPSLATE_COMPASS);
    public static final String TOOLTIP_DEEPSLATE_COMPASS_NO_STRUCTURE = JolCraftLanguageKeys.tooltip(DEEPSLATE_COMPASS, "no_structure");
    public static final String TOOLTIP_DEEPSLATE_COMPASS_LOCATE = JolCraftLanguageKeys.tooltip(DEEPSLATE_COMPASS, "locate");
    public static final String TOOLTIP_DEEPSLATE_COMPASS_DIAL_UNKNOWN = JolCraftLanguageKeys.tooltip(DEEPSLATE_COMPASS_DIAL, UNKNOWN);

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        // Structure fixed strings
        p.putManual(TOOLTIP_STRUCTURE_UNKNOWN, "Unknown");
        p.putManual(TOOLTIP_STRUCTURE_DISCOVERED, "Discovered: ");

        // Deepslate Compass fixed strings
        p.putManual(TOOLTIP_DEEPSLATE_COMPASS_TRACKING, "Currently tracking: ");
        p.putManual(TOOLTIP_DEEPSLATE_COMPASS_NO_STRUCTURE, "No structures found!");
        p.putManual(TOOLTIP_DEEPSLATE_COMPASS_LOCATE, "The tracked %s is at %s (%s blocks away)");

        p.putManual(TOOLTIP_DEEPSLATE_COMPASS_DIAL_UNKNOWN, "Unknown");

        // Dial labels
        for (StructureGroup group : StructureGroup.values()) {
            String key = JolCraftLanguageKeys.tooltip("deepslate_compass_dial", group.id());
            if (p.hasKey(key)) continue;
            p.putManual(key, AbstractLanguageProvider.toTitleCase(group.id()));
        }

        // Vanilla structures
        for (ResourceLocation id : reflectStructureIds()) {
            putStructureNameIfMissing(p, id);
        }

        // JolCraft structures
        for (ResourceLocation id : reflectRegisteredStructureIds()) {
            putStructureNameIfMissing(p, id);
        }

    }

    private static void putStructureNameIfMissing(AbstractLanguageProvider p, ResourceLocation structureId) {
        String key = tooltipStructure(structureId);
        if (p.hasKey(key)) return;

        String english = AbstractLanguageProvider.toTitleCase(structureId.getPath());
        p.putManual(key, english);
    }

    private static List<ResourceLocation> reflectStructureIds() {
        List<ResourceLocation> ids = new ArrayList<>();

        for (Field f : BuiltinStructures.class.getDeclaredFields()) {
            int m = f.getModifiers();
            if (!Modifier.isPublic(m) || !Modifier.isStatic(m)) continue;
            if (f.getType() != net.minecraft.resources.ResourceKey.class) continue;

            Object val;
            try {
                val = f.get(null);
            } catch (IllegalAccessException ignored) {
                continue;
            }
            if (!(val instanceof net.minecraft.resources.ResourceKey<?> rk)) continue;

            ResourceLocation id = rk.location();
            if (!"minecraft".equals(id.getNamespace())) continue;

            ids.add(id);
        }

        return ids;
    }

    private static List<ResourceLocation> reflectRegisteredStructureIds() {
        List<ResourceLocation> ids = new ArrayList<>();

        for (Field f : JolCraftStructures.class.getDeclaredFields()) {
            int m = f.getModifiers();
            if (!Modifier.isPublic(m) || !Modifier.isStatic(m)) continue;

            if (f.getType() != JolCraftStructures.RegisteredStructure.class) continue;

            Object val;
            try {
                val = f.get(null);
            } catch (IllegalAccessException ignored) {
                continue;
            }

            if (!(val instanceof JolCraftStructures.RegisteredStructure<?> rs)) continue;

            ResourceLocation id = rs.id();
            if (!JolCraft.MOD_ID.equals(id.getNamespace())) continue;

            ids.add(id);
        }

        return ids;
    }
}