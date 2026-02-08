package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.data.JolCraftAttributes;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;

@OnlyIn(Dist.CLIENT)
public final class AttributeLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        p.putManual(JolCraftAttributes.XP_BOOST, "Experience Boost");
        p.putManual(JolCraftAttributes.EXTRA_CROP, "Extra Crop Harvest");
        p.putManual(JolCraftAttributes.MOVEMENT_SPEED_BOOST_DAY, "Sunlight Speed Boost");
        p.putManual(JolCraftAttributes.MOVEMENT_SPEED_BOOST_NIGHT, "Moonlight Speed Boost");

        for (DeferredHolder<?, ?> holder : JolCraftAttributes.ATTRIBUTES.getEntries()) {
            Object value = holder.get();
            if (!(value instanceof Attribute attr)) continue;

            ResourceLocation id = BuiltInRegistries.ATTRIBUTE.getKey(attr);
            if (id == null) continue;

            String key = "attribute." + id.getNamespace() + "." + id.getPath();
            if (p.hasKey(key)) continue;

            p.put(key, AbstractLanguageProvider.toTitleCase(id.getPath()));
        }
    }
}
