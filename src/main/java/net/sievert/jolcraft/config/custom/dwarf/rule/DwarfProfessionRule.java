package net.sievert.jolcraft.config.custom.dwarf.rule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData.Level;

public sealed interface DwarfProfessionRule permits
        DwarfProfessionRule.Always,
        DwarfProfessionRule.MinMerchantLevel {

    String KEY_TYPE = JolCraftDictionary.TYPE;
    String KEY_LEVEL = JolCraftDictionary.LEVEL;

    String TYPE_ALWAYS = JolCraftDictionary.ALWAYS;

    String TYPE_MIN_MERCHANT_LEVEL = JolCraftStrings.underscored(
            JolCraftDictionary.MIN,
            JolCraftDictionary.MERCHANT,
            JolCraftDictionary.LEVEL
    );

    Codec<DwarfProfessionRule> CODEC = Codec.STRING.dispatch(
            KEY_TYPE,
            DwarfProfessionRule::typeId,
            DwarfProfessionRule::mapCodecForType
    );

    DwarfProfessionRule ALWAYS = new Always();

    static DwarfProfessionRule minMerchantLevel(Level level) {
        return new MinMerchantLevel(level);
    }

    static MapCodec<? extends DwarfProfessionRule> mapCodecForType(
            String typeId
    ) {
        if (TYPE_ALWAYS.equals(typeId)) {
            return Always.MAP_CODEC;
        }

        if (TYPE_MIN_MERCHANT_LEVEL.equals(typeId)) {
            return MinMerchantLevel.MAP_CODEC;
        }

        throw new IllegalStateException(
                "Unknown rule type: " + typeId
        );
    }

    String typeId();

    record Always() implements DwarfProfessionRule {

        static final MapCodec<Always> MAP_CODEC =
                MapCodec.unit(new Always());

        @Override
        public String typeId() {
            return TYPE_ALWAYS;
        }
    }

    record MinMerchantLevel(
            Level level
    ) implements DwarfProfessionRule {

        static final MapCodec<MinMerchantLevel> MAP_CODEC =
                RecordCodecBuilder.mapCodec(instance ->
                        instance.group(
                                Level.CODEC
                                        .fieldOf(KEY_LEVEL)
                                        .forGetter(MinMerchantLevel::level)
                        ).apply(instance, MinMerchantLevel::new)
                );

        @Override
        public String typeId() {
            return TYPE_MIN_MERCHANT_LEVEL;
        }
    }
}