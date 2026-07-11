package net.sievert.jolcraft.datagen.loot.table.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.FishingHookPredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.predicates.LocationCheck;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataDomain;
import net.sievert.jolcraft.datagen.base.JolCraftMainDataProvider;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.loot.JolCraftLootTables;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftFishingLootTableProvider
        implements LootTableSubProvider, JolCraftMainDataProvider<JolCraftFishingLootTableProvider> {

    private final HolderLookup.Provider registries;
    private @Nullable BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output;
    private @Nullable JolCraftDataTracking tracking;

    public JolCraftFishingLootTableProvider(@NotNull HolderLookup.Provider registries) {
        this.registries = registries;
    }

    @Override
    public @NotNull JolCraftDataDomain domain() {
        return JolCraftDataDomain.LOOT;
    }

    @Override
    public @NotNull String id() {
        return JolCraftStrings.underscored(
                JolCraftDictionary.FISHING,
                JolCraftDictionary.LOOT,
                JolCraftDictionary.TABLE
        );
    }

    @Override
    public void generate(
            @NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output
    ) {
        this.output = output;
        this.tracking = null;

        try {
            generate(
                    this,
                    null,
                    CompletableFuture.completedFuture(registries),
                    null
            );
        } finally {
            this.output = null;
            this.tracking = null;
        }
    }

    @Override
    public void run(
            @NotNull JolCraftFishingLootTableProvider target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        this.tracking = tracking;

        HolderGetter<Biome> biomes = registries.lookupOrThrow(Registries.BIOME);

        target.accept(
                JolCraftLootTables.Fishing.FISHING,
                LootTable.lootTable().withPool(LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Fishing.JUNK)
                                                .setWeight(2)
                                                .setQuality(-2))
                                        .add(
                                                NestedLootTable.lootTableReference(JolCraftLootTables.Fishing.TREASURE)
                                                        .setWeight(1)
                                                        .setQuality(2)
                                                        .when(
                                                                LootItemEntityPropertyCondition.hasProperties(
                                                                        LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().subPredicate(FishingHookPredicate.inOpenWater(true))
                                                                )
                                                        )
                                                        .when(inBiome(biomes, JolCraftTags.Biomes.DWARVEN))
                                        )
                        ));

        target.accept(
                JolCraftLootTables.Fishing.JUNK,
                LootTable.lootTable().withPool(LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_COINS.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_PICKAXE.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_AMULET.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_BELT.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_MUG.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.EXPIRED_POTION.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.INGOT_MOULD.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_SCRAP.get()).setWeight(1))
                                            .when(inBiome(biomes, JolCraftTags.Biomes.DWARVEN))
                                        .add(LootItem.lootTableItem(JolCraftItems.OLD_FABRIC.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.RUSTY_TONGS.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_MITHRIL_SWORD.get()).setWeight(1))
                                            .when(inBiome(biomes, JolCraftTags.Biomes.DWARVEN))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_TABLET.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_DEEPSLATE_PLATES.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_MITHRIL_PLATE.get()).setWeight(1))
                                            .when(inBiome(biomes, JolCraftTags.Biomes.DWARVEN))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_DEEPSLATE_GEAR.get()).setWeight(1))
                                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_DEEPSLATE_PICKAXE_HEAD.get()).setWeight(1))
                        ));

        target.accept(
                JolCraftLootTables.Fishing.TREASURE,
                LootTable.lootTable().withPool(LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(JolCraftItems.UNIDENTIFIED_DWARVEN_TOME.get()).setWeight(9))
                                        .add(LootItem.lootTableItem(JolCraftItems.UNIDENTIFIED_ANCIENT_DWARVEN_TOME.get()).setWeight(1))
                        ));

        tracking.logTrackedOutputCount(
                this,
                JolCraftStrings.spaced(
                        JolCraftDictionary.LOOT,
                        JolCraftStrings.plural(JolCraftDictionary.TABLE)
                )
        );
    }

    private void accept(
            @NotNull ResourceKey<LootTable> key,
            @NotNull LootTable.Builder builder
    ) {
        BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output = this.output;

        if (output == null) {
            throw new IllegalStateException(
                    "Loot output not initialized: " + id()
            );
        }

        output.accept(key, builder);

        JolCraftDataTracking tracking = this.tracking;

        if (tracking != null) {
            tracking.record(this, key.location().getPath());
        }
    }

    public static LootItemCondition.Builder inBiome(
            HolderGetter<Biome> biomes,
            TagKey<Biome> tag
    ) {
        return LocationCheck.checkLocation(
                LocationPredicate.Builder.location().setBiomes(biomes.getOrThrow(tag))
        );
    }
}