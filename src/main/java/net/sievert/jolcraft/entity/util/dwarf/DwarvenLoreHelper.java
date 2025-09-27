package net.sievert.jolcraft.entity.util.dwarf;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;

import java.util.*;
import java.util.stream.Collectors;

public class DwarvenLoreHelper {

    public enum LoreRarity { COMMON, UNCOMMON, RARE, EPIC, LEGENDARY }

    public record DwarvenLoreEntry(String key, Component text, LoreRarity rarity) {}

    private static final Map<String, DwarvenLoreEntry> MODERN_ENTRIES = Map.ofEntries(
            // 11 COMMON
            Map.entry("tunnel_stability", entry("tunnel_stability", "A Survey of Tunnel Stability in Soft Granite, Volume II", LoreRarity.COMMON)),
            Map.entry("barrel_sealing", entry("barrel_sealing", "Proper Barrel Sealing Techniques, Volume I", LoreRarity.COMMON)),
            Map.entry("turnip_yields", entry("turnip_yields", "A Record of Turnip Yields, Year 538", LoreRarity.COMMON)),
            Map.entry("beard_grooming", entry("beard_grooming", "The Art of Beard Grooming: A Beginner's Guide, 4th Edition", LoreRarity.COMMON)),
            Map.entry("minecart_wheels", entry("minecart_wheels", "Catalog of Minecart Wheel Failures, Volume VII", LoreRarity.COMMON)),
            Map.entry("furnace_temperatures", entry("furnace_temperatures", "Furnace Temperatures and You, Revised 987", LoreRarity.COMMON)),
            Map.entry("pipeworks_karram_dun", entry("pipeworks_karram_dun", "The Pipeworks of Lower Karram-Dûn, Year 1112", LoreRarity.COMMON)),
            Map.entry("forge_etiquette", entry("forge_etiquette", "Basic Forge Etiquette for Apprentices, Volume IV", LoreRarity.COMMON)),
            Map.entry("ledgers", entry("ledgers", "Ledgers and Ledgers: On the Keeping of Ledgers, Volume IX", LoreRarity.COMMON)),
            Map.entry("fungus_upper_caverns", entry("fungus_upper_caverns", "Common Fungus of the Upper Caverns, Survey of 1014", LoreRarity.COMMON)),
            Map.entry("echo_patterns", entry("echo_patterns", "Observations on Echo Patterns in Vaulted Halls, Volume II", LoreRarity.COMMON)),
            // 11 UNCOMMON
            Map.entry("chiseled_deepslate", entry("chiseled_deepslate", "Properties of Chiseled Deepslate, Volume VI", LoreRarity.UNCOMMON)),
            Map.entry("forge_marks", entry("forge_marks", "Ancestral Forge Marks and Their Variants, Volume III", LoreRarity.UNCOMMON)),
            Map.entry("aqueduct_collapse", entry("aqueduct_collapse", "Survey of the Northern Aqueduct Collapse, Year 1198", LoreRarity.UNCOMMON)),
            Map.entry("gem_vein_lunar_cycle", entry("gem_vein_lunar_cycle", "Gem Vein Activity by Lunar Cycle, Volume VIII", LoreRarity.UNCOMMON)),
            Map.entry("mold_id_containment", entry("mold_id_containment", "Subterranean Mold: Identification & Containment, Year 1243", LoreRarity.UNCOMMON)),
            Map.entry("whispers_old_pillars", entry("whispers_old_pillars", "Whispers Among the Old Pillars, Volume V", LoreRarity.UNCOMMON)),
            Map.entry("queen_hraga", entry("queen_hraga", "A Disputed Account of Forge-Queen Hraga's Reign, Volume I", LoreRarity.UNCOMMON)),
            Map.entry("unspoken_tunnels", entry("unspoken_tunnels", "The Unspoken Tunnels: A Guard Captain’s Memoir, Year 982", LoreRarity.UNCOMMON)),
            Map.entry("hall_lanterns", entry("hall_lanterns", "Inventory of the Hall of Lanterns, Year 1024", LoreRarity.UNCOMMON)),
            Map.entry("ritual_beard_oil", entry("ritual_beard_oil", "On the Use of Beard Oil in Ritual Contexts, Volume X", LoreRarity.UNCOMMON)),
            Map.entry("roof_collapse_chronology", entry("roof_collapse_chronology", "Chronology of Roof Collapses in Irondeep Sector, Volume IX", LoreRarity.UNCOMMON)),
            // 11 RARE
            Map.entry("echo_cartography", entry("echo_cartography", "Echo-Chamber Cartography: The First Attempts, Year 1251", LoreRarity.RARE)),
            Map.entry("gemlines_bearers", entry("gemlines_bearers", "The Fifteen Gemlines and Their Bearers, Volume I", LoreRarity.RARE)),
            Map.entry("stoneguard_protocols", entry("stoneguard_protocols", "Stoneguard Protocols for Deep Siege Defense, Year 1066", LoreRarity.RARE)),
            Map.entry("arcanist_binding_rituals", entry("arcanist_binding_rituals", "Rituals of Binding: Arcanist Practices, Volume XIII", LoreRarity.RARE)),
            Map.entry("mithril_forging", entry("mithril_forging", "On the Forging of Mithril Alloy, Year 987", LoreRarity.RARE)),
            Map.entry("contract_seals", entry("contract_seals", "Contract Seals and Binding Ink Formulas, Volume XI", LoreRarity.RARE)),
            Map.entry("emberglass_fires", entry("emberglass_fires", "Mysteries of the Emberglass Furnace-Fires, Year 1187", LoreRarity.RARE)),
            Map.entry("deepmarrow_sigils", entry("deepmarrow_sigils", "Ancestral Sigils of the Deepmarrow Keepers, Volume XII", LoreRarity.RARE)),
            Map.entry("chaos_dwarves_warning", entry("chaos_dwarves_warning", "Chaos Dwarves: A Warning to the Forgeborn, Year 1293", LoreRarity.RARE)),
            Map.entry("woecrystal_runes", entry("woecrystal_runes", "Runes of Woecrystal and Their Applications, Volume XVI", LoreRarity.RARE)),
            Map.entry("lost_caravans", entry("lost_caravans", "Ledger of Lost Caravans, Volume VI", LoreRarity.RARE)),
            // 11 EPIC
            Map.entry("breweries_stews", entry("breweries_stews", "Warden-Blessed Breweries and Sacred Stews, Volume XIV", LoreRarity.EPIC)),
            Map.entry("statue_spirit_binding", entry("statue_spirit_binding", "Spirit-Binding Rites for Guardian Statues, Year 1010", LoreRarity.EPIC)),
            Map.entry("furnace_experiments", entry("furnace_experiments", "Experimental Furnace Designs, Year 1303", LoreRarity.EPIC)),
            Map.entry("secret_trade_routes", entry("secret_trade_routes", "Secret Trade Routes of the Westward Expansion, Year 1027", LoreRarity.EPIC)),
            Map.entry("giant_spore_blooms", entry("giant_spore_blooms", "Giant Spore Blooms of the Deeps, Volume V", LoreRarity.EPIC)),
            Map.entry("the_last_balrog", entry("the_last_balrog", "The Last Balrog Sighting, Year 1387", LoreRarity.EPIC)),
            Map.entry("deepfire_balrog", entry("deepfire_balrog", "Chronicle of the Deepfire Balrog", LoreRarity.EPIC)),
            Map.entry("magma_writings", entry("magma_writings", "Writings from the Magma Archives, Volume XIX", LoreRarity.EPIC)),
            Map.entry("stormcarved_ledge", entry("stormcarved_ledge", "Chronicle of the Stormcarved Ledge, Year 1502", LoreRarity.EPIC)),
            Map.entry("ancient_tomb_keys", entry("ancient_tomb_keys", "Keys of the Ancient Tombs, Volume X", LoreRarity.EPIC)),
            Map.entry("starfall_ledger", entry("starfall_ledger", "Ledger of the Starfall Years, Volume XVII", LoreRarity.EPIC))
            //LEGENDARY

    );

