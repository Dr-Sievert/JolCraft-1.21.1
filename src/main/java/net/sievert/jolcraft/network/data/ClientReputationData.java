package net.sievert.jolcraft.network.data;

import net.sievert.jolcraft.world.entity.util.dwarf.profession.DwarfProfession;

import java.util.EnumSet;

public final class ClientReputationData {
    private static int tier;
    private static EnumSet<DwarfProfession> endorsements = EnumSet.noneOf(DwarfProfession.class);
    private static int revision;

    public static int getTier() {
        return tier;
    }

    public static EnumSet<DwarfProfession> getAllEndorsements() {
        return EnumSet.copyOf(endorsements);
    }

    public static int revision() {
        return revision;
    }

    public static void setTier(int value) {
        if (tier == value) return;
        tier = value;
        revision++;
    }

    public static void setEndorsements(EnumSet<DwarfProfession> value) {
        EnumSet<DwarfProfession> next = (value == null)
                ? EnumSet.noneOf(DwarfProfession.class)
                : EnumSet.copyOf(value);

        if (endorsements.equals(next)) return;

        endorsements = next;
        revision++;
    }
}