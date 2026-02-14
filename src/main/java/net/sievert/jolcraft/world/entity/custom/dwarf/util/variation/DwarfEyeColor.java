package net.sievert.jolcraft.world.entity.custom.dwarf.util.variation;

import net.sievert.jolcraft.data.id.directory.JolCraftDirectoryIds;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

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

    public String getTextureName() {
        return JolCraftStrings.underscored(JolCraftDirectoryIds.EYE, name().toLowerCase(Locale.ROOT));
    }
}