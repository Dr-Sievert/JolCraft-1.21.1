package net.sievert.jolcraft.data.id.menu;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public final class JolCraftMenuIds extends JolCraftIds {

    private JolCraftMenuIds() {}

    public static final String STRONGBOX = menu(JolCraftBlockIds.STRONGBOX);
    public static final String LOCKED_STRONGBOX = join(JolCraftDictionary.LOCKED, STRONGBOX);
    public static final String LAPIDARY_BENCH = menu(JolCraftBlockIds.LAPIDARY_BENCH);
    public static final String DWARF_MERCHANT = menu(join(JolCraftDictionary.DWARF, JolCraftDictionary.MERCHANT));

    private static String menu(String name) {
        return join(name, JolCraftDictionary.MENU);
    }
}
