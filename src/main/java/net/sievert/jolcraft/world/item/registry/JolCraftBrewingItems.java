package net.sievert.jolcraft.world.item.registry;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.world.block.fluid.JolCraftFluids;
import net.sievert.jolcraft.world.block.fluid.util.brewing.BrewingColors;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.custom.brewing.TanninItem;
import net.sievert.jolcraft.world.item.custom.brewing.YeastCultureItem;
import net.sievert.jolcraft.world.item.custom.brewing.YeastItem;
import net.sievert.jolcraft.world.item.custom.food.brewing.DwarvenBrewBucketItem;
import net.sievert.jolcraft.world.item.custom.food.brewing.DwarvenBrewItem;
import net.sievert.jolcraft.world.item.food.JolCraftFoodProperties;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

import java.util.function.Supplier;

public final class JolCraftBrewingItems {

    public static final int BOTTLE_VOLUME = 250;

    private JolCraftBrewingItems() {}

    public static DeferredItem<Item> registerBarleyMalt() {
        return JolCraftItemRegistryHelper.registerSimpleItem(JolCraftItemIds.BARLEY_MALT);
    }

    public static DeferredItem<Item> registerYeastCulture() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.YEAST_CULTURE,
                properties -> new YeastCultureItem(
                        properties
                                .craftRemainder(
                                        Items.GLASS_BOTTLE
                                )
                                .stacksTo(
                                        16
                                )
                                .component(
                                        JolCraftDataComponents.BREWING_SPEED.get(),
                                        DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED
                                )
                )
        );
    }

    public static DeferredItem<Item> registerYeast(
            Supplier<? extends Fluid> fluid
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.YEAST,
                properties -> new YeastItem(
                        properties
                                .craftRemainder(
                                        Items.GLASS_BOTTLE
                                )
                                .stacksTo(
                                        16
                                )
                                .component(
                                        JolCraftDataComponents.FLUID_CONTENT.get(),
                                        SimpleFluidContent.copyOf(
                                                createYeastFluid(
                                                        fluid.get()
                                                )
                                        )
                                )
                )
        );
    }

    public static DeferredItem<Item> registerTannin(
            Supplier<? extends Fluid> fluid
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.TANNIN,
                properties -> new TanninItem(
                        properties
                                .craftRemainder(
                                        Items.GLASS_BOTTLE
                                )
                                .stacksTo(
                                        16
                                )
                                .component(
                                        JolCraftDataComponents.FLUID_CONTENT.get(),
                                        SimpleFluidContent.copyOf(
                                                createTanninFluid(
                                                        fluid.get(),
                                                        DwarvenBrewAge.MATURED
                                                )
                                        )
                                )
                )
        );
    }

    public static DeferredItem<Item> registerGlassMug() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.GLASS_MUG,
                properties -> new Item(
                        properties.stacksTo(
                                16
                        )
                )
        );
    }

    public static DeferredItem<Item> registerDwarvenBrew(
            DeferredItem<Item> glassMug
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.DWARVEN_BREW,
                properties -> new DwarvenBrewItem(
                        properties.food(
                                        JolCraftFoodProperties.DWARVEN_BREW
                                )
                                .craftRemainder(
                                        glassMug.get()
                                )
                                .stacksTo(
                                        1
                                )
                                .component(
                                        JolCraftDataComponents.FLUID_CONTENT.get(),
                                        SimpleFluidContent.copyOf(
                                                DwarvenBrewFluidHelper.createDwarvenBrew(
                                                        DwarvenBrewFluidHelper.MUG_VOLUME
                                                )
                                        )
                                )
                )
        );
    }

    public static DeferredItem<Item> registerDwarvenBrewBucket() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.DWARVEN_BREW_BUCKET,
                properties -> new DwarvenBrewBucketItem(
                        properties.craftRemainder(
                                        Items.BUCKET
                                )
                                .stacksTo(
                                        1
                                )
                                .component(
                                        JolCraftDataComponents.FLUID_CONTENT.get(),
                                        SimpleFluidContent.copyOf(
                                                DwarvenBrewFluidHelper.createDwarvenBrew(
                                                        FluidType.BUCKET_VOLUME
                                                )
                                        )
                                )
                )
        );
    }

    public static ItemStack createYeastCultureStack(
            ItemLike item,
            float brewingSpeed
    ) {
        ItemStack stack = new ItemStack(
                item
        );

        stack.set(
                JolCraftDataComponents.BREWING_SPEED.get(),
                brewingSpeed
        );

        return stack;
    }

    public static ItemStack createYeastStack(
            ItemLike item,
            float brewingSpeed
    ) {
        FluidStack fluid = createYeastFluid(
                JolCraftFluids.YEAST.get()
        );

        fluid.set(
                JolCraftDataComponents.BREWING_SPEED.get(),
                brewingSpeed
        );

        return createFilledBrewingStack(
                item,
                fluid
        );
    }

    public static ItemStack createTanninStack(
            ItemLike item,
            Fluid fluid,
            DwarvenBrewAge maxAge
    ) {
        return createFilledBrewingStack(
                item,
                createTanninFluid(
                        fluid,
                        maxAge
                )
        );
    }

    private static ItemStack createFilledBrewingStack(
            ItemLike item,
            FluidStack fluid
    ) {
        ItemStack stack = new ItemStack(
                item
        );

        stack.set(
                JolCraftDataComponents.FLUID_CONTENT.get(),
                SimpleFluidContent.copyOf(
                        fluid
                )
        );

        return stack;
    }

    public static ItemStack createDwarvenBrewStack(
            DwarvenBrewAge age
    ) {
        return createFilledBrewingStack(
                JolCraftItems.DWARVEN_BREW,
                DwarvenBrewFluidHelper.createDwarvenBrew(
                        DwarvenBrewFluidHelper.MUG_VOLUME,
                        age,
                        DwarvenBrewFluidHelper.DEFAULT_MAX_AGE,
                        DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED,
                        PotionContents.EMPTY.withEffectAdded(
                                new MobEffectInstance(
                                        MobEffects.DAMAGE_BOOST,
                                        600,
                                        age.amplifierBonus()
                                )
                        )
                )
        );
    }

    public static FluidStack createYeastFluid(
            Fluid fluid
    ) {
        FluidStack yeast =
                new FluidStack(
                        fluid,
                        BOTTLE_VOLUME
                );

        yeast.set(
                JolCraftDataComponents.BREW_COLOR.get(),
                BrewingColors.YEAST
        );

        yeast.set(
                JolCraftDataComponents.BREWING_SPEED.get(),
                DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED
        );

        return yeast;
    }

    public static FluidStack createTanninFluid(
            Fluid fluid,
            DwarvenBrewAge maxAge
    ) {
        FluidStack tannin =
                new FluidStack(
                        fluid,
                        BOTTLE_VOLUME
                );

        tannin.set(
                JolCraftDataComponents.BREW_COLOR.get(),
                fluid == JolCraftFluids.REFINED_TANNIN.get()
                        ? BrewingColors.REFINED_TANNIN
                        : BrewingColors.TANNIN
        );

        tannin.set(
                JolCraftDataComponents.MAX_BREW_AGE.get(),
                maxAge
        );

        return tannin;
    }
}