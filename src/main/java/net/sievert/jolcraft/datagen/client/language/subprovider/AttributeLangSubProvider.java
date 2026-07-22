package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import net.sievert.jolcraft.util.JolCraftStrings;

@OnlyIn(Dist.CLIENT)
public final class AttributeLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftStrings.plural(JolCraftDictionary.ATTRIBUTE);
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }

    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        for (DeferredHolder<?, ?> holder : JolCraftAttributes.ATTRIBUTES.getEntries()) {
            Object value = holder.get();
            if (!(value instanceof Attribute)) continue;

            ResourceLocation id = holder.getId();
            String key = AbstractLanguageKeys.attribute(id.getPath());
            if (hasKey(translations, key)) continue;

            put(translations, key, JolCraftStrings.toTitleCase(id.getPath()));
        }
    }
}
