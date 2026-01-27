package net.sievert.jolcraft.datagen.language.subprovider;

import net.sievert.jolcraft.datagen.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.datagen.language.util.JolCraftLanguageKeys;

public final class JeiLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        // JEI categories
        p.putManual(
                JolCraftLanguageKeys.jei("dwarf_trades"),
                "Dwarf Trades"
        );
        p.putManual(
                JolCraftLanguageKeys.jei("info_page"),
                "Information"
        );

        // JEI info pages
        p.putManual(
                JolCraftLanguageKeys.jei("info_page.reputation_tablet"),
                "To gain endorsements, give your reputation tablet to a master-level dwarf with a profession. " +
                        "Endorsements are unique per profession and can only be gained once. To advance in reputation level " +
                        "you need endorsements from dwarves with professions. When you have enough, hand over your tablet to a guildmaster to update it."
        );

        p.putManual(
                JolCraftLanguageKeys.jei("info_page.strongbox"),
                "Strongboxes are chests that can generate with locks that needs to be picked. Breaking a locked strongbox " +
                        "also breaks the lock but removes any ungenerated loot. Breaking a strongbox with silk touch retains all of its contents."
        );

        p.putManual(
                JolCraftLanguageKeys.jei("info_page.deepslate_compass"),
                "Empty deepslate compass needs to be combined with a deepslate compass dial. Hold the empty compass and the dial one in each hand then right-click to combine them. " +
                        "Dials are sold by explorers or found as loot. Combined compass points to a structure from a pool based on the dial in the compass. " +
                        "Compass can be dyed multiple times to blend color. Shapeless craft empty compass to remove dye and combined compass to remove the dial."
        );

        p.putManual(
                JolCraftLanguageKeys.jei("info_page.coin_pouch"),
                "Stores up to 999 coins. Can be used in trades. Right-click when held to deposit all coins in inventory. " +
                        "Left-click with coins in inventory to deposit up to a stack. Right-click in inventory to withdraw up to a stack."
        );

        p.putManual(
                JolCraftLanguageKeys.jei("info_page.dwarven_lexicon"),
                "Can be used to learn dwarvish. Found in mineshafts or stronghold libraries. Can be bought from master librarians."
        );

        p.putManual(
                JolCraftLanguageKeys.jei("info_page.ancient_dwarven_lexicon"),
                "Can be used to learn ancient dwarvish. Found in ancient dwarven ruins."
        );

        p.putManual(
                JolCraftLanguageKeys.jei("info_page.hearth"),
                "Can be activated if placed within 10 blocks of a claimed bed. Need to sleep in a bed to activate a hearth again. " +
                        "Multiple players can be bound to a hearth. Provides Homestead effect to bound players within range."
        );

        p.putManual(
                JolCraftLanguageKeys.jei("info_page.verdant"),
                "JolCraft crops ignores growing conditions and also grow faster on these. Other crops can be planted but no extra effects."
        );

        p.putManual(
                JolCraftLanguageKeys.jei("info_page.mushroom"),
                "Planted and spreads like vanilla mushrooms. No giant variant."
        );

        p.putManual(
                JolCraftLanguageKeys.jei("info_page.festerling"),
                "Can be cultivated with rotten flesh. Planted on top of log ends. Spreads like vanilla mushrooms. No giant variant."
        );
    }
}