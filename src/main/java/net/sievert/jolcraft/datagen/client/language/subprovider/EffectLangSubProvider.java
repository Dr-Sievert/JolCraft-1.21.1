package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.effect.JolCraftEffects;
import net.minecraft.world.effect.MobEffect;

@OnlyIn(Dist.CLIENT)
public final class EffectLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftStrings.plural(JolCraftDictionary.EFFECT);
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }


    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        for (DeferredHolder<?, ?> holder : JolCraftEffects.MOB_EFFECTS.getEntries()) {
            Object value = holder.get();
            if (!(value instanceof MobEffect)) continue;
            ResourceLocation id = holder.getId();
            String key = AbstractLanguageKeys.effect(id.getPath());
            if (hasKey(translations, key)) continue;

            put(translations, key, JolCraftStrings.toTitleCase(id.getPath()));
        }
    }
}
