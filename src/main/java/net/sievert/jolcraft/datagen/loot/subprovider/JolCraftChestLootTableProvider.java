package net.sievert.jolcraft.datagen.loot.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.data.loot.JolCraftLootTables;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public record JolCraftChestLootTableProvider(HolderLookup.Provider registries) implements LootTableSubProvider {

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        output.accept(
                JolCraftLootTables.Chests.DWARVEN_LEXICON_IN_STRONGHOLD_LIBRARY,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(JolCraftItems.DWARVEN_LEXICON.get()).setWeight(1))
                                        .add(EmptyLootItem.emptyItem().setWeight(1))
                        )
        );

        output.accept(
                JolCraftLootTables.Chests.DWARVEN_LEXICON_IN_ABANDONED_MINESHAFT,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(JolCraftItems.DWARVEN_LEXICON.get()).setWeight(1))
                                        .add(EmptyLootItem.emptyItem().setWeight(2))
                        )
        );

        output.accept(
                JolCraftLootTables.Chests.UNCUT_GEMS,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(JolCraftItems.AEGISCORE.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.ASHFANG.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.DEEPMARROW.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.EARTHBLOOD.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.EMBERGLASS.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.FROSTVEIN.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.GRIMSTONE.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.IRONHEART.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.LUMIERE.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.MOONSHARD.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.RUSTAGATE.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.SKYBURROW.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.SUNGLEAM.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.VERDANITE.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.WOECRYSTAL.get()).setWeight(1))
                        )
        );

        output.accept(
                JolCraftLootTables.Chests.SALVAGE,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_COINS.get())
                                                .setWeight(1)
                                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(5.0F)))
                                        )
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_PICKAXE.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_AMULET.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_BELT.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_MUG.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.EXPIRED_POTION.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.INGOT_MOULD.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_SCRAP.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.OLD_FABRIC.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.RUSTY_TONGS.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_MITHRIL_SWORD.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_TABLET.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_DEEPSLATE_PLATES.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_MITHRIL_PLATE.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_DEEPSLATE_GEAR.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_DEEPSLATE_PICKAXE_HEAD.get()).setWeight(1))
                        )
        );

        output.accept(
                JolCraftLootTables.Chests.DWARVEN_TOMES,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(
                                                LootItem.lootTableItem(JolCraftItems.UNIDENTIFIED_DWARVEN_TOME.get())
                                                        .setWeight(9)
                                        )
                                        .add(
                                                LootItem.lootTableItem(JolCraftItems.UNIDENTIFIED_ANCIENT_DWARVEN_TOME.get())
                                                        .setWeight(1)
                                        )
                        )
        );
    }
}
