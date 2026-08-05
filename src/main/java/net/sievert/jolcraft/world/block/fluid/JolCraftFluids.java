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
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
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

    // =====================================================================
    // Finished dwarven brew
    // =====================================================================

    public static final DeferredHolder<FluidType, FluidType> DWARVEN_BREW_TYPE =
            FLUID_TYPES.register(
                    JolCraftFluidIds.DWARVEN_BREW,
                    () -> new DwarvenBrewFluidType(
                            FluidType.Properties.create()
                                    .sound(
                                            SoundActions.BUCKET_FILL,
                                            SoundEvents.BUCKET_FILL
                                    )
                                    .sound(
                                            SoundActions.BUCKET_EMPTY,
                                            SoundEvents.BUCKET_EMPTY
                                    )
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
                    flowingId(
                            JolCraftFluidIds.DWARVEN_BREW
                    ),
                    () -> new BaseFlowingFluid.Flowing(
                            createDwarvenBrewProperties()
                    )
            );

    // =====================================================================
    // Unfinished dwarven brew
    // =====================================================================

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
                    flowingId(
                            JolCraftFluidIds.UNFINISHED_DWARVEN_BREW
                    ),
                    () -> new BaseFlowingFluid.Flowing(
                            createUnfinishedDwarvenBrewProperties()
                    )
            );

    // =====================================================================
    // Finished yeast
    // =====================================================================

    public static final DeferredHolder<FluidType, FluidType> YEAST_TYPE =
            FLUID_TYPES.register(
                    JolCraftFluidIds.YEAST,
                    () -> new FluidType(
                            FluidType.Properties.create()
                    )
            );

    public static final DeferredHolder<Fluid, FlowingFluid> YEAST =
            FLUIDS.register(
                    JolCraftFluidIds.YEAST,
                    () -> new BaseFlowingFluid.Source(
                            createYeastProperties()
                    )
            );

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_YEAST =
            FLUIDS.register(
                    flowingId(
                            JolCraftFluidIds.YEAST
                    ),
                    () -> new BaseFlowingFluid.Flowing(
                            createYeastProperties()
                    )
            );

    // =====================================================================
    // Unfinished yeast
    // =====================================================================

    public static final DeferredHolder<FluidType, FluidType> UNFINISHED_YEAST_TYPE =
            FLUID_TYPES.register(
                    JolCraftFluidIds.UNFINISHED_YEAST,
                    () -> new FluidType(
                            FluidType.Properties.create()
                    )
            );

    public static final DeferredHolder<Fluid, FlowingFluid> UNFINISHED_YEAST =
            FLUIDS.register(
                    JolCraftFluidIds.UNFINISHED_YEAST,
                    () -> new BaseFlowingFluid.Source(
                            createUnfinishedYeastProperties()
                    )
            );

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_UNFINISHED_YEAST =
            FLUIDS.register(
                    flowingId(
                            JolCraftFluidIds.UNFINISHED_YEAST
                    ),
                    () -> new BaseFlowingFluid.Flowing(
                            createUnfinishedYeastProperties()
                    )
            );

    // =====================================================================
    // Finished tannin
    // =====================================================================

    public static final DeferredHolder<FluidType, FluidType> TANNIN_TYPE =
            FLUID_TYPES.register(
                    JolCraftFluidIds.TANNIN,
                    () -> new FluidType(
                            FluidType.Properties.create()
                    )
            );

    public static final DeferredHolder<Fluid, FlowingFluid> TANNIN =
            FLUIDS.register(
                    JolCraftFluidIds.TANNIN,
                    () -> new BaseFlowingFluid.Source(
                            createTanninProperties()
                    )
            );

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_TANNIN =
            FLUIDS.register(
                    flowingId(
                            JolCraftFluidIds.TANNIN
                    ),
                    () -> new BaseFlowingFluid.Flowing(
                            createTanninProperties()
                    )
            );

    // =====================================================================
    // Finished refined tannin
    // =====================================================================

    public static final DeferredHolder<FluidType, FluidType> REFINED_TANNIN_TYPE =
            FLUID_TYPES.register(
                    JolCraftFluidIds.REFINED_TANNIN,
                    () -> new FluidType(
                            FluidType.Properties.create()
                    )
            );

    public static final DeferredHolder<Fluid, FlowingFluid> REFINED_TANNIN =
            FLUIDS.register(
                    JolCraftFluidIds.REFINED_TANNIN,
                    () -> new BaseFlowingFluid.Source(
                            createRefinedTanninProperties()
                    )
            );

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_REFINED_TANNIN =
            FLUIDS.register(
                    flowingId(
                            JolCraftFluidIds.REFINED_TANNIN
                    ),
                    () -> new BaseFlowingFluid.Flowing(
                            createRefinedTanninProperties()
                    )
            );

    // =====================================================================
    // Unfinished tannin
    // =====================================================================

    public static final DeferredHolder<FluidType, FluidType> UNFINISHED_TANNIN_TYPE =
            FLUID_TYPES.register(
                    JolCraftFluidIds.UNFINISHED_TANNIN,
                    () -> new FluidType(
                            FluidType.Properties.create()
                    )
            );

    public static final DeferredHolder<Fluid, FlowingFluid> UNFINISHED_TANNIN =
            FLUIDS.register(
                    JolCraftFluidIds.UNFINISHED_TANNIN,
                    () -> new BaseFlowingFluid.Source(
                            createUnfinishedTanninProperties()
                    )
            );

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_UNFINISHED_TANNIN =
            FLUIDS.register(
                    flowingId(
                            JolCraftFluidIds.UNFINISHED_TANNIN
                    ),
                    () -> new BaseFlowingFluid.Flowing(
                            createUnfinishedTanninProperties()
                    )
            );

    // =====================================================================
    // Properties
    // =====================================================================

    private static BaseFlowingFluid.Properties createDwarvenBrewProperties() {
        return createProperties(
                DWARVEN_BREW_TYPE,
                DWARVEN_BREW,
                FLOWING_DWARVEN_BREW
        ).bucket(
                JolCraftItems.DWARVEN_BREW_BUCKET
        );
    }

    private static BaseFlowingFluid.Properties createUnfinishedDwarvenBrewProperties() {
        return createProperties(
                UNFINISHED_DWARVEN_BREW_TYPE,
                UNFINISHED_DWARVEN_BREW,
                FLOWING_UNFINISHED_DWARVEN_BREW
        );
    }

    private static BaseFlowingFluid.Properties createYeastProperties() {
        return createProperties(
                YEAST_TYPE,
                YEAST,
                FLOWING_YEAST
        );
    }

    private static BaseFlowingFluid.Properties createUnfinishedYeastProperties() {
        return createProperties(
                UNFINISHED_YEAST_TYPE,
                UNFINISHED_YEAST,
                FLOWING_UNFINISHED_YEAST
        );
    }

    private static BaseFlowingFluid.Properties createTanninProperties() {
        return createProperties(
                TANNIN_TYPE,
                TANNIN,
                FLOWING_TANNIN
        );
    }

    private static BaseFlowingFluid.Properties createRefinedTanninProperties() {
        return createProperties(
                REFINED_TANNIN_TYPE,
                REFINED_TANNIN,
                FLOWING_REFINED_TANNIN
        );
    }

    private static BaseFlowingFluid.Properties createUnfinishedTanninProperties() {
        return createProperties(
                UNFINISHED_TANNIN_TYPE,
                UNFINISHED_TANNIN,
                FLOWING_UNFINISHED_TANNIN
        );
    }

    private static BaseFlowingFluid.Properties createProperties(
            DeferredHolder<FluidType, FluidType> fluidType,
            DeferredHolder<Fluid, FlowingFluid> source,
            DeferredHolder<Fluid, FlowingFluid> flowing
    ) {
        return new BaseFlowingFluid.Properties(
                fluidType,
                source,
                flowing
        );
    }

    private static String flowingId(
            String fluidId
    ) {
        return JolCraftStrings.underscored(
                JolCraftDictionary.FLOWING,
                fluidId
        );
    }

    public static void register(
            IEventBus eventBus
    ) {
        FLUID_TYPES.register(
                eventBus
        );

        FLUIDS.register(
                eventBus
        );

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued {} fluid types and {} fluids",
                FLUID_TYPES.getEntries().size(),
                FLUIDS.getEntries().size()
        );
    }
}