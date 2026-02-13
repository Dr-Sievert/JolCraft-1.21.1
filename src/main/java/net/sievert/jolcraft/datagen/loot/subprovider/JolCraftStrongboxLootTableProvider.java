package net.sievert.jolcraft.datagen.loot.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.loot.JolCraftLootTables;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record JolCraftStrongboxLootTableProvider(HolderLookup.Provider registries) implements LootTableSubProvider {

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(
                JolCraftLootTables.Strongbox.DWARVEN_TRAIL_RUIN,
                LootTable.lootTable()

                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(10))

                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.SALVAGE).setWeight(1))

                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.UNCUT_GEMS).setWeight(2))
                                        .add(EmptyLootItem.emptyItem().setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.GOLD_COIN.get())
                                                .setWeight(2)
                                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(5))))
                        )

                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(3))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.DWARVEN_TOMES).setWeight(2))
                                        .add(EmptyLootItem.emptyItem().setWeight(1))
                        )
        );
    }
}

