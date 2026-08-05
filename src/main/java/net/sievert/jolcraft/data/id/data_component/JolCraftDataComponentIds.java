package net.sievert.jolcraft.data.id.data_component;

import net.sievert.jolcraft.data.id.JolCraftIds;
import net.sievert.jolcraft.data.id.entity.dwarf.JolCraftDwarfIds;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

public final class JolCraftDataComponentIds extends JolCraftIds {

    private JolCraftDataComponentIds() {}

    // -----------------
    // Lore
    // -----------------

    public static final String DWARF_LORE_KEY = join(JolCraftDwarfIds.DWARF, JolCraftDictionary.LORE, JolCraftDictionary.KEY);

    // -----------------
    // Reputation
    // -----------------

    public static final String REPUTATION_OWNER = join(JolCraftDictionary.REPUTATION, JolCraftDictionary.OWNER);
    public static final String REPUTATION_TIER = join(JolCraftDictionary.REPUTATION, JolCraftDictionary.TIER);
    public static final String REPUTATION_ENDORSEMENTS = join(JolCraftDictionary.REPUTATION, JolCraftStrings.plural(JolCraftDictionary.ENDORSEMENT));

    // -----------------
    // Bounty
    // -----------------

    public static final String BOUNTY_TIER     = join(JolCraftItemIds.BOUNTY, JolCraftDictionary.TIER);
    public static final String BOUNTY_TYPE     = join(JolCraftItemIds.BOUNTY, JolCraftDictionary.TYPE);
    public static final String BOUNTY_DATA     = join(JolCraftItemIds.BOUNTY, JolCraftDictionary.DATA);
    public static final String BOUNTY_FILL     = join(JolCraftItemIds.BOUNTY, JolCraftDictionary.FILL);
    public static final String BOUNTY_COMPLETE = join(JolCraftItemIds.BOUNTY, JolCraftDictionary.COMPLETE);
    public static final String REWARD_CRATE_SOURCE = join(JolCraftItemIds.REWARD_CRATE, JolCraftDictionary.SOURCE);

    // -----------------
    // Compass
    // -----------------

    public static final String STRUCTURE_GROUP = join(JolCraftDictionary.STRUCTURE, JolCraftDictionary.GROUP);
    public static final String DEEPSLATE_COMPASS_DIAL_COLOR = join(JolCraftItemIds.DEEPSLATE_COMPASS_DIAL, JolCraftDictionary.COLOR);
    public static final String DEEPSLATE_COMPASS_TARGET = join(JolCraftItemIds.DEEPSLATE_COMPASS, JolCraftDictionary.TARGET);

    // -----------------
    // Strongbox
    // -----------------

    public static final String LOOT_TABLE = join(JolCraftDictionary.LOOT, JolCraftDictionary.TABLE);
    public static final String LOOT_SEED = join(JolCraftDictionary.LOOT, JolCraftDictionary.SEED);
    public static final String LOCKED = JolCraftDictionary.LOCKED;

    // -----------------
    // Items
    // -----------------

    public static final String COIN_POUCH_AMOUNT = join(JolCraftItemIds.COIN_POUCH, JolCraftDictionary.AMOUNT);

    // -----------------
    // Brewing
    // -----------------

    public static final String FLUID_CONTENT = join(JolCraftDictionary.FLUID, JolCraftDictionary.CONTENT);
    public static final String BREW_COLOR = join(JolCraftDictionary.BREW, JolCraftDictionary.COLOR);
    public static final String BREW_AGE = join(JolCraftDictionary.BREW, JolCraftDictionary.AGE);
    public static final String MAX_BREW_AGE = join(JolCraftDictionary.MAX, BREW_AGE);
    public static final String BREWING_SPEED = join(JolCraftDictionary.BREWING, JolCraftDictionary.SPEED);
}
