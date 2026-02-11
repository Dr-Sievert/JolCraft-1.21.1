package net.sievert.jolcraft.world.entity.custom.dwarf.util.variation;

import net.sievert.jolcraft.data.key.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public enum DwarfVariant {
    GREY(0),
    BLUE(1),
    GREEN(2),
    RED(3),
    PURPLE(4),
    WHITE(5),
    YELLOW(6);

    private static final DwarfVariant[] BY_ID = Arrays.stream(values())
            .sorted(Comparator.comparingInt(DwarfVariant::getId))
            .toArray(DwarfVariant[]::new);

    private final int id;
    private final String key;

    DwarfVariant(int id) {
        this.id = id;
        this.key = name().toLowerCase(Locale.ROOT);
    }

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
        return BY_ID[id % BY_ID.length];
    }
}
