package net.sievert.jolcraft.data.recipe.param.input.custom.entity.requirement;

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
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record AttributeRequirement(
        Holder<Attribute> attribute,
        AttributeRequirement.Operator operator,
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
                if (op.id.equals(id)) {
                    return DataResult.success(op);
                }
            }
            return DataResult.error(() -> "Unknown operator: " + id);
        }

        public static Optional<Operator> fromOrdinalSafe(int ordinal) {
            Operator[] values = values();
            if (ordinal < 0 || ordinal >= values.length) {
                return Optional.empty();
            }
            return Optional.of(values[ordinal]);
        }
    }

    private static final Codec<Holder<Attribute>> ATTRIBUTE_CODEC =
            RegistryFixedCodec.create(Registries.ATTRIBUTE);

    private static final Codec<Operator> OPERATOR_CODEC =
            Codec.STRING.comapFlatMap(Operator::fromId, Operator::id);

    private static final Codec<AttributeRequirement> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ATTRIBUTE_CODEC.fieldOf(JolCraftParameterIds.ATTRIBUTE).forGetter(AttributeRequirement::attribute),
                    OPERATOR_CODEC.fieldOf(JolCraftParameterIds.OPERATOR).forGetter(AttributeRequirement::operator),
                    Codec.DOUBLE.fieldOf(JolCraftParameterIds.VALUE).forGetter(AttributeRequirement::value)
            ).apply(instance, AttributeRequirement::new));

    public static final Codec<AttributeRequirement> CODEC = ParamCodecs.validated(RAW_CODEC);

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<Attribute>> ATTRIBUTE_STREAM =
            ByteBufCodecs.holderRegistry(Registries.ATTRIBUTE);

    public static final StreamCodec<RegistryFriendlyByteBuf, AttributeRequirement> STREAM_CODEC =
            StreamCodec.of(
                    (buf, req) -> {
                        Holder<Attribute> attr = req.attribute;
                        boolean hasAttr = attr != null;
                        buf.writeBoolean(hasAttr);
                        if (hasAttr) {
                            ATTRIBUTE_STREAM.encode(buf, attr);
                        }

                        Operator op = req.operator != null ? req.operator : Operator.EQUAL;
                        buf.writeVarInt(op.ordinal());

                        buf.writeDouble(req.value);
                    },
                    buf -> {
                        Holder<Attribute> attribute = null;
                        boolean hasAttr = buf.readBoolean();
                        if (hasAttr) {
                            attribute = ATTRIBUTE_STREAM.decode(buf);
                        }

                        int opOrdinal = buf.readVarInt();
                        Operator op = Operator.fromOrdinalSafe(opOrdinal).orElse(Operator.EQUAL);

                        double value = buf.readDouble();

                        return new AttributeRequirement(attribute, op, value);
                    }
            );

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull RegistryIntrospection introspection() {
        Holder<Attribute> a = attribute;
        if (a == null) {
            return RegistryIntrospection.mixed(Registries.ATTRIBUTE, 0, false);
        }
        return RegistryIntrospection.single(Registries.ATTRIBUTE, a);
    }

    // ---------------------------------------------------------------------
    // VALIDATION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull DataResult<AttributeRequirement> validate() {
        if (attribute == null) {
            return SelfValidating.invalid(JolCraftParameterIds.ATTRIBUTE + " is required");
        }
        if (operator == null) {
            return SelfValidating.invalid(JolCraftParameterIds.OPERATOR + " is required");
        }
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return SelfValidating.invalid(JolCraftParameterIds.VALUE + " must be finite (value=" + value + ")");
        }
        return SelfValidating.ok(this);
    }

    // ---------------------------------------------------------------------
    // MATCHING
    // ---------------------------------------------------------------------

    public boolean matches(Entity entity) {
        if (!(entity instanceof LivingEntity living)) {
            return false;
        }
        if (attribute == null || operator == null) {
            return false;
        }
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return false;
        }

        double current = living.getAttributeValue(attribute);
        return operator.test(current, value);
    }
}