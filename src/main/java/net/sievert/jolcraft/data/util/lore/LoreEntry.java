package net.sievert.jolcraft.data.util.lore;

public interface LoreEntry<K extends Enum<K>> {
    LoreType getType();
    K getKey();
    LoreAge getAge();
    LoreRarity getRarity();
    String getText();
}
