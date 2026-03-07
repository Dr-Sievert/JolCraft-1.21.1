package net.sievert.jolcraft.world.entity.custom.dwarf.variant;

import net.sievert.jolcraft.util.JolCraftEnumHelper;

import java.util.Locale;

public enum DwarfBeardColor implements JolCraftEnumHelper.StringId {
    BROWN,
    RED,
    BLACK,
    GRAY;

    @Override
    public String getId() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static DwarfBeardColor byId(String id) {
        return JolCraftEnumHelper.byStringId(
                DwarfBeardColor.class,
                id,
                BROWN
        );
    }
}