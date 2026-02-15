package net.sievert.jolcraft.world.entity.custom.dwarf.util.variation;

import net.sievert.jolcraft.data.util.JolCraftEnumHelper;

public enum DwarfBeardColor implements JolCraftEnumHelper.IntId {

    BROWN(0),
    RED(1),
    BLACK(2),
    GRAY(3);

    private final int id;

    DwarfBeardColor(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public static DwarfBeardColor byId(int id) {
        return JolCraftEnumHelper.byIntIdModulo(DwarfBeardColor.class, id);
    }
}