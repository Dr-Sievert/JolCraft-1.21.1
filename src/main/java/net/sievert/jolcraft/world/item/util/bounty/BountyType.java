package net.sievert.jolcraft.world.item.util.bounty;

import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Represents the different types of bounties assignable to dwarves and items.
 */
public enum BountyType {

    UNKNOWN(JolCraftDictionary.UNKNOWN),
    MINER(DwarfProfession.MINER.getId()),
    MERCHANT(DwarfProfession.MERCHANT.getId());

    private static final Map<String, BountyType> BY_ID = Stream.of(values()).collect(Collectors.toUnmodifiableMap(BountyType::getId, t -> t));

    private final String id;

    BountyType(String id) {
        this.id = id;
    }

    /** The unique string ID used for saving/loading this type. */
    public String getId() {
        return id;
    }

    /** Looks up a BountyType by its string ID. Returns UNKNOWN if no match. */
    public static BountyType fromString(String id) {
        if (id == null) return UNKNOWN;
        String key = id.trim();
        if (key.isEmpty()) return UNKNOWN;
        return BY_ID.getOrDefault(key, UNKNOWN);
    }
}