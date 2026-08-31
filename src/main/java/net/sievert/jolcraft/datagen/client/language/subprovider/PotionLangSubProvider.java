package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.potion.JolCraftPotions;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class PotionLangSubProvider implements LanguageSubProvider {

    private static final String[] TYPES = {
            "potion",
            "splash_potion",
            "lingering_potion",
            "tipped_arrow"
    };

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
        addBasePotion(translations, JolCraftPotions.INFUSED, "Infused");
        addBasePotion(translations, JolCraftPotions.REFINED, "Refined");
        addBasePotion(translations, JolCraftPotions.EXALTED, "Exalted");

        addPotion(translations, JolCraftPotions.ANCIENT_MEMORY, "Ancient Memory");
        addPotion(translations, JolCraftPotions.LOCKPICKING, "Lockpicking");
        addPotion(translations, JolCraftPotions.DWARVEN_HASTE, "Mining");
        addPotion(translations, JolCraftPotions.BULWARK, "Bulwark");
        addPotion(translations, JolCraftPotions.ALCHEMIST_FOCUS, "Alchemist Focus");
        addPotion(translations, JolCraftPotions.ANCHOR, "Anchoring");
        addPotion(translations, JolCraftPotions.DEXTERITY, "Dexterity");
        addPotion(translations, JolCraftPotions.DWARVEN_RAGE, "Dwarven Rage");
        addPotion(translations, JolCraftPotions.ENDURANCE, "Enduring");
        addPotion(translations, JolCraftPotions.EXPLOSION_RESISTANCE, "Explosion Resistance");
        addPotion(translations, JolCraftPotions.EXPLOSION_VULNERABILITY, "Explosion Vulnerability");
        addPotion(translations, JolCraftPotions.STRONG_FIRE_RESISTANCE, "Fire Resistance");
        addPotion(translations, JolCraftPotions.FIRE_VULNERABILITY, "Fire Vulnerability");
        addPotion(translations, JolCraftPotions.FROST_RESISTANCE, "Frost Resistance");
        addPotion(translations, JolCraftPotions.FROST_VULNERABILITY, "Frost Vulnerability");
        addPotion(translations, JolCraftPotions.MAGIC_RESISTANCE, "Magic Resistance");
        addPotion(translations, JolCraftPotions.MAGIC_VULNERABILITY, "Magic Vulnerability");
        addPotion(translations, JolCraftPotions.POISON_RESISTANCE, "Poison Resistance");
        addPotion(translations, JolCraftPotions.POISON_VULNERABILITY, "Poison Vulnerability");
        addPotion(translations, JolCraftPotions.SLOW_RESISTANCE, "Slow Resistance");
        addPotion(translations, JolCraftPotions.SLOW_VULNERABILITY, "Slow Vulnerability");
        addPotion(translations, JolCraftPotions.WITHER_RESISTANCE, "Wither Resistance");
        addPotion(translations, JolCraftPotions.WITHER_VULNERABILITY, "Wither Vulnerability");
        addPotion(translations, JolCraftPotions.MARKSMAN, "Marksman");
        addPotion(translations, JolCraftPotions.STONE_SKIN, "Stone Skin");
        addPotion(translations, JolCraftPotions.HOARD, "Hoarding");
        addPotion(translations, JolCraftPotions.PIERCING, "Piercing");
        addPotion(translations, JolCraftPotions.TENACITY, "Tenacity");
        addPotion(translations, JolCraftPotions.WISDOM, "Wisdom");
        addPotion(translations, JolCraftPotions.MIGHT, "Might");
        addPotion(translations, JolCraftPotions.HARVEST, "Harvesting");
        addPotion(translations, JolCraftPotions.LUNAR, "the Moon");
        addPotion(translations, JolCraftPotions.CONFLAGRATION, "Conflagration");
        addPotion(translations, JolCraftPotions.SUNFIRE, "Sunfire");
        addPotion(translations, JolCraftPotions.LUMINANCE, "Luminance");
        addPotion(translations, JolCraftPotions.OVERHEAL, "Overhealing");
        addPotion(translations, JolCraftPotions.REJUVENATION, "Rejuvenation");

        addPotion(translations, JolCraftPotions.ATAXIA_CURSE, "Ataxia Cursing");
        addPotion(translations, JolCraftPotions.CURSED_WOUND, "Cursing Wound");
        addPotion(translations, JolCraftPotions.DELIRIUM_CURSE, "Delirium Cursing");
        addPotion(translations, JolCraftPotions.FAMINE_CURSE, "Famine Cursing");
        addPotion(translations, JolCraftPotions.FRAILTY_CURSE, "Frailty Cursing");
        addPotion(translations, JolCraftPotions.HEX, "Hexing");
        addPotion(translations, JolCraftPotions.VITALITY_CURSE, "Vitality Cursing");

        addPotion(translations, JolCraftPotions.DISARMED, "Disarming");
        addPotion(translations, JolCraftPotions.ROOTED, "Rooting");
        addPotion(translations, JolCraftPotions.STUNNED, "Stunning");
        addPotion(translations, JolCraftPotions.SUPPRESSED, "Suppressing");

        addPotion(translations, JolCraftPotions.CORROSION, "Corroding");

        addPotion(translations, JolCraftPotions.UNLUCK, "Bad Luck");
        addPotion(translations, JolCraftPotions.STRONG_LUCK, "Luck");
    }

    private void addBasePotion(
            Map<String, String> translations,
            Holder<Potion> potionHolder,
            String displayName
    ) {
        addVariant(
                translations,
                resolvePotionName(potionHolder),
                displayName,
                true
        );
    }

    private void addPotion(
            Map<String, String> translations,
            Holder<Potion> potionHolder,
            String displayName
    ) {
        JolCraftPotions.PotionFamily family =
                JolCraftPotions.familyOf(potionHolder);

        addVariant(
                translations,
                resolvePotionName(family.base()),
                displayName,
                false
        );

        if (family.longPotion() != null) {
            addVariant(
                    translations,
                    resolvePotionName(family.longPotion()),
                    displayName,
                    false
            );
        }

        if (family.strongPotion() != null) {
            addVariant(
                    translations,
                    resolvePotionName(family.strongPotion()),
                    displayName,
                    false
            );
        }
    }

    private void addVariant(
            Map<String, String> translations,
            String name,
            String displayName,
            boolean basePotion
    ) {
        for (String type : TYPES) {
            put(
                    translations,
                    "item.minecraft." + type + ".effect." + name,
                    formatDisplay(
                            type,
                            displayName,
                            basePotion
                    )
            );
        }
    }

    private String formatDisplay(
            String type,
            String displayName,
            boolean basePotion
    ) {
        if (basePotion) {
            return switch (type) {
                case "potion" -> displayName + " Potion";
                case "splash_potion" -> "Splash " + displayName + " Potion";
                case "lingering_potion" -> "Lingering " + displayName + " Potion";
                case "tipped_arrow" -> displayName + " Tipped Arrow";
                default -> displayName;
            };
        }

        return switch (type) {
            case "potion" -> "Potion of " + displayName;
            case "splash_potion" -> "Splash Potion of " + displayName;
            case "lingering_potion" -> "Lingering Potion of " + displayName;
            case "tipped_arrow" -> "Arrow of " + displayName;
            default -> displayName;
        };
    }

    private static String resolvePotionName(
            Holder<Potion> potionHolder
    ) {
        ResourceKey<Potion> key =
                potionHolder.unwrapKey()
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Unbound potion holder: " + potionHolder
                                )
                        );

        return key.location().getPath();
    }
}