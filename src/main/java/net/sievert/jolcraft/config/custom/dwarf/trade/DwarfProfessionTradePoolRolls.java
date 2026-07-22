package net.sievert.jolcraft.config.custom.dwarf.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

public record DwarfProfessionTradePoolRolls(
        Map<DwarfMerchantData.Level, Integer> rolls
) {

    private static final Codec<DwarfMerchantData.Level> LEVEL_KEY_CODEC =
            Codec.STRING.comapFlatMap(
                    s -> {
                        if (s == null) {
                            return DataResult.error(() -> "merchant level key is null");
                        }
                        try {
                            return DataResult.success(
                                    DwarfMerchantData.Level.valueOf(s.trim().toUpperCase(Locale.ROOT))
                            );
                        } catch (IllegalArgumentException e) {
                            return DataResult.error(() -> "unknown merchant level '" + s + "'");
                        }
                    },
                    lvl -> lvl.name().toLowerCase(Locale.ROOT)
            );

    public static final DwarfProfessionTradePoolRolls DEFAULTS =
            new DwarfProfessionTradePoolRolls(Map.of());

    public static final Codec<DwarfProfessionTradePoolRolls> CODEC =
            Codec.unboundedMap(LEVEL_KEY_CODEC, Codec.INT)
                    .xmap(DwarfProfessionTradePoolRolls::new, DwarfProfessionTradePoolRolls::rolls)
                    .validate(DwarfProfessionTradePoolRolls::validate);

    public DwarfProfessionTradePoolRolls {
        rolls = rolls != null ? Map.copyOf(rolls) : Map.of();
    }

    public static @NotNull DataResult<DwarfProfessionTradePoolRolls> validate(DwarfProfessionTradePoolRolls rolls) {
        if (rolls == null) {
            return DataResult.error(() -> "rolls is required");
        }

        for (Map.Entry<DwarfMerchantData.Level, Integer> entry : rolls.rolls().entrySet()) {
            DwarfMerchantData.Level level = entry.getKey();
            Integer value = entry.getValue();

            if (level == null) {
                return DataResult.error(() -> "trade pool level key cannot be null");
            }

            if (value == null) {
                return DataResult.error(() ->
                        "trade pool rolls for " + level.name().toLowerCase(Locale.ROOT) + " cannot be null");
            }

            if (value < 0) {
                return DataResult.error(() ->
                        "trade pool rolls for " + level.name().toLowerCase(Locale.ROOT) + " must be >= 0");
            }
        }

        return DataResult.success(rolls);
    }

    public int rollsFor(@Nullable DwarfMerchantData.Level level) {
        if (level == null) return 0;
        return Math.max(0, rolls.getOrDefault(level, 0));
    }

    public int rollsUpTo(@Nullable DwarfMerchantData.Level level) {
        if (level == null) return 0;

        int total = 0;
        for (DwarfMerchantData.Level candidate : DwarfMerchantData.Level.values()) {
            if (candidate.getId() <= level.getId()) {
                total += Math.max(0, rolls.getOrDefault(candidate, 0));
            }
        }

        return Math.max(0, total);
    }
}