    private static final Map<String, DwarvenLoreEntry> ANCIENT_ENTRIES = Map.ofEntries(
            // 11 COMMON
            Map.entry("keystone_shapes", entry("keystone_shapes", "On the Shapes and Placement of Keystones, Age of Foundations", LoreRarity.COMMON)),
            Map.entry("cistern_seals", entry("cistern_seals", "Inspections of Cistern Seals, Year 132", LoreRarity.COMMON)),
            Map.entry("brew_yields", entry("brew_yields", "Brew Yields and Yeast Logs, Year 91", LoreRarity.COMMON)),
            Map.entry("beard_oils", entry("beard_oils", "Beard Oil Recipes for the Elder Kin, Volume III", LoreRarity.COMMON)),
            Map.entry("pipe_assembly", entry("pipe_assembly", "Pipe Assembly Diagrams of the Early Guild, Volume I", LoreRarity.COMMON)),
            Map.entry("anvil_wear", entry("anvil_wear", "Patterns of Anvil Wear and Maintenance, First Forgemasters", LoreRarity.COMMON)),
            Map.entry("dowsing_methods", entry("dowsing_methods", "Practical Dowsing Methods for Water and Ore, Second Era", LoreRarity.COMMON)),
            Map.entry("hall_greetings", entry("hall_greetings", "Proper Greetings in the Great Halls, Year 59", LoreRarity.COMMON)),
            Map.entry("ledger_formats", entry("ledger_formats", "Accepted Formats for Stone Ledger Tablets, Early Record-Keepers", LoreRarity.COMMON)),
            Map.entry("fungal_colony_notes", entry("fungal_colony_notes", "Notes on the Great Fungal Colony Collapse, Age of Growth", LoreRarity.COMMON)),
            Map.entry("seating_chart", entry("seating_chart", "Seating Charts for Guild Banquets, Old Calendar", LoreRarity.COMMON)),
            // 11 UNCOMMON
            Map.entry("spare_keys", entry("spare_keys", "The Making and Keeping of Spare Keys, Generation I", LoreRarity.UNCOMMON)),
            Map.entry("rune_accounting", entry("rune_accounting", "Rune-Tallies for Trade Accounting, Year 287", LoreRarity.UNCOMMON)),
            Map.entry("cistern_cleaning", entry("cistern_cleaning", "Cistern Cleaning Practices, Generation IV", LoreRarity.UNCOMMON)),
            Map.entry("starstone_rumors", entry("starstone_rumors", "Rumors of Starstones Falling, Night of Terrors", LoreRarity.UNCOMMON)),
            Map.entry("dust_control", entry("dust_control", "Sweeping Schedules for Dust Control, Early Quarters", LoreRarity.UNCOMMON)),
            Map.entry("lantern_maintenance", entry("lantern_maintenance", "Daily Maintenance of Oil Lanterns, Year 60", LoreRarity.UNCOMMON)),
            Map.entry("champion_oaths", entry("champion_oaths", "The Oaths of the First Champions, Battleborn Era", LoreRarity.UNCOMMON)),
            Map.entry("rat_warnings", entry("rat_warnings", "Old Rat Infestation Warnings, Year 22", LoreRarity.UNCOMMON)),
            Map.entry("bedroll_rules", entry("bedroll_rules", "Bedroll Placement Rules for Shared Chambers, Founders’ Years", LoreRarity.UNCOMMON)),
            Map.entry("root_preserves", entry("root_preserves", "Recipes for Preserving Roots and Tubers, First Preservers", LoreRarity.UNCOMMON)),
            Map.entry("lost_tools", entry("lost_tools", "The Lost Tools Ledger, Age of Loss", LoreRarity.UNCOMMON)),
            // 11 RARE
            Map.entry("stoneguard_seating", entry("stoneguard_seating", "Seating Charts for Stoneguard Banquets, Early Stoneguard", LoreRarity.RARE)),
            Map.entry("animal_tokens", entry("animal_tokens", "Tokens Used in Early Kinship Rituals, Rituals Volume I", LoreRarity.RARE)),
            Map.entry("stoneguard_pact", entry("stoneguard_pact", "The Stoneguard Pact, Pact Year", LoreRarity.RARE)),
            Map.entry("rune_lock_diagrams", entry("rune_lock_diagrams", "Diagrams of Rune-Locked Chests, Keymasters’ Era", LoreRarity.RARE)),
            Map.entry("forge_of_mithril", entry("forge_of_mithril", "The Forging of Mithril, Old Metallurgists", LoreRarity.RARE)),
            Map.entry("contract_signatures", entry("contract_signatures", "Ledger of Old Contract Signatures, Scribe’s Year", LoreRarity.RARE)),
            Map.entry("emberglass_forge_logs", entry("emberglass_forge_logs", "Emberglass Forge Logs, First Era", LoreRarity.RARE)),
            Map.entry("memory_shard_discovery", entry("memory_shard_discovery", "Discovery of the Memory Shards, Year 7", LoreRarity.RARE)),
            Map.entry("exile_records", entry("exile_records", "Records of Exiles and Outcasts, Outcast Scrolls", LoreRarity.RARE)),
            Map.entry("spirit_encounter", entry("spirit_encounter", "An Early Encounter with a Dwarven Spirit, Lost Era", LoreRarity.RARE)),
            Map.entry("sealed_vaults", entry("sealed_vaults", "Account of the Sealed Vaults of Hraga, Vault Year", LoreRarity.RARE)),
            // 11 EPIC
            Map.entry("root_storage", entry("root_storage", "On the Storage of Root Vegetables in Deep Cellars, First Storage", LoreRarity.EPIC)),
            Map.entry("dawn_hall_relics", entry("dawn_hall_relics", "Relics of the Dawn Hall, Dawn Era", LoreRarity.EPIC)),
            Map.entry("primeval_ironworks", entry("primeval_ironworks", "Primeval Ironworks of the Deep, Era of Makers", LoreRarity.EPIC)),
            Map.entry("starforged_helm", entry("starforged_helm", "Discovery of the Starforged Helm, Night of Comets", LoreRarity.EPIC)),
            Map.entry("first_emberglass", entry("first_emberglass", "The First Emberglass Crucible, Age of Flames", LoreRarity.EPIC)),
            Map.entry("binding_of_the_balrog", entry("binding_of_the_balrog", "The Binding of the Deepfire Balrog", LoreRarity.EPIC)),
            Map.entry("eternal_ember", entry("eternal_ember", "Eternal Ember of the Ancients, Cycle XX", LoreRarity.EPIC)),
            Map.entry("oracle_inscriptions", entry("oracle_inscriptions", "Oracle Inscriptions of the Crystal Vault, Volume VII", LoreRarity.EPIC)),
            Map.entry("deep_curse_tablet", entry("deep_curse_tablet", "Tablet of the Deep Curse, Age of Shadows", LoreRarity.EPIC)),
            Map.entry("sunken_forge_rites", entry("sunken_forge_rites", "Rites of the Sunken Forge, Lost Age", LoreRarity.EPIC)),
            Map.entry("cavern_light_chronicle", entry("cavern_light_chronicle", "Chronicle of Cavern Light, Dawn Cycle", LoreRarity.EPIC)),
            //LEGENDARY
            Map.entry(
                    "mithril_forge_technique",
                    entry(
                            "mithril_forge_technique",
                            "Mithril Forging Technique, Forge of the First Flame",
                            LoreRarity.LEGENDARY
                    )
            ),
            Map.entry(
                    "ancient_gemcraft",
                    entry(
                            "ancient_gemcraft",
                            "The Art of Gemcutting, Archives of Karaz-Un",
                            LoreRarity.LEGENDARY
                    )
            ),
            Map.entry(
                    "forgotten_brew_formulas",
                    entry(
                            "forgotten_brew_formulas",
                            "Formulas of Dwarven Brews, Vaults of Stonehearth",
                            LoreRarity.LEGENDARY
                    )
            ),
            Map.entry(
                    "coin_press_manual",
                    entry(
                            "coin_press_manual",
                            "Coin Press Manual, Bank of Barak-Zul",
                            LoreRarity.LEGENDARY
                    )
            )


    );

