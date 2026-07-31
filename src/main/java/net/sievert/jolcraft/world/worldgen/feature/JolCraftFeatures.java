package net.sievert.jolcraft.world.worldgen.feature;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.GeodeConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.HugeMushroomFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.worldgen.JolCraftFeatureIds;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.worldgen.feature.custom.HugeDuskcapFeature;
import net.sievert.jolcraft.world.worldgen.feature.custom.HugeFesterlingFeature;
import net.sievert.jolcraft.world.worldgen.feature.custom.JolCraftGeodeFeature;

public final class JolCraftFeatures {

    private JolCraftFeatures() {}

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(
                    Registries.FEATURE,
                    JolCraft.MOD_ID
            );

    public static final DeferredHolder<Feature<?>, Feature<GeodeConfiguration>> BASALT_GEODE =
            FEATURES.register(
                    JolCraftFeatureIds.BASALT_GEODE,
                    () -> new JolCraftGeodeFeature(
                            GeodeConfiguration.CODEC
                    )
            );

    public static final DeferredHolder<Feature<?>, Feature<HugeMushroomFeatureConfiguration>> HUGE_DUSKCAP =
            FEATURES.register(
                    JolCraftFeatureIds.HUGE_DUSKCAP,
                    () -> new HugeDuskcapFeature(
                            HugeMushroomFeatureConfiguration.CODEC
                    )
            );

    public static final DeferredHolder<Feature<?>, Feature<HugeMushroomFeatureConfiguration>> HUGE_FESTERLING =
            FEATURES.register(
                    JolCraftFeatureIds.HUGE_FESTERLING,
                    () -> new HugeFesterlingFeature(
                            HugeMushroomFeatureConfiguration.CODEC
                    )
            );

    public static void register(IEventBus eventBus) {
        FEATURES.register(eventBus);

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued {} worldgen features",
                FEATURES.getEntries().size()
        );
    }
}