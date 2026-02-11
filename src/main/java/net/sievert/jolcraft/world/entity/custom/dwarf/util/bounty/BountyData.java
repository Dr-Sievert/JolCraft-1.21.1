package net.sievert.jolcraft.world.entity.custom.dwarf.util.bounty;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.key.JolCraftDictionary;

public record BountyData(ResourceLocation targetItem, int requiredCount, int tier, String type) {

    public static final Codec<BountyData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf(JolCraftDictionary.ITEM).forGetter(BountyData::targetItem),
            Codec.INT.fieldOf(JolCraftDictionary.AMOUNT).forGetter(BountyData::requiredCount),
            Codec.INT.fieldOf(JolCraftDictionary.TIER).forGetter(BountyData::tier),
            Codec.STRING.fieldOf(JolCraftDictionary.TYPE).forGetter(BountyData::type)
    ).apply(instance, BountyData::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BountyData> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8.map(ResourceLocation::parse, ResourceLocation::toString), BountyData::targetItem,
            ByteBufCodecs.VAR_INT, BountyData::requiredCount,
            ByteBufCodecs.VAR_INT, BountyData::tier,
            ByteBufCodecs.STRING_UTF8, BountyData::type,
            BountyData::new
    );
}