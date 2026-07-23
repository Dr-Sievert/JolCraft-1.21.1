package net.sievert.jolcraft.data.id.tag;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.id.worldgen.JolCraftStructureIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

public final class JolCraftTagIds extends JolCraftIds {

    private JolCraftTagIds() {}

    // ---------------------------------------------------------------------
    // Item
    // ---------------------------------------------------------------------

    public static final String COINS = plural(JolCraftDictionary.COIN);
    public static final String TOMES = plural(JolCraftDictionary.TOME);
    public static final String SPAWN_EGGS = join(JolCraftDictionary.SPAWN, plural(JolCraftDictionary.EGG));
    public static final String DWARF_SPAWN_EGGS = join(JolCraftDwarfIds.DWARF, SPAWN_EGGS);
    public static final String CREATURE_SPAWN_EGGS = join(JolCraftDictionary.CREATURE, SPAWN_EGGS);
    public static final String MONSTER_SPAWN_EGGS = join(JolCraftDictionary.MONSTER, SPAWN_EGGS);

    public static final String INK_AND_QUILLS = join(JolCraftDictionary.INK, JolCraftDictionary.AND, plural(JolCraftDictionary.QUILL));
    public static final String GEODES = plural(JolCraftDictionary.GEODE);

    public static final String DURABILITY_ENCHANTABLE = join(JolCraftDictionary.DURABILITY, JolCraftDictionary.ENCHANTABLE);

    public static final String WARHAMMERS = plural(JolCraftDictionary.WARHAMMER);
    public static final String SPANNERS = plural(JolCraftDictionary.SPANNER);
    public static final String ARTISAN_HAMMERS = join(JolCraftDictionary.ARTISAN, plural(JolCraftDictionary.HAMMER));
    public static final String CHISELS = plural(JolCraftDictionary.CHISEL);
    public static final String PESTLES = plural(JolCraftDictionary.PESTLE);

    public static final String UNCUT_GEMS = join(JolCraftDictionary.UNCUT, plural(JolCraftDictionary.GEM));
    public static final String CUT_GEMS = join(JolCraftDictionary.CUT, plural(JolCraftDictionary.GEM));
    public static final String GEM_DUSTS = join(JolCraftDictionary.GEM, plural(JolCraftDictionary.DUST));

    public static final String ATTRIBUTE_TRIM_MATERIALS = join(JolCraftDictionary.ATTRIBUTE, JolCraftDictionary.TRIM, plural(JolCraftDictionary.MATERIAL));
    public static final String PROFESSION_CONTRACTS = join(JolCraftDictionary.PROFESSION, plural(JolCraftDictionary.CONTRACT));
    public static final String REPUTATION_TABLETS = join(JolCraftDictionary.REPUTATION, plural(JolCraftDictionary.TABLET));

    public static final String HOPS = plural(JolCraftDictionary.HOP);
    public static final String HOPS_BREW = join(HOPS, JolCraftDictionary.BREW);

    public static final String REPAIRS_DEEPSLATE = join(plural(JolCraftDictionary.REPAIR), JolCraftDictionary.DEEPSLATE);
    public static final String REPAIRS_MITHRIL = join(plural(JolCraftDictionary.REPAIR), JolCraftDictionary.MITHRIL);
    public static final String MITHRIL_ITEMS = join(JolCraftDictionary.MITHRIL, plural(JolCraftDictionary.ITEM));

    public static final String SALVAGE = join(JolCraftDictionary.SALVAGE);
    public static final String GENERAL_SALVAGE = join(JolCraftDictionary.GENERAL, JolCraftDictionary.SALVAGE);
    public static final String DEEPSLATE_SALVAGE = join(JolCraftDictionary.DEEPSLATE, JolCraftDictionary.SALVAGE);
    public static final String TEXTILE_SALVAGE = join(JolCraftDictionary.TEXTILE, JolCraftDictionary.SALVAGE);
    public static final String REDSTONE_SALVAGE = join(JolCraftDictionary.REDSTONE, JolCraftDictionary.SALVAGE);
    public static final String IRON_SALVAGE = join(JolCraftDictionary.IRON, JolCraftDictionary.SALVAGE);
    public static final String GOLD_SALVAGE = join(JolCraftDictionary.GOLD, JolCraftDictionary.SALVAGE);
    public static final String MITHRIL_SALVAGE = join(JolCraftDictionary.MITHRIL, JolCraftDictionary.SALVAGE);

    // ---------------------------------------------------------------------
    // Block
    // ---------------------------------------------------------------------

    public static final String DEEPSLATE_BULBS_PLANTABLE = join(
            JolCraftDictionary.DEEPSLATE,
            plural(JolCraftDictionary.BULB),
            JolCraftDictionary.PLANTABLE
    );

    public static final String VERDANT = JolCraftDictionary.VERDANT;

    public static final String HOPS_TOP = join(plural(JolCraftDictionary.HOP), JolCraftDictionary.TOP);
    public static final String HOPS_BOTTOM = join(plural(JolCraftDictionary.HOP), JolCraftDictionary.BOTTOM);

    // ---------------------------------------------------------------------
    // Structure
    // ---------------------------------------------------------------------

    public static final String FEATURE_PROTECTED = join(JolCraftDictionary.FEATURE, JolCraftDictionary.PROTECTED);

    public static final String ON_DWARVEN_FORTRESS_EXPLORER_MAPS = join(
            JolCraftDictionary.ON,
            JolCraftStructureIds.DWARVEN_FORTRESS,
            JolCraftDictionary.EXPLORER,
            plural(JolCraftDictionary.MAP)
    );


    public static final String DWARVEN = JolCraftDictionary.DWARVEN;
    public static final String VILLAGES = plural(JolCraftDictionary.VILLAGE);
    public static final String PILLAGERS = plural(JolCraftDictionary.PILLAGER);
    public static final String SURFACE = JolCraftDictionary.SURFACE;
    public static final String RUINS = plural(JolCraftDictionary.RUIN);
    public static final String OCEAN = JolCraftDictionary.OCEAN;
    public static final String UNDERGROUND = JolCraftDictionary.UNDERGROUND;
    public static final String NETHER_PORTALS = join(JolCraftDictionary.NETHER, plural(JolCraftDictionary.PORTAL));


    // ---------------------------------------------------------------------
    // Biome
    // ---------------------------------------------------------------------

    public static final String HAS_STRUCTURE = JolCraftStrings.underscored(JolCraftDictionary.HAS, JolCraftDictionary.STRUCTURE);

    public static final String MOUNTAINS_HILLS = join(plural(JolCraftDictionary.MOUNTAIN), plural(JolCraftDictionary.HILL));
}
