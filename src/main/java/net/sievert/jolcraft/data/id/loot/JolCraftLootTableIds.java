package net.sievert.jolcraft.data.id.loot;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.id.item.JolCraftMaterialIds;
import net.sievert.jolcraft.data.id.item.JolCraftRarityIds;
import net.sievert.jolcraft.data.id.tag.JolCraftTagIds;
import net.sievert.jolcraft.data.id.worldgen.JolCraftStructureIds;
import net.sievert.jolcraft.data.id.worldgen.template_pool.JolCraftTemplatePoolIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftLootTableIds extends JolCraftIds {

    private JolCraftLootTableIds() {}

    /* ---------------------------------------------------------------------
     * Chests
     * ------------------------------------------------------------------ */

    public static final String SALVAGE = JolCraftTagIds.SALVAGE;
    public static final String MITHRIL_SALVAGE = join(JolCraftMaterialIds.MITHRIL, SALVAGE);
    public static final String DEEPSLATE_SALVAGE = join(JolCraftMaterialIds.DEEPSLATE, SALVAGE);
    public static final String MISC_SALVAGE = join(JolCraftDictionary.MISC, SALVAGE);
    public static final String SMITHING_SALVAGE = join(JolCraftDictionary.SMITHING, SALVAGE);

    public static final String DWARVEN_TOMES = plural(JolCraftItemIds.DWARVEN_TOME);

    public static final String VANILLA_GEMS = join(JolCraftDictionary.VANILLA, plural(JolCraftDictionary.GEM));
    public static final String VANILLA_METAL = join(JolCraftDictionary.VANILLA, JolCraftDictionary.METAL);

    public static final String DWARVEN_METAL = join(JolCraftDictionary.DWARVEN, JolCraftDictionary.METAL);

    public static final String DEEPSLATE_ARMOR = join(JolCraftMaterialIds.DEEPSLATE, JolCraftDictionary.ARMOR);
    public static final String DEEPSLATE_GEAR = join(JolCraftMaterialIds.DEEPSLATE, JolCraftDictionary.GEAR);
    public static final String MITHRIL_ARMOR = join(JolCraftMaterialIds.MITHRIL, JolCraftDictionary.ARMOR);
    public static final String MITHRIL_GEAR = join(JolCraftMaterialIds.MITHRIL, JolCraftDictionary.GEAR);

    public static final String UNCUT_GEMS = JolCraftTagIds.UNCUT_GEMS;
    public static final String GEODES = plural(JolCraftDictionary.GEODE);
    public static final String SUPPLIES = JolCraftDictionary.SUPPLIES;

    /* ---------------------------------------------------------------------
     * Strongbox
     * ------------------------------------------------------------------ */

    public static final String DWARVEN_FORTRESS = JolCraftStructureIds.DWARVEN_FORTRESS;
    public static final String DWARVEN_FORTRESS_FORGE = join(DWARVEN_FORTRESS, JolCraftTemplatePoolIds.FORGE);
    public static final String DWARVEN_FORTRESS_VAULT = join(DWARVEN_FORTRESS, JolCraftTemplatePoolIds.VAULT);
    public static final String DWARVEN_FORTRESS_GARDEN = join(DWARVEN_FORTRESS, JolCraftTemplatePoolIds.GARDEN);
    public static final String DWARVEN_FORTRESS_ARCHIVES = join(DWARVEN_FORTRESS, JolCraftTemplatePoolIds.ARCHIVES);
    public static final String DWARVEN_FORTRESS_CATACOMBS = join(DWARVEN_FORTRESS, JolCraftTemplatePoolIds.CATACOMBS);

    /* ---------------------------------------------------------------------
     * Crates
     * ------------------------------------------------------------------ */

    public static final String SUPPLY_CRATE = join(JolCraftDictionary.SUPPLY, JolCraftDictionary.CRATE);

    public static final String ALCHEMY_SUPPLIES = suppliesCrate(JolCraftDictionary.ALCHEMY);
    public static final String DWARVEN_FORTRESS_EXCAVATION = join(JolCraftStructureIds.DWARVEN_FORTRESS, JolCraftDictionary.EXCAVATION);
    public static final String ARTISAN_SUPPLIES = suppliesCrate(JolCraftDictionary.ARTISAN);
    public static final String FARMING_SUPPLIES = suppliesCrate(JolCraftDictionary.FARMING);
    public static final String MINING_CACHE = join(JolCraftDictionary.MINING, JolCraftDictionary.CACHE);
    public static final String FISHING_LOOT = join(JolCraftDictionary.FISHING, JolCraftDictionary.LOOT);
    public static final String BLACKSMITH_SUPPLIES = suppliesCrate(JolCraftDictionary.BLACKSMITH);
    public static final String MONSTER_SLAYER_LOOT = join(JolCraftDictionary.MONSTER, JolCraftDictionary.SLAYER, JolCraftDictionary.LOOT);
    public static final String VAULT_LOOT = join(JolCraftDictionary.VAULT, JolCraftDictionary.LOOT);

    /* ---------------------------------------------------------------------
     * Archaeology
     * ------------------------------------------------------------------ */

    public static final String DWARVEN_FORTRESS_COMMON = join(DWARVEN_FORTRESS, JolCraftRarityIds.COMMON);
    public static final String DWARVEN_FORTRESS_RARE = join(DWARVEN_FORTRESS, JolCraftRarityIds.RARE);

    /* ---------------------------------------------------------------------
     * Fishing
     * ------------------------------------------------------------------ */

    public static final String FISHING = JolCraftDictionary.FISHING;
    public static final String JUNK = JolCraftDictionary.JUNK;
    public static final String TREASURE = JolCraftDictionary.TREASURE;

    private static String suppliesCrate(String type){
        return join(type, JolCraftDictionary.SUPPLIES);
    }
}
