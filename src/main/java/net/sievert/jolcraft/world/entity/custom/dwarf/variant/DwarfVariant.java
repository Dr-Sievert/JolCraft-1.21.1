package net.sievert.jolcraft.world.entity.custom.dwarf.variant;

import net.sievert.jolcraft.util.JolCraftEnumHelper;

import java.util.Locale;

public enum DwarfVariant implements JolCraftEnumHelper.StringId {
    GREY,
    BLUE,
    GREEN,
    RED,
    PURPLE,
    WHITE,
    YELLOW;

    @Override
    public String getId() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static DwarfVariant byId(String id) {
        return JolCraftEnumHelper.byStringId(
                DwarfVariant.class,
                id,
                GREY
        );
    }
}