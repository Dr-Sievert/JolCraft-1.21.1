package net.sievert.jolcraft.datagen.loot.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.loot.JolCraftLootTables;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record JolCraftArchaeologyLootTableProvider(HolderLookup.Provider registries) implements LootTableSubProvider {

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {

        output.accept(
                BuiltInLootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(Items.BURN_POTTERY_SHERD))
                                        .add(LootItem.lootTableItem(Items.DANGER_POTTERY_SHERD))
                                        .add(LootItem.lootTableItem(Items.FRIEND_POTTERY_SHERD))
                                        .add(LootItem.lootTableItem(Items.HEART_POTTERY_SHERD))
                                        .add(LootItem.lootTableItem(Items.HEARTBREAK_POTTERY_SHERD))
                                        .add(LootItem.lootTableItem(Items.HOWL_POTTERY_SHERD))
                                        .add(LootItem.lootTableItem(Items.SHEAF_POTTERY_SHERD))
                                        .add(LootItem.lootTableItem(Items.WAYFINDER_ARMOR_TRIM_SMITHING_TEMPLATE))
                                        .add(LootItem.lootTableItem(Items.RAISER_ARMOR_TRIM_SMITHING_TEMPLATE))
                                        .add(LootItem.lootTableItem(Items.SHAPER_ARMOR_TRIM_SMITHING_TEMPLATE))
                                        .add(LootItem.lootTableItem(Items.HOST_ARMOR_TRIM_SMITHING_TEMPLATE))
                                        .add(LootItem.lootTableItem(Items.MUSIC_DISC_RELIC))
                                        .add(LootItem.lootTableItem(JolCraftItems.DWARVEN_LEXICON))
                        )
        );

        output.accept(
                JolCraftLootTables.Archaeology.DWARVEN_TRAIL_RUIN_COMMON,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))

                                        .add(
                                                LootItem.lootTableItem(JolCraftItems.GOLD_COIN.get())
                                                        .setWeight(1)
                                        )

                                        .add(
                                                NestedLootTable.lootTableReference(
                                                        JolCraftLootTables.Chests.SALVAGE
                                                ).setWeight(6)
                                        )
                        )
                        .setRandomSequence(JolCraftLootTables.Archaeology.DWARVEN_TRAIL_RUIN_COMMON.location())
        );


        output.accept(
                JolCraftLootTables.Archaeology.DWARVEN_TRAIL_RUIN_RARE,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))

                                        .add(
                                                LootItem.lootTableItem(JolCraftItems.ANCIENT_DWARVEN_LEXICON.get())
                                                        .setWeight(1)
                                        )

                                        .add(
                                                LootItem.lootTableItem(JolCraftItems.UNIDENTIFIED_ANCIENT_DWARVEN_TOME.get())
                                                        .setWeight(3)
                                        )
                        )
                        .setRandomSequence(JolCraftLootTables.Archaeology.DWARVEN_TRAIL_RUIN_RARE.location())
        );
    }
}

