package net.sievert.jolcraft.config.custom.dwarf.trade;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public record DwarfProfessionTradePoolsConfig(
        Map<TradePoolType, DwarfProfessionTradePoolConfig> values
) {

    private static final Codec<TradePoolType> POOL_TYPE_CODEC =
            Codec.STRING.comapFlatMap(
                    s -> {
                        if (s == null) {
                            return DataResult.error(() -> "pool type is null");
                        }
                        return TradePoolType.fromSerialized(s);
                    },
                    TradePoolType::getId
            );

    public static final DwarfProfessionTradePoolsConfig DEFAULTS =
            new DwarfProfessionTradePoolsConfig(Map.of());

    public static final Codec<DwarfProfessionTradePoolsConfig> CODEC =
            Codec.unboundedMap(POOL_TYPE_CODEC, DwarfProfessionTradePoolConfig.CODEC)
                    .xmap(DwarfProfessionTradePoolsConfig::new, DwarfProfessionTradePoolsConfig::values)
                    .validate(DwarfProfessionTradePoolsConfig::validate);

    public DwarfProfessionTradePoolsConfig {
        values = values != null ? Map.copyOf(values) : Map.of();
    }

    public static @NotNull DataResult<DwarfProfessionTradePoolsConfig> validate(
            DwarfProfessionTradePoolsConfig pools
    ) {
        if (pools == null) {
            return DataResult.error(() -> "trade_pools is required");
        }

        for (Map.Entry<TradePoolType, DwarfProfessionTradePoolConfig> entry : pools.values().entrySet()) {
            TradePoolType type = entry.getKey();
            DwarfProfessionTradePoolConfig config = entry.getValue();

            if (type == null) {
                return DataResult.error(() -> "trade pool type cannot be null");
            }

            if (config == null) {
                return DataResult.error(() ->
                        "trade pool config for " + type.getId() + " cannot be null");
            }

            DataResult<DwarfProfessionTradePoolConfig> validated =
                    DwarfProfessionTradePoolConfig.validate(config);

            var err = validated.error();
            if (err.isPresent()) {
                return DataResult.error(() ->
                        "trade pool config for " + type.getId() + " invalid: " + err.get().message());
            }
        }

        return DataResult.success(pools);
    }

    public boolean has(@Nullable TradePoolType type) {
        return type != null && values.containsKey(type);
    }

    public @NotNull Optional<DwarfProfessionTradePoolConfig> get(@Nullable TradePoolType type) {
        if (type == null) return Optional.empty();
        return Optional.ofNullable(values.get(type));
    }

    public int rollsFor(@Nullable TradePoolType type, @Nullable DwarfMerchantData.Level level) {
        if (type == null || level == null) return 0;

        return get(type)
                .map(cfg -> cfg.rollsFor(type, level))
                .orElse(0);
    }

    public boolean rerollsOnRestock() {
        return values.values().stream().anyMatch(DwarfProfessionTradePoolConfig::rerollsOnRestock);
    }
}