    private static DwarvenLoreEntry entry(String key, String text, LoreRarity rarity) {
        return new DwarvenLoreEntry(key, Component.literal(text).withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC), rarity);
    }

    public static String getRandomKeyWeighted(RandomSource rng, boolean ancient) {
        Map<LoreRarity, Integer> weights = Map.of(
                LoreRarity.COMMON, 8,
                LoreRarity.UNCOMMON, 4,
                LoreRarity.RARE, 2,
                LoreRarity.EPIC, 1
        );
        Collection<DwarvenLoreEntry> entries = (ancient ? ANCIENT_ENTRIES : MODERN_ENTRIES).values();
        List<String> weightedPool = new ArrayList<>();
        for (var entry : entries) {
            int weight = weights.getOrDefault(entry.rarity(), 1);
            for (int i = 0; i < weight; ++i) weightedPool.add(entry.key());
        }
        if (weightedPool.isEmpty()) return "";
        return weightedPool.get(rng.nextInt(weightedPool.size()));
    }

    public static String getRandomLegendaryKey(RandomSource rng) {
        return getRandomKeyByRarity(LoreRarity.LEGENDARY, rng, true);
    }

    public static Set<String> getLegendaryKeys() {
        return ANCIENT_ENTRIES.entrySet().stream()
                .filter(e -> e.getValue().rarity() == LoreRarity.LEGENDARY)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());
    }


    public static String getRandomKeyByRarity(LoreRarity rarity, RandomSource rng, boolean ancient) {
        Map<String, DwarvenLoreEntry> pool = ancient ? ANCIENT_ENTRIES : MODERN_ENTRIES;
        List<String> keys = pool.values().stream()
                .filter(entry -> entry.rarity() == rarity)
                .map(DwarvenLoreEntry::key)
                .toList();
        if (keys.isEmpty()) return "";
        return keys.get(rng.nextInt(keys.size()));
    }

    public static DwarvenLoreEntry get(String key, boolean ancient) {
        return (ancient ? ANCIENT_ENTRIES : MODERN_ENTRIES).get(key);
    }

    public static LoreRarity getRarity(String key, boolean ancient) {
        DwarvenLoreEntry entry = get(key, ancient);
        return entry != null ? entry.rarity() : LoreRarity.COMMON;
    }

    public static Component getText(String key, boolean ancient) {
        DwarvenLoreEntry entry = get(key, ancient);
        return entry != null ? entry.text() : Component.literal("Unknown Lore").withStyle(ChatFormatting.GRAY, ChatFormatting.ITALIC);
    }

    public static Optional<DwarvenLoreEntry> getCrossReference(int index, boolean getAncient) {
        List<DwarvenLoreEntry> modernList = MODERN_ENTRIES.values().stream().toList();
        List<DwarvenLoreEntry> ancientList = ANCIENT_ENTRIES.values().stream().toList();
        if (getAncient && index < ancientList.size()) return Optional.of(ancientList.get(index));
        if (!getAncient && index < modernList.size()) return Optional.of(modernList.get(index));
        return Optional.empty();
    }
}
