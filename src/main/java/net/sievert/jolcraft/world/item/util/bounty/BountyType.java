package net.sievert.jolcraft.world.item.util.bounty;

import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.util.JolCraftEnumHelper;

/**
 * Represents the different types of bounties assignable to dwarves and items.
 */
public enum BountyType implements JolCraftEnumHelper.StringId {

    UNKNOWN(JolCraftDictionary.UNKNOWN),
    MINER(JolCraftDwarfIds.DWARF_MINER),
    MERCHANT(JolCraftDwarfIds.DWARF_MERCHANT);

    private final String id;

    BountyType(String id) {
        this.id = id;
    }

    /** The unique string ID used for saving/loading this type. */
    @Override
    public String getId() {
        return id;
    }

    /** Looks up a BountyType by its string ID. Returns UNKNOWN if no match. */
    public static BountyType fromString(String id) {
        return JolCraftEnumHelper.byStringId(BountyType.class, id, UNKNOWN);
    }
}