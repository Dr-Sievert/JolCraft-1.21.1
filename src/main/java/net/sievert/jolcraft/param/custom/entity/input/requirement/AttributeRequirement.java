package net.sievert.jolcraft.param.custom.entity.input.requirement;

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
import net.sievert.jolcraft.param.base.ParamCodecs;
import net.sievert.jolcraft.param.base.ParamData;
import net.sievert.jolcraft.param.base.ParamValidations;

import java.util.Optional;

public record AttributeRequirement(
        Holder<Attribute> attribute,
        Operator operator,
        double value
) implements ParamData<AttributeRequirement> {

    public enum Operator {
        LESS_THAN("less_than"),
        LESS_THAN_OR_EQUAL("less_than_or_equal"),
        EQUAL("equal"),
        GREATER_THAN_OR_EQUAL("greater_than_or_equal"),
        GREATER_THAN("greater_than");

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
                if (op.id.equals(id)) return ParamValidations.ok(op);
            }

            return ParamValidations.invalid("Unknown operator: " + id);
        }

        public static Optional<Operator> fromOrdinalSafe(int ordinal) {
            Operator[] values = values();
            return ordinal >= 0 && ordinal < values.length
                    ? Optional.of(values[ordinal])
                    : Optional.empty();
        }
    }

    private static final Codec<Holder<Attribute>> ATTRIBUTE_CODEC =
            RegistryFixedCodec.create(Registries.ATTRIBUTE);

    private static final Codec<Operator> OPERATOR_CODEC =
            Codec.STRING.comapFlatMap(Operator::fromId, Operator::id);

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<Attribute>> ATTRIBUTE_STREAM =
            ByteBufCodecs.holderRegistry(Registries.ATTRIBUTE);

    public static final Codec<AttributeRequirement> CODEC =
            ParamCodecs.validated(
                    RecordCodecBuilder.create(inst -> inst.group(
                            ATTRIBUTE_CODEC.fieldOf(JolCraftParameterIds.ATTRIBUTE)
                                    .forGetter(AttributeRequirement::attribute),
                            OPERATOR_CODEC.optionalFieldOf(JolCraftParameterIds.OPERATOR, Operator.EQUAL)
                                    .forGetter(AttributeRequirement::operator),
                            Codec.DOUBLE.fieldOf(JolCraftParameterIds.VALUE)
                                    .forGetter(AttributeRequirement::value)
                    ).apply(inst, AttributeRequirement::new)),
                    AttributeRequirement::validate
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, AttributeRequirement> STREAM_CODEC =
            ParamCodecs.validatedStream(StreamCodec.of(
                    (buf, req) -> {
                        ATTRIBUTE_STREAM.encode(buf, req.attribute());
                        buf.writeVarInt(req.operator().ordinal());
                        buf.writeDouble(req.value());
                    },
                    buf -> new AttributeRequirement(
                            ATTRIBUTE_STREAM.decode(buf),
                            Operator.fromOrdinalSafe(buf.readVarInt())
                                    .orElseThrow(() -> new IllegalArgumentException("Unknown attribute operator")),
                            buf.readDouble()
                    )
            ), AttributeRequirement::validate);

    public AttributeRequirement {
        if (attribute == null) {
            throw new IllegalArgumentException(JolCraftParameterIds.ATTRIBUTE + " is required");
        }

        operator = operator == null ? Operator.EQUAL : operator;
    }

    public boolean matches(Entity entity) {
        if (!(entity instanceof LivingEntity living)) return false;
        if (!Double.isFinite(value)) return false;

        return operator.test(living.getAttributeValue(attribute), value);
    }

    @Override
    public DataResult<AttributeRequirement> validate() {
        return ParamValidations.all(this,
                () -> ParamValidations.notNull(this, attribute, JolCraftParameterIds.ATTRIBUTE),
                () -> ParamValidations.notNull(this, operator, JolCraftParameterIds.OPERATOR),
                () -> ParamValidations.finite(this, value, JolCraftParameterIds.VALUE)
        );
    }

    @Override
    public Codec<AttributeRequirement> codec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, AttributeRequirement> streamCodec() {
        return STREAM_CODEC;
    }
}