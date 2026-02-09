package net.sievert.jolcraft.datagen.loot;

import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
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
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class JolCraftEntityLootTableProvider implements LootTableSubProvider {

    private final HolderLookup.Provider registries;

    public JolCraftEntityLootTableProvider(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    private static final Map<EntityType<?>, BiConsumer<LootTable.Builder, HolderLookup.Provider>> ENTITY_LOOT = new LinkedHashMap<>();

    static {
        ENTITY_LOOT.put(JolCraftEntities.MUFFHORN.get(), JolCraftEntityLootTableProvider::buildMuffhornLoot);
    }

    private static void buildMuffhornLoot(LootTable.Builder builder, HolderLookup.Provider registries) {
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
                        .add(
                                LootItem.lootTableItem(Items.BEEF)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(1.0F, 3.0F)))
                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F)))
                                        .apply(
                                                SmeltItemFunction.smelted().when(
                                                        LootItemEntityPropertyCondition.hasProperties(
                                                                LootContext.EntityTarget.THIS,
                                                                EntityPredicate.Builder.entity()
                                                                        .flags(
                                                                                EntityFlagsPredicate.Builder.flags().setOnFire(true)
                                                                        )
                                                        )
                                                )
                                        )
                        )
        );
    }
    @SuppressWarnings("deprecation")
    @Override
    public void generate(@NotNull BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        for (var entry : ENTITY_LOOT.entrySet()) {
            EntityType<?> type = entry.getKey();
            BiConsumer<LootTable.Builder, HolderLookup.Provider> func = entry.getValue();

            ResourceLocation id = type.builtInRegistryHolder().key().location();

            ResourceLocation lootTableId =
                    ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "entities/" + id.getPath());
            ResourceKey<LootTable> lootTableKey = ResourceKey.create(Registries.LOOT_TABLE, lootTableId);

            LootTable.Builder builder = LootTable.lootTable();
            func.accept(builder, registries);
            output.accept(lootTableKey, builder);
        }
    }
}
