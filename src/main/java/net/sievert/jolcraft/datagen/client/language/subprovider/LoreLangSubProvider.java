package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.item.lore.util.LoreHelper;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class LoreLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftDictionary.LORE;
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }


    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        // MODERN
        add(translations,  DwarfLoreKey.TUNNEL_STABILITY, "A Survey of Tunnel Stability in Soft Granite, Volume II");
        add(translations,  DwarfLoreKey.BARREL_SEALING, "Proper Barrel Sealing Techniques, Volume I");
        add(translations,  DwarfLoreKey.TURNIP_YIELDS, "A Record of Turnip Yields, Year 538");
        add(translations,  DwarfLoreKey.BEARD_GROOMING, "The Art of Beard Grooming: A Beginner's Guide, 4th Edition");
        add(translations,  DwarfLoreKey.MINECART_WHEELS, "Catalog of Minecart Wheel Failures, Volume VII");
        add(translations,  DwarfLoreKey.FURNACE_TEMPERATURES, "Furnace Temperatures and You, Revised 987");
        add(translations,  DwarfLoreKey.PIPEWORKS_KARRAM_DUN, "The Pipeworks of Lower Karram-Dûn, Year 1112");
        add(translations,  DwarfLoreKey.FORGE_ETIQUETTE, "Basic Forge Etiquette for Apprentices, Volume IV");
        add(translations,  DwarfLoreKey.LEDGERS, "Ledgers and Ledgers: On the Keeping of Ledgers, Volume IX");
        add(translations,  DwarfLoreKey.FUNGUS_UPPER_CAVERNS, "Common Fungus of the Upper Caverns, Survey of 1014");
        add(translations,  DwarfLoreKey.ECHO_PATTERNS, "Observations on Echo Patterns in Vaulted Halls, Volume II");

        add(translations,  DwarfLoreKey.CHISELED_DEEPSLATE, "Properties of Chiseled Deepslate, Volume VI");
        add(translations,  DwarfLoreKey.FORGE_MARKS, "Ancestral Forge Marks and Their Variants, Volume III");
        add(translations,  DwarfLoreKey.AQUEDUCT_COLLAPSE, "Survey of the Northern Aqueduct Collapse, Year 1198");
        add(translations,  DwarfLoreKey.GEM_VEIN_LUNAR_CYCLE, "Gem Vein Activity by Lunar Cycle, Volume VIII");
        add(translations,  DwarfLoreKey.MOLD_ID_CONTAINMENT, "Subterranean Mold: Identification & Containment, Year 1243");
        add(translations,  DwarfLoreKey.WHISPERS_OLD_PILLARS, "Whispers Among the Old Pillars, Volume V");
        add(translations,  DwarfLoreKey.QUEEN_HRAGA, "A Disputed Account of Forge-Queen Hraga's Reign, Volume I");
        add(translations,  DwarfLoreKey.UNSPOKEN_TUNNELS, "The Unspoken Tunnels: A Guard Captain’s Memoir, Year 982");
        add(translations,  DwarfLoreKey.HALL_LANTERNS, "Inventory of the Hall of Lanterns, Year 1024");
        add(translations,  DwarfLoreKey.RITUAL_BEARD_OIL, "On the Use of Beard Oil in Ritual Contexts, Volume X");
        add(translations,  DwarfLoreKey.ROOF_COLLAPSE_CHRONOLOGY, "Chronology of Roof Collapses in Irondeep Sector, Volume IX");

        add(translations,  DwarfLoreKey.ECHO_CARTOGRAPHY, "Echo-Chamber Cartography: The First Attempts, Year 1251");
        add(translations,  DwarfLoreKey.GEMLINES_BEARERS, "The Fifteen Gemlines and Their Bearers, Volume I");
        add(translations,  DwarfLoreKey.STONEGUARD_PROTOCOLS, "Stoneguard Protocols for Deep Siege Defense, Year 1066");
        add(translations,  DwarfLoreKey.ARCANIST_BINDING_RITUALS, "Rituals of Binding: Arcanist Practices, Volume XIII");
        add(translations,  DwarfLoreKey.MITHRIL_FORGING, "On the Forging of Mithril Alloy, Year 987");
        add(translations,  DwarfLoreKey.CONTRACT_SEALS, "Contract Seals and Binding Ink Formulas, Volume XI");
        add(translations,  DwarfLoreKey.EMBERGLASS_FIRES, "Mysteries of the Emberglass Furnace-Fires, Year 1187");
        add(translations,  DwarfLoreKey.DEEPMARROW_SIGILS, "Ancestral Sigils of the Deepmarrow Keepers, Volume XII");
        add(translations,  DwarfLoreKey.CHAOS_DWARVES_WARNING, "Chaos Dwarves: A Warning to the Forgeborn, Year 1293");
        add(translations,  DwarfLoreKey.WOECRYSTAL_RUNES, "Runes of Woecrystal and Their Applications, Volume XVI");
        add(translations,  DwarfLoreKey.LOST_CARAVANS, "Ledger of Lost Caravans, Volume VI");

        add(translations,  DwarfLoreKey.BREWERIES_STEWS, "Warden-Blessed Breweries and Sacred Stews, Volume XIV");
        add(translations,  DwarfLoreKey.STATUE_SPIRIT_BINDING, "Spirit-Binding Rites for Guardian Statues, Year 1010");
        add(translations,  DwarfLoreKey.FURNACE_EXPERIMENTS, "Experimental Furnace Designs, Year 1303");
        add(translations,  DwarfLoreKey.SECRET_TRADE_ROUTES, "Secret Trade Routes of the Westward Expansion, Year 1027");
        add(translations,  DwarfLoreKey.GIANT_SPORE_BLOOMS, "Giant Spore Blooms of the Deeps, Volume V");
        add(translations,  DwarfLoreKey.THE_LAST_BALROG, "The Last Balrog Sighting, Year 1387");
        add(translations,  DwarfLoreKey.DEEPFIRE_BALROG, "Chronicle of the Deepfire Balrog");
        add(translations,  DwarfLoreKey.MAGMA_WRITINGS, "Writings from the Magma Archives, Volume XIX");
        add(translations,  DwarfLoreKey.STORMCARVED_LEDGE, "Chronicle of the Stormcarved Ledge, Year 1502");
        add(translations,  DwarfLoreKey.ANCIENT_TOMB_KEYS, "Keys of the Ancient Tombs, Volume X");
        add(translations,  DwarfLoreKey.STARFALL_LEDGER, "Ledger of the Starfall Years, Volume XVII");

        // ANCIENT
        add(translations,  DwarfLoreKey.KEYSTONE_SHAPES, "On the Shapes and Placement of Keystones, Age of Foundations");
        add(translations,  DwarfLoreKey.CISTERN_SEALS, "Inspections of Cistern Seals, Year 132");
        add(translations,  DwarfLoreKey.BREW_YIELDS, "Brew Yields and Yeast Logs, Year 91");
        add(translations,  DwarfLoreKey.BEARD_OILS, "Beard Oil Recipes for the Elder Kin, Volume III");
        add(translations,  DwarfLoreKey.PIPE_ASSEMBLY, "Pipe Assembly Diagrams of the Early Guild, Volume I");
        add(translations,  DwarfLoreKey.ANVIL_WEAR, "Patterns of Anvil Wear and Maintenance, First Forgemasters");
        add(translations,  DwarfLoreKey.DOWSING_METHODS, "Practical Dowsing Methods for Water and Ore, Second Era");
        add(translations,  DwarfLoreKey.HALL_GREETINGS, "Proper Greetings in the Great Halls, Year 59");
        add(translations,  DwarfLoreKey.LEDGER_FORMATS, "Accepted Formats for Stone Ledger Tablets, Early Record-Keepers");
        add(translations,  DwarfLoreKey.FUNGAL_COLONY_NOTES, "Notes on the Great Fungal Colony Collapse, Age of Growth");
        add(translations,  DwarfLoreKey.SEATING_CHART, "Seating Charts for Guild Banquets, Old Calendar");

        add(translations,  DwarfLoreKey.SPARE_KEYS, "The Making and Keeping of Spare Keys, Generation I");
        add(translations,  DwarfLoreKey.RUNE_ACCOUNTING, "Rune-Tallies for Trade Accounting, Year 287");
        add(translations,  DwarfLoreKey.CISTERN_CLEANING, "Cistern Cleaning Practices, Generation IV");
        add(translations,  DwarfLoreKey.STARSTONE_RUMORS, "Rumors of Starstones Falling, Night of Terrors");
        add(translations,  DwarfLoreKey.DUST_CONTROL, "Sweeping Schedules for Dust Control, Early Quarters");
        add(translations,  DwarfLoreKey.LANTERN_MAINTENANCE, "Daily Maintenance of Oil Lanterns, Year 60");
        add(translations,  DwarfLoreKey.CHAMPION_OATHS, "The Oaths of the First Champions, Battleborn Era");
        add(translations,  DwarfLoreKey.RAT_WARNINGS, "Old Rat Infestation Warnings, Year 22");
        add(translations,  DwarfLoreKey.BEDROLL_RULES, "Bedroll Placement Rules for Shared Chambers, Founders’ Years");
        add(translations,  DwarfLoreKey.ROOT_PRESERVES, "Recipes for Preserving Roots and Tubers, First Preservers");
        add(translations,  DwarfLoreKey.LOST_TOOLS, "The Lost Tools Ledger, Age of Loss");

        add(translations,  DwarfLoreKey.STONEGUARD_SEATING, "Seating Charts for Stoneguard Banquets, Early Stoneguard");
        add(translations,  DwarfLoreKey.ANIMAL_TOKENS, "Tokens Used in Early Kinship Rituals, Rituals Volume I");
        add(translations,  DwarfLoreKey.STONEGUARD_PACT, "The Stoneguard Pact, Pact Year");
        add(translations,  DwarfLoreKey.RUNE_LOCK_DIAGRAMS, "Diagrams of Rune-Locked Chests, Keymasters’ Era");
        add(translations,  DwarfLoreKey.FORGE_OF_MITHRIL, "The Forging of Mithril, Old Metallurgists");
        add(translations,  DwarfLoreKey.CONTRACT_SIGNATURES, "Ledger of Old Contract Signatures, Scribe’s Year");
        add(translations,  DwarfLoreKey.EMBERGLASS_FORGE_LOGS, "Emberglass Forge Logs, First Era");
        add(translations,  DwarfLoreKey.MEMORY_SHARD_DISCOVERY, "Discovery of the Memory Shards, Year 7");
        add(translations,  DwarfLoreKey.EXILE_RECORDS, "Records of Exiles and Outcasts, Outcast Scrolls");
        add(translations,  DwarfLoreKey.SPIRIT_ENCOUNTER, "An Early Encounter with a Dwarven Spirit, Lost Era");
        add(translations,  DwarfLoreKey.SEALED_VAULTS, "Account of the Sealed Vaults of Hraga, Vault Year");

        add(translations,  DwarfLoreKey.ROOT_STORAGE, "On the Storage of Root Vegetables in Deep Cellars, First Storage");
        add(translations,  DwarfLoreKey.DAWN_HALL_RELICS, "Relics of the Dawn Hall, Dawn Era");
        add(translations,  DwarfLoreKey.PRIMEVAL_IRONWORKS, "Primeval Ironworks of the Deep, Era of Makers");
        add(translations,  DwarfLoreKey.STARFORGED_HELM, "Discovery of the Starforged Helm, Night of Comets");
        add(translations,  DwarfLoreKey.FIRST_EMBERGLASS, "The First Emberglass Crucible, Age of Flames");
        add(translations,  DwarfLoreKey.BINDING_OF_THE_BALROG, "The Binding of the Deepfire Balrog");
        add(translations,  DwarfLoreKey.ETERNAL_EMBER, "Eternal Ember of the Ancients, Cycle XX");
        add(translations,  DwarfLoreKey.ORACLE_INSCRIPTIONS, "Oracle Inscriptions of the Crystal Vault, Volume VII");
        add(translations,  DwarfLoreKey.DEEP_CURSE_TABLET, "Tablet of the Deep Curse, Age of Shadows");
        add(translations,  DwarfLoreKey.SUNKEN_FORGE_RITES, "Rites of the Sunken Forge, Lost Age");
        add(translations,  DwarfLoreKey.CAVERN_LIGHT_CHRONICLE, "Chronicle of Cavern Light, Dawn Cycle");

        add(translations,  DwarfLoreKey.MITHRIL_FORGE_TECHNIQUE, "Mithril Forging Technique, Forge of the First Flame");
        add(translations,  DwarfLoreKey.ANCIENT_GEMCRAFT, "The Art of Gemcutting, Archives of Karaz-Un");
        add(translations,  DwarfLoreKey.FORGOTTEN_BREW_FORMULAS, "Formulas of Dwarven Brews, Vaults of Stonehearth");
        add(translations,  DwarfLoreKey.COIN_PRESS_MANUAL, "Coin Press Manual, Bank of Barak-Zul");
        add(translations,  DwarfLoreKey.ALCHEMY_RECIPES, "Codex Alchemica, Transcribed by the Final Thaumaturge");
        add(translations,  DwarfLoreKey.MINING_RHYTHM, "Rhythm of the Deep, First Mining Guild");
    }

    private void add(Map<String, String> translations, DwarfLoreKey key, String text) {
        String translationKey = LoreHelper.getEntryTranslationKey(key);
        putManual(translations, translationKey, text);
    }
}