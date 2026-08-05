package net.sievert.jolcraft.datagen.loot.table.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.block.fluid.util.brewing.BrewingColors;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.entity.effect.JolCraftEffects;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.loot.JolCraftLootTables;
import net.sievert.jolcraft.datagen.base.JolCraftDataDomain;
import net.sievert.jolcraft.datagen.base.JolCraftMainDataProvider;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class JolCraftChestLootTableProvider implements LootTableSubProvider, JolCraftMainDataProvider<JolCraftChestLootTableProvider> {

    private final HolderLookup.Provider registries;
    private @Nullable BiConsumer<ResourceKey<LootTable>, LootTable.Builder> output;
    private @Nullable JolCraftDataTracking tracking;

    public JolCraftChestLootTableProvider(@NotNull HolderLookup.Provider registries) {
        this.registries = registries;
    }

    @Override
    public @NotNull JolCraftDataDomain domain() {
        return JolCraftDataDomain.LOOT;
    }

    @Override
    public @NotNull String id() {
        return JolCraftStrings.underscored(JolCraftDictionary.CHEST, JolCraftDictionary.LOOT, JolCraftDictionary.TABLE);
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
            @NotNull JolCraftChestLootTableProvider target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        this.tracking = tracking;

        target.accept(JolCraftLootTables.Chests.VANILLA_GEMS,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.DIAMOND).setWeight(1))
                        .add(LootItem.lootTableItem(Items.EMERALD).setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 2))))
                        .add(LootItem.lootTableItem(Items.AMETHYST_SHARD).setWeight(3)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                        .add(LootItem.lootTableItem(Items.LAPIS_LAZULI).setWeight(4)
                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 10))))

        ));

        target.accept(JolCraftLootTables.Chests.VANILLA_METAL,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(Items.COPPER_INGOT).setWeight(3)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8))))
                        .add(LootItem.lootTableItem(Items.IRON_INGOT).setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                        .add(LootItem.lootTableItem(Items.GOLD_INGOT).setWeight(1)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))

                ));

        target.accept(JolCraftLootTables.Chests.DWARVEN_METAL,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_PLATE).setWeight(7)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 4))))
                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_NUGGET).setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 8))))
                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_INGOT).setWeight(1))
                ));

        target.accept(JolCraftLootTables.Chests.DEEPSLATE_ARMOR,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_HELMET.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_CHESTPLATE.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_LEGGINGS.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_BOOTS.get()).setWeight(1))
                ));

        target.accept(JolCraftLootTables.Chests.DEEPSLATE_GEAR,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_WARHAMMER.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_SWORD.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_PICKAXE.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_SHOVEL.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_AXE.get()).setWeight(1))
                ));

        target.accept(JolCraftLootTables.Chests.MITHRIL_ARMOR,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_HELMET.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_CHESTPLATE.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_LEGGINGS.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_BOOTS.get()).setWeight(1))
                ));

        target.accept(JolCraftLootTables.Chests.MITHRIL_GEAR,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_WARHAMMER.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_SWORD.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_PICKAXE.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_SHOVEL.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_AXE.get()).setWeight(1))
                ));

        target.accept(JolCraftLootTables.Chests.UNCUT_GEMS,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
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
                ));

        target.accept(JolCraftLootTables.Chests.MISC_SALVAGE,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_COINS.get()).setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                        .add(LootItem.lootTableItem(JolCraftItems.OLD_FABRIC.get()).setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_PICKAXE.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_AMULET.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_BELT.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.EXPIRED_POTION.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.RUSTY_TONGS.get()).setWeight(1))
                ));

        target.accept(JolCraftLootTables.Chests.DEEPSLATE_SALVAGE,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_DEEPSLATE_PLATES.get()).setWeight(1)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                        .add(LootItem.lootTableItem(JolCraftItems.DEEPSLATE_MUG.get()).setWeight(2))
                        .add(LootItem.lootTableItem(JolCraftItems.INGOT_MOULD.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.GUILD_SIGIL_MOULD.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_TABLET.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_DEEPSLATE_PICKAXE_HEAD.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_DEEPSLATE_GEAR.get()).setWeight(1))
                ));

        target.accept(JolCraftLootTables.Chests.MITHRIL_SALVAGE,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(JolCraftItems.MITHRIL_SCRAP.get()).setWeight(1)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_MITHRIL_SWORD.get()).setWeight(1))
                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_MITHRIL_PLATE.get()).setWeight(1))
                ));

        target.accept(JolCraftLootTables.Chests.SALVAGE,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.MISC_SALVAGE).setWeight(6))
                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.DEEPSLATE_SALVAGE).setWeight(3))
                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.MITHRIL_SALVAGE).setWeight(1))
                ));

        target.accept(JolCraftLootTables.Chests.SMITHING_SALVAGE,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(JolCraftItems.RUSTY_TONGS.get()).setWeight(2))
                        .add(LootItem.lootTableItem(JolCraftItems.INGOT_MOULD.get()).setWeight(2))
                        .add(LootItem.lootTableItem(JolCraftItems.BROKEN_DEEPSLATE_PLATES.get()).setWeight(2)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                        .add(NestedLootTable.lootTableReference(JolCraftLootTables.Chests.MITHRIL_SALVAGE).setWeight(1))
                ));

        target.accept(JolCraftLootTables.Chests.DWARVEN_TOMES,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(JolCraftItems.UNIDENTIFIED_DWARVEN_TOME.get()).setWeight(82))
                        .add(LootItem.lootTableItem(JolCraftItems.UNIDENTIFIED_ANCIENT_DWARVEN_TOME.get()).setWeight(17))
                        .add(LootItem.lootTableItem(JolCraftItems.UNIDENTIFIED_LEGENDARY_ANCIENT_DWARVEN_TOME.get()).setWeight(1))
                ));

        target.accept(JolCraftLootTables.Chests.GEODES,
                LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantValue.exactly(1))
                        .add(LootItem.lootTableItem(JolCraftItems.GEODE_SMALL.get()).setWeight(3))
                        .add(LootItem.lootTableItem(JolCraftItems.GEODE_MEDIUM.get()).setWeight(2))
                        .add(LootItem.lootTableItem(JolCraftItems.GEODE_LARGE.get()).setWeight(1))
                ));

        target.accept(JolCraftLootTables.Chests.SUPPLIES,
                LootTable.lootTable()
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(UniformGenerator.between(1, 4))
                                        .add(EmptyLootItem.emptyItem().setWeight(5))
                                        .add(LootItem.lootTableItem(Items.ROTTEN_FLESH).setWeight(5))
                                        .add(LootItem.lootTableItem(Items.BREAD).setWeight(5))
                                        .add(LootItem.lootTableItem(Items.BAKED_POTATO).setWeight(5))
                                        .add(LootItem.lootTableItem(Items.CARROT).setWeight(5))
                                        .add(LootItem.lootTableItem(Items.COOKED_BEEF).setWeight(3))
                                        .add(LootItem.lootTableItem(Items.COOKED_MUTTON).setWeight(3))
                                        .add(LootItem.lootTableItem(Items.COOKED_CHICKEN).setWeight(3))
                                        .add(LootItem.lootTableItem(Items.COOKED_PORKCHOP).setWeight(3))
                                        .add(LootItem.lootTableItem(Items.COOKED_COD).setWeight(3))
                                        .add(LootItem.lootTableItem(Items.COOKED_SALMON).setWeight(3))
                                        .add(LootItem.lootTableItem(Items.COOKED_PORKCHOP).setWeight(3))
                                        .add(LootItem.lootTableItem(Items.PUMPKIN_PIE).setWeight(1))
                                        .add(LootItem.lootTableItem(Items.BEETROOT_SOUP).setWeight(1))
                                        .add(LootItem.lootTableItem(Items.RABBIT_STEW).setWeight(1))
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(Items.GOLDEN_APPLE).setWeight(5))
                                        .add(LootItem.lootTableItem(Items.ENCHANTED_GOLDEN_APPLE).setWeight(1))
                                        .add(EmptyLootItem.emptyItem().setWeight(94))
                        )
                        .withPool(
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(3))
                                        .add(LootItem.lootTableItem(JolCraftItems.GLASS_MUG).setWeight(20))
                                        .add(LootItem.lootTableItem(Items.WHEAT).setWeight(10)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(JolCraftItems.BARLEY).setWeight(10)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 5))))
                                        .add(LootItem.lootTableItem(JolCraftItems.BARLEY_MALT).setWeight(6)
                                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(1, 3))))
                                        .add(LootItem.lootTableItem(JolCraftItems.YEAST).setWeight(6))
                                        .add(LootItem.lootTableItem(JolCraftItems.DWARVEN_BREW).setWeight(1)
                                                .apply(vintageBrewComponent(
                                                        MobEffects.DAMAGE_BOOST,
                                                        6000,
                                                        3
                                                        )
                                                )
                                        )
                                        .add(LootItem.lootTableItem(JolCraftItems.DWARVEN_BREW).setWeight(1)
                                                .apply(vintageBrewComponent(
                                                        JolCraftEffects.BULWARK,
                                                                6000,
                                                                3
                                                        )
                                                )
                                        )
                                        .add(LootItem.lootTableItem(JolCraftItems.DWARVEN_BREW).setWeight(1)
                                                .apply(vintageBrewComponent(
                                                                MobEffects.HEALTH_BOOST,
                                                                6000,
                                                                3
                                                        )
                                                )
                                        )
                                        .add(LootItem.lootTableItem(JolCraftItems.DWARVEN_BREW).setWeight(1)
                                                .apply(vintageBrewComponent(
                                                                MobEffects.ABSORPTION,
                                                                6000,
                                                                3
                                                        )
                                                )
                                        )
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

    @SuppressWarnings({"rawtypes", "SameParameterValue"})
    private static SetComponentsFunction.Builder vintageBrewComponent(
            Holder<MobEffect> effect,
            int duration,
            int amplifier
    ) {
        FluidStack brew = DwarvenBrewFluidHelper.createDwarvenBrew(
                DwarvenBrewFluidHelper.MUG_VOLUME,
                BrewingColors.DWARVEN_BREW,
                DwarvenBrewAge.VINTAGE.thresholdTicks(),
                DwarvenBrewAge.VINTAGE,
                DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED,
                new PotionContents(
                        Optional.empty(),
                        Optional.empty(),
                        List.of(
                                new MobEffectInstance(
                                        effect,
                                        duration,
                                        amplifier
                                )
                        )
                )
        );

        return SetComponentsFunction.setComponent(
                JolCraftDataComponents.FLUID_CONTENT.get(),
                SimpleFluidContent.copyOf(brew)
        );
    }
}
