package net.sievert.jolcraft.datagen.loot.table.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.EnchantRandomlyFunction;
import net.minecraft.world.level.storage.loot.functions.EnchantWithLevelsFunction;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.lore.dwarf.DwarfLoreKey;
import net.sievert.jolcraft.world.item.lore.util.LoreHelper;
import net.sievert.jolcraft.world.loot.JolCraftLootTables;
import net.sievert.jolcraft.datagen.base.JolCraftDataDomain;
import net.sievert.jolcraft.datagen.base.JolCraftMainDataProvider;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftStrongboxLootTableProvider
        implements LootTableSubProvider, JolCraftMainDataProvider<JolCraftStrongboxLootTableProvider> {

    private final HolderLookup.Provider registries;
    private @Nullable BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output;
    private @Nullable JolCraftDataTracking tracking;

    public JolCraftStrongboxLootTableProvider(@NotNull HolderLookup.Provider registries) {
        this.registries = registries;
    }

    @Override
    public @NotNull JolCraftDataDomain domain() {
        return JolCraftDataDomain.LOOT;
    }

    @Override
    public @NotNull String id() {
        return JolCraftStrings.underscored(JolCraftDictionary.STRONGBOX, JolCraftDictionary.LOOT, JolCraftDictionary.TABLE);
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
            @NotNull JolCraftStrongboxLootTableProvider target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        this.tracking = tracking;
        HolderLookup.RegistryLookup<Enchantment> registrylookup = this.registries.lookupOrThrow(Registries.ENCHANTMENT);

        target.accept(
                JolCraftLootTables.Strongbox.DWARVEN_FORTRESS_FORGE,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(5))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.SMITHING_SALVAGE).setWeight(30))
                                        .add(LootItem.lootTableItem(Items.COAL).setWeight(19))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.VANILLA_METAL).setWeight(15))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.DWARVEN_METAL).setWeight(14))
                                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_CHAINWEAVE).setWeight(3))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.DEEPSLATE_ARMOR).setWeight(5))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.DEEPSLATE_GEAR).setWeight(5))
                                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_ARTISAN_HAMMER).setWeight(5))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.MITHRIL_ARMOR).setWeight(1))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.MITHRIL_GEAR).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_ARTISAN_HAMMER).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE).setWeight(1))
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY).setWeight(1))
                                        .apply(
                                                SetComponentsFunction.setComponent(
                                                        JolCraftDataComponents.DWARF_LORE_KEY.get(),
                                                        LoreHelper.toLoreKeyString(DwarfLoreKey.MITHRIL_FORGE_TECHNIQUE)
                                                )
                                        )
                                        .add(EmptyLootItem.emptyItem().setWeight(9))
                        )
        );

        target.accept(
                JolCraftLootTables.Strongbox.DWARVEN_FORTRESS_VAULT,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(5))
                                        .add(LootItem.lootTableItem(JolCraftItems.GOLD_COIN.get()).setWeight(50)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.VANILLA_GEMS).setWeight(12))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.UNCUT_GEMS).setWeight(32)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.MITHRIL_ARMOR).setWeight(3))
                                                .apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(5.0F, 30.0F)))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.MITHRIL_GEAR).setWeight(3))
                                                .apply(EnchantWithLevelsFunction.enchantWithLevels(this.registries, UniformGenerator.between(5.0F, 30.0F)))
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(UniformGenerator.between(1, 3))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.DWARVEN_TOMES).setWeight(13))
                                        .add(LootItem.lootTableItem(JolCraftItems.UNIDENTIFIED_LEGENDARY_ANCIENT_DWARVEN_TOME).setWeight(1))
                                        .add(EmptyLootItem.emptyItem().setWeight(6))
                        )
        );

        target.accept(
                JolCraftLootTables.Strongbox.DWARVEN_FORTRESS_GARDEN,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(7))
                                        .add(LootItem.lootTableItem(JolCraftItems.GOLD_COIN.get()).setWeight(22)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(Items.BONE_MEAL).setWeight(20)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(JolCraftBlocks.FESTERLING).setWeight(10))
                                        .add(LootItem.lootTableItem(JolCraftBlocks.DUSKCAP).setWeight(10))
                                        .add(LootItem.lootTableItem(JolCraftBlocks.VERDANT_SOIL).setWeight(10))
                                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_BULBS).setWeight(10))
                                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_HOE).setWeight(3))
                                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_PESTLE).setWeight(3))
                                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_HOE).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_PESTLE).setWeight(1))
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
                                        .add(EmptyLootItem.emptyItem().setWeight(9))
                        )
        );

        target.accept(
                JolCraftLootTables.Strongbox.DWARVEN_FORTRESS_ARCHIVES,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(5))
                                        .add(LootItem.lootTableItem(JolCraftItems.PARCHMENT.get()).setWeight(9)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(JolCraftItems.QUILL_EMPTY.get()).setWeight(2))
                                        .add(LootItem.lootTableItem(JolCraftItems.QUILL_SMALL.get()).setWeight(2))
                                        .add(LootItem.lootTableItem(JolCraftItems.QUILL_HALF.get()).setWeight(2))
                                        .add(LootItem.lootTableItem(JolCraftItems.QUILL_FULL.get()).setWeight(2))
                                        .add(LootItem.lootTableItem(JolCraftItems.LEGENDARY_PAGE.get()).setWeight(1)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(
                                                LootItem.lootTableItem(Items.BOOK)
                                                        .setWeight(1)
                                                        .apply(
                                                                new EnchantRandomlyFunction.Builder()
                                                                        .withOneOf(
                                                                                HolderSet.direct(
                                                                                        registrylookup.getOrThrow(Enchantments.MENDING),
                                                                                        registrylookup.getOrThrow(Enchantments.SHARPNESS),
                                                                                        registrylookup.getOrThrow(Enchantments.EFFICIENCY),
                                                                                        registrylookup.getOrThrow(Enchantments.FORTUNE),
                                                                                        registrylookup.getOrThrow(Enchantments.LOOTING),
                                                                                        registrylookup.getOrThrow(Enchantments.UNBREAKING)
                                                                                )
                                                                        )
                                                        )
                                        )
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(UniformGenerator.between(1, 4))
                                        .add(LootItem.lootTableItem(JolCraftItems.UNIDENTIFIED_DWARVEN_TOME).setWeight(30))
                                        .add(LootItem.lootTableItem(JolCraftItems.UNIDENTIFIED_ANCIENT_DWARVEN_TOME).setWeight(10))
                                        .add(LootItem.lootTableItem(JolCraftItems.UNIDENTIFIED_LEGENDARY_ANCIENT_DWARVEN_TOME).setWeight(1))
                                        .add(EmptyLootItem.emptyItem().setWeight(9))
                        )
        );

        target.accept(
                JolCraftLootTables.Strongbox.DWARVEN_FORTRESS_CATACOMBS,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(5))
                                        .add(LootItem.lootTableItem(JolCraftItems.GOLD_COIN.get()).setWeight(80)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(Items.BONE).setWeight(60)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 8.0F))))
                                        .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(43)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 8.0F))))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.UNCUT_GEMS).setWeight(5))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.DEEPSLATE_ARMOR).setWeight(5))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.DEEPSLATE_GEAR).setWeight(5))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.MITHRIL_ARMOR).setWeight(1))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.MITHRIL_GEAR).setWeight(1))
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(UniformGenerator.between(0, 1))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.DWARVEN_TOMES).setWeight(1))
                                        .add(EmptyLootItem.emptyItem().setWeight(3))
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
