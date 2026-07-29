package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.id.directory.JolCraftDirectoryIds;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class JadeLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftDictionary.JADE;
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }

    @Override
    public void addTranslations(
            @NotNull Map<String, String> translations
    ) {
        putManual(
                translations,
                JolCraftLanguageKeys.JADE_CONFIG_FERMENTING_BARREL,
                JolCraftStrings.toTitleCase(JolCraftBlockIds.FERMENTING_BARREL)
        );

        putManual(
                translations,
                JolCraftLanguageKeys.JADE_CONFIG_FERMENTING_CAULDRON,
                JolCraftStrings.toTitleCase(JolCraftBlockIds.FERMENTING_CAULDRON)
        );

        putManual(
                translations,
                JolCraftLanguageKeys.JADE_CONFIG_DWARF_PROFESSION,
                JolCraftStrings.toTitleCase(JolCraftStrings.underscored(JolCraftDwarfIds.DWARF, JolCraftDirectoryIds.PROFESSION))
        );
    }
}