package net.sievert.jolcraft.world.item.lore;

import net.minecraft.world.item.Rarity;

public interface LoreEntry<K extends Enum<K>> {
    LoreType getType();
    K getKey();
    LoreAge getAge();
    Rarity getRarity();
}
