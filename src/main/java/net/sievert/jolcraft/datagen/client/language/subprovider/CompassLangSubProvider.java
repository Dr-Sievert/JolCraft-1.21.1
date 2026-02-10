package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftKeyParts;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.util.compass.StructureGroup;
import net.sievert.jolcraft.world.worldgen.structure.JolCraftStructures;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class CompassLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        // Structure fixed strings
        p.putManual(JolCraftLanguageKeys.TOOLTIP_STRUCTURE_UNKNOWN, "Unknown");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_STRUCTURE_DISCOVERED, "Discovered: ");

        // Deepslate Compass fixed strings
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DEEPSLATE_COMPASS_TRACKING, "Currently tracking: ");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DEEPSLATE_COMPASS_NO_STRUCTURE, "No structures found!");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DEEPSLATE_COMPASS_LOCATE, "The tracked %s is at %s (%s blocks away)");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DEEPSLATE_COMPASS_DIAL_UNKNOWN, "Unknown");

        // Dial labels
        for (StructureGroup group : StructureGroup.values()) {
            String key = JolCraftLanguageKeys.tooltip(JolCraftKeyParts.DEEPSLATE_COMPASS_DIAL, group.id());
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
        String key = JolCraftLanguageKeys.tooltip("structure", structureId.toString());
        if (p.hasKey(key)) return;

        String english = AbstractLanguageProvider.toTitleCase(structureId.getPath());
        p.putManual(key, english);
    }

    private static List<ResourceLocation> reflectStructureIds() {
        List<ResourceLocation> ids = new ArrayList<>();

        for (Field f : BuiltinStructures.class.getDeclaredFields()) {
            int m = f.getModifiers();
            if (!Modifier.isPublic(m) || !Modifier.isStatic(m)) continue;
            if (f.getType() != ResourceKey.class) continue;

            Object val;
            try {
                val = f.get(null);
            } catch (IllegalAccessException ignored) {
                continue;
            }
            if (!(val instanceof ResourceKey<?> rk)) continue;

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