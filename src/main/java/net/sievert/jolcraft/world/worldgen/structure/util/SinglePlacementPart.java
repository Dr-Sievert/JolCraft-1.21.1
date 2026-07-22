package net.sievert.jolcraft.world.worldgen.structure.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public record SinglePlacementPart(
        ResourceKey<StructureTemplatePool> pool,
        ResourceLocation template
) {

    public static final Codec<SinglePlacementPart> CODEC =
            RecordCodecBuilder.create(instance ->
                    instance.group(
                            ResourceKey.codec(Registries.TEMPLATE_POOL)
                                    .fieldOf(JolCraftDictionary.POOL)
                                    .forGetter(SinglePlacementPart::pool),
                            ResourceLocation.CODEC
                                    .fieldOf(JolCraftDictionary.TEMPLATE)
                                    .forGetter(SinglePlacementPart::template)
                    ).apply(instance, SinglePlacementPart::new)
            );
}