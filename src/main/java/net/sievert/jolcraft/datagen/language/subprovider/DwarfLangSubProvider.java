package net.sievert.jolcraft.datagen.language.subprovider;

import net.sievert.jolcraft.datagen.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.datagen.language.util.JolCraftLanguageCategory;
import net.sievert.jolcraft.datagen.language.util.JolCraftLanguageKeys;

public final class DwarfLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    // ---------------------------------------------------------------------
    // Shared ids
    // ---------------------------------------------------------------------

    public static final String LOCKED = "locked";
    public static final String UNLOCKED = "unlocked";
    public static final String USE = "use";
    public static final String KNOWS = "knows";

    // ---------------------------------------------------------------------
    // Root tooltip keys (tooltip.<modid>.<path>)
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_NEED_LANG = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "need_lang");

    public static final String TOOLTIP_NEED_ANCIENT = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "need_ancient");

    public static final String TOOLTIP_ANCIENT_MEMORY = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "ancient_memory");

    public static final String TOOLTIP_UNIDENTIFIED = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "unidentified");

    public static final String TOOLTIP_UNIDENTIFIED_DWARVEN_TOME = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "unidentified_dwarven_tome");

    public static final String TOOLTIP_DWARVEN_TOME_SHIFT = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "dwarven_tome.shift");

    public static final String TOOLTIP_ANCIENT_DWARVEN_TOME_UNIDENTIFIED = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_tome.unidentified");

    public static final String TOOLTIP_ANCIENT_DWARVEN_TOME_PARTIAL_UNDERSTANDING = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_tome.partial_understanding");

    public static final String TOOLTIP_LEGENDARY_ANCIENT_DWARVEN_TOME_SHIFT = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "legendary_ancient_dwarven_tome.shift");

    public static final String TOOLTIP_DWARVEN_TOME_IDENTIFY_SUCCESS = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "dwarven_tome.identify_success");

    public static final String TOOLTIP_DWARVEN_TOME_IDENTIFY_FAIL = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "dwarven_tome.identify_fail");

    public static final String TOOLTIP_DWARVEN_TOME_LOCKED = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "dwarven_tome." + LOCKED);

    public static final String TOOLTIP_DWARVEN_TOME_UNLOCKED = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "dwarven_tome." + UNLOCKED);

    public static final String TOOLTIP_ANCIENT_DWARVEN_TOME_UNLOCKED = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_tome." + UNLOCKED);

    public static final String TOOLTIP_LEGENDARY_PAGE = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "legendary_page");

    public static final String TOOLTIP_PAPER_LOCKED = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "paper.locked");

    public static final String TOOLTIP_PARCHMENT_LOCKED = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "parchment.locked");

    public static final String TOOLTIP_STONE_LOCKED = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "stone.locked");

    // ---------------------------------------------------------------------
    // Lexicon keys (root tooltips)
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_DWARVEN_LEXICON_LOCKED = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "dwarven_lexicon." + LOCKED);

    public static final String TOOLTIP_DWARVEN_LEXICON_UNLOCKED = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "dwarven_lexicon." + UNLOCKED);

    public static final String TOOLTIP_DWARVEN_LEXICON_USE = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "dwarven_lexicon." + USE);

    public static final String TOOLTIP_DWARVEN_LEXICON_KNOWS = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "dwarven_lexicon." + KNOWS);

    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_LOCKED = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_lexicon." + LOCKED);

    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_UNLOCKED = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_lexicon." + UNLOCKED);

    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_USE = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_lexicon." + USE);

    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANT_READ = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_lexicon.cant_read");

    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANT_USE = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_lexicon.cant_use");

    public static final String TOOLTIP_ANCIENT_DWARVEN_LEXICON_KNOWS = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "ancient_dwarven_lexicon." + KNOWS);

    // ---------------------------------------------------------------------
    // Tome unlock keys (tooltip.<modid>.tome_unlock.<path>)
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_TOME_UNLOCK_EMPTY = JolCraftLanguageKeys.tooltip("tome_unlock", "empty");

    public static final String TOOLTIP_TOME_UNLOCK_BREW = JolCraftLanguageKeys.tooltip("tome_unlock", "brew");

    public static final String TOOLTIP_TOME_UNLOCK_GEMS = JolCraftLanguageKeys.tooltip("tome_unlock", "gems");

    // ---------------------------------------------------------------------
    // Contract keys
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_WRITTEN_CONTRACT = JolCraftLanguageKeys.tooltip("contract","written");

    public static final String TOOLTIP_SIGNED_CONTRACT = JolCraftLanguageKeys.tooltip("contract","signed");

    public static final String TOOLTIP_PROFESSION_CONTRACT = JolCraftLanguageKeys.tooltip("contract","profession");


    // ---------------------------------------------------------------------
    // Dwarf unlock keys
    // ---------------------------------------------------------------------
    public static final String DWARF = "dwarf";

    public static final String TOOLTIP_DWARF_LOCKED = JolCraftLanguageKeys.tooltip(DWARF, LOCKED);
    public static final String TOOLTIP_DWARF_BUSY = JolCraftLanguageKeys.tooltip(DWARF, "busy");
    public static final String TOOLTIP_DWARF_NOT_PAID = JolCraftLanguageKeys.tooltip(DWARF, "not_paid");
    public static final String TOOLTIP_DWARF_CANNOT_PROMOTE = JolCraftLanguageKeys.tooltip(DWARF, "cannot_promote");
    public static final String TOOLTIP_DWARF_CANNOT_SIGN = JolCraftLanguageKeys.tooltip(DWARF, "cannot_sign");
    public static final String TOOLTIP_GUARD_PROMOTION = JolCraftLanguageKeys.tooltip("guard", "promotion");

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        // -----------------------------------------------------------------
        // Language gating / effects
        // -----------------------------------------------------------------

        p.putManual(TOOLTIP_NEED_LANG, "You need to understand dwarvish to use this.");
        p.putManual(TOOLTIP_NEED_ANCIENT, "You need to understand ancient dwarvish to use this.");
        p.putManual(TOOLTIP_ANCIENT_MEMORY, "Ancient memory effect gives you temporary understanding of ancient dwarvish.");

        // -----------------------------------------------------------------
        // Dwarven Lexicon
        // -----------------------------------------------------------------

        p.putManual(TOOLTIP_DWARVEN_LEXICON_LOCKED, "The pages are filled with unfamiliar symbols.");
        p.putManual(TOOLTIP_DWARVEN_LEXICON_UNLOCKED, "The key to dwarven speech lies within.");
        p.putManual(TOOLTIP_DWARVEN_LEXICON_USE, "You have learned to understand the dwarven language!");
        p.putManual(TOOLTIP_DWARVEN_LEXICON_KNOWS, "You already understand the dwarven language.");

        // -----------------------------------------------------------------
        // Ancient Dwarven Lexicon
        // -----------------------------------------------------------------

        p.putManual(TOOLTIP_ANCIENT_DWARVEN_LEXICON_LOCKED, "The pages are filled with unfamiliar symbols.");
        p.putManual(TOOLTIP_ANCIENT_DWARVEN_LEXICON_UNLOCKED, "What was once silent may now speak again.");
        p.putManual(TOOLTIP_ANCIENT_DWARVEN_LEXICON_USE, "You have learned to understand the ancient dwarven language!");
        p.putManual(TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANT_READ, "You have no idea how to decipher this.");
        p.putManual(TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANT_USE, "The text is clearly dwarvish, but you cannot decipher its secrets.");
        p.putManual(TOOLTIP_ANCIENT_DWARVEN_LEXICON_KNOWS, "You already understand the ancient dwarven language.");

        // -----------------------------------------------------------------
        // Tomes / Identification
        // -----------------------------------------------------------------

        p.putManual(TOOLTIP_UNIDENTIFIED, "Right-click to identify.");
        p.putManual(TOOLTIP_UNIDENTIFIED_DWARVEN_TOME, "An unidentified dwarven tome.");
        p.putManual(TOOLTIP_DWARVEN_TOME_SHIFT, "Can be sold to Dwarven Historians.");
        p.putManual(TOOLTIP_ANCIENT_DWARVEN_TOME_UNIDENTIFIED, "An unidentified dwarven tome, written in ancient dwarvish.");
        p.putManual(TOOLTIP_ANCIENT_DWARVEN_TOME_PARTIAL_UNDERSTANDING, "You recognize the language as Dwarvish, but cannot understand it.");
        p.putManual(TOOLTIP_LEGENDARY_ANCIENT_DWARVEN_TOME_SHIFT, "Can be used to gain permanent knowledge.");
        p.putManual(TOOLTIP_DWARVEN_TOME_IDENTIFY_SUCCESS, "You identify the contents of the tome.");
        p.putManual(TOOLTIP_DWARVEN_TOME_IDENTIFY_FAIL, "You cannot make sense of the dwarven runes.");
        p.putManual(TOOLTIP_DWARVEN_TOME_LOCKED, "The pages are filled with unfamiliar symbols.");
        p.putManual(TOOLTIP_DWARVEN_TOME_UNLOCKED, "A dwarven tome.");
        p.putManual(TOOLTIP_ANCIENT_DWARVEN_TOME_UNLOCKED, "An ancient dwarven tome.");
        p.putManual(TOOLTIP_LEGENDARY_PAGE, "Salvaged from ancient tomes by historians. Can be used to restore ancient legendary tomes by certain dwarven professions.");

        // -----------------------------------------------------------------
        // Tome unlock messages
        // -----------------------------------------------------------------

        p.putManual(TOOLTIP_TOME_UNLOCK_EMPTY, "This tome lacks knowledge that you find useful.");
        p.putManual(TOOLTIP_TOME_UNLOCK_BREW, "You can now brew with multiple ingredients!");
        p.putManual(TOOLTIP_TOME_UNLOCK_GEMS, "You can now cut gems using a chisel!");

        // -----------------------------------------------------------------
        // Locked item variants
        // -----------------------------------------------------------------

        p.putManual(TOOLTIP_PAPER_LOCKED, "The paper is marked with unfamiliar symbols.");
        p.putManual(TOOLTIP_PARCHMENT_LOCKED, "The parchment is marked with unfamiliar symbols.");
        p.putManual(TOOLTIP_STONE_LOCKED, "The stone is marked with unfamiliar symbols.");

        // ---------------------------------------------------------------------
        // Contract keys
        // ---------------------------------------------------------------------

        p.putManual(
                TOOLTIP_WRITTEN_CONTRACT,
                "Given to dwarves without professions to get signed contracts. " +
                        "Signed contracts are used to buy profession contracts from a guildmaster. " +
                        "If given to a dwarf with a profession they will create a contract for their profession."
        );

        p.putManual(
                TOOLTIP_SIGNED_CONTRACT,
                "Signed contracts are used to buy profession contracts from a guildmaster."
        );

        p.putManual(
                TOOLTIP_PROFESSION_CONTRACT,
                "Profession contracts can be given to dwarves without professions to set their profession."
        );

        // -----------------------------------------------------------------
        // Dwarf
        // -----------------------------------------------------------------

        p.putManual(TOOLTIP_DWARF_LOCKED, "You do not understand each other.");
        p.putManual(TOOLTIP_DWARF_BUSY, "This dwarf is busy.");
        p.putManual(TOOLTIP_DWARF_NOT_PAID, "You have not paid this dwarf yet.");
        p.putManual(TOOLTIP_DWARF_CANNOT_PROMOTE, "This dwarf cannot be promoted.");
        p.putManual(TOOLTIP_DWARF_CANNOT_SIGN, "This dwarf cannot sign contracts.");
        p.putManual(TOOLTIP_GUARD_PROMOTION, "Guard promoted to %s!");
    }
}