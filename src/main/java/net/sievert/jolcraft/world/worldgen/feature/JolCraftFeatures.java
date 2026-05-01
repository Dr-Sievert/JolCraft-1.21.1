package net.sievert.jolcraft.world.worldgen.feature;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.worldgen.JolCraftFeatureIds;
import net.sievert.jolcraft.world.worldgen.feature.custom.JolCraftGeodeFeature;

public final class JolCraftFeatures {

    private JolCraftFeatures() {}

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, JolCraft.MOD_ID);

    public static final DeferredHolder<Feature<?>, Feature<GeodeConfiguration>> BASALT_GEODE =
            FEATURES.register(JolCraftFeatureIds.BASALT_GEODE, () -> new JolCraftGeodeFeature(GeodeConfiguration.CODEC));

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);
    }
}