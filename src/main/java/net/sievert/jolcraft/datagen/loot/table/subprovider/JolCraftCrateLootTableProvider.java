package net.sievert.jolcraft.datagen.loot.table.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.item.lore.util.LoreHelper;
import net.sievert.jolcraft.world.loot.JolCraftLootTables;
import net.sievert.jolcraft.datagen.base.JolCraftDataDomain;
import net.sievert.jolcraft.datagen.base.JolCraftMainDataProvider;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftCrateLootTableProvider implements LootTableSubProvider, JolCraftMainDataProvider<JolCraftCrateLootTableProvider> {

    private final HolderLookup.Provider registries;
    private @Nullable BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output;
    private @Nullable JolCraftDataTracking tracking;

    public JolCraftCrateLootTableProvider(@NotNull HolderLookup.Provider registries) {
        this.registries = registries;
    }

    @Override
    public @NotNull JolCraftDataDomain domain() {
        return JolCraftDataDomain.LOOT;
    }

    @Override
    public @NotNull String id() {
        return JolCraftStrings.underscored(JolCraftDictionary.CRATE, JolCraftDictionary.LOOT, JolCraftDictionary.TABLE);
    }

    @Override
    public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        this.output = output;
        this.tracking = null;
        try {
            generate(this, null, CompletableFuture.completedFuture(registries), null);
        } finally {
            this.output = null;
            this.tracking = null;
        }
    }

    @Override
    public void run(
            @NotNull JolCraftCrateLootTableProvider target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        this.tracking = tracking;

        //TODO: revise and and more to each loot table

        target.accept(
                JolCraftLootTables.Crates.SUPPLY_CRATE,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(5))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.SUPPLIES).setWeight(1))
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY).setWeight(1))
                                        .apply(
                                                SetComponentsFunction.setComponent(
                                                        JolCraftDataComponents.DWARF_LORE_KEY.get(),
                                                        LoreHelper.toLoreKeyString(DwarfLoreKey.FORGOTTEN_BREW_FORMULAS)
                                                )
                                        )
                                        .add(EmptyLootItem.emptyItem().setWeight(99))
                        )
        );


        target.accept(
                JolCraftLootTables.Crates.ALCHEMY_SUPPLIES,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(5))
                                        .add(LootItem.lootTableItem(JolCraftItems.INVERIX).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(JolCraftBlocks.DUSKCAP).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_COINS).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(JolCraftItems.DEEPMARROW_DUST).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(JolCraftItems.EARTHBLOOD_DUST).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(JolCraftItems.RUSTAGATE_DUST).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(JolCraftItems.SUNGLEAM_DUST).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(JolCraftItems.YANILLIAN_HOPS).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(Items.REDSTONE).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(Items.GLOWSTONE_DUST).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(Items.BLAZE_POWDER).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY).setWeight(1))
                                        .apply(
                                                SetComponentsFunction.setComponent(
                                                        JolCraftDataComponents.DWARF_LORE_KEY.get(),
                                                        LoreHelper.toLoreKeyString(DwarfLoreKey.ALCHEMY_RECIPES)
                                                )
                                        )
                                        .add(EmptyLootItem.emptyItem().setWeight(99))
                        )
        );

        target.accept(
                JolCraftLootTables.Crates.DWARVEN_FORTRESS_EXCAVATION,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(5))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Archaeology.DWARVEN_FORTRESS_COMMON).setWeight(99))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Archaeology.DWARVEN_FORTRESS_RARE).setWeight(1))
                        )
        );

        target.accept(
                JolCraftLootTables.Crates.ARTISAN_SUPPLIES,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(5))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.VANILLA_GEMS).setWeight(2))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.UNCUT_GEMS).setWeight(1))
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY).setWeight(1))
                                        .apply(
                                                SetComponentsFunction.setComponent(
                                                        JolCraftDataComponents.DWARF_LORE_KEY.get(),
                                                        LoreHelper.toLoreKeyString(DwarfLoreKey.ANCIENT_GEMCRAFT)
                                                )
                                        )
                                        .add(EmptyLootItem.emptyItem().setWeight(99))
                        )
        );

        target.accept(
                JolCraftLootTables.Crates.FARMING_SUPPLIES,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(5))
                                        .add(LootItem.lootTableItem(Items.WHEAT_SEEDS).setWeight(12)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(Items.BEETROOT_SEEDS).setWeight(12)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(JolCraftItems.BARLEY_SEEDS).setWeight(12)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))

                                        .add(LootItem.lootTableItem(Items.PUMPKIN_SEEDS).setWeight(6)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(Items.MELON_SEEDS).setWeight(6)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(Items.CARROT).setWeight(6)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(Items.POTATO).setWeight(6)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(Items.SUGAR_CANE).setWeight(6)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(Items.RED_MUSHROOM).setWeight(6)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(Items.BROWN_MUSHROOM).setWeight(6)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(JolCraftBlocks.FESTERLING).setWeight(6)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                                        .add(LootItem.lootTableItem(Items.COCOA_BEANS).setWeight(6)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(Items.CACTUS).setWeight(6)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(Items.SWEET_BERRIES).setWeight(6)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(Items.GLOW_BERRIES).setWeight(6)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))

                                        .add(LootItem.lootTableItem(Items.NETHER_WART).setWeight(3)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(Items.CRIMSON_FUNGUS).setWeight(3)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(Items.WARPED_FUNGUS).setWeight(3)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(JolCraftBlocks.DUSKCAP).setWeight(3)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_BULBS).setWeight(3)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))

                                        .add(LootItem.lootTableItem(Items.CHORUS_FRUIT).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(Items.TORCHFLOWER_SEEDS).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(Items.PITCHER_POD).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(JolCraftItems.ASGARNIAN_SEEDS).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                                        .add(LootItem.lootTableItem(JolCraftItems.DUSKHOLD_SEEDS).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                                        .add(LootItem.lootTableItem(JolCraftItems.KRANDONIAN_SEEDS).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                                        .add(LootItem.lootTableItem(JolCraftItems.YANILLIAN_SEEDS).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                                        .add(LootItem.lootTableItem(JolCraftBlocks.VERDANT_SOIL).setWeight(1)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        )
        );

        target.accept(
                JolCraftLootTables.Crates.MINING_CACHE,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(5))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.VANILLA_GEMS).setWeight(18))
                                        .add(LootItem.lootTableItem(Items.COAL).setWeight(15)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                                        .add(LootItem.lootTableItem(Items.RAW_COPPER).setWeight(15)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2, 4))))
                                        .add(LootItem.lootTableItem(Items.RAW_IRON).setWeight(12)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(Items.RAW_GOLD).setWeight(6)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                                        .add(LootItem.lootTableItem(Items.REDSTONE).setWeight(6)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                                        .add(LootItem.lootTableItem(JolCraftItems.IMPURE_MITHRIL).setWeight(1))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.GEODES).setWeight(1))
                        )
        );

        target.accept(
                JolCraftLootTables.Crates.FISHING_LOOT,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(5))
                                        .add(NestedLootTable.lootTableReference(BuiltInLootTables.FISHING_FISH).setWeight(289))
                                        .add(NestedLootTable.lootTableReference(BuiltInLootTables.FISHING_JUNK).setWeight(34))
                                        .add(NestedLootTable.lootTableReference(BuiltInLootTables.FISHING_TREASURE).setWeight(17))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Fishing.JUNK).setWeight(51))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Fishing.TREASURE).setWeight(9))
                        )
        );

        target.accept(
                JolCraftLootTables.Crates.BLACKSMITH_SUPPLIES,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Strongbox.DWARVEN_FORTRESS_FORGE).setWeight(1))
                        )
        );

        target.accept(
                JolCraftLootTables.Crates.MONSTER_SLAYER_LOOT,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(5))
                                        .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(12)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                                        .add(LootItem.lootTableItem(Items.BONE).setWeight(12)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                                        .add(LootItem.lootTableItem(Items.GUNPOWDER).setWeight(10)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                                        .add(LootItem.lootTableItem(Items.SLIME_BALL).setWeight(8)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(Items.SPIDER_EYE).setWeight(6)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(Items.PRISMARINE_SHARD).setWeight(5)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 5))))
                                        .add(LootItem.lootTableItem(Items.PRISMARINE_CRYSTALS).setWeight(5)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(Items.ENDER_PEARL).setWeight(4)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(Items.BLAZE_ROD).setWeight(4)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(Items.BREEZE_ROD).setWeight(4)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(Items.GHAST_TEAR).setWeight(3)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                                        .add(LootItem.lootTableItem(Items.SHULKER_SHELL).setWeight(2)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                                        .add(LootItem.lootTableItem(Items.TOTEM_OF_UNDYING).setWeight(1))
                        )
        );

        target.accept(
                JolCraftLootTables.Crates.VAULT_LOOT,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Strongbox.DWARVEN_FORTRESS_VAULT))
                        )
        );

        tracking.logTrackedOutputCount(
                this,
                JolCraftStrings.spaced(JolCraftDictionary.LOOT, JolCraftStrings.plural(JolCraftDictionary.TABLE))
        );
    }

    private void accept(@NotNull ResourceKey<LootTable> key, @NotNull LootTable.Builder builder) {
        BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output = this.output;
        if (output == null) {
            throw new IllegalStateException("Loot output not initialized: " + id());
        }

        output.accept(key, builder);

        JolCraftDataTracking tracking = this.tracking;
        if (tracking != null) {
            tracking.record(this, key.location().getPath());
        }
    }
}
