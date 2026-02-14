package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.item.potion.JolCraftPotions;
import net.neoforged.neoforge.registries.DeferredHolder;

@OnlyIn(Dist.CLIENT)
public final class PotionLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        addPotion(p, JolCraftPotions.ANCIENT_MEMORY, "Ancient Memory", true, false);
        addPotion(p, JolCraftPotions.LOCKPICKING, "Lockpicking", true, true);
        addPotion(p, JolCraftPotions.DWARVEN_HASTE, "Mining", true, true);
        addPotion(p, JolCraftPotions.CORROSION, "Corrosion", true, true);
        addPotion(p, JolCraftPotions.DELIRIUM_CURSE, "Delirium Curse", false, false);
        addPotion(p, JolCraftPotions.CURSED_WOUND, "Cursed Wound", false, false);
    }


    private void addPotion(AbstractLanguageProvider p,
                           Object potionHolder,
                           String displayName,
                           boolean hasLong,
                           boolean hasStrong) {

        String baseName = resolvePotionName(potionHolder);

        p.put("item.minecraft.potion.effect." + baseName, displayName + " Potion");
        p.put("item.minecraft.splash_potion.effect." + baseName, displayName + " Splash Potion");
        p.put("item.minecraft.lingering_potion.effect." + baseName, displayName + " Lingering Potion");
        p.put("item.minecraft.tipped_arrow.effect." + baseName, "Arrow of " + displayName);

        if (hasLong) {
            p.put("item.minecraft.potion.effect.long_" + baseName, displayName + " Potion");
            p.put("item.minecraft.splash_potion.effect.long_" + baseName, displayName + " Splash Potion");
            p.put("item.minecraft.lingering_potion.effect.long_" + baseName, displayName + " Lingering Potion");
            p.put("item.minecraft.tipped_arrow.effect.long_" + baseName, "Arrow of " + displayName);
        }

        if (hasStrong) {
            p.put("item.minecraft.potion.effect.strong_" + baseName, displayName + " Potion");
            p.put("item.minecraft.splash_potion.effect.strong_" + baseName, displayName + " Splash Potion");
            p.put("item.minecraft.lingering_potion.effect.strong_" + baseName, displayName + " Lingering Potion");
            p.put("item.minecraft.tipped_arrow.effect.strong_" + baseName, "Arrow of " + displayName);
        }
    }

    private String resolvePotionName(Object potionHolder) {

        if (potionHolder instanceof Holder.Reference<?> ref) {
            ResourceKey<?> key = ref.unwrapKey().orElse(null);
            if (key != null) return key.location().getPath();
        }

        if (potionHolder instanceof DeferredHolder<?, ?> deferred) {
            return deferred.getId().getPath();
        }

        try {
            var getMethod = potionHolder.getClass().getMethod("getEntityType");
            Object actual = getMethod.invoke(potionHolder);
            if (actual != null && actual != potionHolder) {
                return resolvePotionName(actual);
            }
        } catch (ReflectiveOperationException | SecurityException e) {
        JolCraftLogs.debug(
                JolCraftLogTags.DATAGEN,
                "PotionLangSubProvider: reflection resolve failed for type={} value={} err={}",
                potionHolder.getClass().getName(),
                potionHolder,
                e.toString()
        );
    }


        if (potionHolder instanceof String str) return str;

        throw new IllegalArgumentException("Can't resolve potion name for " + potionHolder);
    }
}