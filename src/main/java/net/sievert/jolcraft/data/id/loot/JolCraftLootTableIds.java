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

    public static final String DWARVEN_TRAIL_RUIN = JolCraftStructureIds.DWARVEN_TRAIL_RUIN;

    /* ---------------------------------------------------------------------
     * Archaeology
     * ------------------------------------------------------------------ */

    public static final String DWARVEN_TRAIL_RUIN_COMMON = join(DWARVEN_TRAIL_RUIN, JolCraftRarityIds.COMMON);
    public static final String DWARVEN_TRAIL_RUIN_RARE = join(DWARVEN_TRAIL_RUIN, JolCraftRarityIds.RARE);
}
