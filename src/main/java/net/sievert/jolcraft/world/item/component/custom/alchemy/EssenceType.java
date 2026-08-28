package net.sievert.jolcraft.world.item.component.custom.alchemy;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.util.JolCraftEnumHelper;

import java.util.Locale;

public enum EssenceType implements JolCraftEnumHelper.StringId {

    INFUSED(0xFF6ECBE5),
    REFINED(0xFFBF2900),
    EXALTED(0xFFE6C65C),
    CORRUPTED(0xFF401C4F);

    public static final Codec<EssenceType> CODEC =
            Codec.STRING.comapFlatMap(
                    EssenceType::decode,
                    EssenceType::getId
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, EssenceType> STREAM_CODEC =
            StreamCodec.of(
                    FriendlyByteBuf::writeEnum,
                    buffer -> buffer.readEnum(EssenceType.class)
            );

    private final int color;

    EssenceType(int color) {
        this.color = color;
    }

    @Override
    public String getId() {
        return name().toLowerCase(Locale.ROOT);
    }

    public int color() {
        return color;
    }

    public String translationKey() {
        return JolCraftLanguageKeys.essenceType(getId());
    }

    public Component getName() {
        return Component.translatable(translationKey());
    }

    private static DataResult<EssenceType> decode(String id) {
        EssenceType type = JolCraftEnumHelper.byStringIdNullable(
                EssenceType.class,
                id,
                null
        );

        return type == null
                ? DataResult.error(() -> "Unknown essence type: " + id)
                : DataResult.success(type);
    }

    public static EssenceType byId(String id) {
        return JolCraftEnumHelper.byStringId(
                EssenceType.class,
                id,
                INFUSED
        );
    }
}
