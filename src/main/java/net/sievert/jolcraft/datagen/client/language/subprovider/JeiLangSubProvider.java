package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;

@OnlyIn(Dist.CLIENT)
public final class JeiLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftDictionary.JEI;
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }


    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        // JEI categories
        putManual(translations, JolCraftLanguageKeys.JEI_CATEGORY_DWARF_TRADES, "Dwarf Trades");
        putManual(translations, JolCraftLanguageKeys.JEI_CATEGORY_INFO_PAGE, "Information");
        putManual(translations, JolCraftLanguageKeys.JEI_CATEGORY_LAPIDARY_BENCH, "Lapidary Bench");
        putManual(translations, JolCraftLanguageKeys.JEI_CATEGORY_HAND_INTERACTION, "Hand Interaction");
        putManual(translations, JolCraftLanguageKeys.JEI_CATEGORY_FERMENTING_CAULDRON, "Fermenting Cauldron");
        putManual(translations, JolCraftLanguageKeys.JEI_CATEGORY_FERMENTING_BARREL, "Fermenting Barrel");
        putManual(translations, JolCraftLanguageKeys.JEI_CATEGORY_BOUNTY_TASK, "Bounty Task");
        putManual(translations, JolCraftLanguageKeys.JEI_CATEGORY_BOUNTY_REWARD, "Bounty Reward");

        //Misc support

        putManual(translations, JolCraftLanguageKeys.JEI_TOOLTIP_SPAWN, "Spawn");
        putManual(translations, JolCraftLanguageKeys.JEI_TOOLTIP_CHANCE_ROLL, "Chance per roll: %s");
        putManual(translations, JolCraftLanguageKeys.JEI_TOOLTIP_CHANCE_TOTAL, "Chance at max level: %s");

        // JEI info pages

        putManual(translations,
                JolCraftLanguageKeys.JEI_INFO_REPUTATION_TABLET,
                "To gain endorsements, give your reputation tablet to a master-level dwarf with a profession. " +
                        "Endorsements are unique per profession and can only be gained once. To advance to the next reputation level, " +
                        "you need endorsements from dwarves with professions. When you have enough, hand over your tablet to a guildmaster to update it."
        );

        putManual(translations,
                JolCraftLanguageKeys.JEI_INFO_STRONGBOX,
                "Strongboxes are chests that can generate with locks that need to be picked. Breaking a locked strongbox " +
                        "also breaks the lock but removes any ungenerated loot. Breaking a strongbox with Silk Touch retains all its contents."
        );

        putManual(translations,
                JolCraftLanguageKeys.JEI_INFO_DEEPSLATE_COMPASS,
                "An empty deepslate compass must be combined with a deepslate compass dial. " +
                        "Dials are found by exploring. A combined compass points to a structure selected from the structure group on a dial. " +
                        "The compass can be dyed multiple times to blend colors. Craft an empty compass by itself to remove its dye, or a combined compass by itself to remove its dial."
        );

        putManual(translations,
                JolCraftLanguageKeys.JEI_INFO_COIN_POUCH,
                "Stores up to 999 coins. Can be used in trades. Right-click when held to deposit all coins in inventory. " +
                        "Left-click with coins in inventory to deposit up to a stack. Right-click in inventory to withdraw up to a stack."
        );

        putManual(translations,
                JolCraftLanguageKeys.JEI_INFO_DWARVEN_LEXICON,
                "Can be used to learn dwarvish. Found in mineshafts or stronghold libraries. Can be bought from master librarians."
        );

        putManual(translations,
                JolCraftLanguageKeys.JEI_INFO_ANCIENT_DWARVEN_LEXICON,
                "Can be used to learn ancient dwarvish. Found in ancient dwarven ruins."
        );

        putManual(translations,
                JolCraftLanguageKeys.JEI_INFO_HEARTH,
                "Can be activated if placed within 10 blocks of a claimed bed. You need to sleep in a bed before activating a hearth again. " +
                        "Multiple players can be bound to a hearth. It provides the Homestead effect to bound players within range."
        );

        putManual(translations,
                JolCraftLanguageKeys.JEI_INFO_VERDANT,
                "Crops ignore their normal growing conditions and grow faster on these blocks."
        );

        putManual(translations,
                JolCraftLanguageKeys.JEI_INFO_FESTERLING,
                "Can be cultivated with rotten flesh and planted on top of log ends."
        );

        putManual(translations,
                JolCraftLanguageKeys.TOOLTIP_HOPS_SEEDS,
                "Needs two blocks of vertical space and a light level of 8 or less to grow.");

        putManual(translations,
                JolCraftLanguageKeys.TOOLTIP_DEEPSLATE_BULBS,
                "Needs a light level of 8 or less and a Y-level of 0 or below to grow. Can only be planted on deepslate, tuff, or verdant soil."
        );

        putManual(translations,
                JolCraftLanguageKeys.TOOLTIP_DEEPSLATE_MITHRIL_ORE,
                "Mithril generates only in deepslate between Y -64 and 0, " +
                "with a triangular distribution concentrated toward the middle of that range. Small, medium, and large veins can appear throughout the Overworld, " +
                "while additional exposed and fully buried veins are restricted to mountain and hill biomes.");
    }
}