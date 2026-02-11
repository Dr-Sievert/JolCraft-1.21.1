package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.data.language.AbstractLanguageKeys;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.JolCraftEntities;

@OnlyIn(Dist.CLIENT)
public final class EntityLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        for (DeferredHolder<?, ?> holder : JolCraftEntities.ENTITY_TYPES.getEntries()) {
            Object value = holder.get();
            if (!(value instanceof EntityType<?>)) continue;

            ResourceLocation id = holder.getId();

            String key = AbstractLanguageKeys.entity(id.getPath());
            if (p.hasKey(key)) continue;

            p.put(key, JolCraftStrings.toTitleCase(id.getPath()));
        }
    }
}
