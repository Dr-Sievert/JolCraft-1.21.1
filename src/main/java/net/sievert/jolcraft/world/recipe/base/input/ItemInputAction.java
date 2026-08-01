package net.sievert.jolcraft.world.recipe.base.input;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public record ItemInputAction(
        @NotNull Type type,
        @Nullable Integer amount,
        @Nullable Integer maxAmount
) {

    private static final String MIN = "min";
    private static final String MAX = "max";

    public static final ItemInputAction CATALYST =
            new ItemInputAction(Type.CATALYST, null, null);

    public ItemInputAction(
            @NotNull Type type,
            @Nullable Integer amount
    ) {
        this(type, amount, amount);
    }

    public ItemInputAction {
        Objects.requireNonNull(type, JolCraftDictionary.TYPE);

        if (type == Type.CATALYST) {
            amount = null;
            maxAmount = null;
        } else if (amount != null && maxAmount == null) {
            maxAmount = amount;
        }
    }

    private static final Codec<Either<Integer, AmountRange>> AMOUNT_CODEC =
            Codec.either(
                    Codec.INT,
                    AmountRange.CODEC
            );

    public static final Codec<ItemInputAction> CODEC =
            RecordCodecBuilder.<ItemInputAction>create(instance ->
                    instance.group(
                            Type.CODEC
                                    .optionalFieldOf(
                                            JolCraftDictionary.TYPE,
                                            Type.CATALYST
                                    )
                                    .forGetter(ItemInputAction::type),

                            AMOUNT_CODEC
                                    .optionalFieldOf(
                                            JolCraftDictionary.AMOUNT
                                    )
                                    .forGetter(action -> {
                                        if (action.amount() == null) {
                                            return Optional.empty();
                                        }

                                        if (action.resolvedMinAmount()
                                                == action.resolvedMaxAmount()) {
                                            return Optional.of(
                                                    Either.left(
                                                            action.resolvedMinAmount()
                                                    )
                                            );
                                        }

                                        return Optional.of(
                                                Either.right(
                                                        new AmountRange(
                                                                action.resolvedMinAmount(),
                                                                action.resolvedMaxAmount()
                                                        )
                                                )
                                        );
                                    })
                    ).apply(
                            instance,
                            (type, serializedAmount) -> {
                                if (serializedAmount.isEmpty()) {
                                    return new ItemInputAction(
                                            type,
                                            null,
                                            null
                                    );
                                }

                                return serializedAmount.get().map(
                                        value -> new ItemInputAction(
                                                type,
                                                value,
                                                value
                                        ),
                                        range -> new ItemInputAction(
                                                type,
                                                range.min(),
                                                range.max()
                                        )
                                );
                            }
                    )
            ).validate(ItemInputAction::validateAndNormalize);

    public static final StreamCodec<
            RegistryFriendlyByteBuf,
            ItemInputAction
            > STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8,
            action -> action.type().getSerializedName(),

            ByteBufCodecs.VAR_INT,
            action -> action.amount() == null
                    ? 0
                    : Math.max(0, action.amount()),

            ByteBufCodecs.VAR_INT,
            action -> action.maxAmount() == null
                    ? 0
                    : Math.max(0, action.maxAmount()),

            (typeId, encodedAmount, encodedMaxAmount) -> {
                Type type = Type.byIdOrThrow(typeId);

                if (type == Type.CATALYST) {
                    return CATALYST;
                }

                int min = Math.max(1, encodedAmount);
                int max = Math.max(min, encodedMaxAmount);

                return new ItemInputAction(
                        type,
                        min,
                        max
                );
            }
    );

    public static ItemInputAction consume() {
        return consume(1);
    }

    public static ItemInputAction consume(int amount) {
        return new ItemInputAction(
                Type.CONSUME,
                amount,
                amount
        );
    }

    public static ItemInputAction consume(
            int minAmount,
            int maxAmount
    ) {
        return new ItemInputAction(
                Type.CONSUME,
                minAmount,
                maxAmount
        );
    }

    public static ItemInputAction damage() {
        return damage(1);
    }

    public static ItemInputAction damage(int amount) {
        return new ItemInputAction(
                Type.DAMAGE,
                amount,
                amount
        );
    }

    public static ItemInputAction damage(
            int minAmount,
            int maxAmount
    ) {
        return new ItemInputAction(
                Type.DAMAGE,
                minAmount,
                maxAmount
        );
    }

    public int resolvedAmount() {
        return resolvedMinAmount();
    }

    public int resolvedAmount(RandomSource random) {
        int min = resolvedMinAmount();
        int max = resolvedMaxAmount();

        return min == max
                ? min
                : random.nextIntBetweenInclusive(min, max);
    }

    public int resolvedMinAmount() {
        return amount == null || amount < 1
                ? 1
                : amount;
    }

    public int resolvedMaxAmount() {
        return maxAmount == null || maxAmount < resolvedMinAmount()
                ? resolvedMinAmount()
                : maxAmount;
    }

    public boolean isSatisfied(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }

        return switch (type) {
            case CATALYST -> true;

            case CONSUME ->
                    stack.getCount() >= resolvedMaxAmount();

            case DAMAGE ->
                    stack.isDamageableItem();
        };
    }

    public void apply(
            ServerLevel level,
            @Nullable ServerPlayer player,
            ItemStack stack
    ) {
        if (type == Type.CATALYST || !isSatisfied(stack)) {
            return;
        }

        if (player != null && player.isCreative()) {
            return;
        }

        int resolvedAmount = resolvedAmount(level.getRandom());

        switch (type) {
            case CONSUME ->
                    stack.shrink(resolvedAmount);

            case DAMAGE ->
                    stack.hurtAndBreak(
                            resolvedAmount,
                            level,
                            player,
                            brokenItem -> {
                                if (player != null) {
                                    level.playSound(
                                            null,
                                            player.blockPosition(),
                                            brokenItem.getBreakingSound(),
                                            player.getSoundSource(),
                                            1.0F,
                                            1.0F
                                    );
                                }
                            }
                    );
        }
    }

    public DataResult<ItemInputAction> validate() {
        return validateAndNormalize(this);
    }

    private static DataResult<ItemInputAction> validateAndNormalize(
            ItemInputAction action
    ) {
        if (action.type() == Type.CATALYST) {
            return DataResult.success(CATALYST);
        }

        int min = action.amount() == null
                ? 1
                : action.amount();

        int max = action.maxAmount() == null
                ? min
                : action.maxAmount();

        if (min < 1) {
            return DataResult.error(() ->
                    JolCraftDictionary.AMOUNT
                            + "." + MIN
                            + " must be at least 1"
            );
        }

        if (max < 1) {
            return DataResult.error(() ->
                    JolCraftDictionary.AMOUNT
                            + "." + MAX
                            + " must be at least 1"
            );
        }

        if (max < min) {
            return DataResult.error(() ->
                    JolCraftDictionary.AMOUNT
                            + "." + MAX
                            + " must be greater than or equal to "
                            + JolCraftDictionary.AMOUNT
                            + "." + MIN
            );
        }

        return DataResult.success(
                new ItemInputAction(
                        action.type(),
                        min,
                        max
                )
        );
    }

    private record AmountRange(
            int min,
            int max
    ) {

        private static final Codec<AmountRange> CODEC =
                RecordCodecBuilder.create(instance ->
                        instance.group(
                                Codec.INT
                                        .fieldOf(MIN)
                                        .forGetter(AmountRange::min),

                                Codec.INT
                                        .fieldOf(MAX)
                                        .forGetter(AmountRange::max)
                        ).apply(
                                instance,
                                AmountRange::new
                        )
                );
    }

    public enum Type implements StringRepresentable {
        CONSUME(JolCraftDictionary.CONSUME),
        CATALYST(JolCraftDictionary.CATALYST),
        DAMAGE(JolCraftDictionary.DAMAGE);

        public static final Codec<Type> CODEC =
                StringRepresentable.fromEnum(Type::values);

        private final String serializedName;

        Type(String serializedName) {
            this.serializedName = serializedName;
        }

        @Override
        public @NotNull String getSerializedName() {
            return serializedName;
        }

        public static Type byId(@Nullable String id) {
            if (id != null) {
                for (Type type : values()) {
                    if (type.serializedName.equals(id)) {
                        return type;
                    }
                }
            }

            return CATALYST;
        }

        public static Type byIdOrThrow(@Nullable String id) {
            if (id != null) {
                for (Type type : values()) {
                    if (type.serializedName.equals(id)) {
                        return type;
                    }
                }
            }

            throw new IllegalArgumentException(
                    "Unknown item input action type: " + id
            );
        }
    }
}