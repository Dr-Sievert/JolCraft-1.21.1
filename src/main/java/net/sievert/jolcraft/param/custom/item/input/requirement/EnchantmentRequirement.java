package net.sievert.jolcraft.param.custom.item.input.requirement;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Encoder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamMatching;
import net.sievert.jolcraft.param.base.ParamValidations;
import net.sievert.jolcraft.param.base.RegistryTarget;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public record EnchantmentRequirement(
        RegistryTarget<Enchantment> target,
        int minLevel,
        int maxLevel
) implements ParamData<EnchantmentRequirement>, ParamMatching<ItemStack> {

    private static final int RANGE_SIZE = 2;

    private static final Codec<RegistryTarget<Enchantment>> TARGET_CODEC =
            ParamCodecs.registryTargetValue(Registries.ENCHANTMENT);

    private static final StreamCodec<RegistryFriendlyByteBuf, RegistryTarget<Enchantment>> TARGET_STREAM_CODEC =
            ParamCodecs.registryTargetValueStream(Registries.ENCHANTMENT);

    private static final Codec<LevelRange> LEVEL_RANGE_CODEC =
            ParamCodecs.either(
                    Codec.INT,
                    Codec.INT.listOf(),
                    either -> either.map(
                            level -> LevelRange.create(level, level),
                            LevelRange::fromList
                    ),
                    range -> ParamValidations.ok(
                            range.isExact()
                                    ? Either.left(range.min())
                                    : Either.right(List.of(range.min(), range.max()))
                    )
            );

    private static final Encoder<List<EnchantmentRequirement>> MAP_ENCODER =
            new Encoder<>() {
                @Override
                public <T> DataResult<T> encode(List<EnchantmentRequirement> input, DynamicOps<T> ops, T prefix) {
                    Map<T, T> out = new LinkedHashMap<>();

                    for (EnchantmentRequirement requirement : input) {
                        DataResult<String> keyResult = requirement.targetKey();
                        if (keyResult.error().isPresent()) {
                            return keyResult.flatMap(key -> ParamValidations.invalid("failed to encode enchantment key"));
                        }

                        DataResult<T> valueResult = LEVEL_RANGE_CODEC.encodeStart(
                                ops,
                                new LevelRange(requirement.minLevel(), requirement.maxLevel())
                        );

                        if (valueResult.error().isPresent()) return valueResult;

                        out.put(
                                ops.createString(keyResult.result().orElseThrow()),
                                valueResult.result().orElseThrow()
                        );
                    }

                    return ParamValidations.ok(ops.createMap(out));
                }
            };

    private static final Decoder<List<EnchantmentRequirement>> MAP_DECODER =
            new Decoder<>() {
                @Override
                public <T> DataResult<Pair<List<EnchantmentRequirement>, T>> decode(DynamicOps<T> ops, T input) {
                    return Codec.unboundedMap(Codec.STRING, LEVEL_RANGE_CODEC)
                            .decode(ops, input)
                            .flatMap(pair -> fromMap(ops, pair.getFirst())
                                    .map(requirements -> Pair.of(requirements, pair.getSecond())));
                }
            };

    public static final Codec<List<EnchantmentRequirement>> MAP_CODEC =
            Codec.of(MAP_ENCODER, MAP_DECODER);

    public static final Codec<EnchantmentRequirement> CODEC =
            ParamCodecs.validated(
                    MAP_CODEC.flatXmap(
                            list -> list.size() == 1
                                    ? ParamValidations.ok(list.getFirst())
                                    : ParamValidations.invalid("expected exactly one enchantment requirement"),
                            requirement -> ParamValidations.ok(List.of(requirement))
                    ),
                    EnchantmentRequirement::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentRequirement> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.composite(
                    TARGET_STREAM_CODEC,
                    EnchantmentRequirement::target,
                    ByteBufCodecs.VAR_INT,
                    EnchantmentRequirement::minLevel,
                    ByteBufCodecs.VAR_INT,
                    EnchantmentRequirement::maxLevel,
                    EnchantmentRequirement::new
            ), EnchantmentRequirement::validate);

    public EnchantmentRequirement {
        if (target == null) {
            throw new IllegalArgumentException("missing required field '" + JolCraftParameterIds.ENCHANTMENT + "'");
        }
    }

    private static <T> DataResult<List<EnchantmentRequirement>> fromMap(
            DynamicOps<T> ops,
            Map<String, LevelRange> map
    ) {
        List<EnchantmentRequirement> requirements = new ArrayList<>();

        for (Map.Entry<String, LevelRange> entry : map.entrySet()) {
            DataResult<RegistryTarget<Enchantment>> targetResult =
                    TARGET_CODEC.parse(ops, ops.createString(entry.getKey()));

            if (targetResult.error().isPresent()) {
                return targetResult.map(target -> List.of());
            }

            LevelRange range = entry.getValue();
            EnchantmentRequirement requirement = new EnchantmentRequirement(
                    targetResult.result().orElseThrow(),
                    range.min(),
                    range.max()
            );

            DataResult<EnchantmentRequirement> valid = requirement.validate();
            if (valid.error().isPresent()) {
                return valid.map(List::of);
            }

            requirements.add(requirement);
        }

        return requirements.isEmpty()
                ? ParamValidations.invalid("enchantments must not be empty")
                : ParamValidations.ok(List.copyOf(requirements));
    }

    private DataResult<String> targetKey() {
        return target.value().map(
                holder -> holder.unwrapKey()
                        .map(key -> ParamValidations.ok(key.location().toString()))
                        .orElseGet(() -> ParamValidations.invalid("unregistered enchantment holder")),
                tag -> ParamValidations.ok("#" + tag.location())
        );
    }

    @Override
    public boolean matches(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        if (minLevel < 1 || maxLevel < minLevel) return false;

        return target.value().map(
                enchantment -> matchesLevel(stack.getEnchantmentLevel(enchantment)),
                tag -> {
                    for (Holder<Enchantment> enchantment : stack.getTagEnchantments().keySet()) {
                        if (enchantment.is(tag) && matchesLevel(stack.getEnchantmentLevel(enchantment))) {
                            return true;
                        }
                    }

                    return false;
                }
        );
    }

    private boolean matchesLevel(int level) {
        return level >= minLevel && level <= maxLevel;
    }

    @Override
    public DataResult<EnchantmentRequirement> validate() {
        return ParamValidations.all(this,
                () -> ParamValidations.wrap(this, target.validate(), JolCraftParameterIds.ENCHANTMENT),
                () -> ParamValidations.positive(this, minLevel, "enchantment min level"),
                () -> ParamValidations.positive(this, maxLevel, "enchantment max level"),
                () -> ParamValidations.minMax(this, minLevel, maxLevel, "enchantment level")
        );
    }

    @Override
    public Codec<EnchantmentRequirement> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, EnchantmentRequirement> streamCodec() {
        return STREAM_CODEC;
    }

    private record LevelRange(int min, int max) {

        private static DataResult<LevelRange> create(int min, int max) {
            return new LevelRange(min, max).validate();
        }

        private static DataResult<LevelRange> fromList(List<Integer> values) {
            if (values.size() != RANGE_SIZE) {
                return ParamValidations.invalid("enchantment range must contain exactly two integers [min, max]");
            }

            return create(values.get(0), values.get(1));
        }

        private boolean isExact() {
            return min == max;
        }

        private DataResult<LevelRange> validate() {
            return ParamValidations.all(this,
                    () -> ParamValidations.positive(this, min, "enchantment min level"),
                    () -> ParamValidations.positive(this, max, "enchantment max level"),
                    () -> ParamValidations.minMax(this, min, max, "enchantment range")
            );
        }
    }
}