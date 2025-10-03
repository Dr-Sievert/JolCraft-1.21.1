package net.sievert.jolcraft.data.util.lore.dwarf;

import net.sievert.jolcraft.data.util.lore.LoreAge;
import net.sievert.jolcraft.data.util.lore.LoreRarity;

import java.util.Map;

public final class DwarfLoreEntries {
    private DwarfLoreEntries() {}

    public static final Map<DwarfLoreKey, DwarfLoreEntry> ALL = Map.<DwarfLoreKey, DwarfLoreEntry>ofEntries(

            // ------------- MODERN -------------

            Map.entry(DwarfLoreKey.TUNNEL_STABILITY, new DwarfLoreEntry(DwarfLoreKey.TUNNEL_STABILITY, LoreAge.MODERN, LoreRarity.COMMON, "A Survey of Tunnel Stability in Soft Granite, Volume II")),
            Map.entry(DwarfLoreKey.BARREL_SEALING, new DwarfLoreEntry(DwarfLoreKey.BARREL_SEALING, LoreAge.MODERN, LoreRarity.COMMON, "Proper Barrel Sealing Techniques, Volume I")),
            Map.entry(DwarfLoreKey.TURNIP_YIELDS, new DwarfLoreEntry(DwarfLoreKey.TURNIP_YIELDS, LoreAge.MODERN, LoreRarity.COMMON, "A Record of Turnip Yields, Year 538")),
            Map.entry(DwarfLoreKey.BEARD_GROOMING, new DwarfLoreEntry(DwarfLoreKey.BEARD_GROOMING, LoreAge.MODERN, LoreRarity.COMMON, "The Art of Beard Grooming: A Beginner's Guide, 4th Edition")),
            Map.entry(DwarfLoreKey.MINECART_WHEELS, new DwarfLoreEntry(DwarfLoreKey.MINECART_WHEELS, LoreAge.MODERN, LoreRarity.COMMON, "Catalog of Minecart Wheel Failures, Volume VII")),
            Map.entry(DwarfLoreKey.FURNACE_TEMPERATURES, new DwarfLoreEntry(DwarfLoreKey.FURNACE_TEMPERATURES, LoreAge.MODERN, LoreRarity.COMMON, "Furnace Temperatures and You, Revised 987")),
            Map.entry(DwarfLoreKey.PIPEWORKS_KARRAM_DUN, new DwarfLoreEntry(DwarfLoreKey.PIPEWORKS_KARRAM_DUN, LoreAge.MODERN, LoreRarity.COMMON, "The Pipeworks of Lower Karram-Dûn, Year 1112")),
            Map.entry(DwarfLoreKey.FORGE_ETIQUETTE, new DwarfLoreEntry(DwarfLoreKey.FORGE_ETIQUETTE, LoreAge.MODERN, LoreRarity.COMMON, "Basic Forge Etiquette for Apprentices, Volume IV")),
            Map.entry(DwarfLoreKey.LEDGERS, new DwarfLoreEntry(DwarfLoreKey.LEDGERS, LoreAge.MODERN, LoreRarity.COMMON, "Ledgers and Ledgers: On the Keeping of Ledgers, Volume IX")),
            Map.entry(DwarfLoreKey.FUNGUS_UPPER_CAVERNS, new DwarfLoreEntry(DwarfLoreKey.FUNGUS_UPPER_CAVERNS, LoreAge.MODERN, LoreRarity.COMMON, "Common Fungus of the Upper Caverns, Survey of 1014")),
            Map.entry(DwarfLoreKey.ECHO_PATTERNS, new DwarfLoreEntry(DwarfLoreKey.ECHO_PATTERNS, LoreAge.MODERN, LoreRarity.COMMON, "Observations on Echo Patterns in Vaulted Halls, Volume II")),

            Map.entry(DwarfLoreKey.CHISELED_DEEPSLATE, new DwarfLoreEntry(DwarfLoreKey.CHISELED_DEEPSLATE, LoreAge.MODERN, LoreRarity.UNCOMMON, "Properties of Chiseled Deepslate, Volume VI")),
            Map.entry(DwarfLoreKey.FORGE_MARKS, new DwarfLoreEntry(DwarfLoreKey.FORGE_MARKS, LoreAge.MODERN, LoreRarity.UNCOMMON, "Ancestral Forge Marks and Their Variants, Volume III")),
            Map.entry(DwarfLoreKey.AQUEDUCT_COLLAPSE, new DwarfLoreEntry(DwarfLoreKey.AQUEDUCT_COLLAPSE, LoreAge.MODERN, LoreRarity.UNCOMMON, "Survey of the Northern Aqueduct Collapse, Year 1198")),
            Map.entry(DwarfLoreKey.GEM_VEIN_LUNAR_CYCLE, new DwarfLoreEntry(DwarfLoreKey.GEM_VEIN_LUNAR_CYCLE, LoreAge.MODERN, LoreRarity.UNCOMMON, "Gem Vein Activity by Lunar Cycle, Volume VIII")),
            Map.entry(DwarfLoreKey.MOLD_ID_CONTAINMENT, new DwarfLoreEntry(DwarfLoreKey.MOLD_ID_CONTAINMENT, LoreAge.MODERN, LoreRarity.UNCOMMON, "Subterranean Mold: Identification & Containment, Year 1243")),
            Map.entry(DwarfLoreKey.WHISPERS_OLD_PILLARS, new DwarfLoreEntry(DwarfLoreKey.WHISPERS_OLD_PILLARS, LoreAge.MODERN, LoreRarity.UNCOMMON, "Whispers Among the Old Pillars, Volume V")),
            Map.entry(DwarfLoreKey.QUEEN_HRAGA, new DwarfLoreEntry(DwarfLoreKey.QUEEN_HRAGA, LoreAge.MODERN, LoreRarity.UNCOMMON, "A Disputed Account of Forge-Queen Hraga's Reign, Volume I")),
            Map.entry(DwarfLoreKey.UNSPOKEN_TUNNELS, new DwarfLoreEntry(DwarfLoreKey.UNSPOKEN_TUNNELS, LoreAge.MODERN, LoreRarity.UNCOMMON, "The Unspoken Tunnels: A Guard Captain’s Memoir, Year 982")),
            Map.entry(DwarfLoreKey.HALL_LANTERNS, new DwarfLoreEntry(DwarfLoreKey.HALL_LANTERNS, LoreAge.MODERN, LoreRarity.UNCOMMON, "Inventory of the Hall of Lanterns, Year 1024")),
            Map.entry(DwarfLoreKey.RITUAL_BEARD_OIL, new DwarfLoreEntry(DwarfLoreKey.RITUAL_BEARD_OIL, LoreAge.MODERN, LoreRarity.UNCOMMON, "On the Use of Beard Oil in Ritual Contexts, Volume X")),
            Map.entry(DwarfLoreKey.ROOF_COLLAPSE_CHRONOLOGY, new DwarfLoreEntry(DwarfLoreKey.ROOF_COLLAPSE_CHRONOLOGY, LoreAge.MODERN, LoreRarity.UNCOMMON, "Chronology of Roof Collapses in Irondeep Sector, Volume IX")),

            Map.entry(DwarfLoreKey.ECHO_CARTOGRAPHY, new DwarfLoreEntry(DwarfLoreKey.ECHO_CARTOGRAPHY, LoreAge.MODERN, LoreRarity.RARE, "Echo-Chamber Cartography: The First Attempts, Year 1251")),
            Map.entry(DwarfLoreKey.GEMLINES_BEARERS, new DwarfLoreEntry(DwarfLoreKey.GEMLINES_BEARERS, LoreAge.MODERN, LoreRarity.RARE, "The Fifteen Gemlines and Their Bearers, Volume I")),
            Map.entry(DwarfLoreKey.STONEGUARD_PROTOCOLS, new DwarfLoreEntry(DwarfLoreKey.STONEGUARD_PROTOCOLS, LoreAge.MODERN, LoreRarity.RARE, "Stoneguard Protocols for Deep Siege Defense, Year 1066")),
            Map.entry(DwarfLoreKey.ARCANIST_BINDING_RITUALS, new DwarfLoreEntry(DwarfLoreKey.ARCANIST_BINDING_RITUALS, LoreAge.MODERN, LoreRarity.RARE, "Rituals of Binding: Arcanist Practices, Volume XIII")),
            Map.entry(DwarfLoreKey.MITHRIL_FORGING, new DwarfLoreEntry(DwarfLoreKey.MITHRIL_FORGING, LoreAge.MODERN, LoreRarity.RARE, "On the Forging of Mithril Alloy, Year 987")),
            Map.entry(DwarfLoreKey.CONTRACT_SEALS, new DwarfLoreEntry(DwarfLoreKey.CONTRACT_SEALS, LoreAge.MODERN, LoreRarity.RARE, "Contract Seals and Binding Ink Formulas, Volume XI")),
            Map.entry(DwarfLoreKey.EMBERGLASS_FIRES, new DwarfLoreEntry(DwarfLoreKey.EMBERGLASS_FIRES, LoreAge.MODERN, LoreRarity.RARE, "Mysteries of the Emberglass Furnace-Fires, Year 1187")),
            Map.entry(DwarfLoreKey.DEEPMARROW_SIGILS, new DwarfLoreEntry(DwarfLoreKey.DEEPMARROW_SIGILS, LoreAge.MODERN, LoreRarity.RARE, "Ancestral Sigils of the Deepmarrow Keepers, Volume XII")),
            Map.entry(DwarfLoreKey.CHAOS_DWARVES_WARNING, new DwarfLoreEntry(DwarfLoreKey.CHAOS_DWARVES_WARNING, LoreAge.MODERN, LoreRarity.RARE, "Chaos Dwarves: A Warning to the Forgeborn, Year 1293")),
            Map.entry(DwarfLoreKey.WOECRYSTAL_RUNES, new DwarfLoreEntry(DwarfLoreKey.WOECRYSTAL_RUNES, LoreAge.MODERN, LoreRarity.RARE, "Runes of Woecrystal and Their Applications, Volume XVI")),
            Map.entry(DwarfLoreKey.LOST_CARAVANS, new DwarfLoreEntry(DwarfLoreKey.LOST_CARAVANS, LoreAge.MODERN, LoreRarity.RARE, "Ledger of Lost Caravans, Volume VI")),

            Map.entry(DwarfLoreKey.BREWERIES_STEWS, new DwarfLoreEntry(DwarfLoreKey.BREWERIES_STEWS, LoreAge.MODERN, LoreRarity.EPIC, "Warden-Blessed Breweries and Sacred Stews, Volume XIV")),
            Map.entry(DwarfLoreKey.STATUE_SPIRIT_BINDING, new DwarfLoreEntry(DwarfLoreKey.STATUE_SPIRIT_BINDING, LoreAge.MODERN, LoreRarity.EPIC, "Spirit-Binding Rites for Guardian Statues, Year 1010")),
            Map.entry(DwarfLoreKey.FURNACE_EXPERIMENTS, new DwarfLoreEntry(DwarfLoreKey.FURNACE_EXPERIMENTS, LoreAge.MODERN, LoreRarity.EPIC, "Experimental Furnace Designs, Year 1303")),
            Map.entry(DwarfLoreKey.SECRET_TRADE_ROUTES, new DwarfLoreEntry(DwarfLoreKey.SECRET_TRADE_ROUTES, LoreAge.MODERN, LoreRarity.EPIC, "Secret Trade Routes of the Westward Expansion, Year 1027")),
            Map.entry(DwarfLoreKey.GIANT_SPORE_BLOOMS, new DwarfLoreEntry(DwarfLoreKey.GIANT_SPORE_BLOOMS, LoreAge.MODERN, LoreRarity.EPIC, "Giant Spore Blooms of the Deeps, Volume V")),
            Map.entry(DwarfLoreKey.THE_LAST_BALROG, new DwarfLoreEntry(DwarfLoreKey.THE_LAST_BALROG, LoreAge.MODERN, LoreRarity.EPIC, "The Last Balrog Sighting, Year 1387")),
            Map.entry(DwarfLoreKey.DEEPFIRE_BALROG, new DwarfLoreEntry(DwarfLoreKey.DEEPFIRE_BALROG, LoreAge.MODERN, LoreRarity.EPIC, "Chronicle of the Deepfire Balrog")),
            Map.entry(DwarfLoreKey.MAGMA_WRITINGS, new DwarfLoreEntry(DwarfLoreKey.MAGMA_WRITINGS, LoreAge.MODERN, LoreRarity.EPIC, "Writings from the Magma Archives, Volume XIX")),
            Map.entry(DwarfLoreKey.STORMCARVED_LEDGE, new DwarfLoreEntry(DwarfLoreKey.STORMCARVED_LEDGE, LoreAge.MODERN, LoreRarity.EPIC, "Chronicle of the Stormcarved Ledge, Year 1502")),
            Map.entry(DwarfLoreKey.ANCIENT_TOMB_KEYS, new DwarfLoreEntry(DwarfLoreKey.ANCIENT_TOMB_KEYS, LoreAge.MODERN, LoreRarity.EPIC, "Keys of the Ancient Tombs, Volume X")),
            Map.entry(DwarfLoreKey.STARFALL_LEDGER, new DwarfLoreEntry(DwarfLoreKey.STARFALL_LEDGER, LoreAge.MODERN, LoreRarity.EPIC, "Ledger of the Starfall Years, Volume XVII")),

            // ------------- ANCIENT -------------

            Map.entry(DwarfLoreKey.KEYSTONE_SHAPES, new DwarfLoreEntry(DwarfLoreKey.KEYSTONE_SHAPES, LoreAge.ANCIENT, LoreRarity.COMMON, "On the Shapes and Placement of Keystones, Age of Foundations")),
            Map.entry(DwarfLoreKey.CISTERN_SEALS, new DwarfLoreEntry(DwarfLoreKey.CISTERN_SEALS, LoreAge.ANCIENT, LoreRarity.COMMON, "Inspections of Cistern Seals, Year 132")),
            Map.entry(DwarfLoreKey.BREW_YIELDS, new DwarfLoreEntry(DwarfLoreKey.BREW_YIELDS, LoreAge.ANCIENT, LoreRarity.COMMON, "Brew Yields and Yeast Logs, Year 91")),
            Map.entry(DwarfLoreKey.BEARD_OILS, new DwarfLoreEntry(DwarfLoreKey.BEARD_OILS, LoreAge.ANCIENT, LoreRarity.COMMON, "Beard Oil Recipes for the Elder Kin, Volume III")),
            Map.entry(DwarfLoreKey.PIPE_ASSEMBLY, new DwarfLoreEntry(DwarfLoreKey.PIPE_ASSEMBLY, LoreAge.ANCIENT, LoreRarity.COMMON, "Pipe Assembly Diagrams of the Early Guild, Volume I")),
            Map.entry(DwarfLoreKey.ANVIL_WEAR, new DwarfLoreEntry(DwarfLoreKey.ANVIL_WEAR, LoreAge.ANCIENT, LoreRarity.COMMON, "Patterns of Anvil Wear and Maintenance, First Forgemasters")),
            Map.entry(DwarfLoreKey.DOWSING_METHODS, new DwarfLoreEntry(DwarfLoreKey.DOWSING_METHODS, LoreAge.ANCIENT, LoreRarity.COMMON, "Practical Dowsing Methods for Water and Ore, Second Era")),
            Map.entry(DwarfLoreKey.HALL_GREETINGS, new DwarfLoreEntry(DwarfLoreKey.HALL_GREETINGS, LoreAge.ANCIENT, LoreRarity.COMMON, "Proper Greetings in the Great Halls, Year 59")),
            Map.entry(DwarfLoreKey.LEDGER_FORMATS, new DwarfLoreEntry(DwarfLoreKey.LEDGER_FORMATS, LoreAge.ANCIENT, LoreRarity.COMMON, "Accepted Formats for Stone Ledger Tablets, Early Record-Keepers")),
            Map.entry(DwarfLoreKey.FUNGAL_COLONY_NOTES, new DwarfLoreEntry(DwarfLoreKey.FUNGAL_COLONY_NOTES, LoreAge.ANCIENT, LoreRarity.COMMON, "Notes on the Great Fungal Colony Collapse, Age of Growth")),
            Map.entry(DwarfLoreKey.SEATING_CHART, new DwarfLoreEntry(DwarfLoreKey.SEATING_CHART, LoreAge.ANCIENT, LoreRarity.COMMON, "Seating Charts for Guild Banquets, Old Calendar")),

            Map.entry(DwarfLoreKey.SPARE_KEYS, new DwarfLoreEntry(DwarfLoreKey.SPARE_KEYS, LoreAge.ANCIENT, LoreRarity.UNCOMMON, "The Making and Keeping of Spare Keys, Generation I")),
            Map.entry(DwarfLoreKey.RUNE_ACCOUNTING, new DwarfLoreEntry(DwarfLoreKey.RUNE_ACCOUNTING, LoreAge.ANCIENT, LoreRarity.UNCOMMON, "Rune-Tallies for Trade Accounting, Year 287")),
            Map.entry(DwarfLoreKey.CISTERN_CLEANING, new DwarfLoreEntry(DwarfLoreKey.CISTERN_CLEANING, LoreAge.ANCIENT, LoreRarity.UNCOMMON, "Cistern Cleaning Practices, Generation IV")),
            Map.entry(DwarfLoreKey.STARSTONE_RUMORS, new DwarfLoreEntry(DwarfLoreKey.STARSTONE_RUMORS, LoreAge.ANCIENT, LoreRarity.UNCOMMON, "Rumors of Starstones Falling, Night of Terrors")),
            Map.entry(DwarfLoreKey.DUST_CONTROL, new DwarfLoreEntry(DwarfLoreKey.DUST_CONTROL, LoreAge.ANCIENT, LoreRarity.UNCOMMON, "Sweeping Schedules for Dust Control, Early Quarters")),
            Map.entry(DwarfLoreKey.LANTERN_MAINTENANCE, new DwarfLoreEntry(DwarfLoreKey.LANTERN_MAINTENANCE, LoreAge.ANCIENT, LoreRarity.UNCOMMON, "Daily Maintenance of Oil Lanterns, Year 60")),
            Map.entry(DwarfLoreKey.CHAMPION_OATHS, new DwarfLoreEntry(DwarfLoreKey.CHAMPION_OATHS, LoreAge.ANCIENT, LoreRarity.UNCOMMON, "The Oaths of the First Champions, Battleborn Era")),
            Map.entry(DwarfLoreKey.RAT_WARNINGS, new DwarfLoreEntry(DwarfLoreKey.RAT_WARNINGS, LoreAge.ANCIENT, LoreRarity.UNCOMMON, "Old Rat Infestation Warnings, Year 22")),
            Map.entry(DwarfLoreKey.BEDROLL_RULES, new DwarfLoreEntry(DwarfLoreKey.BEDROLL_RULES, LoreAge.ANCIENT, LoreRarity.UNCOMMON, "Bedroll Placement Rules for Shared Chambers, Founders’ Years")),
            Map.entry(DwarfLoreKey.ROOT_PRESERVES, new DwarfLoreEntry(DwarfLoreKey.ROOT_PRESERVES, LoreAge.ANCIENT, LoreRarity.UNCOMMON, "Recipes for Preserving Roots and Tubers, First Preservers")),
            Map.entry(DwarfLoreKey.LOST_TOOLS, new DwarfLoreEntry(DwarfLoreKey.LOST_TOOLS, LoreAge.ANCIENT, LoreRarity.UNCOMMON, "The Lost Tools Ledger, Age of Loss")),

            Map.entry(DwarfLoreKey.STONEGUARD_SEATING, new DwarfLoreEntry(DwarfLoreKey.STONEGUARD_SEATING, LoreAge.ANCIENT, LoreRarity.RARE, "Seating Charts for Stoneguard Banquets, Early Stoneguard")),
            Map.entry(DwarfLoreKey.ANIMAL_TOKENS, new DwarfLoreEntry(DwarfLoreKey.ANIMAL_TOKENS, LoreAge.ANCIENT, LoreRarity.RARE, "Tokens Used in Early Kinship Rituals, Rituals Volume I")),
            Map.entry(DwarfLoreKey.STONEGUARD_PACT, new DwarfLoreEntry(DwarfLoreKey.STONEGUARD_PACT, LoreAge.ANCIENT, LoreRarity.RARE, "The Stoneguard Pact, Pact Year")),
            Map.entry(DwarfLoreKey.RUNE_LOCK_DIAGRAMS, new DwarfLoreEntry(DwarfLoreKey.RUNE_LOCK_DIAGRAMS, LoreAge.ANCIENT, LoreRarity.RARE, "Diagrams of Rune-Locked Chests, Keymasters’ Era")),
            Map.entry(DwarfLoreKey.FORGE_OF_MITHRIL, new DwarfLoreEntry(DwarfLoreKey.FORGE_OF_MITHRIL, LoreAge.ANCIENT, LoreRarity.RARE, "The Forging of Mithril, Old Metallurgists")),
            Map.entry(DwarfLoreKey.CONTRACT_SIGNATURES, new DwarfLoreEntry(DwarfLoreKey.CONTRACT_SIGNATURES, LoreAge.ANCIENT, LoreRarity.RARE, "Ledger of Old Contract Signatures, Scribe’s Year")),
            Map.entry(DwarfLoreKey.EMBERGLASS_FORGE_LOGS, new DwarfLoreEntry(DwarfLoreKey.EMBERGLASS_FORGE_LOGS, LoreAge.ANCIENT, LoreRarity.RARE, "Emberglass Forge Logs, First Era")),
            Map.entry(DwarfLoreKey.MEMORY_SHARD_DISCOVERY, new DwarfLoreEntry(DwarfLoreKey.MEMORY_SHARD_DISCOVERY, LoreAge.ANCIENT, LoreRarity.RARE, "Discovery of the Memory Shards, Year 7")),
            Map.entry(DwarfLoreKey.EXILE_RECORDS, new DwarfLoreEntry(DwarfLoreKey.EXILE_RECORDS, LoreAge.ANCIENT, LoreRarity.RARE, "Records of Exiles and Outcasts, Outcast Scrolls")),
            Map.entry(DwarfLoreKey.SPIRIT_ENCOUNTER, new DwarfLoreEntry(DwarfLoreKey.SPIRIT_ENCOUNTER, LoreAge.ANCIENT, LoreRarity.RARE, "An Early Encounter with a Dwarven Spirit, Lost Era")),
            Map.entry(DwarfLoreKey.SEALED_VAULTS, new DwarfLoreEntry(DwarfLoreKey.SEALED_VAULTS, LoreAge.ANCIENT, LoreRarity.RARE, "Account of the Sealed Vaults of Hraga, Vault Year")),

            Map.entry(DwarfLoreKey.ROOT_STORAGE, new DwarfLoreEntry(DwarfLoreKey.ROOT_STORAGE, LoreAge.ANCIENT, LoreRarity.EPIC, "On the Storage of Root Vegetables in Deep Cellars, First Storage")),
            Map.entry(DwarfLoreKey.DAWN_HALL_RELICS, new DwarfLoreEntry(DwarfLoreKey.DAWN_HALL_RELICS, LoreAge.ANCIENT, LoreRarity.EPIC, "Relics of the Dawn Hall, Dawn Era")),
            Map.entry(DwarfLoreKey.PRIMEVAL_IRONWORKS, new DwarfLoreEntry(DwarfLoreKey.PRIMEVAL_IRONWORKS, LoreAge.ANCIENT, LoreRarity.EPIC, "Primeval Ironworks of the Deep, Era of Makers")),
            Map.entry(DwarfLoreKey.STARFORGED_HELM, new DwarfLoreEntry(DwarfLoreKey.STARFORGED_HELM, LoreAge.ANCIENT, LoreRarity.EPIC, "Discovery of the Starforged Helm, Night of Comets")),
            Map.entry(DwarfLoreKey.FIRST_EMBERGLASS, new DwarfLoreEntry(DwarfLoreKey.FIRST_EMBERGLASS, LoreAge.ANCIENT, LoreRarity.EPIC, "The First Emberglass Crucible, Age of Flames")),
            Map.entry(DwarfLoreKey.BINDING_OF_THE_BALROG, new DwarfLoreEntry(DwarfLoreKey.BINDING_OF_THE_BALROG, LoreAge.ANCIENT, LoreRarity.EPIC, "The Binding of the Deepfire Balrog")),
            Map.entry(DwarfLoreKey.ETERNAL_EMBER, new DwarfLoreEntry(DwarfLoreKey.ETERNAL_EMBER, LoreAge.ANCIENT, LoreRarity.EPIC, "Eternal Ember of the Ancients, Cycle XX")),
            Map.entry(DwarfLoreKey.ORACLE_INSCRIPTIONS, new DwarfLoreEntry(DwarfLoreKey.ORACLE_INSCRIPTIONS, LoreAge.ANCIENT, LoreRarity.EPIC, "Oracle Inscriptions of the Crystal Vault, Volume VII")),
            Map.entry(DwarfLoreKey.DEEP_CURSE_TABLET, new DwarfLoreEntry(DwarfLoreKey.DEEP_CURSE_TABLET, LoreAge.ANCIENT, LoreRarity.EPIC, "Tablet of the Deep Curse, Age of Shadows")),
            Map.entry(DwarfLoreKey.SUNKEN_FORGE_RITES, new DwarfLoreEntry(DwarfLoreKey.SUNKEN_FORGE_RITES, LoreAge.ANCIENT, LoreRarity.EPIC, "Rites of the Sunken Forge, Lost Age")),
            Map.entry(DwarfLoreKey.CAVERN_LIGHT_CHRONICLE, new DwarfLoreEntry(DwarfLoreKey.CAVERN_LIGHT_CHRONICLE, LoreAge.ANCIENT, LoreRarity.EPIC, "Chronicle of Cavern Light, Dawn Cycle")),

            Map.entry(DwarfLoreKey.MITHRIL_FORGE_TECHNIQUE, new DwarfLoreEntry(DwarfLoreKey.MITHRIL_FORGE_TECHNIQUE, LoreAge.ANCIENT, LoreRarity.LEGENDARY, "Mithril Forging Technique, Forge of the First Flame")),
            Map.entry(DwarfLoreKey.ANCIENT_GEMCRAFT, new DwarfLoreEntry(DwarfLoreKey.ANCIENT_GEMCRAFT, LoreAge.ANCIENT, LoreRarity.LEGENDARY, "The Art of Gemcutting, Archives of Karaz-Un")),
            Map.entry(DwarfLoreKey.FORGOTTEN_BREW_FORMULAS, new DwarfLoreEntry(DwarfLoreKey.FORGOTTEN_BREW_FORMULAS, LoreAge.ANCIENT, LoreRarity.LEGENDARY, "Formulas of Dwarven Brews, Vaults of Stonehearth")),
            Map.entry(DwarfLoreKey.COIN_PRESS_MANUAL, new DwarfLoreEntry(DwarfLoreKey.COIN_PRESS_MANUAL, LoreAge.ANCIENT, LoreRarity.LEGENDARY, "Coin Press Manual, Bank of Barak-Zul")),
            Map.entry(DwarfLoreKey.ALCHEMY_RECIPES, new DwarfLoreEntry(DwarfLoreKey.ALCHEMY_RECIPES, LoreAge.ANCIENT, LoreRarity.LEGENDARY, "Codex Alchemica, Transcribed by the Final Thaumaturge"))
    );
}
