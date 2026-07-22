package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
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
import net.sievert.jolcraft.world.entity.JolCraftEntities;

@OnlyIn(Dist.CLIENT)
public final class EntityLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftDictionary.ENTITIES;
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }


    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        for (DeferredHolder<?, ?> holder : JolCraftEntities.ENTITY_TYPES.getEntries()) {
            Object value = holder.get();
            if (!(value instanceof EntityType<?>)) continue;

            ResourceLocation id = holder.getId();

            String key = AbstractLanguageKeys.entity(id.getPath());
            if (hasKey(translations, key)) continue;

            put(translations, key, JolCraftStrings.toTitleCase(id.getPath()));
        }
    }
}
