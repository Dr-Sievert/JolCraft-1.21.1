package net.sievert.jolcraft.config.custom.dwarf.rule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
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

    // partialDispatch, not dispatch: this codec is now also decoded from ClientboundConfigSyncPacket,
    // and a thrown exception there disconnects the player instead of failing the decode.
    Codec<DwarfProfessionRule> CODEC = Codec.STRING.partialDispatch(
            KEY_TYPE,
            rule -> DataResult.success(rule.typeId()),
            DwarfProfessionRule::mapCodecForType
    );

    DwarfProfessionRule ALWAYS = new Always();

    static DwarfProfessionRule minMerchantLevel(Level level) {
        return new MinMerchantLevel(level);
    }

    static DataResult<MapCodec<? extends DwarfProfessionRule>> mapCodecForType(
            String typeId
    ) {
        if (TYPE_ALWAYS.equals(typeId)) {
            return DataResult.success(Always.MAP_CODEC);
        }

        if (TYPE_MIN_MERCHANT_LEVEL.equals(typeId)) {
            return DataResult.success(MinMerchantLevel.MAP_CODEC);
        }

        return DataResult.error(
                () -> "Unknown rule type: " + typeId
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