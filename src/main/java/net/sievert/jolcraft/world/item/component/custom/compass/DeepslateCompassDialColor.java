package net.sievert.jolcraft.world.item.component.custom.compass;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.language.JolCraftDictionary;

public record DeepslateCompassDialColor(int color) {

    private static final String COLOR = JolCraftDictionary.COLOR;

    private static final Codec<DeepslateCompassDialColor> FULL_CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    Codec.INT.fieldOf(COLOR).forGetter(DeepslateCompassDialColor::color)
            ).apply(inst, DeepslateCompassDialColor::new)
    );

    public static final Codec<DeepslateCompassDialColor> CODEC =
            Codec.withAlternative(FULL_CODEC, Codec.INT, DeepslateCompassDialColor::new);

    public static final StreamCodec<ByteBuf, DeepslateCompassDialColor> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.INT,
                    DeepslateCompassDialColor::color,
                    DeepslateCompassDialColor::new
            );
}