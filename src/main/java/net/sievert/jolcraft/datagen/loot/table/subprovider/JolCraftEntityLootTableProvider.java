package net.sievert.jolcraft.datagen.loot.table.subprovider;

import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.functions.SmeltItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataDomain;
import net.sievert.jolcraft.datagen.base.JolCraftMainDataProvider;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public final class JolCraftEntityLootTableProvider
        implements LootTableSubProvider, JolCraftMainDataProvider<JolCraftEntityLootTableProvider> {

    private static final Map<EntityType<?>, BiConsumer<LootTable.Builder, HolderLookup.Provider>> ENTITY_LOOT = new LinkedHashMap<>();

    static {
        ENTITY_LOOT.put(JolCraftEntities.MUFFHORN.get(), JolCraftEntityLootTableProvider::buildMuffhornLoot);
    }

    private final HolderLookup.Provider registries;
    private @Nullable BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output;
    private @Nullable JolCraftDataTracking tracking;

    public JolCraftEntityLootTableProvider(@NotNull HolderLookup.Provider registries) {
        this.registries = registries;
    }

    @Override
    public @NotNull JolCraftDataDomain domain() {
        return JolCraftDataDomain.LOOT;
    }

    @Override
    public @NotNull String id() {
        return JolCraftStrings.underscored(JolCraftDictionary.ENTITY, JolCraftDictionary.LOOT, JolCraftDictionary.TABLE);
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

    @SuppressWarnings("deprecation")
    @Override
    public void run(
            @NotNull JolCraftEntityLootTableProvider target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        this.tracking = tracking;

        for (var entry : ENTITY_LOOT.entrySet()) {
            EntityType<?> type = entry.getKey();
            BiConsumer<LootTable.Builder, HolderLookup.Provider> func = entry.getValue();

            ResourceLocation id = type.builtInRegistryHolder().key().location();
            ResourceLocation lootTableId = ResourceLocation.fromNamespaceAndPath(
                    id.getNamespace(),
                    JolCraftDictionary.ENTITIES + "/" + id.getPath()
            );
            ResourceKey<LootTable> lootTableKey = ResourceKey.create(Registries.LOOT_TABLE, lootTableId);

            LootTable.Builder builder = LootTable.lootTable();
            func.accept(builder, registries);
            target.accept(lootTableKey, builder);
        }

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

    private static void buildMuffhornLoot(@NotNull LootTable.Builder builder, @NotNull HolderLookup.Provider registries) {
        builder.withPool(
                LootPool.lootPool()
                        .setRolls(UniformGenerator.between(0, 1))
                        .add(LootItem.lootTableItem(JolCraftItems.MUFFHORN_FUR.get())
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F)))
                        )
        );

        builder.withPool(
                LootPool.lootPool()
                        .setRolls(UniformGenerator.between(1, 1))
                        .add(LootItem.lootTableItem(Items.LEATHER)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F)))
                        )
        );

        builder.withPool(
                LootPool.lootPool()
                        .setRolls(UniformGenerator.between(1, 1))
                        .add(LootItem.lootTableItem(Items.BEEF)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F)))
                                .apply(
                                        SmeltItemFunction.smelted().when(
                                                LootItemEntityPropertyCondition.hasProperties(
                                                        LootContext.EntityTarget.THIS,
                                                        EntityPredicate.Builder.entity()
                                                                .flags(EntityFlagsPredicate.Builder.flags().setOnFire(true))
                                                )
                                        )
                                )
                        )
        );
    }
}
