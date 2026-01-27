package net.sievert.jolcraft.datagen.language.subprovider;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.datagen.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.world.effect.JolCraftEffects;
import net.minecraft.world.effect.MobEffect;

public final class EffectsLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        for (DeferredHolder<?, ?> holder : JolCraftEffects.MOB_EFFECTS.getEntries()) {
            Object value = holder.get();
            if (!(value instanceof MobEffect effect)) continue;

            ResourceLocation id = BuiltInRegistries.MOB_EFFECT.getKey(effect);
            if (id == null) continue;

            String key = "effect." + id.getNamespace() + "." + id.getPath();
            if (p.hasKey(key)) continue;

            p.put(key, AbstractLanguageProvider.toTitleCase(id.getPath()));
        }
    }
}
