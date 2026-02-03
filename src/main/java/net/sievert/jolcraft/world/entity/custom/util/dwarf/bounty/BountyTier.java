package net.sievert.jolcraft.world.entity.custom.util.dwarf.bounty;

public enum BountyTier {

    UNKNOWN(0, "Unknown"),
    NOVICE(1, "Novice"),
    APPRENTICE(2, "Apprentice"),
    JOURNEYMAN(3, "Journeyman"),
    EXPERT(4, "Expert"),
    MASTER(5, "Master");

    private final int value;
    private final String displayName;

    BountyTier(int value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    /** For saving to DataComponent/NBT. */
    public int getValue() {
        return value;
    }

    /** For tooltips/UI. */
    public String getDisplayName() {
        return displayName;
    }

    /** Get enum from int, or null if not found. */
    public static BountyTier fromValue(int value) {
        for (BountyTier tier : values()) if (tier.value == value) return tier;
        return UNKNOWN;
    }
}
