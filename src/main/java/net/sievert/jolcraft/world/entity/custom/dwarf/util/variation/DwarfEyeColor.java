package net.sievert.jolcraft.world.entity.custom.dwarf.util.variation;

import net.sievert.jolcraft.data.id.directory.JolCraftDirectoryIds;
import net.sievert.jolcraft.data.util.JolCraftEnumHelper;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.Locale;

public enum DwarfEyeColor implements JolCraftEnumHelper.IntId {

    BROWN(0),
    DARK_BROWN(1),
    BLUE(2),
    GREEN(3),
    GRAY(4);

    private final int id;

    DwarfEyeColor(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return id;
    }

    public static DwarfEyeColor byId(int id) {
        return JolCraftEnumHelper.byIntIdModulo(DwarfEyeColor.class, id);
    }

    public String getTextureName() {
        return JolCraftStrings.underscored(
                JolCraftDirectoryIds.EYE,
                name().toLowerCase(Locale.ROOT)
        );
    }
}