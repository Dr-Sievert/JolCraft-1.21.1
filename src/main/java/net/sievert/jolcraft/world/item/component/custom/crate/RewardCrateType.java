package net.sievert.jolcraft.world.item.component.custom.crate;

import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.storage.loot.LootTable;
import net.sievert.jolcraft.data.JolCraftEnumExtensions;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.loot.JolCraftLootTables;
import org.jetbrains.annotations.NotNull;

public enum RewardCrateType {

    //Basic

    SUPPLY_CRATE(
            Rarity.COMMON,
            JolCraftLootTables.Crates.SUPPLY_CRATE,
            Component.translatable(JolCraftLanguageKeys.SUPPLY_CRATE)
    ),

    //Custom Texture

    ALCHEMY_SUPPLIES(
            Rarity.UNCOMMON,
            JolCraftLootTables.Crates.ALCHEMY_SUPPLIES,
            Component.translatable(JolCraftLanguageKeys.ALCHEMY_SUPPLIES)
    ),

    DWARVEN_FORTRESS_EXCAVATION(
            Rarity.RARE,
            JolCraftLootTables.Crates.DWARVEN_FORTRESS_EXCAVATION,
            Component.translatable(JolCraftLanguageKeys.DWARVEN_FORTRESS_EXCAVATION)
    ),

    ARTISAN_SUPPLIES(
            Rarity.EPIC,
            JolCraftLootTables.Crates.ARTISAN_SUPPLIES,
            Component.translatable(JolCraftLanguageKeys.ARTISAN_SUPPLIES)
    ),

    FARMING_SUPPLIES(
            Rarity.COMMON,
            JolCraftLootTables.Crates.FARMING_SUPPLIES,
            Component.translatable(JolCraftLanguageKeys.FARMING_SUPPLIES)
    ),

    MINING_CACHE(
            Rarity.RARE,
            JolCraftLootTables.Crates.MINING_CACHE,
            Component.translatable(JolCraftLanguageKeys.MINING_CACHE)
    ),


    FISHING_LOOT(
            Rarity.UNCOMMON,
            JolCraftLootTables.Crates.FISHING_LOOT,
            Component.translatable(JolCraftLanguageKeys.FISHING_LOOT)
    ),

    BLACKSMITH_SUPPLIES(
            Rarity.EPIC,
            JolCraftLootTables.Crates.BLACKSMITH_SUPPLIES,
            Component.translatable(JolCraftLanguageKeys.BLACKSMITH_SUPPLIES)
    ),

    MONSTER_SLAYER_LOOT(
            Rarity.UNCOMMON,
            JolCraftLootTables.Crates.MONSTER_SLAYER_LOOT,
            Component.translatable(JolCraftLanguageKeys.MONSTER_SLAYER_LOOT)
    ),

    VAULT_LOOT(
            JolCraftEnumExtensions.Rarity.LEGENDARY.getValue(),
            JolCraftLootTables.Crates.VAULT_LOOT,
            Component.translatable(JolCraftLanguageKeys.VAULT_LOOT)
    );

    private final Rarity rarity;
    private final ResourceKey<LootTable> lootTable;
    private final Component displayName;

    RewardCrateType(
            @NotNull Rarity rarity,
            @NotNull ResourceKey<LootTable> lootTable,
            @NotNull Component displayName
    ) {
        this.rarity = rarity;
        this.lootTable = lootTable;
        this.displayName = displayName;
    }

    public @NotNull Rarity rarity() {
        return rarity;
    }

    public @NotNull ResourceKey<LootTable> lootTable() {
        return lootTable;
    }

    public @NotNull Component displayName() {
        return displayName;
    }

    public @NotNull ItemStack createStack() {

        ItemStack stack = new ItemStack(JolCraftItems.REWARD_CRATE.get());

        stack.set(
                DataComponents.RARITY,
                rarity
        );

        stack.set(
                DataComponents.CUSTOM_NAME,
                displayName
        );

        stack.set(
                JolCraftDataComponents.REWARD_CRATE_SOURCE.get(),
                RewardCrateSource.lootTable(lootTable)
        );

        return stack;
    }
}