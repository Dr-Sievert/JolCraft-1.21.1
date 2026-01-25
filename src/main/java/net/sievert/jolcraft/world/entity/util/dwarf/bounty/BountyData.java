package net.sievert.jolcraft.world.entity.util.dwarf.bounty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

public record BountyData(ResourceLocation targetItem, int requiredCount, int tier, String type) {

    public static final Codec<BountyData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("target_item").forGetter(BountyData::targetItem),
            Codec.INT.fieldOf("required_count").forGetter(BountyData::requiredCount),
            Codec.INT.fieldOf("tier").forGetter(BountyData::tier),
            Codec.STRING.fieldOf("type").forGetter(BountyData::type)
    ).apply(instance, BountyData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BountyData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(ResourceLocation::parse, ResourceLocation::toString), BountyData::targetItem,
            ByteBufCodecs.VAR_INT, BountyData::requiredCount,
            ByteBufCodecs.VAR_INT, BountyData::tier,
            ByteBufCodecs.STRING_UTF8, BountyData::type,
            BountyData::new
    );
}
