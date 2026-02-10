package net.sievert.jolcraft.data.lore.dwarf;

import net.sievert.jolcraft.data.lore.*;

public record DwarfLoreEntry(
        DwarfLoreKey key,
        LoreAge age,
        LoreRarity rarity
) implements LoreEntry<DwarfLoreKey> {

    @Override
    public LoreType getType() {
        return LoreType.DWARVEN;
    }

    @Override
    public DwarfLoreKey getKey() {
        return key;
    }

    @Override
    public LoreAge getAge() {
        return age;
    }

    @Override
    public LoreRarity getRarity() {
        return rarity;
    }
}
