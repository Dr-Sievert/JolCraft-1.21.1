package net.sievert.jolcraft.config.custom.dwarf.rule;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

public record DwarfProfessionRulesConfig(
        DwarfProfessionRule canSign,
        DwarfProfessionRule canEndorse,
        DwarfProfessionRule canTrade
) {

    public static final String KEY_CAN_SIGN =
            JolCraftStrings.underscored(JolCraftDictionary.CAN, JolCraftDictionary.SIGN);

    public static final String KEY_CAN_ENDORSE =
            JolCraftStrings.underscored(JolCraftDictionary.CAN, JolCraftDictionary.ENDORSE);

    public static final String KEY_CAN_TRADE =
            JolCraftStrings.underscored(JolCraftDictionary.CAN, JolCraftDictionary.TRADE);

    public static final DwarfProfessionRulesConfig DEFAULTS = new DwarfProfessionRulesConfig(
            DwarfProfessionRule.ALWAYS,
            DwarfProfessionRule.minMerchantLevel(1),
            DwarfProfessionRule.ALWAYS
    );

    public static final Codec<DwarfProfessionRulesConfig> CODEC =
            RecordCodecBuilder.create(i -> i.group(
                    DwarfProfessionRule.CODEC.optionalFieldOf(KEY_CAN_SIGN, DEFAULTS.canSign())
                            .forGetter(DwarfProfessionRulesConfig::canSign),

                    DwarfProfessionRule.CODEC.optionalFieldOf(KEY_CAN_ENDORSE, DEFAULTS.canEndorse())
                            .forGetter(DwarfProfessionRulesConfig::canEndorse),

                    DwarfProfessionRule.CODEC.optionalFieldOf(KEY_CAN_TRADE, DEFAULTS.canTrade())
                            .forGetter(DwarfProfessionRulesConfig::canTrade)
            ).apply(i, DwarfProfessionRulesConfig::new));
}