package net.sievert.jolcraft.datagen.block;

import net.minecraft.advancements.critereon.*;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.block.custom.*;
import net.sievert.jolcraft.block.custom.crop.*;
import net.sievert.jolcraft.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class JolCraftBlockLootTableProvider extends BlockLootSubProvider {
    public JolCraftBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {

        dropOther(JolCraftBlocks.DEEPSLATE_MORTAR.get(), JolCraftItems.DEEPSLATE_MORTAR_ITEM.get());

        add(JolCraftBlocks.GEODE_BLOCK.get(),
                createGeodeOreDrop(
                        JolCraftBlocks.GEODE_BLOCK.get(),
                        JolCraftItems.GEODE_SMALL.get(),
                        JolCraftItems.GEODE_MEDIUM.get(),
                        JolCraftItems.GEODE_LARGE.get()
                )
        );

        dropSelf(JolCraftBlocks.LAPIDARY_BENCH.get());

        add(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get(),
                block -> createOreDrop(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get(), JolCraftItems.IMPURE_MITHRIL.get()));
        dropSelf(JolCraftBlocks.PURE_MITHRIL_BLOCK.get());
        dropSelf(JolCraftBlocks.MITHRIL_BLOCK.get());

        dropSelf(JolCraftBlocks.DEEPSLATE_PLATE_BLOCK.get());

        dropOther(JolCraftBlocks.STRONGBOX.get(), JolCraftItems.STRONGBOX_ITEM.get());
        add(JolCraftBlocks.STRONGBOX_DUMMY.get(), LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(0))));


        add(JolCraftBlocks.HEARTH.get(), block ->
                createSinglePropConditionTable(block, HearthBlock.HALF, DoubleBlockHalf.LOWER)
        );

        dropSelf(JolCraftBlocks.VERDANT_SOIL.get());
        dropOther(JolCraftBlocks.VERDANT_FARMLAND.get(), JolCraftBlocks.VERDANT_SOIL.get());

        dropSelf(JolCraftBlocks.DUSKCAP.get());
        dropPottedContents(JolCraftBlocks.POTTED_DUSKCAP.get());

        dropSelf(JolCraftBlocks.FESTERLING.get());
        dropPottedContents(JolCraftBlocks.POTTED_FESTERLING.get());

        add(JolCraftBlocks.FESTERLING_CROP.get(),
                LootTable.lootTable()
                        .withPool(LootPool.lootPool()
                                .when(LootItemBlockStatePropertyCondition
                                        .hasBlockStateProperties(JolCraftBlocks.FESTERLING_CROP.get())
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FesterlingCropBlock.AGE, 0)))
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(Items.ROTTEN_FLESH))
                        )
                        .withPool(LootPool.lootPool()
                                .when(LootItemBlockStatePropertyCondition
                                        .hasBlockStateProperties(JolCraftBlocks.FESTERLING_CROP.get())
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(FesterlingCropBlock.AGE, 3)))
                                .setRolls(ConstantValue.exactly(1))
                                .add(LootItem.lootTableItem(JolCraftBlocks.FESTERLING.get()))
                        )
        );

        dropSelf(JolCraftBlocks.BARLEY_BLOCK.get());

        dropSelf(JolCraftBlocks.MUFFHORN_FUR_BLOCK.get());

        add(JolCraftBlocks.BARLEY_CROP.get(),
                createCropDrops(
                        JolCraftBlocks.BARLEY_CROP.get(),
                        JolCraftItems.BARLEY.get(),
                        JolCraftItems.BARLEY_SEEDS.get(),
                        BarleyCropBlock.AGE,
                        7
                )
        );

        add(JolCraftBlocks.DEEPSLATE_BULBS_CROP.get(),
                createSelfDropStoneCropDrops(
                        JolCraftBlocks.DEEPSLATE_BULBS_CROP.get(),
                        JolCraftItems.DEEPSLATE_BULBS.get()
                )
        );



        add(JolCraftBlocks.FERMENTING_CAULDRON.get(),
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(LootItem.lootTableItem(Blocks.CAULDRON))
                        )
        );


        addHopsCropDrops(
                JolCraftBlocks.ASGARNIAN_CROP_BOTTOM.get(),
                JolCraftBlocks.ASGARNIAN_CROP_TOP.get(),
                JolCraftItems.ASGARNIAN_SEEDS.get(),
                JolCraftItems.ASGARNIAN_HOPS.get()
        );

        addHopsCropDrops(
                JolCraftBlocks.DUSKHOLD_CROP_BOTTOM.get(),
                JolCraftBlocks.DUSKHOLD_CROP_TOP.get(),
                JolCraftItems.DUSKHOLD_SEEDS.get(),
                JolCraftItems.DUSKHOLD_HOPS.get()
        );

        addHopsCropDrops(
                JolCraftBlocks.KRANDONIAN_CROP_BOTTOM.get(),
                JolCraftBlocks.KRANDONIAN_CROP_TOP.get(),
                JolCraftItems.KRANDONIAN_SEEDS.get(),
                JolCraftItems.KRANDONIAN_HOPS.get()
        );

        addHopsCropDrops(
                JolCraftBlocks.YANILLIAN_CROP_BOTTOM.get(),
                JolCraftBlocks.YANILLIAN_CROP_TOP.get(),
                JolCraftItems.YANILLIAN_SEEDS.get(),
                JolCraftItems.YANILLIAN_HOPS.get()
        );

    }

    protected LootTable.Builder createCropDrops(Block cropBlock, Item cropItem, Item seedItem, IntegerProperty ageProperty, int maxAge) {
        LootItemCondition.Builder mature = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(cropBlock)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(ageProperty, maxAge));
        return createCropDrops(cropBlock, cropItem, seedItem, mature);
    }

    private void addHopsCropDrops(
            Block bottom,
            Block top,
            Item seed,
            Item hops
    ) {
        LootItemCondition.Builder isMatureBottom = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(bottom)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(HopsCropBottomBlock.AGE, HopsCropBottomBlock.MAX_AGE));

        LootTable.Builder bottomLoot = LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(seed))
                )
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(seed)
                                .when(isMatureBottom)
                                .when(LootItemRandomChanceCondition.randomChance(0.2F))
                        )
                )
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(hops)
                                .when(isMatureBottom)
                        )
                );

        LootItemCondition.Builder isMatureTop = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(top)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(HopsCropTopBlock.TOP_AGE, 4));

        LootTable.Builder topLoot = LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(seed))
                )
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(seed)
                                .when(isMatureTop)
                                .when(LootItemRandomChanceCondition.randomChance(0.2F))
                        )
                )
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(hops)
                                .when(isMatureTop)
                        )
                );

        add(bottom, bottomLoot);
        add(top, topLoot);
    }



    protected LootTable.Builder createSelfDropStoneCropDrops(Block cropBlock, Item item) {
        HolderLookup.RegistryLookup<Enchantment> registrylookup = registries.lookupOrThrow(Registries.ENCHANTMENT);

        LootItemCondition.Builder mature = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(cropBlock)
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(DeepslateBulbsCropBlock.AGE, 9));

        return LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(item))
                )
                .withPool(LootPool.lootPool()
                        .when(mature)
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(item)
                                .apply(ApplyBonusCount.addOreBonusCount(registrylookup.getOrThrow(Enchantments.FORTUNE)))
                        )
                )
                .withPool(LootPool.lootPool()
                        .when(mature)
                        .setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1)))
                                .when(LootItemRandomChanceCondition.randomChance(0.20f))
                        )
                );
    }

    protected LootTable.Builder createGeodeOreDrop(Block block, Item small, Item medium, Item large) {
        HolderLookup.RegistryLookup<Enchantment> enchantments = registries.lookupOrThrow(Registries.ENCHANTMENT);

        LootPool.Builder silkTouchPool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(hasSilkTouch())
                .add(LootItem.lootTableItem(block));

        LootPool.Builder geodePool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(hasSilkTouch().invert())
                .add(LootItem.lootTableItem(small).setWeight(4))
                .add(LootItem.lootTableItem(medium).setWeight(2))
                .add(LootItem.lootTableItem(large).setWeight(1));
        enchantments.get(Enchantments.FORTUNE).ifPresent(fortune ->
                geodePool.apply(ApplyBonusCount.addOreBonusCount(fortune))
        );
        geodePool.apply(ApplyExplosionDecay.explosionDecay());

        return LootTable.lootTable()
                .withPool(silkTouchPool)
                .withPool(geodePool);
    }

    protected LootTable.Builder createMultipleOreDrops(Block block, Item item, float minDrops, float maxDrops) {
        HolderLookup.RegistryLookup<Enchantment> enchantments = registries.lookupOrThrow(Registries.ENCHANTMENT);

        LootPool.Builder defaultPool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(hasSilkTouch().invert())
                .add(LootItem.lootTableItem(item)
                        .apply(SetItemCountFunction.setCount(UniformGenerator.between(minDrops, maxDrops)))
                );
        enchantments.get(Enchantments.FORTUNE).ifPresent(fortune ->
                defaultPool.apply(ApplyBonusCount.addOreBonusCount(fortune))
        );
        defaultPool.apply(ApplyExplosionDecay.explosionDecay());

        LootPool.Builder silkTouchPool = LootPool.lootPool()
                .setRolls(ConstantValue.exactly(1))
                .when(hasSilkTouch())
                .add(LootItem.lootTableItem(block));

        return LootTable.lootTable()
                .withPool(silkTouchPool)
                .withPool(defaultPool);
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return JolCraftBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
