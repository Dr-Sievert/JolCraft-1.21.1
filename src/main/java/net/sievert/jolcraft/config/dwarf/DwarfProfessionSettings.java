package net.sievert.jolcraft.config.dwarf;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import net.minecraft.MethodsReturnNonnullByDefault;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class DwarfProfessionSettings {

    private final Optional<TradeSettings> trades;

    public DwarfProfessionSettings(Optional<TradeSettings> trades) {
        this.trades = trades;
    }

    public Optional<TradeSettings> trades() {
        return trades;
    }

    @Nullable
    public TradeSettings tradesOrNull() {
        return trades.orElse(null);
    }

    // -------------------------------------------------------------------------
    // Trades section
    // -------------------------------------------------------------------------

    public record TradeSettings(Int2IntMap poolRollsByLevel, Int2IntMap restockPoolRollsByLevel) {

        public enum PoolType {
            POOL,
            RESTOCK_POOL
        }

        public int rollsFor(PoolType type, int merchantLevel) {
            if (merchantLevel <= 0) return 0;
            return switch (type) {
                case POOL -> poolRollsByLevel.getOrDefault(merchantLevel, 0);
                case RESTOCK_POOL -> restockPoolRollsByLevel.getOrDefault(merchantLevel, 0);
            };
        }

        // ---- Codec ----

        private static Int2IntOpenHashMap emptyRollMap() {
            Int2IntOpenHashMap m = new Int2IntOpenHashMap();
            m.defaultReturnValue(0);
            return m;
        }

        /**
         * JSON entry format:
         * { "level": 1, "rolls": 2 }
         */
        private record LevelRoll(int level, int rolls) {

            private static final Codec<LevelRoll> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                    Codec.INT.fieldOf("level").forGetter(LevelRoll::level),
                    Codec.INT.fieldOf("rolls").forGetter(LevelRoll::rolls)
            ).apply(inst, LevelRoll::new));
        }

        private static DataResult<Int2IntMap> decodeLevelRollList(List<LevelRoll> in) {
            Int2IntOpenHashMap out = emptyRollMap();

            for (LevelRoll e : in) {
                int level = e.level();
                int rolls = e.rolls();

                if (level <= 0) {
                    return DataResult.error(() -> "Invalid merchant level " + level + " (must be >= 1)");
                }
                if (rolls < 0) {
                    return DataResult.error(() -> "Invalid roll count for level " + level + ": " + rolls + " (must be >= 0)");
                }

                out.put(level, rolls);
            }

            return DataResult.success(out);
        }

        private static List<LevelRoll> encodeLevelRollList(Int2IntMap in) {
            // stable output order + omit zero rolls
            List<LevelRoll> out = new ArrayList<>();

            in.int2IntEntrySet().stream()
                    .filter(e -> e.getIntValue() > 0)
                    .sorted(Comparator.comparingInt(Int2IntMap.Entry::getIntKey))
                    .forEach(e -> out.add(new LevelRoll(e.getIntKey(), e.getIntValue())));

            return out;
        }

        private static final Codec<Int2IntMap> LEVEL_ROLLS_CODEC =
                LevelRoll.CODEC.listOf()
                        .comapFlatMap(TradeSettings::decodeLevelRollList, TradeSettings::encodeLevelRollList);

        // NOTE: mutable defaults must be created per-decode (no shared empty map instance).
        // Also: empty maps should not serialize (lets datagen produce "pool only" cleanly).
        private static final MapCodec<Int2IntMap> POOL_ROLLS_FIELD =
                LEVEL_ROLLS_CODEC.optionalFieldOf("pool_rolls")
                        .xmap(
                                opt -> opt.orElseGet(TradeSettings::emptyRollMap),
                                map -> map.isEmpty() ? Optional.empty() : Optional.of(map)
                        );

        private static final MapCodec<Int2IntMap> RESTOCK_POOL_ROLLS_FIELD =
                LEVEL_ROLLS_CODEC.optionalFieldOf("restock_pool_rolls")
                        .xmap(
                                opt -> opt.orElseGet(TradeSettings::emptyRollMap),
                                map -> map.isEmpty() ? Optional.empty() : Optional.of(map)
                        );

        public static final Codec<TradeSettings> CODEC = RecordCodecBuilder.create(inst -> inst.group(
                POOL_ROLLS_FIELD.forGetter(TradeSettings::poolRollsByLevel),
                RESTOCK_POOL_ROLLS_FIELD.forGetter(TradeSettings::restockPoolRollsByLevel)
        ).apply(inst, TradeSettings::new));
    }

    // -------------------------------------------------------------------------
    // Codec
    // -------------------------------------------------------------------------

    public static final Codec<DwarfProfessionSettings> CODEC = RecordCodecBuilder.create(inst -> inst.group(
            TradeSettings.CODEC.optionalFieldOf("trades").forGetter(DwarfProfessionSettings::trades)
    ).apply(inst, DwarfProfessionSettings::new));

    // -------------------------------------------------------------------------
    // Helpers (datagen convenience)
    // -------------------------------------------------------------------------

    public static DwarfProfessionSettings mainOnly() {
        return new DwarfProfessionSettings(Optional.empty());
    }

    public static DwarfProfessionSettings trades(TradeSettings trades) {
        return new DwarfProfessionSettings(Optional.of(trades));
    }

    public static TradeSettings tradeSettingsPoolOnly(Int2IntMap poolRollsByLevel) {
        return new TradeSettings(poolRollsByLevel, TradeSettings.emptyRollMap());
    }

    public static TradeSettings tradeSettingsRestockOnly(Int2IntMap restockPoolRollsByLevel) {
        return new TradeSettings(TradeSettings.emptyRollMap(), restockPoolRollsByLevel);
    }
}