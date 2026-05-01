package net.sievert.jolcraft.world.recipe.param.input.custom.entity.requirement;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.param.base.ParamCodecContract;
import net.sievert.jolcraft.world.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.world.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record AttributeRequirement(
        Holder<Attribute> attribute,
        Operator operator,
        double value
) implements SelfValidating<AttributeRequirement>, RegistryIntrospectable {

    public enum Operator {
        LESS_THAN(JolCraftStrings.underscored(JolCraftDictionary.LESS, JolCraftDictionary.THAN)),
        LESS_THAN_OR_EQUAL(JolCraftStrings.underscored(JolCraftDictionary.LESS, JolCraftDictionary.THAN, JolCraftDictionary.OR, JolCraftDictionary.EQUAL)),
        EQUAL(JolCraftDictionary.EQUAL),
        GREATER_THAN_OR_EQUAL(JolCraftStrings.underscored(JolCraftDictionary.GREATER, JolCraftDictionary.THAN, JolCraftDictionary.OR, JolCraftDictionary.EQUAL)),
        GREATER_THAN(JolCraftStrings.underscored(JolCraftDictionary.GREATER, JolCraftDictionary.THAN));

        private final String id;

        Operator(String id) {
            this.id = id;
        }

        public String id() {
            return id;
        }

        public boolean test(double left, double right) {
            return switch (this) {
                case LESS_THAN -> left < right;
                case LESS_THAN_OR_EQUAL -> left <= right;
                case EQUAL -> Double.compare(left, right) == 0;
                case GREATER_THAN_OR_EQUAL -> left >= right;
                case GREATER_THAN -> left > right;
            };
        }

        public static DataResult<Operator> fromId(String id) {
            for (Operator op : values()) {
                if (op.id.equals(id)) return DataResult.success(op);
            }
            return DataResult.error(() -> "Unknown operator: " + id);
        }

        public static Optional<Operator> fromOrdinalSafe(int ordinal) {
            Operator[] values = values();
            if (ordinal < 0 || ordinal >= values.length) return Optional.empty();
            return Optional.of(values[ordinal]);
        }
    }

    private static final Codec<Holder<Attribute>> ATTRIBUTE_CODEC =
            RegistryFixedCodec.create(Registries.ATTRIBUTE);

    private static final Codec<Operator> OPERATOR_CODEC =
            Codec.STRING.comapFlatMap(Operator::fromId, Operator::id);

    private record Raw(Holder<Attribute> attribute, Operator operator, double value) {}

    private static final Codec<Raw> RAW_CODEC =
            Codec.either(
                    ATTRIBUTE_CODEC,
                    RecordCodecBuilder.<Raw>create(instance -> instance.group(
                            ATTRIBUTE_CODEC.fieldOf(JolCraftParameterIds.ATTRIBUTE).forGetter(Raw::attribute),
                            OPERATOR_CODEC.optionalFieldOf(JolCraftParameterIds.OPERATOR, Operator.EQUAL).forGetter(Raw::operator),
                            Codec.DOUBLE.fieldOf(JolCraftParameterIds.VALUE).forGetter(Raw::value)
                    ).apply(instance, Raw::new))
            ).xmap(
                    either -> either.map(
                            attribute -> new Raw(attribute, Operator.EQUAL, 0.0D),
                            raw -> raw
                    ),
                    raw -> {
                        if (raw.operator() == Operator.EQUAL && Double.compare(raw.value(), 0.0D) == 0) {
                            return Either.left(raw.attribute());
                        }
                        return Either.right(raw);
                    }
            );

    public static final Codec<AttributeRequirement> CODEC =
            ParamCodecContract.create(
                    RAW_CODEC,
                    raw -> DataResult.success(new AttributeRequirement(raw.attribute(), raw.operator(), raw.value())),
                    req -> new Raw(req.attribute(), req.operator(), req.value())
            );

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<Attribute>> ATTRIBUTE_STREAM =
            ByteBufCodecs.holderRegistry(Registries.ATTRIBUTE);

    public static final StreamCodec<RegistryFriendlyByteBuf, AttributeRequirement> STREAM_CODEC =
            StreamCodec.of(
                    (buf, req) -> {
                        ATTRIBUTE_STREAM.encode(buf, req.attribute());
                        buf.writeVarInt(req.operator().ordinal());
                        buf.writeDouble(req.value());
                    },
                    buf -> new AttributeRequirement(
                            ATTRIBUTE_STREAM.decode(buf),
                            Operator.fromOrdinalSafe(buf.readVarInt()).orElse(Operator.EQUAL),
                            buf.readDouble()
                    )
            );

    public AttributeRequirement {
        if (attribute == null) {
            throw new IllegalArgumentException(JolCraftParameterIds.ATTRIBUTE + " is required");
        }
        if (operator == null) {
            throw new IllegalArgumentException(JolCraftParameterIds.OPERATOR + " is required");
        }
    }

    @Override
    public @NotNull RegistryIntrospection introspection() {
        return RegistryIntrospection.single(Registries.ATTRIBUTE, attribute);
    }

    @Override
    public @NotNull DataResult<AttributeRequirement> validate() {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return SelfValidating.invalid(JolCraftParameterIds.VALUE + " must be finite (value=" + value + ")");
        }
        return SelfValidating.ok(this);
    }

    public boolean matches(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;
        if (Double.isNaN(value) || Double.isInfinite(value)) return false;

        double current = living.getAttributeValue(attribute);
        return operator.test(current, value);
    }
}