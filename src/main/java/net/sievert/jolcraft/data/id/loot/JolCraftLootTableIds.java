package net.sievert.jolcraft.data.id.loot;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.id.item.JolCraftRarityIds;
import net.sievert.jolcraft.data.id.tag.JolCraftTagIds;
import net.sievert.jolcraft.data.id.worldgen.JolCraftStructureIds;

public final class JolCraftLootTableIds extends JolCraftIds {

    private JolCraftLootTableIds() {}

    /* ---------------------------------------------------------------------
     * Chests
     * ------------------------------------------------------------------ */

    public static final String UNCUT_GEMS = JolCraftTagIds.UNCUT_GEMS;
    public static final String SALVAGE = JolCraftTagIds.SALVAGE;
    public static final String DWARVEN_TOMES = plural(JolCraftItemIds.DWARVEN_TOME);

    /* ---------------------------------------------------------------------
     * Strongbox
     * ------------------------------------------------------------------ */

    public static final String DWARVEN_FORTRESS = JolCraftStructureIds.DWARVEN_FORTRESS;

    /* ---------------------------------------------------------------------
     * Archaeology
     * ------------------------------------------------------------------ */

    public static final String DWARVEN_FORTRESS_COMMON = join(DWARVEN_FORTRESS, JolCraftRarityIds.COMMON);
    public static final String DWARVEN_FORTRESS_RARE = join(DWARVEN_FORTRESS, JolCraftRarityIds.RARE);
}
