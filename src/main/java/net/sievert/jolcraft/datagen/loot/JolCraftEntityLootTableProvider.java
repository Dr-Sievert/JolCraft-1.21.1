package net.sievert.jolcraft.datagen.loot;

import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
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
import net.sievert.jolcraft.entity.JolCraftEntities;
import net.sievert.jolcraft.item.JolCraftItems;

import java.util.function.BiConsumer;


public class JolCraftEntityLootTableProvider implements LootTableSubProvider {
    private final HolderLookup.Provider registries;

    public JolCraftEntityLootTableProvider(HolderLookup.Provider registries) {
        this.registries = registries;
    }

    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output) {
        ResourceLocation id = BuiltInRegistries.ENTITY_TYPE.getKey(JolCraftEntities.MUFFHORN.get());
        ResourceLocation lootTableId = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "entities/" + id.getPath());
        ResourceKey<LootTable> lootTableKey = ResourceKey.create(Registries.LOOT_TABLE, lootTableId);

        output.accept(
                lootTableKey,
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .setRolls(UniformGenerator.between(0, 1))
                                .add(LootItem.lootTableItem(JolCraftItems.MUFFHORN_FUR.get())
                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F)))
                                )
                        )
                        .withPool(LootPool.lootPool()
                                .setRolls(UniformGenerator.between(1, 1))
                                .add(LootItem.lootTableItem(Items.LEATHER)
                                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(0.0F, 2.0F)))
                                        .apply(EnchantedCountIncreaseFunction.lootingMultiplier(registries, UniformGenerator.between(0.0F, 1.0F)))
                                )
                        )
                        .withPool(LootPool.lootPool()
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
                        )


        );


    }




}