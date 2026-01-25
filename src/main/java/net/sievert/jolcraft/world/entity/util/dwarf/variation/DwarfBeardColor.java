package net.sievert.jolcraft.world.entity.util.dwarf.variation;


import java.util.Arrays;
import java.util.Comparator;

public enum DwarfBeardColor {
    BROWN(0),
    RED(1),
    BLACK(2),
    GRAY(3);

    private static final DwarfBeardColor[] BY_ID = Arrays.stream(values()).sorted(
            Comparator.comparingInt(DwarfBeardColor::getId)).toArray(DwarfBeardColor[]::new);
    private final int id;

    DwarfBeardColor(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public static DwarfBeardColor byId(int id) {
        return BY_ID[id % BY_ID.length];
    }
}