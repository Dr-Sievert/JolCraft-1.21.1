package net.sievert.jolcraft.world.entity.custom.dwarf.variant;

import net.sievert.jolcraft.util.JolCraftEnumHelper;

import java.util.Locale;

public enum DwarfEyeColor implements JolCraftEnumHelper.StringId {
    BROWN,
    DARK_BROWN,
    BLUE,
    GREEN,
    GRAY;

    @Override
    public String getId() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static DwarfEyeColor byId(String id) {
        return JolCraftEnumHelper.byStringId(
                DwarfEyeColor.class,
                id,
                BROWN
        );
    }
}