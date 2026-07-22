package net.sievert.jolcraft.world.item.lore.dwarf;

import net.minecraft.world.item.Rarity;
import net.sievert.jolcraft.world.item.lore.LoreAge;
import net.sievert.jolcraft.world.item.lore.LoreEntry;
import net.sievert.jolcraft.world.item.lore.LoreType;

public record DwarfLoreEntry(
        DwarfLoreKey key,
        LoreAge age,
        Rarity rarity
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
    public Rarity getRarity() {
        return rarity;
    }
}
