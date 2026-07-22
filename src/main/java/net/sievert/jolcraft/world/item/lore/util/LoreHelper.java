package net.sievert.jolcraft.world.item.lore.util;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.world.item.lore.LoreAge;
import net.sievert.jolcraft.world.item.lore.LoreEntry;
import net.sievert.jolcraft.data.JolCraftEnumExtensions;

import javax.annotation.Nullable;
import java.util.*;

public class LoreHelper {

    @Nullable
    public static <K extends Enum<K>> K getLoreKey(ItemStack stack, Class<K> keyClass) {
        if (stack == null || stack.isEmpty()) return null;
        String keyString = stack.get(JolCraftDataComponents.DWARF_LORE_KEY.get());
        if (keyString == null || keyString.isEmpty()) return null;
        return byNameIgnoreCase(keyClass, keyString);
    }

    public static <K extends Enum<K>> void setLoreKey(ItemStack stack, K key) {
        if (stack == null || stack.isEmpty() || key == null) return;
        stack.set(JolCraftDataComponents.DWARF_LORE_KEY.get(), key.name().toLowerCase(Locale.ROOT));
    }

    @Nullable
    public static <K extends Enum<K>> String getEntryTranslationKey(K key) {
        if (key == null) return null;
        return AbstractLanguageKeys.key(
                JolCraftDictionary.LORE,
                JolCraft.MOD_ID,
                key.name().toLowerCase(Locale.ROOT)
        );
    }

    @Nullable
    public static <K extends Enum<K>> String getEntryTranslationKey(ItemStack stack, Class<K> keyClass) {
        K key = getLoreKey(stack, keyClass);
        if (key == null) return null;
        return getEntryTranslationKey(key);
    }

    @Nullable
    public static <K extends Enum<K>> K byNameIgnoreCase(Class<K> keyClass, String name) {
        for (K constant : keyClass.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(name)) {
                return constant;
            }
        }
        return null;
    }

    private static final Map<Rarity, Integer> RARITY_WEIGHTS = Map.of(
            Rarity.COMMON, 16,
            Rarity.UNCOMMON, 8,
            Rarity.RARE, 4,
            Rarity.EPIC, 2,
            JolCraftEnumExtensions.Rarity.LEGENDARY.getValue(), 1
    );

    @Nullable
    public static <K extends Enum<K>, E extends LoreEntry<K>> E getRandomLoreEntry(
            RandomSource rng,
            LoreAge age,
            Collection<E> entries,
            Set<Rarity> allowedRarities
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

    @Nullable
    public static <K extends Enum<K>, E extends LoreEntry<K>> E getRandomLoreEntry(
            RandomSource rng,
            LoreAge age,
            Collection<E> entries
    ) {
        return getRandomLoreEntry(rng, age, entries, RARITY_WEIGHTS.keySet());
    }

    @Nullable
    public static <K extends Enum<K>, E extends LoreEntry<K>> E getRandomLoreEntry(
            RandomSource rng,
            LoreAge age,
            Collection<E> entries,
            Rarity rarity
    ) {
        return getRandomLoreEntry(rng, age, entries, Set.of(rarity));
    }

    @Nullable
    public static <K extends Enum<K>> String toLoreKeyString(K key) {
        if (key == null) return null;
        return key.name().toLowerCase(Locale.ROOT);
    }
}
