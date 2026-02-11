package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.key.JolCraftDictionary;
import net.sievert.jolcraft.data.language.AbstractLanguageKeys;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import net.sievert.jolcraft.world.item.material.trim.JolCraftTrimMaterials;
import net.sievert.jolcraft.world.item.trim.JolCraftTrimPatterns;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Objects;

@OnlyIn(Dist.CLIENT)
public final class TrimLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {
        p.putManual(JolCraftLanguageKeys.TOOLTIP_TRIM_MATERIALS, "Can be used to trim armor.");
        p.putManual(
                JolCraftLanguageKeys.TOOLTIP_ATTRIBUTE_TRIM_MATERIALS,
                "Can be used to trim armor for bonus stats. Applying additional cosmetic trims does not override given stats."
        );

        addTrimMaterials(p);
        addTrimPatterns(p);
    }

    // -------------------------------------------------------------------------
    // Trim materials (single source of truth)
    // -------------------------------------------------------------------------

    private static void addTrimMaterials(AbstractLanguageProvider p) {

        for (JolCraftMaterials.Material material : JolCraftMaterials.Material.values()) {
            addTrimMaterial(p, material.trimKey().location());
        }

        for (JolCraftTrimMaterials.Attribute attribute : JolCraftTrimMaterials.Attribute.values()) {
            addTrimMaterial(p, attribute.key().location());
        }
    }

    private static void addTrimMaterial(AbstractLanguageProvider p, ResourceLocation id) {
        if (!JolCraft.MOD_ID.equals(id.getNamespace())) return;

        String langKey = trimMaterial(id.getPath());
        if (p.hasKey(langKey)) return;

        p.putManual(langKey, JolCraftStrings.toTitleCase(id.getPath()) + " Material");
    }

    // -------------------------------------------------------------------------
    // Trim patterns
    // -------------------------------------------------------------------------

    private static void addTrimPatterns(AbstractLanguageProvider p) {
        for (ResourceKey<TrimPattern> key : reflectTrimPatternKeys()) {
            ResourceLocation id = key.location();
            if (!JolCraft.MOD_ID.equals(id.getNamespace())) continue;

            String langKey = trimPattern(id.getPath());
            if (p.hasKey(langKey)) continue;

            p.putManual(langKey, JolCraftStrings.toTitleCase(id.getPath()) + " Armor Trim");
        }
    }

    @SuppressWarnings("unchecked")
    private static ResourceKey<TrimPattern>[] reflectTrimPatternKeys() {
        ResourceLocation expectedRegistryLoc = Registries.TRIM_PATTERN.location();

        return (ResourceKey<TrimPattern>[]) Arrays.stream(JolCraftTrimPatterns.class.getDeclaredFields())
                .filter(f -> Modifier.isStatic(f.getModifiers()))
                .filter(f -> Modifier.isPublic(f.getModifiers()))
                .filter(f -> f.getType() == ResourceKey.class)
                .map(f -> (ResourceKey<?>) getStaticFieldValue(f))
                .filter(Objects::nonNull)
                .filter(k -> k.registry().equals(expectedRegistryLoc))
                .toArray(ResourceKey[]::new);
    }

    private static Object getStaticFieldValue(Field f) {
        try {
            return f.get(null);
        } catch (IllegalAccessException ignored) {
            return null;
        }
    }

    // -------------------------------------------------------------------------

    public static String trimMaterial(String path) {
        return AbstractLanguageKeys.category(JolCraftDictionary.TRIM_MATERIAL, path);
    }

    public static String trimPattern(String path) {
        return AbstractLanguageKeys.category(JolCraftDictionary.TRIM_PATTERN, path);
    }
}