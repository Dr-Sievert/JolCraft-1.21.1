package net.sievert.jolcraft.world.entity.custom.dwarf.util.variation;

import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.util.JolCraftEnumHelper;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.Locale;

public enum DwarfVariant implements JolCraftEnumHelper.IntId {

    GREY(0),
    BLUE(1),
    GREEN(2),
    RED(3),
    PURPLE(4),
    WHITE(5),
    YELLOW(6);

    private final int id;
    private final String key;

    DwarfVariant(int id) {
        this.id = id;
        this.key = name().toLowerCase(Locale.ROOT);
    }

    @Override
    public int getId() {
        return id;
    }

    /** Lowercase string id (e.g. "grey"). */
    public String getKey() {
        return key;
    }

    /** Texture suffix (e.g. "dwarf_grey"). */
    public String getTextureName() {
        return JolCraftStrings.underscored(JolCraftDictionary.DWARF, key);
    }

    public static DwarfVariant byId(int id) {
        return JolCraftEnumHelper.byIntIdModulo(DwarfVariant.class, id);
    }
}