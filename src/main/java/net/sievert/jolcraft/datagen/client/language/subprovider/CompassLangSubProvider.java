package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassStructureGroup;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.worldgen.structure.JolCraftStructures;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public final class CompassLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftStrings.plural(JolCraftDictionary.COMPASS);
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }


    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        // Structure fixed strings
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_STRUCTURE_DISCOVERED, "Discovered: %s");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_STRUCTURE_ALREADY_DISCOVERED, "You already discovered this structure!");

        // Deepslate Compass fixed strings
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DEEPSLATE_COMPASS_TRACKING, "Currently tracking: ");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DEEPSLATE_COMPASS_NO_STRUCTURE, "No structures found!");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DEEPSLATE_COMPASS_LOCATE, "The tracked %s is at %s (%s blocks away)");

        // Dial labels
        for (DeepslateCompassStructureGroup group : DeepslateCompassStructureGroup.values()) {
            String key = JolCraftLanguageKeys.tooltip(
                    JolCraftItemIds.DEEPSLATE_COMPASS_DIAL,
                    group.getId()
            );

            if (hasKey(translations, key)) continue;

            putManual(translations, key, JolCraftStrings.toTitleCase(group.getId()));
        }

        // Vanilla structures
        for (ResourceLocation id : reflectStructureIds()) {
            putStructureNameIfMissing(translations,  id);
        }

        // JolCraft structures
        for (ResourceLocation id : reflectRegisteredStructureIds()) {
            putStructureNameIfMissing(translations,  id);
        }
    }

    private void putStructureNameIfMissing(Map<String, String> translations, ResourceLocation structureId) {
        String key = JolCraftLanguageKeys.tooltip(JolCraftDictionary.STRUCTURE, structureId.toString());
        if (hasKey(translations, key)) return;

        String english = JolCraftStrings.toTitleCase(structureId.getPath());
        putManual(translations, key, english);
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
            if (!ResourceLocation.DEFAULT_NAMESPACE.equals(id.getNamespace())) continue;

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