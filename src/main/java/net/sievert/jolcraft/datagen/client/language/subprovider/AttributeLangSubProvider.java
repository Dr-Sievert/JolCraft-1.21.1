package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.data.JolCraftAttributes;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.util.JolCraftStrings;

@OnlyIn(Dist.CLIENT)
public final class AttributeLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        p.putManual(JolCraftAttributes.XP_INCREASE, "Experience Boost");
        p.putManual(JolCraftAttributes.MOVEMENT_SPEED_DAY_INCREASE, "Sunlight Speed Boost");
        p.putManual(JolCraftAttributes.MOVEMENT_SPEED_NIGHT_INCREASE, "Moonlight Speed Boost");

        for (DeferredHolder<?, ?> holder : JolCraftAttributes.ATTRIBUTES.getEntries()) {
            Object value = holder.get();
            if (!(value instanceof Attribute)) continue;

            ResourceLocation id = holder.getId();
            String key = AbstractLanguageKeys.attribute(id.getPath());
            if (p.hasKey(key)) continue;

            p.put(key, JolCraftStrings.toTitleCase(id.getPath()));
        }
    }
}
