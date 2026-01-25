package net.sievert.jolcraft.world.entity.util.dwarf.bounty;

import org.jetbrains.annotations.Nullable;

/**
 * Represents the different types of bounties assignable to dwarves and items.
 */
public enum BountyType {

    UNKNOWN("unknown"),
    MINER("miner"),
    MERCHANT("merchant");

    private final String id;

    BountyType(String id) {
        this.id = id;
    }

    /**
     * The unique string ID used for saving/loading this type.
     */
    public String getId() {
        return id;
    }

    /**
     * Looks up a BountyType by its string ID.
     * Returns null if no match.
     */
    @Nullable
    public static BountyType fromString(String id) {
        if (id == null || id.isEmpty()) return null;
        for (BountyType type : values()) {
            if (type.id.equals(id)) return type;
        }
        return null;
    }
}
