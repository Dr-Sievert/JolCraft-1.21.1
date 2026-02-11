package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.data.language.AbstractLanguageKeys;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.effect.JolCraftEffects;
import net.minecraft.world.effect.MobEffect;

@OnlyIn(Dist.CLIENT)
public final class EffectLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        for (DeferredHolder<?, ?> holder : JolCraftEffects.MOB_EFFECTS.getEntries()) {
            Object value = holder.get();
            if (!(value instanceof MobEffect)) continue;
            ResourceLocation id = holder.getId();
            String key = AbstractLanguageKeys.item(id.getPath());
            if (p.hasKey(key)) continue;

            p.put(key, JolCraftStrings.toTitleCase(id.getPath()));
        }
    }
}
