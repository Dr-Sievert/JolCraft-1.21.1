package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;

@OnlyIn(Dist.CLIENT)
public final class SubtitleLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        // ------------------------------------------------------------------
        // Dwarf
        // ------------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.SUBTITLE_DWARF_AMBIENT, "Dwarf mumbles");
        p.putManual(JolCraftLanguageKeys.SUBTITLE_DWARF_HIT, "Dwarf hurts");
        p.putManual(JolCraftLanguageKeys.SUBTITLE_DWARF_DEATH, "Dwarf dies");
        p.putManual(JolCraftLanguageKeys.SUBTITLE_DWARF_YES, "Dwarf agrees");
        p.putManual(JolCraftLanguageKeys.SUBTITLE_DWARF_NO, "Dwarf disagrees");
        p.putManual(JolCraftLanguageKeys.SUBTITLE_DWARF_TRADE, "Dwarf haggles");

        // ------------------------------------------------------------------
        // Misc
        // ------------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.SUBTITLE_LEVEL_UP, "Celebration");
        p.putManual(JolCraftLanguageKeys.SUBTITLE_ARMOR_EQUIP_DEEPSLATE, "Deepslate armor rumbles");
        p.putManual(JolCraftLanguageKeys.SUBTITLE_GEM_CUT, "Gem cut");
        p.putManual(JolCraftLanguageKeys.SUBTITLE_CURSE, "Curse");

        // ------------------------------------------------------------------
        // Strongbox
        // ------------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.SUBTITLE_STRONGBOX_OPEN, "Strongbox opens");
        p.putManual(JolCraftLanguageKeys.SUBTITLE_STRONGBOX_CLOSE, "Strongbox closes");
        p.putManual(JolCraftLanguageKeys.SUBTITLE_STRONGBOX_LOCKPICK, "Lock being picked");
        p.putManual(JolCraftLanguageKeys.SUBTITLE_STRONGBOX_LOCKPICK_BREAK, "Lockpick breaks");
        p.putManual(JolCraftLanguageKeys.SUBTITLE_STRONGBOX_UNLOCK, "Strongbox unlocked");

        // ------------------------------------------------------------------
        // Coins
        // ------------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.SUBTITLE_COIN_STACK, "Coins clink");
        p.putManual(JolCraftLanguageKeys.SUBTITLE_COIN_SINGLE, "Coin clinks");
    }
}