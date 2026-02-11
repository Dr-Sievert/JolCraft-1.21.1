package net.sievert.jolcraft.data.id.menu;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.key.JolCraftDataKeys;

public final class JolCraftMenuIds extends JolCraftIds {

    private JolCraftMenuIds() {}

    public static final String STRONGBOX = menu(JolCraftBlockIds.STRONGBOX);
    public static final String LOCKED_STRONGBOX = joined(JolCraftDataKeys.LOCKED, STRONGBOX);
    public static final String LAPIDARY_BENCH = menu(JolCraftDataKeys.LAPIDARY_BENCH);
    public static final String DWARF_MERCHANT = menu(joined(JolCraftDataKeys.DWARF, JolCraftDataKeys.MERCHANT));

    private static String menu(String name) {
        return joined(name, JolCraftDataKeys.MENU);
    }
}
