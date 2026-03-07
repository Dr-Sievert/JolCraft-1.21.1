package net.sievert.jolcraft.util;

import net.sievert.jolcraft.data.language.JolCraftDictionary;

/**
 * Centralized log tags for JolCraft.
 *
 * - Backed by JolCraftDictionary constants
 * - Uppercase enforced once
 * - Implements StringId for enum consistency
 * - Type-safe (no raw strings drifting)
 */
public enum JolCraftLogTags implements JolCraftEnumHelper.StringId {

    ADVANCEMENT(JolCraftDictionary.ADVANCEMENT),
    ATTACHMENT(JolCraftDictionary.ATTACHMENT),
    BLOCK(JolCraftDictionary.BLOCK),
    BLOCK_ENTITY(JolCraftDictionary.BLOCK + JolCraftDictionary.ENTITY),
    CONFIG(JolCraftDictionary.CONFIG),
    DATA(JolCraftDictionary.DATA),
    DATAGEN(JolCraftDictionary.DATAGEN),
    ENTITY(JolCraftDictionary.ENTITY),
    INIT(JolCraftDictionary.INIT),
    ITEM(JolCraftDictionary.ITEM),
    NETWORK(JolCraftDictionary.NETWORK),
    PLAYER(JolCraftDictionary.PLAYER),
    RECIPE(JolCraftDictionary.RECIPE);

    private final String id;

    JolCraftLogTags(String base) {
        this.id = base.toUpperCase();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id;
    }
}