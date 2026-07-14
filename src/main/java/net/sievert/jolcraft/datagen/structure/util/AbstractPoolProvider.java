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
    protected final String directoryId;

    private final HolderGetter<StructureTemplatePool> pools;
    private final Holder<StructureTemplatePool> emptyPool;
    private final HolderGetter<StructureProcessorList> processors;
    private final ResourceKey<StructureProcessorList> defaultProcessor;

    protected AbstractPoolProvider(
            BootstrapContext<StructureTemplatePool> context,
            String directoryId
    ) {
        this(context, directoryId, null);
    }

    protected AbstractPoolProvider(
            BootstrapContext<StructureTemplatePool> context,
            String directoryId,
            ResourceKey<StructureProcessorList> defaultProcessor
    ) {
        this.context = context;
        this.directoryId = directoryId;
        this.defaultProcessor = defaultProcessor;

        this.pools = context.lookup(Registries.TEMPLATE_POOL);
        this.processors = context.lookup(Registries.PROCESSOR_LIST);
        this.emptyPool = pools.getOrThrow(Pools.EMPTY);
    }

    protected static ResourceKey<StructureTemplatePool> poolKey(
            String directoryId,
            String name
    ) {
        return ResourceKey.create(
                Registries.TEMPLATE_POOL,
                location(directoryId, name)
        );
    }

    protected static ResourceLocation location(String directoryId, String name) {
        return JolCraft.location(JolCraftStrings.slashed(directoryId, name));
    }

    protected record PoolEntry(
            Function<StructureTemplatePool.Projection, ? extends StructurePoolElement> element,
            int weight
    ) {}

    protected final PoolEntry entry(String template) {
        return entry(template, 1);
    }

    protected final PoolEntry entry(String template, int weight) {
        return element(location(directoryId, template), defaultProcessor, weight);
    }

    protected final PoolEntry processed(
            String template,
            ResourceKey<StructureProcessorList> processor
    ) {
        return processed(template, processor, 1);
    }

    protected final PoolEntry processed(
            String template,
            ResourceKey<StructureProcessorList> processor,
            int weight
    ) {
        return element(location(directoryId, template), processor, weight);
    }

    protected final PoolEntry external(
            String directoryId,
            String template,
            ResourceKey<StructureProcessorList> processor,
            int weight
    ) {
        return element(location(directoryId, template), processor, weight);
    }

    protected static PoolEntry empty() {
        return empty(1);
    }

    protected static PoolEntry empty(int weight) {
        validateWeight(Pools.EMPTY.location().toString(), weight);
        return new PoolEntry(StructurePoolElement.empty(), weight);
    }

    protected final void register(
            ResourceKey<StructureTemplatePool> poolKey,
            String... templates
    ) {
        register(
                poolKey,
                Arrays.stream(templates)
                        .map(this::entry)
                        .toArray(PoolEntry[]::new)
        );
    }

    protected final void register(
            ResourceKey<StructureTemplatePool> poolKey,
            ResourceKey<StructureTemplatePool> fallbackKey,
            String... templates
    ) {
        register(
                poolKey,
                fallbackKey,
                Arrays.stream(templates)
                        .map(this::entry)
                        .toArray(PoolEntry[]::new)
        );
    }

    protected final void register(
            ResourceKey<StructureTemplatePool> poolKey,
            PoolEntry... entries
    ) {
        register(poolKey, emptyPool, entries);
    }

    protected final void register(
            ResourceKey<StructureTemplatePool> poolKey,
            ResourceKey<StructureTemplatePool> fallbackKey,
            PoolEntry... entries
    ) {
        register(poolKey, pools.getOrThrow(fallbackKey), entries);
    }

    private void register(
            ResourceKey<StructureTemplatePool> poolKey,
            Holder<StructureTemplatePool> fallback,
            PoolEntry... entries
    ) {
        ImmutableList.Builder<Pair<
                Function<StructureTemplatePool.Projection, ? extends StructurePoolElement>,
                Integer
                >> elements = ImmutableList.builder();

        for (PoolEntry entry : entries) {
            elements.add(Pair.of(entry.element(), entry.weight()));
        }

        context.register(
                poolKey,
                new StructureTemplatePool(
                        fallback,
                        elements.build(),
                        StructureTemplatePool.Projection.RIGID
                )
        );
    }

    private PoolEntry element(
            ResourceLocation template,
            ResourceKey<StructureProcessorList> processor,
            int weight
    ) {
        validateWeight(template.toString(), weight);

        return new PoolEntry(
                processor == null
                        ? StructurePoolElement.single(template.toString())
                        : StructurePoolElement.single(
                        template.toString(),
                        processors.getOrThrow(processor)
                ),
                weight
        );
    }

    private static void validateWeight(String name, int weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException(
                    "Template pool weight must be positive: " + name
            );
        }
    }
}