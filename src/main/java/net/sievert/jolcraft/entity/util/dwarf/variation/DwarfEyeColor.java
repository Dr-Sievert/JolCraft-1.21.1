package net.sievert.jolcraft.entity.util.dwarf.variation;

import java.util.Arrays;
import java.util.Comparator;

public enum DwarfEyeColor {
    BROWN(0),
    DARK_BROWN(1),
    BLUE(2),
    GREEN(3),
    GRAY(4);

    private static final DwarfEyeColor[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(DwarfEyeColor::getId)).toArray(DwarfEyeColor[]::new);
    private final int id;

    DwarfEyeColor(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static DwarfEyeColor byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}