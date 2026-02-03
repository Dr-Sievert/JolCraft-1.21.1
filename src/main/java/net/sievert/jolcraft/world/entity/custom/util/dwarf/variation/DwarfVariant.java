package net.sievert.jolcraft.world.entity.custom.util.dwarf.variation;

import java.util.Arrays;
import java.util.Comparator;

public enum DwarfVariant {
    GREY(0),
    BLUE(1),
    GREEN(2),
    RED(3),
    PURPLE(4),
    WHITE(5),
    YELLOW(6),;

    private static final DwarfVariant[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(DwarfVariant::getId)).toArray(DwarfVariant[]::new);
    private final int id;

    DwarfVariant(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static DwarfVariant byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}
