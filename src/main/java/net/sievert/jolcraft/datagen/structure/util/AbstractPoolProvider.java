package net.sievert.jolcraft.datagen.structure.util;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.Arrays;
import java.util.function.Function;

public abstract class AbstractPoolProvider {

    protected final BootstrapContext<StructureTemplatePool> context;
    protected final Holder<StructureTemplatePool> emptyPool;
    protected final HolderGetter<StructureProcessorList> processors;
    protected final String directoryId;

    protected AbstractPoolProvider(BootstrapContext<StructureTemplatePool> context, String directoryId) {
        this.context = context;
        this.directoryId = directoryId;
        this.processors = context.lookup(Registries.PROCESSOR_LIST);

        HolderGetter<StructureTemplatePool> pools = context.lookup(Registries.TEMPLATE_POOL);
        this.emptyPool = pools.getOrThrow(Pools.EMPTY);
    }

    protected ResourceKey<StructureProcessorList> defaultProcessor() {
        return null;
    }

    protected static String path(String directoryId, String name) {
        return JolCraftStrings.slashed(directoryId, name);
    }

    protected static ResourceLocation location(String directoryId, String name) {
        return JolCraft.location(path(directoryId, name));
    }

    protected static ResourceKey<StructureTemplatePool> poolKey(String directoryId, String name) {
        return ResourceKey.create(Registries.TEMPLATE_POOL, location(directoryId, name));
    }

    protected record PoolEntry(
            Function<StructureTemplatePool.Projection, ? extends StructurePoolElement> element,
            int weight
    ) {}

    protected final PoolEntry entry(String template) {
        return entry(template, defaultProcessor(), 1);
    }

    protected final PoolEntry entry(String template, int weight) {
        return entry(template, defaultProcessor(), weight);
    }

    protected final PoolEntry entry(String template, ResourceKey<StructureProcessorList> processorKey) {
        return entry(template, processorKey, 1);
    }

    protected final PoolEntry entry(String template, ResourceKey<StructureProcessorList> processorKey, int weight) {
        validateWeight(template, weight);

        String templateLocation = location(directoryId, template).toString();

        return new PoolEntry(
                processorKey == null
                        ? StructurePoolElement.single(templateLocation)
                        : StructurePoolElement.single(templateLocation, processors.getOrThrow(processorKey)),
                weight
        );
    }

    protected final PoolEntry empty() {
        return empty(1);
    }

    protected static PoolEntry empty(int weight) {
        validateWeight(Pools.EMPTY.location().toString(), weight);
        return new PoolEntry(StructurePoolElement.empty(), weight);
    }

    protected final void registerRigid(
            ResourceKey<StructureTemplatePool> poolKey,
            String... templates
    ) {
        registerRigid(
                poolKey,
                Arrays.stream(templates)
                        .map(this::entry)
                        .toArray(PoolEntry[]::new)
        );
    }

    protected final void registerRigid(
            ResourceKey<StructureTemplatePool> poolKey,
            PoolEntry... entries
    ) {
        ImmutableList.Builder<Pair<Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>, Integer>> elements =
                ImmutableList.builder();

        for (PoolEntry entry : entries) {
            elements.add(Pair.of(entry.element(), entry.weight()));
        }

        context.register(
                poolKey,
                new StructureTemplatePool(
                        emptyPool,
                        elements.build(),
                        StructureTemplatePool.Projection.RIGID
                )
        );
    }

    private static void validateWeight(String name, int weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Template pool weight must be positive: " + name);
        }
    }
}