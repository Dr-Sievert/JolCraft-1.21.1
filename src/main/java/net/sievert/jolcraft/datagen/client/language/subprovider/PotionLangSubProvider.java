package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;

import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import net.sievert.jolcraft.world.item.potion.JolCraftPotions;

@OnlyIn(Dist.CLIENT)
public final class PotionLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftStrings.plural(JolCraftDictionary.POTION);
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }


    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        addPotion(translations,  JolCraftPotions.ANCIENT_MEMORY, "Ancient Memory", true, false);
        addPotion(translations,  JolCraftPotions.LOCKPICKING, "Lockpicking", true, true);
        addPotion(translations,  JolCraftPotions.DWARVEN_HASTE, "Mining", true, true);
        addPotion(translations,  JolCraftPotions.BULWARK, "Bulwark", true, true);
        addPotion(translations,  JolCraftPotions.ALCHEMIST_FOCUS, "Alchemist Focus", true, true);
        addPotion(translations,  JolCraftPotions.ANCHOR, "Anchor", true, false);
        addPotion(translations,  JolCraftPotions.DEXTERITY, "Dexterity", true, true);
        addPotion(translations,  JolCraftPotions.DWARVEN_RAGE, "Dwarven Rage", true, true);
        addPotion(translations,  JolCraftPotions.ENDURANCE, "Endurance", true, true);
        addPotion(translations,  JolCraftPotions.MAGIC_RESISTANCE, "Magic Resistance", true, true);
        addPotion(translations,  JolCraftPotions.POISON_RESISTANCE, "Poison Resistance", true, true);
        addPotion(translations,  JolCraftPotions.SLOW_RESISTANCE, "Slow Resistance", true, true);
        addPotion(translations,  JolCraftPotions.MARKSMAN, "Marksman", true, true);
        addPotion(translations,  JolCraftPotions.STONE_SKIN, "Stone Skin", true, true);
        addPotion(translations,  JolCraftPotions.HOARD, "Hoarding", true, true);
        addPotion(translations,  JolCraftPotions.PIERCING, "Piercing", true, true);
        addPotion(translations,  JolCraftPotions.TENACITY, "Tenacity", true, true);
        addPotion(translations,  JolCraftPotions.WISDOM, "Wisdom", true, true);
        addPotion(translations,  JolCraftPotions.MIGHT, "Might", true, true);
        addPotion(translations,  JolCraftPotions.HARVEST, "Harvest", true, true);
        addPotion(translations,  JolCraftPotions.LUNAR, "the Moon", true, true);

        addPotion(translations,  JolCraftPotions.ATAXIA_CURSE, "Ataxia Cursing", false, false);
        addPotion(translations,  JolCraftPotions.CURSED_WOUND, "Cursing Wound", false, false);
        addPotion(translations,  JolCraftPotions.DELIRIUM_CURSE, "Delirium Cursing", false, false);
        addPotion(translations,  JolCraftPotions.FAMINE_CURSE, "Famine Cursing", false, false);
        addPotion(translations,  JolCraftPotions.FRAILTY_CURSE, "Frailty Cursing", false, false);
        addPotion(translations,  JolCraftPotions.HEX, "Hex", false, false);
        addPotion(translations,  JolCraftPotions.VITALITY_CURSE, "Vitality Cursing", false, false);

        addPotion(translations,  JolCraftPotions.DISARMED, "Disarming", false, false);
        addPotion(translations,  JolCraftPotions.ROOTED, "Rooting", false, false);
        addPotion(translations,  JolCraftPotions.STUNNED, "Stunning", false, false);
        addPotion(translations,  JolCraftPotions.SUPPRESSED, "Suppression", false, false);

        addPotion(translations,  JolCraftPotions.CORROSION, "Corrosion", true, true);

        addPotion(translations,  JolCraftPotions.UNLUCK, "Bad Luck", false, true);
        addPotion(translations,  JolCraftPotions.STRONG_LUCK, "Luck", false, false);
    }

    private static final String[] TYPES = {
            "potion",
            "splash_potion",
            "lingering_potion",
            "tipped_arrow"
    };

    private void addPotion(
            Map<String, String> translations,
            Holder<Potion> potionHolder,
            String displayName,
            boolean hasLong,
            boolean hasStrong
    ) {
        String baseName = resolvePotionName(potionHolder);

        addVariant(translations, baseName, displayName);

        if (hasLong) {
            addVariant(translations, "long_" + baseName, displayName);
        }

        if (hasStrong) {
            addVariant(translations, "strong_" + baseName, displayName);
        }
    }

    private void addVariant(Map<String, String> translations, String name, String displayName) {
        for (String type : TYPES) {
            put(
                    translations,
                    "item.minecraft." + type + ".effect." + name,
                    formatDisplay(type, displayName)
            );
        }
    }

    private String formatDisplay(String type, String displayName) {
        return switch (type) {
            case "potion" -> "Potion of " + displayName;
            case "splash_potion" -> "Splash Potion of " + displayName;
            case "lingering_potion" -> "Lingering Potion of " + displayName;
            case "tipped_arrow" -> "Arrow of " + displayName;
            default -> displayName;
        };
    }

    private static String resolvePotionName(Holder<Potion> potionHolder) {
        ResourceKey<Potion> key = potionHolder.unwrapKey()
                .orElseThrow(() -> new IllegalArgumentException("Unbound potion holder: " + potionHolder));
        return key.location().getPath();
    }
}