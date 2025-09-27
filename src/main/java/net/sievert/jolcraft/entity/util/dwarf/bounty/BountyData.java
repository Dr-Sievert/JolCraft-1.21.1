package net.sievert.jolcraft.entity.util.dwarf.bounty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

public record BountyData(ResourceLocation targetItem, int requiredCount, int tier, String type) {
    public static final Codec<BountyData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("target_item").forGetter(BountyData::targetItem),
            Codec.INT.fieldOf("required_count").forGetter(BountyData::requiredCount),
            Codec.INT.fieldOf("tier").forGetter(BountyData::tier),
            Codec.STRING.fieldOf("type").forGetter(BountyData::type)
    ).apply(instance, BountyData::new));
}
