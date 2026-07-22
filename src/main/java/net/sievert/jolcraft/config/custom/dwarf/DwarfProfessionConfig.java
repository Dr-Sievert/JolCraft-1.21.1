package net.sievert.jolcraft.config.custom.dwarf;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.sievert.jolcraft.config.custom.dwarf.attribute.DwarfProfessionAttributesConfig;
import net.sievert.jolcraft.config.custom.dwarf.rule.DwarfProfessionRulesConfig;
import net.sievert.jolcraft.config.custom.dwarf.sound.DwarfProfessionSoundsConfig;
import net.sievert.jolcraft.config.custom.dwarf.trade.DwarfProfessionTradePoolsConfig;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

public record DwarfProfessionConfig(
        int requiredTier,
        long restockTicks,
        float voicePitch,

        boolean canReroll,
        boolean canEndorse,

        boolean showProgressBar,
        boolean showLevel,

        DwarfProfessionRulesConfig rules,
        DwarfProfessionSoundsConfig sounds,
        DwarfProfessionAttributesConfig attributes,
        DwarfProfessionTradePoolsConfig tradePools
) {

    // Keys

    public static final String KEY_REQUIRED_TIER =
            JolCraftStrings.underscored(JolCraftDictionary.REQUIRED, JolCraftDictionary.TIER);

    public static final String KEY_RESTOCK_TICKS =
            JolCraftStrings.underscored(JolCraftDictionary.RESTOCK, JolCraftStrings.plural(JolCraftDictionary.TICK));

    public static final String KEY_VOICE_PITCH =
            JolCraftStrings.underscored(JolCraftDictionary.VOICE, JolCraftDictionary.PITCH);

    public static final String KEY_CAN_REROLL =
            JolCraftStrings.underscored(JolCraftDictionary.CAN, JolCraftDictionary.REROLL);

    public static final String KEY_CAN_ENDORSE =
            JolCraftStrings.underscored(JolCraftDictionary.CAN, JolCraftDictionary.ENDORSE);

    public static final String KEY_SHOW_PROGRESS_BAR =
            JolCraftStrings.underscored(JolCraftDictionary.SHOW, JolCraftDictionary.PROGRESS, JolCraftDictionary.BAR);

    public static final String KEY_SHOW_LEVEL =
            JolCraftStrings.underscored(JolCraftDictionary.SHOW, JolCraftDictionary.LEVEL);

    public static final String KEY_RULES =
            JolCraftStrings.plural(JolCraftDictionary.RULE);

    public static final String KEY_SOUNDS =
            JolCraftStrings.plural(JolCraftDictionary.SOUND);

    public static final String KEY_ATTRIBUTES =
            JolCraftStrings.plural(JolCraftDictionary.ATTRIBUTE);

    public static final String KEY_TRADE_POOLS =
            JolCraftStrings.underscored(JolCraftDictionary.TRADE, JolCraftStrings.plural(JolCraftDictionary.POOL));

    // Defaults

    public static final DwarfProfessionConfig DEFAULTS = new DwarfProfessionConfig(
            0,
            6000L,
            1.0F,

            true,
            true,

            true,
            true,

            DwarfProfessionRulesConfig.DEFAULTS,
            DwarfProfessionSoundsConfig.DEFAULTS,
            DwarfProfessionAttributesConfig.DEFAULTS,
            DwarfProfessionTradePoolsConfig.DEFAULTS
    );

    // Codec

    public static final Codec<DwarfProfessionConfig> CODEC =
            RecordCodecBuilder.create((RecordCodecBuilder.Instance<DwarfProfessionConfig> i) -> i.group(

                            Codec.INT.optionalFieldOf(KEY_REQUIRED_TIER, DEFAULTS.requiredTier())
                                    .forGetter(DwarfProfessionConfig::requiredTier),

                            Codec.LONG.optionalFieldOf(KEY_RESTOCK_TICKS, DEFAULTS.restockTicks())
                                    .forGetter(DwarfProfessionConfig::restockTicks),

                            Codec.FLOAT.optionalFieldOf(KEY_VOICE_PITCH, DEFAULTS.voicePitch())
                                    .forGetter(DwarfProfessionConfig::voicePitch),

                            Codec.BOOL.optionalFieldOf(KEY_CAN_REROLL, DEFAULTS.canReroll())
                                    .forGetter(DwarfProfessionConfig::canReroll),

                            Codec.BOOL.optionalFieldOf(KEY_CAN_ENDORSE, DEFAULTS.canEndorse())
                                    .forGetter(DwarfProfessionConfig::canEndorse),

                            Codec.BOOL.optionalFieldOf(KEY_SHOW_PROGRESS_BAR, DEFAULTS.showProgressBar())
                                    .forGetter(DwarfProfessionConfig::showProgressBar),

                            Codec.BOOL.optionalFieldOf(KEY_SHOW_LEVEL, DEFAULTS.showLevel())
                                    .forGetter(DwarfProfessionConfig::showLevel),

                            DwarfProfessionRulesConfig.CODEC.optionalFieldOf(KEY_RULES, DEFAULTS.rules())
                                    .forGetter(DwarfProfessionConfig::rules),

                            DwarfProfessionSoundsConfig.CODEC.optionalFieldOf(KEY_SOUNDS, DEFAULTS.sounds())
                                    .forGetter(DwarfProfessionConfig::sounds),

                            DwarfProfessionAttributesConfig.CODEC.optionalFieldOf(KEY_ATTRIBUTES, DEFAULTS.attributes())
                                    .forGetter(DwarfProfessionConfig::attributes),

                            DwarfProfessionTradePoolsConfig.CODEC.optionalFieldOf(KEY_TRADE_POOLS, DEFAULTS.tradePools())
                                    .forGetter(DwarfProfessionConfig::tradePools)

                    ).apply(i, DwarfProfessionConfig::new))
                    .validate(DwarfProfessionConfig::validate);

    // Validation

    private static @NotNull DataResult<DwarfProfessionConfig> validate(DwarfProfessionConfig cfg) {
        if (cfg.requiredTier() < 0) {
            return DataResult.error(() -> "required_tier must be >= 0");
        }
        if (cfg.restockTicks() <= 0L) {
            return DataResult.error(() -> "restock_ticks must be > 0");
        }
        if (cfg.voicePitch() <= 0.0F) {
            return DataResult.error(() -> "voice_pitch must be > 0");
        }

        if (cfg.rules() == null) {
            return DataResult.error(() -> "rules are required");
        }
        if (cfg.sounds() == null) {
            return DataResult.error(() -> "sounds are required");
        }
        if (cfg.attributes() == null) {
            return DataResult.error(() -> "attributes are required");
        }
        if (cfg.tradePools() == null) {
            return DataResult.error(() -> "trade_pools are required");
        }

        DataResult<DwarfProfessionTradePoolsConfig> poolsValidation =
                DwarfProfessionTradePoolsConfig.validate(cfg.tradePools());

        var err = poolsValidation.error();
        return err.<DataResult<DwarfProfessionConfig>>map(e ->
                DataResult.error(e::message)).orElseGet(() -> DataResult.success(cfg));
    }
}