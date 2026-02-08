package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;

@OnlyIn(Dist.CLIENT)
public final class DwarfLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        // -----------------------------------------------------------------
        // Language gating / effects
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.TOOLTIP_NEED_LANG, "You need to understand dwarvish to use this.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_NEED_ANCIENT, "You need to understand ancient dwarvish to use this.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_ANCIENT_MEMORY, "Ancient memory effect gives you temporary understanding of ancient dwarvish.");

        // -----------------------------------------------------------------
        // Dwarven Lexicon
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARVEN_LEXICON_LOCKED, "The pages are filled with unfamiliar symbols.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARVEN_LEXICON_UNLOCKED, "The key to dwarven speech lies within.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARVEN_LEXICON_USE, "You have learned to understand the dwarven language!");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARVEN_LEXICON_KNOWS, "You already understand the dwarven language.");

        // -----------------------------------------------------------------
        // Ancient Dwarven Lexicon
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_LOCKED, "The pages are filled with unfamiliar symbols.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_UNLOCKED, "What was once silent may now speak again.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_USE, "You have learned to understand the ancient dwarven language!");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANT_READ, "You have no idea how to decipher this.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANT_USE, "The text is clearly dwarvish, but you cannot decipher its secrets.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_KNOWS, "You already understand the ancient dwarven language.");

        // -----------------------------------------------------------------
        // Tomes / Identification
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.TOOLTIP_UNIDENTIFIED, "Right-click to identify.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_UNIDENTIFIED_DWARVEN_TOME, "An unidentified dwarven tome.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_SHIFT, "Can be sold to Dwarven Historians.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_TOME_UNIDENTIFIED, "An unidentified dwarven tome, written in ancient dwarvish.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_TOME_PARTIAL_UNDERSTANDING, "You recognize the language as Dwarvish, but cannot understand it.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_LEGENDARY_ANCIENT_DWARVEN_TOME_SHIFT, "Can be used to gain permanent knowledge.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_IDENTIFY_SUCCESS, "You identify the contents of the tome.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_IDENTIFY_FAIL, "You cannot make sense of the dwarven runes.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_LOCKED, "The pages are filled with unfamiliar symbols.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_UNLOCKED, "A dwarven tome.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_TOME_UNLOCKED, "An ancient dwarven tome.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_LEGENDARY_PAGE, "Salvaged from ancient tomes by historians. Can be used to restore ancient legendary tomes by certain dwarven professions.");

        // -----------------------------------------------------------------
        // Tome unlock messages
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.TOOLTIP_TOME_UNLOCK_EMPTY, "This tome lacks knowledge that you find useful.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_TOME_UNLOCK_BREW, "You can now brew with multiple ingredients!");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_TOME_UNLOCK_GEMS, "You can now cut gems using a chisel!");

        // -----------------------------------------------------------------
        // Locked item variants
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.TOOLTIP_PAPER_LOCKED, "The paper is marked with unfamiliar symbols.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_PARCHMENT_LOCKED, "The parchment is marked with unfamiliar symbols.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_STONE_LOCKED, "The stone is marked with unfamiliar symbols.");

        // -----------------------------------------------------------------
        // Contract keys
        // -----------------------------------------------------------------

        p.putManual(
                JolCraftLanguageKeys.TOOLTIP_WRITTEN_CONTRACT,
                "Given to dwarves without professions to get signed contracts. " +
                        "Signed contracts are used to buy profession contracts from a guildmaster. " +
                        "If given to a dwarf with a profession they will create a contract for their profession."
        );

        p.putManual(JolCraftLanguageKeys.TOOLTIP_SIGNED_CONTRACT, "Signed contracts are used to buy profession contracts from a guildmaster.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_PROFESSION_CONTRACT, "Profession contracts can be given to dwarves without professions to set their profession.");

        p.putManual(JolCraftLanguageKeys.TOOLTIP_GUILD_SIGIL, "Can be bought from a master dwarf without a profession.");

        // -----------------------------------------------------------------
        // Dwarf
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARF_LOCKED, "You do not understand each other.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARF_BUSY, "This dwarf is busy.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARF_NOT_PAID, "You have not paid this dwarf yet.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARF_CANNOT_PROMOTE, "This dwarf cannot be promoted.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_DWARF_CANNOT_SIGN, "This dwarf cannot sign contracts.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_GUARD_PROMOTION, "Guard promoted to %s!");
    }
}