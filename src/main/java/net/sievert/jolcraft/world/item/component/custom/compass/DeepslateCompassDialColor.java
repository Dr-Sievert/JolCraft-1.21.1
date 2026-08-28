package net.sievert.jolcraft.world.item.component.custom.compass;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.client.JolCraftColors;

public record DeepslateCompassDialColor(String color) {

    private static final String COLOR = JolCraftDictionary.COLOR;

    private static final Codec<String> COLOR_CODEC = Codec.withAlternative(
            Codec.STRING.comapFlatMap(
                    DeepslateCompassDialColor::decodeColor,
                    value -> value
            ),
            Codec.INT.xmap(
                    JolCraftColors::hex,
                    JolCraftColors::rgb
            )
    );

    private static final Codec<DeepslateCompassDialColor> FULL_CODEC = RecordCodecBuilder.create(inst ->
            inst.group(
                    COLOR_CODEC.fieldOf(COLOR).forGetter(DeepslateCompassDialColor::color)
            ).apply(inst, DeepslateCompassDialColor::new)
    );

    public static final Codec<DeepslateCompassDialColor> CODEC =
            Codec.withAlternative(
                    FULL_CODEC,
                    COLOR_CODEC.xmap(
                            DeepslateCompassDialColor::new,
                            DeepslateCompassDialColor::color
                    )
            );

    public static final StreamCodec<ByteBuf, DeepslateCompassDialColor> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.STRING_UTF8,
                    DeepslateCompassDialColor::color,
                    DeepslateCompassDialColor::new
            );

    private static DataResult<String> decodeColor(String color) {
        try {
            return DataResult.success(
                    JolCraftColors.hex(
                            JolCraftColors.rgb(color)
                    )
            );
        } catch (IllegalArgumentException exception) {
            return DataResult.error(
                    exception::getMessage
            );
        }
    }
}
