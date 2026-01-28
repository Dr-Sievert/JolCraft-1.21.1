package net.sievert.jolcraft.datagen.language.subprovider;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.world.item.trim.JolCraftTrimMaterials;
import net.sievert.jolcraft.world.item.trim.JolCraftTrimPatterns;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public final class TrimsLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {
        addTrimMaterials(p);
        addTrimPatterns(p);
    }

    private static void addTrimMaterials(AbstractLanguageProvider p) {
        for (ResourceKey<TrimMaterial> key : reflectResourceKeys(JolCraftTrimMaterials.class, TrimMaterial.class)) {
            ResourceLocation id = key.location();
            if (!JolCraft.MOD_ID.equals(id.getNamespace())) continue;

            String langKey = trimMaterial(id.getPath());
            if (p.hasKey(langKey)) continue;

            p.putManual(langKey, AbstractLanguageProvider.toTitleCase(id.getPath()));
        }
    }

    private static void addTrimPatterns(AbstractLanguageProvider p) {
        for (ResourceKey<TrimPattern> key : reflectResourceKeys(JolCraftTrimPatterns.class, TrimPattern.class)) {
            ResourceLocation id = key.location();
            if (!JolCraft.MOD_ID.equals(id.getNamespace())) continue;

            String langKey = trimPattern(id.getPath());
            if (p.hasKey(langKey)) continue;

            p.putManual(langKey, AbstractLanguageProvider.toTitleCase(id.getPath()) + " Armor Trim");
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> ResourceKey<T>[] reflectResourceKeys(Class<?> owner, Class<T> registryType) {
        return (ResourceKey<T>[]) java.util.Arrays.stream(owner.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> Modifier.isPublic(f.getModifiers()))
                .filter(f -> f.getType() == ResourceKey.class)
                .map(f -> (ResourceKey<?>) getStaticFieldValue(f))
                .filter(java.util.Objects::nonNull)
                .toArray(ResourceKey[]::new);
    }

    private static Object getStaticFieldValue(Field f) {
        try {
            return f.get(null);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    public static String trimMaterial(String path) {
        return "trim_material." + JolCraft.MOD_ID + "." + path;
    }
    public static String trimPattern(String path) {
        return "trim_pattern." + JolCraft.MOD_ID + "." + path;
    }
}