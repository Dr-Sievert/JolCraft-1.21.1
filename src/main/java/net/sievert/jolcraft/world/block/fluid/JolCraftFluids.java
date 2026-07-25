package net.sievert.jolcraft.world.block.fluid;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.block.JolCraftFluidIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.block.fluid.custom.DwarvenBrewFluidType;
import net.sievert.jolcraft.world.item.JolCraftItems;

public final class JolCraftFluids {

    private JolCraftFluids() {}

    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(
                    NeoForgeRegistries.Keys.FLUID_TYPES,
                    JolCraft.MOD_ID
            );

    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(
                    BuiltInRegistries.FLUID,
                    JolCraft.MOD_ID
            );

    public static final DeferredHolder<FluidType, FluidType> DWARVEN_BREW_TYPE =
            FLUID_TYPES.register(
                    JolCraftFluidIds.DWARVEN_BREW,
                    () -> new DwarvenBrewFluidType(
                            FluidType.Properties.create()
                                    .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL)
                                    .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY)
                    )
            );

    public static final DeferredHolder<Fluid, FlowingFluid> DWARVEN_BREW =
            FLUIDS.register(
                    JolCraftFluidIds.DWARVEN_BREW,
                    () -> new BaseFlowingFluid.Source(
                            createDwarvenBrewProperties()
                    )
            );

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_DWARVEN_BREW =
            FLUIDS.register(
                    JolCraftStrings.underscored(
                            JolCraftDictionary.FLOWING,
                            JolCraftFluidIds.DWARVEN_BREW
                    ),
                    () -> new BaseFlowingFluid.Flowing(
                            createDwarvenBrewProperties()
                    )
            );

    public static final DeferredHolder<FluidType, FluidType> UNFINISHED_DWARVEN_BREW_TYPE =
            FLUID_TYPES.register(
                    JolCraftFluidIds.UNFINISHED_DWARVEN_BREW,
                    () -> new FluidType(
                            FluidType.Properties.create()
                    )
            );

    public static final DeferredHolder<Fluid, FlowingFluid> UNFINISHED_DWARVEN_BREW =
            FLUIDS.register(
                    JolCraftFluidIds.UNFINISHED_DWARVEN_BREW,
                    () -> new BaseFlowingFluid.Source(
                            createUnfinishedDwarvenBrewProperties()
                    )
            );

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_UNFINISHED_DWARVEN_BREW =
            FLUIDS.register(
                    JolCraftStrings.underscored(
                            JolCraftDictionary.FLOWING,
                            JolCraftFluidIds.UNFINISHED_DWARVEN_BREW
                    ),
                    () -> new BaseFlowingFluid.Flowing(
                            createUnfinishedDwarvenBrewProperties()
                    )
            );

    private static BaseFlowingFluid.Properties createDwarvenBrewProperties() {
        return new BaseFlowingFluid.Properties(
                DWARVEN_BREW_TYPE,
                DWARVEN_BREW,
                FLOWING_DWARVEN_BREW
        ).bucket(
                JolCraftItems.DWARVEN_BREW_BUCKET
        );
    }

    private static BaseFlowingFluid.Properties createUnfinishedDwarvenBrewProperties() {
        return new BaseFlowingFluid.Properties(
                UNFINISHED_DWARVEN_BREW_TYPE,
                UNFINISHED_DWARVEN_BREW,
                FLOWING_UNFINISHED_DWARVEN_BREW
        );
    }

    public static void register(IEventBus eventBus) {
        FLUID_TYPES.register(eventBus);
        FLUIDS.register(eventBus);
    }
}