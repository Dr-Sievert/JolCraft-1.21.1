package net.sievert.jolcraft.world.item.lore.dwarf;

import net.minecraft.world.item.Rarity;
import net.sievert.jolcraft.world.item.lore.LoreAge;
import net.sievert.jolcraft.data.JolCraftEnumExtensions;

import java.util.Map;

public final class DwarfLoreEntries {
    private DwarfLoreEntries() {}

    public static final Map<DwarfLoreKey, DwarfLoreEntry> ALL = Map.<DwarfLoreKey, DwarfLoreEntry>ofEntries(
            // ------------- MODERN -------------
            Map.entry(DwarfLoreKey.TUNNEL_STABILITY,         new DwarfLoreEntry(DwarfLoreKey.TUNNEL_STABILITY,         LoreAge.MODERN, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.BARREL_SEALING,           new DwarfLoreEntry(DwarfLoreKey.BARREL_SEALING,           LoreAge.MODERN, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.TURNIP_YIELDS,            new DwarfLoreEntry(DwarfLoreKey.TURNIP_YIELDS,            LoreAge.MODERN, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.BEARD_GROOMING,           new DwarfLoreEntry(DwarfLoreKey.BEARD_GROOMING,           LoreAge.MODERN, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.MINECART_WHEELS,          new DwarfLoreEntry(DwarfLoreKey.MINECART_WHEELS,          LoreAge.MODERN, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.FURNACE_TEMPERATURES,     new DwarfLoreEntry(DwarfLoreKey.FURNACE_TEMPERATURES,     LoreAge.MODERN, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.PIPEWORKS_KARRAM_DUN,     new DwarfLoreEntry(DwarfLoreKey.PIPEWORKS_KARRAM_DUN,     LoreAge.MODERN, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.FORGE_ETIQUETTE,          new DwarfLoreEntry(DwarfLoreKey.FORGE_ETIQUETTE,          LoreAge.MODERN, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.LEDGERS,                  new DwarfLoreEntry(DwarfLoreKey.LEDGERS,                  LoreAge.MODERN, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.FUNGUS_UPPER_CAVERNS,     new DwarfLoreEntry(DwarfLoreKey.FUNGUS_UPPER_CAVERNS,     LoreAge.MODERN, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.ECHO_PATTERNS,            new DwarfLoreEntry(DwarfLoreKey.ECHO_PATTERNS,            LoreAge.MODERN, Rarity.COMMON)),

            Map.entry(DwarfLoreKey.CHISELED_DEEPSLATE,       new DwarfLoreEntry(DwarfLoreKey.CHISELED_DEEPSLATE,       LoreAge.MODERN, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.FORGE_MARKS,              new DwarfLoreEntry(DwarfLoreKey.FORGE_MARKS,              LoreAge.MODERN, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.AQUEDUCT_COLLAPSE,        new DwarfLoreEntry(DwarfLoreKey.AQUEDUCT_COLLAPSE,        LoreAge.MODERN, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.GEM_VEIN_LUNAR_CYCLE,     new DwarfLoreEntry(DwarfLoreKey.GEM_VEIN_LUNAR_CYCLE,     LoreAge.MODERN, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.MOLD_ID_CONTAINMENT,      new DwarfLoreEntry(DwarfLoreKey.MOLD_ID_CONTAINMENT,      LoreAge.MODERN, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.WHISPERS_OLD_PILLARS,     new DwarfLoreEntry(DwarfLoreKey.WHISPERS_OLD_PILLARS,     LoreAge.MODERN, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.QUEEN_HRAGA,              new DwarfLoreEntry(DwarfLoreKey.QUEEN_HRAGA,              LoreAge.MODERN, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.UNSPOKEN_TUNNELS,         new DwarfLoreEntry(DwarfLoreKey.UNSPOKEN_TUNNELS,         LoreAge.MODERN, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.HALL_LANTERNS,            new DwarfLoreEntry(DwarfLoreKey.HALL_LANTERNS,            LoreAge.MODERN, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.RITUAL_BEARD_OIL,         new DwarfLoreEntry(DwarfLoreKey.RITUAL_BEARD_OIL,         LoreAge.MODERN, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.ROOF_COLLAPSE_CHRONOLOGY, new DwarfLoreEntry(DwarfLoreKey.ROOF_COLLAPSE_CHRONOLOGY, LoreAge.MODERN, Rarity.UNCOMMON)),

            Map.entry(DwarfLoreKey.ECHO_CARTOGRAPHY,         new DwarfLoreEntry(DwarfLoreKey.ECHO_CARTOGRAPHY,         LoreAge.MODERN, Rarity.RARE)),
            Map.entry(DwarfLoreKey.GEMLINES_BEARERS,         new DwarfLoreEntry(DwarfLoreKey.GEMLINES_BEARERS,         LoreAge.MODERN, Rarity.RARE)),
            Map.entry(DwarfLoreKey.STONEGUARD_PROTOCOLS,     new DwarfLoreEntry(DwarfLoreKey.STONEGUARD_PROTOCOLS,     LoreAge.MODERN, Rarity.RARE)),
            Map.entry(DwarfLoreKey.ARCANIST_BINDING_RITUALS, new DwarfLoreEntry(DwarfLoreKey.ARCANIST_BINDING_RITUALS, LoreAge.MODERN, Rarity.RARE)),
            Map.entry(DwarfLoreKey.MITHRIL_FORGING,          new DwarfLoreEntry(DwarfLoreKey.MITHRIL_FORGING,          LoreAge.MODERN, Rarity.RARE)),
            Map.entry(DwarfLoreKey.CONTRACT_SEALS,           new DwarfLoreEntry(DwarfLoreKey.CONTRACT_SEALS,           LoreAge.MODERN, Rarity.RARE)),
            Map.entry(DwarfLoreKey.EMBERGLASS_FIRES,         new DwarfLoreEntry(DwarfLoreKey.EMBERGLASS_FIRES,         LoreAge.MODERN, Rarity.RARE)),
            Map.entry(DwarfLoreKey.DEEPMARROW_SIGILS,        new DwarfLoreEntry(DwarfLoreKey.DEEPMARROW_SIGILS,        LoreAge.MODERN, Rarity.RARE)),
            Map.entry(DwarfLoreKey.CHAOS_DWARVES_WARNING,    new DwarfLoreEntry(DwarfLoreKey.CHAOS_DWARVES_WARNING,    LoreAge.MODERN, Rarity.RARE)),
            Map.entry(DwarfLoreKey.WOECRYSTAL_RUNES,         new DwarfLoreEntry(DwarfLoreKey.WOECRYSTAL_RUNES,         LoreAge.MODERN, Rarity.RARE)),
            Map.entry(DwarfLoreKey.LOST_CARAVANS,            new DwarfLoreEntry(DwarfLoreKey.LOST_CARAVANS,            LoreAge.MODERN, Rarity.RARE)),

            Map.entry(DwarfLoreKey.BREWERIES_STEWS,          new DwarfLoreEntry(DwarfLoreKey.BREWERIES_STEWS,          LoreAge.MODERN, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.STATUE_SPIRIT_BINDING,    new DwarfLoreEntry(DwarfLoreKey.STATUE_SPIRIT_BINDING,    LoreAge.MODERN, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.FURNACE_EXPERIMENTS,      new DwarfLoreEntry(DwarfLoreKey.FURNACE_EXPERIMENTS,      LoreAge.MODERN, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.SECRET_TRADE_ROUTES,      new DwarfLoreEntry(DwarfLoreKey.SECRET_TRADE_ROUTES,      LoreAge.MODERN, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.GIANT_SPORE_BLOOMS,       new DwarfLoreEntry(DwarfLoreKey.GIANT_SPORE_BLOOMS,       LoreAge.MODERN, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.THE_LAST_BALROG,          new DwarfLoreEntry(DwarfLoreKey.THE_LAST_BALROG,          LoreAge.MODERN, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.DEEPFIRE_BALROG,          new DwarfLoreEntry(DwarfLoreKey.DEEPFIRE_BALROG,          LoreAge.MODERN, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.MAGMA_WRITINGS,           new DwarfLoreEntry(DwarfLoreKey.MAGMA_WRITINGS,           LoreAge.MODERN, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.STORMCARVED_LEDGE,        new DwarfLoreEntry(DwarfLoreKey.STORMCARVED_LEDGE,        LoreAge.MODERN, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.ANCIENT_TOMB_KEYS,        new DwarfLoreEntry(DwarfLoreKey.ANCIENT_TOMB_KEYS,        LoreAge.MODERN, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.STARFALL_LEDGER,          new DwarfLoreEntry(DwarfLoreKey.STARFALL_LEDGER,          LoreAge.MODERN, Rarity.EPIC)),

            // ------------- ANCIENT -------------
            Map.entry(DwarfLoreKey.KEYSTONE_SHAPES,          new DwarfLoreEntry(DwarfLoreKey.KEYSTONE_SHAPES,          LoreAge.ANCIENT, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.CISTERN_SEALS,            new DwarfLoreEntry(DwarfLoreKey.CISTERN_SEALS,            LoreAge.ANCIENT, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.BREW_YIELDS,              new DwarfLoreEntry(DwarfLoreKey.BREW_YIELDS,              LoreAge.ANCIENT, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.BEARD_OILS,               new DwarfLoreEntry(DwarfLoreKey.BEARD_OILS,               LoreAge.ANCIENT, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.PIPE_ASSEMBLY,            new DwarfLoreEntry(DwarfLoreKey.PIPE_ASSEMBLY,            LoreAge.ANCIENT, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.ANVIL_WEAR,               new DwarfLoreEntry(DwarfLoreKey.ANVIL_WEAR,               LoreAge.ANCIENT, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.DOWSING_METHODS,          new DwarfLoreEntry(DwarfLoreKey.DOWSING_METHODS,          LoreAge.ANCIENT, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.HALL_GREETINGS,           new DwarfLoreEntry(DwarfLoreKey.HALL_GREETINGS,           LoreAge.ANCIENT, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.LEDGER_FORMATS,           new DwarfLoreEntry(DwarfLoreKey.LEDGER_FORMATS,           LoreAge.ANCIENT, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.FUNGAL_COLONY_NOTES,      new DwarfLoreEntry(DwarfLoreKey.FUNGAL_COLONY_NOTES,      LoreAge.ANCIENT, Rarity.COMMON)),
            Map.entry(DwarfLoreKey.SEATING_CHART,            new DwarfLoreEntry(DwarfLoreKey.SEATING_CHART,            LoreAge.ANCIENT, Rarity.COMMON)),

            Map.entry(DwarfLoreKey.SPARE_KEYS,               new DwarfLoreEntry(DwarfLoreKey.SPARE_KEYS,               LoreAge.ANCIENT, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.RUNE_ACCOUNTING,          new DwarfLoreEntry(DwarfLoreKey.RUNE_ACCOUNTING,          LoreAge.ANCIENT, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.CISTERN_CLEANING,         new DwarfLoreEntry(DwarfLoreKey.CISTERN_CLEANING,         LoreAge.ANCIENT, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.STARSTONE_RUMORS,         new DwarfLoreEntry(DwarfLoreKey.STARSTONE_RUMORS,         LoreAge.ANCIENT, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.DUST_CONTROL,             new DwarfLoreEntry(DwarfLoreKey.DUST_CONTROL,             LoreAge.ANCIENT, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.LANTERN_MAINTENANCE,      new DwarfLoreEntry(DwarfLoreKey.LANTERN_MAINTENANCE,      LoreAge.ANCIENT, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.CHAMPION_OATHS,           new DwarfLoreEntry(DwarfLoreKey.CHAMPION_OATHS,           LoreAge.ANCIENT, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.RAT_WARNINGS,             new DwarfLoreEntry(DwarfLoreKey.RAT_WARNINGS,             LoreAge.ANCIENT, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.BEDROLL_RULES,            new DwarfLoreEntry(DwarfLoreKey.BEDROLL_RULES,            LoreAge.ANCIENT, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.ROOT_PRESERVES,           new DwarfLoreEntry(DwarfLoreKey.ROOT_PRESERVES,           LoreAge.ANCIENT, Rarity.UNCOMMON)),
            Map.entry(DwarfLoreKey.LOST_TOOLS,               new DwarfLoreEntry(DwarfLoreKey.LOST_TOOLS,               LoreAge.ANCIENT, Rarity.UNCOMMON)),

            Map.entry(DwarfLoreKey.STONEGUARD_SEATING,       new DwarfLoreEntry(DwarfLoreKey.STONEGUARD_SEATING,       LoreAge.ANCIENT, Rarity.RARE)),
            Map.entry(DwarfLoreKey.ANIMAL_TOKENS,            new DwarfLoreEntry(DwarfLoreKey.ANIMAL_TOKENS,            LoreAge.ANCIENT, Rarity.RARE)),
            Map.entry(DwarfLoreKey.STONEGUARD_PACT,          new DwarfLoreEntry(DwarfLoreKey.STONEGUARD_PACT,          LoreAge.ANCIENT, Rarity.RARE)),
            Map.entry(DwarfLoreKey.RUNE_LOCK_DIAGRAMS,       new DwarfLoreEntry(DwarfLoreKey.RUNE_LOCK_DIAGRAMS,       LoreAge.ANCIENT, Rarity.RARE)),
            Map.entry(DwarfLoreKey.FORGE_OF_MITHRIL,         new DwarfLoreEntry(DwarfLoreKey.FORGE_OF_MITHRIL,         LoreAge.ANCIENT, Rarity.RARE)),
            Map.entry(DwarfLoreKey.CONTRACT_SIGNATURES,      new DwarfLoreEntry(DwarfLoreKey.CONTRACT_SIGNATURES,      LoreAge.ANCIENT, Rarity.RARE)),
            Map.entry(DwarfLoreKey.EMBERGLASS_FORGE_LOGS,    new DwarfLoreEntry(DwarfLoreKey.EMBERGLASS_FORGE_LOGS,    LoreAge.ANCIENT, Rarity.RARE)),
            Map.entry(DwarfLoreKey.MEMORY_SHARD_DISCOVERY,   new DwarfLoreEntry(DwarfLoreKey.MEMORY_SHARD_DISCOVERY,   LoreAge.ANCIENT, Rarity.RARE)),
            Map.entry(DwarfLoreKey.EXILE_RECORDS,            new DwarfLoreEntry(DwarfLoreKey.EXILE_RECORDS,            LoreAge.ANCIENT, Rarity.RARE)),
            Map.entry(DwarfLoreKey.SPIRIT_ENCOUNTER,         new DwarfLoreEntry(DwarfLoreKey.SPIRIT_ENCOUNTER,         LoreAge.ANCIENT, Rarity.RARE)),
            Map.entry(DwarfLoreKey.SEALED_VAULTS,            new DwarfLoreEntry(DwarfLoreKey.SEALED_VAULTS,            LoreAge.ANCIENT, Rarity.RARE)),

            Map.entry(DwarfLoreKey.ROOT_STORAGE,             new DwarfLoreEntry(DwarfLoreKey.ROOT_STORAGE,             LoreAge.ANCIENT, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.DAWN_HALL_RELICS,         new DwarfLoreEntry(DwarfLoreKey.DAWN_HALL_RELICS,         LoreAge.ANCIENT, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.PRIMEVAL_IRONWORKS,       new DwarfLoreEntry(DwarfLoreKey.PRIMEVAL_IRONWORKS,       LoreAge.ANCIENT, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.STARFORGED_HELM,          new DwarfLoreEntry(DwarfLoreKey.STARFORGED_HELM,          LoreAge.ANCIENT, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.FIRST_EMBERGLASS,         new DwarfLoreEntry(DwarfLoreKey.FIRST_EMBERGLASS,         LoreAge.ANCIENT, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.BINDING_OF_THE_BALROG,    new DwarfLoreEntry(DwarfLoreKey.BINDING_OF_THE_BALROG,    LoreAge.ANCIENT, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.ETERNAL_EMBER,            new DwarfLoreEntry(DwarfLoreKey.ETERNAL_EMBER,            LoreAge.ANCIENT, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.ORACLE_INSCRIPTIONS,      new DwarfLoreEntry(DwarfLoreKey.ORACLE_INSCRIPTIONS,      LoreAge.ANCIENT, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.DEEP_CURSE_TABLET,        new DwarfLoreEntry(DwarfLoreKey.DEEP_CURSE_TABLET,        LoreAge.ANCIENT, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.SUNKEN_FORGE_RITES,       new DwarfLoreEntry(DwarfLoreKey.SUNKEN_FORGE_RITES,       LoreAge.ANCIENT, Rarity.EPIC)),
            Map.entry(DwarfLoreKey.CAVERN_LIGHT_CHRONICLE,   new DwarfLoreEntry(DwarfLoreKey.CAVERN_LIGHT_CHRONICLE,   LoreAge.ANCIENT, Rarity.EPIC)),

            Map.entry(DwarfLoreKey.MITHRIL_FORGE_TECHNIQUE,  new DwarfLoreEntry(DwarfLoreKey.MITHRIL_FORGE_TECHNIQUE,  LoreAge.ANCIENT, JolCraftEnumExtensions.Rarity.LEGENDARY.getValue())),
            Map.entry(DwarfLoreKey.ANCIENT_GEMCRAFT,         new DwarfLoreEntry(DwarfLoreKey.ANCIENT_GEMCRAFT,         LoreAge.ANCIENT, JolCraftEnumExtensions.Rarity.LEGENDARY.getValue())),
            Map.entry(DwarfLoreKey.FORGOTTEN_BREW_FORMULAS,  new DwarfLoreEntry(DwarfLoreKey.FORGOTTEN_BREW_FORMULAS,  LoreAge.ANCIENT, JolCraftEnumExtensions.Rarity.LEGENDARY.getValue())),
            Map.entry(DwarfLoreKey.COIN_PRESS_MANUAL,        new DwarfLoreEntry(DwarfLoreKey.COIN_PRESS_MANUAL,        LoreAge.ANCIENT, JolCraftEnumExtensions.Rarity.LEGENDARY.getValue())),
            Map.entry(DwarfLoreKey.ALCHEMY_RECIPES,          new DwarfLoreEntry(DwarfLoreKey.ALCHEMY_RECIPES,          LoreAge.ANCIENT, JolCraftEnumExtensions.Rarity.LEGENDARY.getValue())),
            Map.entry(DwarfLoreKey.MINING_RHYTHM,new DwarfLoreEntry(DwarfLoreKey.MINING_RHYTHM,LoreAge.ANCIENT, JolCraftEnumExtensions.Rarity.LEGENDARY.getValue()))
    );
}
