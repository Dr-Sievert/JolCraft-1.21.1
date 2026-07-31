package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftTrimIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import net.sievert.jolcraft.world.item.material.trim.JolCraftTrimMaterials;
import net.sievert.jolcraft.world.item.material.trim.JolCraftTrimPatterns;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class TrimLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftStrings.plural(JolCraftDictionary.TRIM);
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }

    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        for (JolCraftMaterials.Material material : JolCraftMaterials.Material.values()) {
            addTrimMaterial(translations, material.trimKey().location());
        }

        for (JolCraftTrimMaterials.Attribute attribute : JolCraftTrimMaterials.Attribute.values()) {
            addTrimMaterial(translations, attribute.key().location());
        }

        for (JolCraftTrimPatterns.Entry entry : JolCraftTrimPatterns.entries()) {
            addTrimPattern(translations, entry.key().location());
        }
    }

    // -------------------------------------------------------------------------
    // Trim materials
    // -------------------------------------------------------------------------

    private void addTrimMaterial(@NotNull Map<String, String> translations, @NotNull ResourceLocation id) {
        if (!JolCraft.MOD_ID.equals(id.getNamespace())) return;

        String langKey = trimMaterial(id.getPath());
        if (hasKey(translations, langKey)) return;

        putManual(translations, langKey, JolCraftStrings.toTitleCase(JolCraftStrings.spaced(id.getPath(), JolCraftDictionary.MATERIAL)));
    }

    // -------------------------------------------------------------------------
    // Trim patterns
    // -------------------------------------------------------------------------

    private void addTrimPattern(@NotNull Map<String, String> translations, @NotNull ResourceLocation id) {
        if (!JolCraft.MOD_ID.equals(id.getNamespace())) return;

        String langKey = trimPattern(id.getPath());
        if (hasKey(translations, langKey)) return;

        putManual(translations, langKey, JolCraftStrings.toTitleCase(JolCraftStrings.spaced(id.getPath(), JolCraftDictionary.ARMOR, JolCraftDictionary.TRIM)));
    }

    // -------------------------------------------------------------------------

    public static String trimMaterial(String path) {
        return AbstractLanguageKeys.category(JolCraftTrimIds.TRIM_MATERIAL, path);
    }

    public static String trimPattern(String path) {
        return AbstractLanguageKeys.category(JolCraftTrimIds.TRIM_PATTERN, path);
    }
}