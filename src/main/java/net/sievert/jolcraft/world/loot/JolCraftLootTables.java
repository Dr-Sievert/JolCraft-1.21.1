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


        public static final ResourceKey<LootTable> MISC_SALVAGE = chest(register(JolCraftLootTableIds.MISC_SALVAGE));
        public static final ResourceKey<LootTable> DEEPSLATE_SALVAGE = chest(register(JolCraftLootTableIds.DEEPSLATE_SALVAGE));
        public static final ResourceKey<LootTable> MITHRIL_SALVAGE = chest(register(JolCraftLootTableIds.MITHRIL_SALVAGE));
        public static final ResourceKey<LootTable> SALVAGE = chest(register(JolCraftLootTableIds.SALVAGE));
        public static final ResourceKey<LootTable> SMITHING_SALVAGE = chest(register(JolCraftLootTableIds.SMITHING_SALVAGE));

        public static final ResourceKey<LootTable> DWARVEN_TOMES = chest(register(JolCraftLootTableIds.DWARVEN_TOMES));

        public static final ResourceKey<LootTable> UNCUT_GEMS = chest(register(JolCraftLootTableIds.UNCUT_GEMS));

        public static final ResourceKey<LootTable> SUPPLIES = chest(register(JolCraftLootTableIds.SUPPLIES));
    }

    /* ---------------------------------------------------------------------
     * Strongbox
     * ------------------------------------------------------------------ */

    public static final class Strongbox {

        public static final ResourceKey<LootTable> DWARVEN_FORTRESS_FORGE = strongbox(register(JolCraftLootTableIds.DWARVEN_FORTRESS_FORGE));
        public static final ResourceKey<LootTable> DWARVEN_FORTRESS_VAULT = strongbox(register(JolCraftLootTableIds.DWARVEN_FORTRESS_VAULT));
        public static final ResourceKey<LootTable> DWARVEN_FORTRESS_GARDEN = strongbox(register(JolCraftLootTableIds.DWARVEN_FORTRESS_GARDEN));
        public static final ResourceKey<LootTable> DWARVEN_FORTRESS_ARCHIVES = strongbox(register(JolCraftLootTableIds.DWARVEN_FORTRESS_ARCHIVES));
    }

    /* ---------------------------------------------------------------------
     * Archaeology
     * ------------------------------------------------------------------ */

    public static final class Archaeology {

        public static final ResourceKey<LootTable> DWARVEN_FORTRESS_COMMON =
                archaeology(register(JolCraftLootTableIds.DWARVEN_FORTRESS_COMMON));

        public static final ResourceKey<LootTable> DWARVEN_FORTRESS_RARE =
                archaeology(register(JolCraftLootTableIds.DWARVEN_FORTRESS_RARE));
    }

    /* ---------------------------------------------------------------------
     * Fishing
     * ------------------------------------------------------------------ */

    public static final class Fishing {

        public static final ResourceKey<LootTable> FISHING =
                fishing(register(JolCraftLootTableIds.FISHING));

        public static final ResourceKey<LootTable> JUNK =
                fishing(register(JolCraftLootTableIds.JUNK));

        public static final ResourceKey<LootTable> TREASURE =
                fishing(register(JolCraftLootTableIds.TREASURE));
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

    private static ResourceKey<LootTable> fishing(ResourceKey<LootTable> key){
        return inFolder(JolCraftDictionary.FISHING, key);
    }

    public static Set<ResourceKey<LootTable>> all() {
        return IMMUTABLE_LOCATIONS;
    }
}
