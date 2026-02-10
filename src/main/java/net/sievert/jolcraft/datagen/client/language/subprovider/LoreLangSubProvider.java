package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.data.lore.util.LoreHelper;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;

@OnlyIn(Dist.CLIENT)
public final class LoreLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        // MODERN
        add(p, DwarfLoreKey.TUNNEL_STABILITY, "A Survey of Tunnel Stability in Soft Granite, Volume II");
        add(p, DwarfLoreKey.BARREL_SEALING, "Proper Barrel Sealing Techniques, Volume I");
        add(p, DwarfLoreKey.TURNIP_YIELDS, "A Record of Turnip Yields, Year 538");
        add(p, DwarfLoreKey.BEARD_GROOMING, "The Art of Beard Grooming: A Beginner's Guide, 4th Edition");
        add(p, DwarfLoreKey.MINECART_WHEELS, "Catalog of Minecart Wheel Failures, Volume VII");
        add(p, DwarfLoreKey.FURNACE_TEMPERATURES, "Furnace Temperatures and You, Revised 987");
        add(p, DwarfLoreKey.PIPEWORKS_KARRAM_DUN, "The Pipeworks of Lower Karram-Dûn, Year 1112");
        add(p, DwarfLoreKey.FORGE_ETIQUETTE, "Basic Forge Etiquette for Apprentices, Volume IV");
        add(p, DwarfLoreKey.LEDGERS, "Ledgers and Ledgers: On the Keeping of Ledgers, Volume IX");
        add(p, DwarfLoreKey.FUNGUS_UPPER_CAVERNS, "Common Fungus of the Upper Caverns, Survey of 1014");
        add(p, DwarfLoreKey.ECHO_PATTERNS, "Observations on Echo Patterns in Vaulted Halls, Volume II");

        add(p, DwarfLoreKey.CHISELED_DEEPSLATE, "Properties of Chiseled Deepslate, Volume VI");
        add(p, DwarfLoreKey.FORGE_MARKS, "Ancestral Forge Marks and Their Variants, Volume III");
        add(p, DwarfLoreKey.AQUEDUCT_COLLAPSE, "Survey of the Northern Aqueduct Collapse, Year 1198");
        add(p, DwarfLoreKey.GEM_VEIN_LUNAR_CYCLE, "Gem Vein Activity by Lunar Cycle, Volume VIII");
        add(p, DwarfLoreKey.MOLD_ID_CONTAINMENT, "Subterranean Mold: Identification & Containment, Year 1243");
        add(p, DwarfLoreKey.WHISPERS_OLD_PILLARS, "Whispers Among the Old Pillars, Volume V");
        add(p, DwarfLoreKey.QUEEN_HRAGA, "A Disputed Account of Forge-Queen Hraga's Reign, Volume I");
        add(p, DwarfLoreKey.UNSPOKEN_TUNNELS, "The Unspoken Tunnels: A Guard Captain’s Memoir, Year 982");
        add(p, DwarfLoreKey.HALL_LANTERNS, "Inventory of the Hall of Lanterns, Year 1024");
        add(p, DwarfLoreKey.RITUAL_BEARD_OIL, "On the Use of Beard Oil in Ritual Contexts, Volume X");
        add(p, DwarfLoreKey.ROOF_COLLAPSE_CHRONOLOGY, "Chronology of Roof Collapses in Irondeep Sector, Volume IX");

        add(p, DwarfLoreKey.ECHO_CARTOGRAPHY, "Echo-Chamber Cartography: The First Attempts, Year 1251");
        add(p, DwarfLoreKey.GEMLINES_BEARERS, "The Fifteen Gemlines and Their Bearers, Volume I");
        add(p, DwarfLoreKey.STONEGUARD_PROTOCOLS, "Stoneguard Protocols for Deep Siege Defense, Year 1066");
        add(p, DwarfLoreKey.ARCANIST_BINDING_RITUALS, "Rituals of Binding: Arcanist Practices, Volume XIII");
        add(p, DwarfLoreKey.MITHRIL_FORGING, "On the Forging of Mithril Alloy, Year 987");
        add(p, DwarfLoreKey.CONTRACT_SEALS, "Contract Seals and Binding Ink Formulas, Volume XI");
        add(p, DwarfLoreKey.EMBERGLASS_FIRES, "Mysteries of the Emberglass Furnace-Fires, Year 1187");
        add(p, DwarfLoreKey.DEEPMARROW_SIGILS, "Ancestral Sigils of the Deepmarrow Keepers, Volume XII");
        add(p, DwarfLoreKey.CHAOS_DWARVES_WARNING, "Chaos Dwarves: A Warning to the Forgeborn, Year 1293");
        add(p, DwarfLoreKey.WOECRYSTAL_RUNES, "Runes of Woecrystal and Their Applications, Volume XVI");
        add(p, DwarfLoreKey.LOST_CARAVANS, "Ledger of Lost Caravans, Volume VI");

        add(p, DwarfLoreKey.BREWERIES_STEWS, "Warden-Blessed Breweries and Sacred Stews, Volume XIV");
        add(p, DwarfLoreKey.STATUE_SPIRIT_BINDING, "Spirit-Binding Rites for Guardian Statues, Year 1010");
        add(p, DwarfLoreKey.FURNACE_EXPERIMENTS, "Experimental Furnace Designs, Year 1303");
        add(p, DwarfLoreKey.SECRET_TRADE_ROUTES, "Secret Trade Routes of the Westward Expansion, Year 1027");
        add(p, DwarfLoreKey.GIANT_SPORE_BLOOMS, "Giant Spore Blooms of the Deeps, Volume V");
        add(p, DwarfLoreKey.THE_LAST_BALROG, "The Last Balrog Sighting, Year 1387");
        add(p, DwarfLoreKey.DEEPFIRE_BALROG, "Chronicle of the Deepfire Balrog");
        add(p, DwarfLoreKey.MAGMA_WRITINGS, "Writings from the Magma Archives, Volume XIX");
        add(p, DwarfLoreKey.STORMCARVED_LEDGE, "Chronicle of the Stormcarved Ledge, Year 1502");
        add(p, DwarfLoreKey.ANCIENT_TOMB_KEYS, "Keys of the Ancient Tombs, Volume X");
        add(p, DwarfLoreKey.STARFALL_LEDGER, "Ledger of the Starfall Years, Volume XVII");

        // ANCIENT
        add(p, DwarfLoreKey.KEYSTONE_SHAPES, "On the Shapes and Placement of Keystones, Age of Foundations");
        add(p, DwarfLoreKey.CISTERN_SEALS, "Inspections of Cistern Seals, Year 132");
        add(p, DwarfLoreKey.BREW_YIELDS, "Brew Yields and Yeast Logs, Year 91");
        add(p, DwarfLoreKey.BEARD_OILS, "Beard Oil Recipes for the Elder Kin, Volume III");
        add(p, DwarfLoreKey.PIPE_ASSEMBLY, "Pipe Assembly Diagrams of the Early Guild, Volume I");
        add(p, DwarfLoreKey.ANVIL_WEAR, "Patterns of Anvil Wear and Maintenance, First Forgemasters");
        add(p, DwarfLoreKey.DOWSING_METHODS, "Practical Dowsing Methods for Water and Ore, Second Era");
        add(p, DwarfLoreKey.HALL_GREETINGS, "Proper Greetings in the Great Halls, Year 59");
        add(p, DwarfLoreKey.LEDGER_FORMATS, "Accepted Formats for Stone Ledger Tablets, Early Record-Keepers");
        add(p, DwarfLoreKey.FUNGAL_COLONY_NOTES, "Notes on the Great Fungal Colony Collapse, Age of Growth");
        add(p, DwarfLoreKey.SEATING_CHART, "Seating Charts for Guild Banquets, Old Calendar");

        add(p, DwarfLoreKey.SPARE_KEYS, "The Making and Keeping of Spare Keys, Generation I");
        add(p, DwarfLoreKey.RUNE_ACCOUNTING, "Rune-Tallies for Trade Accounting, Year 287");
        add(p, DwarfLoreKey.CISTERN_CLEANING, "Cistern Cleaning Practices, Generation IV");
        add(p, DwarfLoreKey.STARSTONE_RUMORS, "Rumors of Starstones Falling, Night of Terrors");
        add(p, DwarfLoreKey.DUST_CONTROL, "Sweeping Schedules for Dust Control, Early Quarters");
        add(p, DwarfLoreKey.LANTERN_MAINTENANCE, "Daily Maintenance of Oil Lanterns, Year 60");
        add(p, DwarfLoreKey.CHAMPION_OATHS, "The Oaths of the First Champions, Battleborn Era");
        add(p, DwarfLoreKey.RAT_WARNINGS, "Old Rat Infestation Warnings, Year 22");
        add(p, DwarfLoreKey.BEDROLL_RULES, "Bedroll Placement Rules for Shared Chambers, Founders’ Years");
        add(p, DwarfLoreKey.ROOT_PRESERVES, "Recipes for Preserving Roots and Tubers, First Preservers");
        add(p, DwarfLoreKey.LOST_TOOLS, "The Lost Tools Ledger, Age of Loss");

        add(p, DwarfLoreKey.STONEGUARD_SEATING, "Seating Charts for Stoneguard Banquets, Early Stoneguard");
        add(p, DwarfLoreKey.ANIMAL_TOKENS, "Tokens Used in Early Kinship Rituals, Rituals Volume I");
        add(p, DwarfLoreKey.STONEGUARD_PACT, "The Stoneguard Pact, Pact Year");
        add(p, DwarfLoreKey.RUNE_LOCK_DIAGRAMS, "Diagrams of Rune-Locked Chests, Keymasters’ Era");
        add(p, DwarfLoreKey.FORGE_OF_MITHRIL, "The Forging of Mithril, Old Metallurgists");
        add(p, DwarfLoreKey.CONTRACT_SIGNATURES, "Ledger of Old Contract Signatures, Scribe’s Year");
        add(p, DwarfLoreKey.EMBERGLASS_FORGE_LOGS, "Emberglass Forge Logs, First Era");
        add(p, DwarfLoreKey.MEMORY_SHARD_DISCOVERY, "Discovery of the Memory Shards, Year 7");
        add(p, DwarfLoreKey.EXILE_RECORDS, "Records of Exiles and Outcasts, Outcast Scrolls");
        add(p, DwarfLoreKey.SPIRIT_ENCOUNTER, "An Early Encounter with a Dwarven Spirit, Lost Era");
        add(p, DwarfLoreKey.SEALED_VAULTS, "Account of the Sealed Vaults of Hraga, Vault Year");

        add(p, DwarfLoreKey.ROOT_STORAGE, "On the Storage of Root Vegetables in Deep Cellars, First Storage");
        add(p, DwarfLoreKey.DAWN_HALL_RELICS, "Relics of the Dawn Hall, Dawn Era");
        add(p, DwarfLoreKey.PRIMEVAL_IRONWORKS, "Primeval Ironworks of the Deep, Era of Makers");
        add(p, DwarfLoreKey.STARFORGED_HELM, "Discovery of the Starforged Helm, Night of Comets");
        add(p, DwarfLoreKey.FIRST_EMBERGLASS, "The First Emberglass Crucible, Age of Flames");
        add(p, DwarfLoreKey.BINDING_OF_THE_BALROG, "The Binding of the Deepfire Balrog");
        add(p, DwarfLoreKey.ETERNAL_EMBER, "Eternal Ember of the Ancients, Cycle XX");
        add(p, DwarfLoreKey.ORACLE_INSCRIPTIONS, "Oracle Inscriptions of the Crystal Vault, Volume VII");
        add(p, DwarfLoreKey.DEEP_CURSE_TABLET, "Tablet of the Deep Curse, Age of Shadows");
        add(p, DwarfLoreKey.SUNKEN_FORGE_RITES, "Rites of the Sunken Forge, Lost Age");
        add(p, DwarfLoreKey.CAVERN_LIGHT_CHRONICLE, "Chronicle of Cavern Light, Dawn Cycle");

        add(p, DwarfLoreKey.MITHRIL_FORGE_TECHNIQUE, "Mithril Forging Technique, Forge of the First Flame");
        add(p, DwarfLoreKey.ANCIENT_GEMCRAFT, "The Art of Gemcutting, Archives of Karaz-Un");
        add(p, DwarfLoreKey.FORGOTTEN_BREW_FORMULAS, "Formulas of Dwarven Brews, Vaults of Stonehearth");
        add(p, DwarfLoreKey.COIN_PRESS_MANUAL, "Coin Press Manual, Bank of Barak-Zul");
        add(p, DwarfLoreKey.ALCHEMY_RECIPES, "Codex Alchemica, Transcribed by the Final Thaumaturge");
    }

    private static void add(AbstractLanguageProvider p, DwarfLoreKey key, String text) {
        String translationKey = LoreHelper.getEntryTranslationKey(key);
        p.putManual(translationKey, text);
    }
}