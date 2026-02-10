package net.sievert.jolcraft.data.lore;

public interface LoreEntry<K extends Enum<K>> {
    LoreType getType();
    K getKey();
    LoreAge getAge();
    LoreRarity getRarity();
}
