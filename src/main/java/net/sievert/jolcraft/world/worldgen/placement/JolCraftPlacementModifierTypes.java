package net.sievert.jolcraft.world.worldgen.placement;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.worldgen.JolCraftPlacementModifierTypeIds;
import net.sievert.jolcraft.world.worldgen.placement.custom.MaxYPlacementFilter;
import net.sievert.jolcraft.world.worldgen.placement.custom.MinYPlacementFilter;

public final class JolCraftPlacementModifierTypes {

    private JolCraftPlacementModifierTypes() {}

    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES =
            DeferredRegister.create(Registries.PLACEMENT_MODIFIER_TYPE, JolCraft.MOD_ID);

    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<MinYPlacementFilter>> MIN_Y =
            PLACEMENT_MODIFIER_TYPES.register(
                    JolCraftPlacementModifierTypeIds.MIN_Y,
                    () -> () -> MinYPlacementFilter.CODEC
            );

    public static final DeferredHolder<PlacementModifierType<?>, PlacementModifierType<MaxYPlacementFilter>> MAX_Y =
            PLACEMENT_MODIFIER_TYPES.register(
                    JolCraftPlacementModifierTypeIds.MAX_Y,
                    () -> () -> MaxYPlacementFilter.CODEC
            );

    public static void register(IEventBus eventBus) {
        PLACEMENT_MODIFIER_TYPES.register(eventBus);
    }
}