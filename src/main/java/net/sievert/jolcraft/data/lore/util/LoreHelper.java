package net.sievert.jolcraft.data.lore.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.key.JolCraftDataKeys;
import net.sievert.jolcraft.data.language.AbstractLanguageKeys;
import net.sievert.jolcraft.data.lore.LoreAge;
import net.sievert.jolcraft.data.lore.LoreEntry;
import net.sievert.jolcraft.data.lore.LoreRarity;

import java.util.*;

public class LoreHelper {

    public static <K extends Enum<K>> K getLoreKey(ItemStack stack, Class<K> keyClass) {
        if (stack == null || stack.isEmpty()) return null;
        String keyString = stack.get(JolCraftDataComponents.LORE_KEY.get());
        if (keyString == null || keyString.isEmpty()) return null;
        return byNameIgnoreCase(keyClass, keyString);
    }

    public static <K extends Enum<K>> void setLoreKey(ItemStack stack, K key) {
        if (stack == null || stack.isEmpty() || key == null) return;
        stack.set(JolCraftDataComponents.LORE_KEY.get(), key.name().toLowerCase(Locale.ROOT));
    }

    public static <K extends Enum<K>> String getEntryTranslationKey(K key) {
        if (key == null) return null;
        return AbstractLanguageKeys.key(
                JolCraftDataKeys.LORE,
                JolCraft.MOD_ID,
                key.name().toLowerCase(Locale.ROOT)
        );
    }

    public static <K extends Enum<K>> String getEntryTranslationKey(ItemStack stack, Class<K> keyClass) {
        K key = getLoreKey(stack, keyClass);
        if (key == null) return null;
        return getEntryTranslationKey(key);
    }

    public static <K extends Enum<K>> K byNameIgnoreCase(Class<K> keyClass, String name) {
        for (K constant : keyClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(name)) {
                return constant;
            }
        }
        return null;
    }

    private static final Map<LoreRarity, Integer> RARITY_WEIGHTS = Map.of(
            LoreRarity.COMMON, 16,
            LoreRarity.UNCOMMON, 8,
            LoreRarity.RARE, 4,
            LoreRarity.EPIC, 2,
            LoreRarity.LEGENDARY, 1
    );

    public static <K extends Enum<K>, E extends LoreEntry<K>> E getRandomLoreEntry(
            RandomSource rng,
            LoreAge age,
            Collection<E> entries,
            Set<LoreRarity> allowedRarities
    ) {
        List<E> weighted = new ArrayList<>();
        for (E entry : entries) {
            if (entry.getAge() != age) continue;
            if (!allowedRarities.contains(entry.getRarity())) continue;
            int weight = RARITY_WEIGHTS.getOrDefault(entry.getRarity(), 1);
            for (int i = 0; i < weight; ++i) {
                weighted.add(entry);
            }
        }

        if (weighted.isEmpty()) return null;
        return weighted.get(rng.nextInt(weighted.size()));
    }

    public static <K extends Enum<K>, E extends LoreEntry<K>> E getRandomLoreEntry(
            RandomSource rng,
            LoreAge age,
            Collection<E> entries
    ) {
        return getRandomLoreEntry(rng, age, entries, RARITY_WEIGHTS.keySet());
    }

    public static <K extends Enum<K>, E extends LoreEntry<K>> E getRandomLoreEntry(
            RandomSource rng,
            LoreAge age,
            Collection<E> entries,
            LoreRarity rarity
    ) {
        return getRandomLoreEntry(rng, age, entries, Set.of(rarity));
    }

    public static <K extends Enum<K>> String toLoreKeyString(K key) {
        if (key == null) return null;
        return key.name().toLowerCase(Locale.ROOT);
    }
}
