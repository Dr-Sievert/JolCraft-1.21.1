package net.sievert.jolcraft.config.custom.dwarf;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public record DwarfProfessionConfig(
        int requiredTier,
        long restockTicks,
        float voicePitch,

        boolean canReroll,
        boolean canEndorse,

        boolean showProgressBar,
        boolean showLevel,

        Rules rules,
        Sounds sounds,
        AttributesConfig attributes,
        TradePools tradePools
) {

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

    public static final DwarfProfessionConfig DEFAULTS = new DwarfProfessionConfig(
            0,
            6000L,
            1.0F,

            true,
            true,

            true,
            true,

            Rules.DEFAULTS,
            Sounds.DEFAULTS,
            AttributesConfig.DEFAULTS,
            TradePools.DEFAULTS
    );

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

                    Rules.CODEC.optionalFieldOf(KEY_RULES, DEFAULTS.rules())
                            .forGetter(DwarfProfessionConfig::rules),

                    Sounds.CODEC.optionalFieldOf(KEY_SOUNDS, DEFAULTS.sounds())
                            .forGetter(DwarfProfessionConfig::sounds),

                    AttributesConfig.CODEC.optionalFieldOf(KEY_ATTRIBUTES, DEFAULTS.attributes())
                            .forGetter(DwarfProfessionConfig::attributes),

                    TradePools.CODEC.optionalFieldOf(KEY_TRADE_POOLS, DEFAULTS.tradePools())
                            .forGetter(DwarfProfessionConfig::tradePools)

            ).apply(i, DwarfProfessionConfig::new)).validate(DwarfProfessionConfig::validate);

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
            return DataResult.error(() -> "rules is required");
        }
        if (cfg.sounds() == null) {
            return DataResult.error(() -> "sounds is required");
        }
        if (cfg.attributes() == null) {
            return DataResult.error(() -> "attributes is required");
        }
        if (cfg.tradePools() == null) {
            return DataResult.error(() -> "trade_pools is required");
        }

        DataResult<TradePools> poolsValidation = TradePools.validate(cfg.tradePools());
        var poolsErr = poolsValidation.error();
        return poolsErr.<DataResult<DwarfProfessionConfig>>map(err ->
                DataResult.error(err::message)).orElseGet(() -> DataResult.success(cfg));
    }

    public record Rules(Rule canSign, Rule canEndorse, Rule canTrade) {

        public static final String KEY_CAN_SIGN =
                JolCraftStrings.underscored(JolCraftDictionary.CAN, JolCraftDictionary.SIGN);

        public static final String KEY_CAN_ENDORSE =
                JolCraftStrings.underscored(JolCraftDictionary.CAN, JolCraftDictionary.ENDORSE);

        public static final String KEY_CAN_TRADE =
                JolCraftStrings.underscored(JolCraftDictionary.CAN, JolCraftDictionary.TRADE);

        public static final Rules DEFAULTS = new Rules(
                Rule.ALWAYS,
                Rule.minMerchantLevel(1),
                Rule.ALWAYS
        );

        public static final Codec<Rules> CODEC =
                RecordCodecBuilder.create((RecordCodecBuilder.Instance<Rules> i) -> i.group(
                        Rule.CODEC.optionalFieldOf(KEY_CAN_SIGN, DEFAULTS.canSign()).forGetter(Rules::canSign),
                        Rule.CODEC.optionalFieldOf(KEY_CAN_ENDORSE, DEFAULTS.canEndorse()).forGetter(Rules::canEndorse),
                        Rule.CODEC.optionalFieldOf(KEY_CAN_TRADE, DEFAULTS.canTrade()).forGetter(Rules::canTrade)
                ).apply(i, Rules::new));
    }

    public sealed interface Rule permits Rule.Always, Rule.MinMerchantLevel {

        String KEY_TYPE = JolCraftDictionary.TYPE;
        String KEY_LEVEL = JolCraftDictionary.LEVEL;

        String TYPE_ALWAYS = JolCraftDictionary.ALWAYS;

        String TYPE_MIN_MERCHANT_LEVEL = JolCraftStrings.underscored(
                JolCraftDictionary.MIN,
                JolCraftDictionary.MERCHANT,
                JolCraftDictionary.LEVEL
        );

        Codec<Rule> CODEC = Codec.STRING.dispatch(
                KEY_TYPE,
                Rule::typeId,
                Rule::mapCodecForType
        );

        static MapCodec<? extends Rule> mapCodecForType(String typeId) {
            if (TYPE_MIN_MERCHANT_LEVEL.equals(typeId)) return MinMerchantLevel.MAP_CODEC;
            return Always.MAP_CODEC;
        }

        String typeId();

        Rule ALWAYS = new Always();

        static Rule minMerchantLevel(int level) {
            return new MinMerchantLevel(level);
        }

        record Always() implements Rule {
            static final MapCodec<Always> MAP_CODEC = MapCodec.unit(new Always());

            @Override
            public String typeId() {
                return TYPE_ALWAYS;
            }
        }

        record MinMerchantLevel(int level) implements Rule {

            static final MapCodec<MinMerchantLevel> MAP_CODEC =
                    RecordCodecBuilder.mapCodec((RecordCodecBuilder.Instance<MinMerchantLevel> i) -> i.group(
                            Codec.INT.fieldOf(KEY_LEVEL).forGetter(MinMerchantLevel::level)
                    ).apply(i, MinMerchantLevel::new));

            @Override
            public String typeId() {
                return TYPE_MIN_MERCHANT_LEVEL;
            }
        }
    }

    public record Sounds(Optional<ResourceLocation> restock, Optional<ResourceLocation> reroll) {

        public static final String KEY_RESTOCK = JolCraftDictionary.RESTOCK;
        public static final String KEY_REROLL = JolCraftDictionary.REROLL;

        public static final Sounds DEFAULTS = new Sounds(Optional.empty(), Optional.empty());

        public static final Codec<Sounds> CODEC =
                RecordCodecBuilder.create((RecordCodecBuilder.Instance<Sounds> i) -> i.group(
                        ResourceLocation.CODEC.optionalFieldOf(KEY_RESTOCK).forGetter(Sounds::restock),
                        ResourceLocation.CODEC.optionalFieldOf(KEY_REROLL).forGetter(Sounds::reroll)
                ).apply(i, Sounds::new));
    }

    public record AttributesConfig(Map<ResourceLocation, Double> overrides) {

        public static final String KEY_OVERRIDES =
                JolCraftStrings.plural(JolCraftDictionary.OVERRIDE);

        public static final AttributesConfig DEFAULTS = new AttributesConfig(Map.of(
                idOf(Attributes.MAX_HEALTH), 30D,
                idOf(Attributes.MOVEMENT_SPEED), 0.20D,
                idOf(Attributes.FOLLOW_RANGE), 24D,
                idOf(Attributes.TEMPT_RANGE), 16D,
                idOf(Attributes.ATTACK_DAMAGE), 3.0D
        ));

        public static final Codec<AttributesConfig> CODEC =
                RecordCodecBuilder.create((RecordCodecBuilder.Instance<AttributesConfig> i) -> i.group(
                        Codec.unboundedMap(ResourceLocation.CODEC, Codec.DOUBLE)
                                .optionalFieldOf(KEY_OVERRIDES, DEFAULTS.overrides())
                                .forGetter(AttributesConfig::overrides)
                ).apply(i, AttributesConfig::new));

        private static ResourceLocation idOf(Holder<Attribute> holder) {
            return holder.unwrapKey()
                    .map(ResourceKey::location)
                    .orElse(ResourceLocation.withDefaultNamespace("empty"));
        }
    }

    public enum RerollRule {
        RESTOCK,
        REROLL;

        public String serializedName() {
            return name().toLowerCase(Locale.ROOT);
        }

        public static @NotNull DataResult<RerollRule> fromSerialized(@NotNull String s) {
            try {
                return DataResult.success(valueOf(s.trim().toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                return DataResult.error(() -> "unknown reroll rule '" + s + "'");
            }
        }
    }

    public enum PoolType {
        GLOBAL,
        CUMULATIVE,
        EXACT_LEVEL;

        public String serializedName() {
            return switch (this) {
                case GLOBAL -> JolCraftDictionary.GLOBAL;
                case CUMULATIVE -> JolCraftDictionary.CUMULATIVE;
                case EXACT_LEVEL -> JolCraftStrings.underscored(JolCraftDictionary.EXACT, JolCraftDictionary.LEVEL);
            };
        }

        public static @NotNull DataResult<PoolType> fromSerialized(@NotNull String s) {
            String normalized = s.trim().toUpperCase(Locale.ROOT);
            return switch (normalized) {
                case "GLOBAL" -> DataResult.success(GLOBAL);
                case "CUMULATIVE" -> DataResult.success(CUMULATIVE);
                case "EXACT_LEVEL" -> DataResult.success(EXACT_LEVEL);
                default -> DataResult.error(() -> "unknown pool type '" + s + "'");
            };
        }
    }

    public record TradePools(Map<PoolType, PoolConfig> values) {

        private static final Codec<PoolType> POOL_TYPE_CODEC =
                Codec.STRING.comapFlatMap(
                        s -> {
                            if (s == null) {
                                return DataResult.error(() -> "pool type is null");
                            }
                            return PoolType.fromSerialized(s);
                        },
                        PoolType::serializedName
                );

        public static final TradePools DEFAULTS = new TradePools(Map.of());

        public static final Codec<TradePools> CODEC =
                Codec.unboundedMap(POOL_TYPE_CODEC, PoolConfig.CODEC)
                        .xmap(TradePools::new, TradePools::values)
                        .validate(TradePools::validate);

        public TradePools {
            values = values != null ? Map.copyOf(values) : Map.of();
        }

        public static @NotNull DataResult<TradePools> validate(TradePools pools) {
            for (Map.Entry<PoolType, PoolConfig> entry : pools.values().entrySet()) {
                PoolType type = entry.getKey();
                PoolConfig config = entry.getValue();

                if (type == null) {
                    return DataResult.error(() -> "trade pool type cannot be null");
                }

                if (config == null) {
                    return DataResult.error(() -> "trade pool config for " + type.serializedName() + " cannot be null");
                }

                DataResult<PoolConfig> validated = PoolConfig.validate(config);
                var err = validated.error();
                if (err.isPresent()) {
                    return DataResult.error(() ->
                            "trade pool config for " + type.serializedName() + " invalid: " + err.get().message());
                }
            }

            return DataResult.success(pools);
        }

        public boolean has(PoolType type) {
            return type != null && values.containsKey(type);
        }

        public Optional<PoolConfig> get(PoolType type) {
            if (type == null) {
                return Optional.empty();
            }
            return Optional.ofNullable(values.get(type));
        }

        public int rollsFor(PoolType type, DwarfMerchantData.Level level) {
            return get(type).map(cfg -> cfg.rollsFor(level)).orElse(0);
        }

        public boolean rerollsOnRestock() {
            return values.values().stream().anyMatch(PoolConfig::rerollsOnRestock);
        }
    }

    public record PoolConfig(
            PoolRolls rolls,
            RerollRule rerollRule
    ) {

        public static final String KEY_ROLLS = JolCraftStrings.plural(JolCraftDictionary.ROLL);

        public static final String KEY_REROLL_RULE =
                JolCraftStrings.underscored(JolCraftDictionary.REROLL, JolCraftDictionary.RULE);

        private static final Codec<RerollRule> REROLL_RULE_CODEC =
                Codec.STRING.comapFlatMap(
                        s -> {
                            if (s == null) {
                                return DataResult.error(() -> "reroll rule is null");
                            }
                            return RerollRule.fromSerialized(s);
                        },
                        RerollRule::serializedName
                );

        public static final PoolConfig DEFAULTS = new PoolConfig(
                PoolRolls.DEFAULTS,
                RerollRule.RESTOCK
        );

        public static final Codec<PoolConfig> CODEC =
                RecordCodecBuilder.create((RecordCodecBuilder.Instance<PoolConfig> i) -> i.group(
                                PoolRolls.CODEC.optionalFieldOf(KEY_ROLLS, DEFAULTS.rolls()).forGetter(PoolConfig::rolls),
                                REROLL_RULE_CODEC.optionalFieldOf(KEY_REROLL_RULE, DEFAULTS.rerollRule())
                                        .forGetter(PoolConfig::rerollRule)
                        ).apply(i, PoolConfig::new))
                        .validate(PoolConfig::validate);

        public static @NotNull DataResult<PoolConfig> validate(PoolConfig config) {
            if (config.rolls() == null) {
                return DataResult.error(() -> "rolls is required");
            }

            if (config.rerollRule() == null) {
                return DataResult.error(() -> "reroll_rule is required");
            }

            DataResult<PoolRolls> rollsValidation = PoolRolls.validate(config.rolls());
            var rollsErr = rollsValidation.error();
            return rollsErr.<DataResult<PoolConfig>>map(err ->
                    DataResult.error(err::message)).orElseGet(() -> DataResult.success(config));
        }

        public int rollsFor(DwarfMerchantData.Level level) {
            return rolls.rollsFor(level);
        }

        public boolean rerollsOnRestock() {
            return rerollRule == RerollRule.RESTOCK;
        }

        public boolean rerollsOnlyOnManualReroll() {
            return rerollRule == RerollRule.REROLL;
        }
    }

    public record PoolRolls(Map<DwarfMerchantData.Level, Integer> rolls) {

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

        public static final PoolRolls DEFAULTS = new PoolRolls(Map.of());

        public static final Codec<PoolRolls> CODEC =
                Codec.unboundedMap(LEVEL_KEY_CODEC, Codec.INT)
                        .xmap(PoolRolls::new, PoolRolls::rolls)
                        .validate(PoolRolls::validate);

        public PoolRolls {
            rolls = rolls != null ? Map.copyOf(rolls) : Map.of();
        }

        public static @NotNull DataResult<PoolRolls> validate(PoolRolls rolls) {
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

        public int rollsFor(DwarfMerchantData.Level level) {
            if (level == null) {
                return 0;
            }
            return Math.max(0, rolls.getOrDefault(level, 0));
        }
    }
}