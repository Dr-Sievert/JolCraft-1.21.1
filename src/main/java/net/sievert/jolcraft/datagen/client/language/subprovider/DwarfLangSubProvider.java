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
public final class DwarfLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftDictionary.DWARVEN;
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }


    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        // -----------------------------------------------------------------
        // Language gating / effects
        // -----------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_NEED_DWARVEN_LANGUAGE, "You need to understand dwarvish to use this.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_NEED_ANCIENT_DWARVEN_LANGUAGE, "You need to understand ancient dwarvish to use this.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_ANCIENT_MEMORY, "The Ancient Memory effect gives you a temporary understanding of ancient dwarvish.");

        // -----------------------------------------------------------------
        // Dwarven Lexicon
        // -----------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_LEXICON_LOCKED, "The pages are filled with unfamiliar symbols.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_LEXICON_UNLOCKED, "The key to dwarven speech lies within.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_LEXICON_USE, "You have learned to understand the dwarven language!");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_LEXICON_KNOWS_DWARVEN_LANGUAGE, "You already understand the dwarven language.");

        // -----------------------------------------------------------------
        // Ancient Dwarven Lexicon
        // -----------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_LOCKED, "The pages are filled with unfamiliar symbols.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_UNLOCKED, "What was once silent may now speak again.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_USE, "You have learned to understand the ancient dwarven language!");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANNOT_READ, "You have no idea how to decipher this.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_CANNOT_USE, "The text is clearly dwarvish, but you cannot decipher its secrets.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_LEXICON_KNOWS_ANCIENT_DWARVEN_LANGUAGE, "You already understand the ancient dwarven language.");

        // -----------------------------------------------------------------
        // Tomes / Identification
        // -----------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_UNIDENTIFIED, "Right-click to identify.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_UNIDENTIFIED_DWARVEN_TOME, "An unidentified dwarven tome.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_SHIFT, "Can be sold to dwarven historians.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_UNIDENTIFIED_ANCIENT_DWARVEN_TOME, "An unidentified dwarven tome, written in ancient dwarvish.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_TOME_PARTIAL_UNDERSTANDING, "You recognize the language as dwarvish but cannot understand it.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_LEGENDARY_ANCIENT_DWARVEN_TOME_SHIFT, "Can be used to gain permanent knowledge.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_IDENTIFY_SUCCESS, "You identify the contents of the tome.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_IDENTIFY_FAIL, "You cannot make sense of the dwarven runes.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_LOCKED, "The pages are filled with unfamiliar symbols.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_UNLOCKED, "A dwarven tome.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_ANCIENT_DWARVEN_TOME_UNLOCKED, "An ancient dwarven tome.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_LEGENDARY_PAGE, "Salvaged from ancient tomes by historians. Can be used to restore ancient legendary tomes by certain dwarven professions.");

        // -----------------------------------------------------------------
        // Tome unlock messages
        // -----------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_UNLOCK_EMPTY, "This tome contains no knowledge useful to you.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_UNLOCK_BREW, "You can now brew with multiple ingredients!");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_UNLOCK_GEMS, "You can now cut gems using a chisel!");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_UNLOCK_MINING, "You learned how to channel Rhythm of the Deep!");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARVEN_TOME_UNLOCK_ALCHEMY, "You learned how to grind with multiple ingredients!");

        // -----------------------------------------------------------------
        // Locked item variants
        // -----------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_PAPER_LOCKED, "The paper is marked with unfamiliar symbols.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_PARCHMENT_LOCKED, "The parchment is marked with unfamiliar symbols.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_STONE_LOCKED, "The stone is marked with unfamiliar symbols.");

        // -----------------------------------------------------------------
        // Dwarf
        // -----------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARF_LOCKED, "You do not understand each other.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARF_BUSY, "This dwarf is busy.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARF_NOT_PAID, "You have not paid this dwarf yet.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARF_CANNOT_PROMOTE, "This dwarf cannot be promoted.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARF_CANNOT_SIGN, "This dwarf cannot sign contracts.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_DWARF_GUARD_PROMOTION, "Guard promoted to %s!");

        // -----------------------------------------------------------------
        // Crates
        // -----------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_RESTOCK_CRATE, "Can be used to restock the inventory of a dwarf or villager trader.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_RESTOCK_CRATE_NO_NEED, "This trader doesn't need restocking.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_RESTOCK_CRATE_SUCCESS, "Trader's inventory restocked!");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_REROLL_CRATE, "Can be used to reroll the inventory of a dwarf or villager trader.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_REROLL_CRATE_FAIL, "This trader's inventory cannot be rerolled!");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_REROLL_CRATE_SUCCESS, "Trader's inventory rerolled!");

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_CRATE_COOLDOWN, "You must wait before you can use another crate.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_CRATE_NO_OFFERS_VILLAGER, "This villager has no trades!");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_CRATE_NO_OFFERS_DWARF, "This dwarf has no trades!");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_REWARD_CRATE, "There is loot inside!");

        // -----------------------------------------------------------------
        // Custom crates
        // -----------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.SUPPLY_CRATE, "Supply Crate");
        putManual(translations, JolCraftLanguageKeys.ALCHEMY_SUPPLIES, "Alchemy Supplies");
        putManual(translations, JolCraftLanguageKeys.DWARVEN_FORTRESS_EXCAVATION, "Dwarven Fortress Excavation");
        putManual(translations, JolCraftLanguageKeys.ARTISAN_SUPPLIES, "Artisan Supplies");
        putManual(translations, JolCraftLanguageKeys.FARMING_SUPPLIES, "Farming Supplies");
        putManual(translations, JolCraftLanguageKeys.MINING_CACHE, "Mining Cache");
        putManual(translations, JolCraftLanguageKeys.FISHING_LOOT, "Fishing Loot");
        putManual(translations, JolCraftLanguageKeys.BLACKSMITH_SUPPLIES, "Blacksmith Supplies");
        putManual(translations, JolCraftLanguageKeys.MONSTER_SLAYER_LOOT, "Monster Slayer Loot");
        putManual(translations, JolCraftLanguageKeys.VAULT_LOOT, "Vault Loot");
    }
}