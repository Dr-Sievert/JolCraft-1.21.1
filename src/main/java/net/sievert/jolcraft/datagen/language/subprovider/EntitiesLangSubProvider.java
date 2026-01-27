package net.sievert.jolcraft.datagen.language.subprovider;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.datagen.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.datagen.language.util.JolCraftLanguageCategory;
import net.sievert.jolcraft.datagen.language.util.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.entity.JolCraftEntities;

public final class EntitiesLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        for (DeferredHolder<?, ?> holder : JolCraftEntities.ENTITY_TYPES.getEntries()) {
            Object value = holder.get();
            if (!(value instanceof EntityType<?> type)) continue;

            ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(type);

            String key = JolCraftLanguageKeys.category(JolCraftLanguageCategory.ENTITY, id.getPath());
            if (p.hasKey(key)) continue;

            p.put(key, AbstractLanguageProvider.toTitleCase(id.getPath()));
        }
    }
}
