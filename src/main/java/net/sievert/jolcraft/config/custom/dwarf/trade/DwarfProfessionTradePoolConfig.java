package net.sievert.jolcraft.config.custom.dwarf.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record DwarfProfessionTradePoolConfig(
        DwarfProfessionTradePoolRolls rolls,
        TradeRerollType rerollType
) {

    public static final String KEY_ROLLS =
            JolCraftStrings.plural(JolCraftDictionary.ROLL);

    public static final String KEY_REROLL_TYPE =
            JolCraftStrings.underscored(JolCraftDictionary.REROLL, JolCraftDictionary.TYPE);

    private static final Codec<TradeRerollType> REROLL_TYPE_CODEC =
            Codec.STRING.comapFlatMap(
                    s -> {
                        if (s == null) {
                            return DataResult.error(() -> "reroll type is null");
                        }
                        return TradeRerollType.fromSerialized(s);
                    },
                    TradeRerollType::getId
            );

    public static final DwarfProfessionTradePoolConfig DEFAULTS =
            new DwarfProfessionTradePoolConfig(
                    DwarfProfessionTradePoolRolls.DEFAULTS,
                    TradeRerollType.RESTOCK
            );

    public static final Codec<DwarfProfessionTradePoolConfig> CODEC =
            RecordCodecBuilder.create((RecordCodecBuilder.Instance<DwarfProfessionTradePoolConfig> i) -> i.group(
                            DwarfProfessionTradePoolRolls.CODEC
                                    .optionalFieldOf(KEY_ROLLS, DEFAULTS.rolls())
                                    .forGetter(DwarfProfessionTradePoolConfig::rolls),

                            REROLL_TYPE_CODEC
                                    .optionalFieldOf(KEY_REROLL_TYPE, DEFAULTS.rerollType())
                                    .forGetter(DwarfProfessionTradePoolConfig::rerollType)
                    ).apply(i, DwarfProfessionTradePoolConfig::new))
                    .validate(DwarfProfessionTradePoolConfig::validate);

    public static @NotNull DataResult<DwarfProfessionTradePoolConfig> validate(
            DwarfProfessionTradePoolConfig config
    ) {
        if (config == null) {
            return DataResult.error(() -> "trade pool config is required");
        }

        if (config.rolls() == null) {
            return DataResult.error(() -> "rolls is required");
        }

        if (config.rerollType() == null) {
            return DataResult.error(() -> "reroll_type is required");
        }

        DataResult<DwarfProfessionTradePoolRolls> rollsValidation =
                DwarfProfessionTradePoolRolls.validate(config.rolls());

        var err = rollsValidation.error();
        return err.<DataResult<DwarfProfessionTradePoolConfig>>map(e ->
                DataResult.error(e::message)
        ).orElseGet(() -> DataResult.success(config));
    }

    public int rollsFor(@Nullable TradePoolType type, @Nullable DwarfMerchantData.Level level) {
        if (type == null || level == null) return 0;

        return switch (type) {
            case GLOBAL, EXACT_LEVEL -> rolls.rollsFor(level);
            case CUMULATIVE -> rolls.rollsUpTo(level);
        };
    }

    public boolean rerollsOnRestock() {
        return rerollType == TradeRerollType.RESTOCK;
    }

    public boolean rerollsOnlyOnManualReroll() {
        return rerollType == TradeRerollType.REROLL;
    }
}