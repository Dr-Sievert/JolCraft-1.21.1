package net.sievert.jolcraft.world.loot;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootTable;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.loot.JolCraftLootTableIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Stores IDs for JolCraft built-in loot tables, i.e. loot tables which are not based directly on a block or entity ID.
 * Mirrors vanilla's BuiltInLootTables pattern.
 */
public final class JolCraftLootTables {

    private JolCraftLootTables() {}

    private static final Set<ResourceKey<LootTable>> LOCATIONS = new HashSet<>();
    private static final Set<ResourceKey<LootTable>> IMMUTABLE_LOCATIONS = Collections.unmodifiableSet(LOCATIONS);

    /* ---------------------------------------------------------------------
     * Chests
     * ------------------------------------------------------------------ */

    public static final class Chests {

        public static final ResourceKey<LootTable> UNCUT_GEMS = chest(register(JolCraftLootTableIds.UNCUT_GEMS));
        public static final ResourceKey<LootTable> SALVAGE = chest(register(JolCraftLootTableIds.SALVAGE));
        public static final ResourceKey<LootTable> DWARVEN_TOMES = chest(register(JolCraftLootTableIds.DWARVEN_TOMES));
    }

    /* ---------------------------------------------------------------------
     * Strongbox
     * ------------------------------------------------------------------ */

    public static final class Strongbox {

        public static final ResourceKey<LootTable> DWARVEN_TRAIL_RUIN =
                strongbox(register(JolCraftLootTableIds.DWARVEN_TRAIL_RUIN));
    }


    /* ---------------------------------------------------------------------
     * Archaeology
     * ------------------------------------------------------------------ */

    public static final class Archaeology {

        public static final ResourceKey<LootTable> DWARVEN_TRAIL_RUIN_COMMON =
                archaeology(register(JolCraftLootTableIds.DWARVEN_TRAIL_RUIN_COMMON));

        public static final ResourceKey<LootTable> DWARVEN_TRAIL_RUIN_RARE =
                archaeology(register(JolCraftLootTableIds.DWARVEN_TRAIL_RUIN_RARE));
    }

    /* ---------------------------------------------------------------------
     * Registration helpers
     * ------------------------------------------------------------------ */

    private static ResourceKey<LootTable> register(String path) {
        ResourceKey<LootTable> key = ResourceKey.create(
                Registries.LOOT_TABLE,
                JolCraft.location(path)
        );

        if (LOCATIONS.add(key)) {
            return key;
        }

        throw new IllegalArgumentException(key.location() + " is already a registered JolCraft loot table");
    }

    private static ResourceKey<LootTable> inFolder(String folder, ResourceKey<LootTable> key) {
        ResourceLocation id = key.location();
        return register(JolCraftStrings.slashed(folder, id.getPath()));
    }

    private static ResourceKey<LootTable> inject(ResourceKey<LootTable> key){
        return inFolder(JolCraftDictionary.INJECT, key);
    }

    private static ResourceKey<LootTable> chest(ResourceKey<LootTable> key){
        return inFolder(JolCraftDictionary.CHEST, key);
    }

    private static ResourceKey<LootTable> strongbox(ResourceKey<LootTable> key){
        return inFolder(JolCraftDictionary.STRONGBOX, key);
    }

    private static ResourceKey<LootTable> archaeology(ResourceKey<LootTable> key){
        return inFolder(JolCraftDictionary.ARCHAEOLOGY, key);
    }

    public static Set<ResourceKey<LootTable>> all() {
        return IMMUTABLE_LOCATIONS;
    }
}
