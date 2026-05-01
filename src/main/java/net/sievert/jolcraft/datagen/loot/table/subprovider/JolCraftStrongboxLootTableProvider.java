package net.sievert.jolcraft.datagen.loot.table.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
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

        target.accept(
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
