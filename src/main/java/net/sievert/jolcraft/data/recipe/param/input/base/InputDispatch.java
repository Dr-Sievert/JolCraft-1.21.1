package net.sievert.jolcraft.data.recipe.param.input.base;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.ParamDispatch;
import net.sievert.jolcraft.data.recipe.param.level.WorldContext;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public final class InputDispatch {

    private InputDispatch() {}

    // ---------------------------------------------------------------------
    // TYPE IDS
    // ---------------------------------------------------------------------

    public static final ResourceLocation TYPE_NONE =
            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.EMPTY, JolCraftParameterIds.INPUT));

    public static final ResourceLocation TYPE_INVALID =
            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.INVALID, JolCraftParameterIds.INPUT));

    public static final ResourceLocation TYPE_MISSING =
            JolCraft.location(JolCraftStrings.underscored(
                    JolCraftParameterIds.INPUT,
                    JolCraftParameterIds.TYPE,
                    JolCraftDictionary.MISSING
            ));

    public static final String KEY_UNKNOWN_TYPE =
            JolCraftStrings.underscored(JolCraftDictionary.UNKNOWN, JolCraftParameterIds.TYPE);

    // ---------------------------------------------------------------------
    // RESERVED DISCRIMINATORS
    // ---------------------------------------------------------------------

    public static final byte DISC_NONE = 0;
    public static final byte DISC_INVALID = 127;

    // ---------------------------------------------------------------------
    // MUTABLE TYPE TABLE
    // ---------------------------------------------------------------------

    private static final Map<ResourceLocation, ParamDispatch.Entry<InputParam<?, ?>>> BY_TYPE_ID = new LinkedHashMap<>();
    private static final Map<Byte, ParamDispatch.Entry<InputParam<?, ?>>> BY_DISC = new LinkedHashMap<>();

    private static final ParamDispatch.Entry<InputParam<?, ?>> ENTRY_NONE =
            new ParamDispatch.Entry<>(
                    TYPE_NONE,
                    DISC_NONE,
                    None.CODEC,
                    None.STREAM_CODEC
            );

    private static final ParamDispatch.Entry<InputParam<?, ?>> ENTRY_INVALID =
            new ParamDispatch.Entry<>(
                    TYPE_INVALID,
                    DISC_INVALID,
                    Invalid.CODEC,
                    Invalid.STREAM_CODEC
            );

    private static boolean FROZEN = false;

    static {
        BY_TYPE_ID.putIfAbsent(TYPE_NONE, ENTRY_NONE);
        BY_DISC.putIfAbsent(DISC_NONE, ENTRY_NONE);

        BY_TYPE_ID.putIfAbsent(TYPE_INVALID, ENTRY_INVALID);
        BY_DISC.putIfAbsent(DISC_INVALID, ENTRY_INVALID);
    }

    // ---------------------------------------------------------------------
    // REGISTRATION
    // ---------------------------------------------------------------------

    private static boolean isReservedDisc(byte disc) {
        return disc == DISC_NONE || disc == DISC_INVALID;
    }

    /**
     * Fail-closed discriminator allocator.
     *
     * @return free discriminator, or {@link #DISC_INVALID} if exhausted
     */
    private static byte nextFreeDiscOrInvalid() {
        for (int i = 1; i <= 126; i++) {
            byte disc = (byte) i;
            if (!isReservedDisc(disc) && !BY_DISC.containsKey(disc)) {
                return disc;
            }
        }
        return DISC_INVALID;
    }

    /**
     * Register a new InputParam type with an auto-assigned discriminator.
     *
     * Fail-closed:
     * - frozen => false
     * - reserved ids => false
     * - duplicate id => keeps first; returns true only if idempotent
     * - discriminator exhaustion => false
     */
    public static <T extends InputParam<T, S>, S> boolean register(
            @NotNull ResourceLocation typeId,
            @NotNull Codec<T> codec,
            @NotNull StreamCodec<RegistryFriendlyByteBuf, T> streamCodec
    ) {
        if (FROZEN) return false;

        if (typeId.equals(TYPE_NONE) || typeId.equals(TYPE_INVALID) || typeId.equals(TYPE_MISSING)) {
            return false;
        }

        ParamDispatch.Entry<InputParam<?, ?>> existing = BY_TYPE_ID.get(typeId);
        if (existing != null) {
            return existing.codec() == codec && existing.streamCodec() == streamCodec;
        }

        byte discriminator = nextFreeDiscOrInvalid();
        if (discriminator == DISC_INVALID) return false;

        ParamDispatch.Entry<InputParam<?, ?>> entry =
                new ParamDispatch.Entry<>(
                        typeId,
                        discriminator,
                        codec,
                        streamCodec
                );

        BY_TYPE_ID.put(typeId, entry);
        BY_DISC.put(discriminator, entry);
        return true;
    }

    public static void freeze() {
        FROZEN = true;
    }

    // ---------------------------------------------------------------------
    // CODEC / STREAM_CODEC
    // ---------------------------------------------------------------------

    private static final InputParam<?, ?> DEFAULT_VALUE = new Invalid(TYPE_MISSING);
    private static final Supplier<InputParam<?, ?>> DEFAULT = () -> DEFAULT_VALUE;

    private static final Function<InputParam<?, ?>, ResourceLocation> TYPE_FN = v -> {
        if (v == null) return TYPE_INVALID;
        return v.typeId();
    };

    private static final Codec<InputParam<?, ?>> BASE_CODEC =
            ParamDispatch.codec(
                    JolCraftParameterIds.TYPE,
                    DEFAULT,
                    TYPE_FN,
                    BY_TYPE_ID,
                    ENTRY_INVALID,
                    Invalid::new,
                    TYPE_MISSING,
                    TYPE_INVALID
            );

    public static final Codec<InputParam<?, ?>> CODEC = new Codec<>() {

        @Override
        public <O> DataResult<Pair<InputParam<?, ?>, O>> decode(DynamicOps<O> ops, O input) {
            return BASE_CODEC.decode(ops, input).flatMap(pair -> {
                InputParam<?, ?> v = pair.getFirst();
                if (v == null) {
                    return DataResult.error(() -> "input param invalid: decoded null");
                }

                DataResult<?> vr = v.validate();
                var err = vr.error();
                if (err.isPresent()) {
                    String msg = err.get().message();
                    return DataResult.error(() -> "input param invalid: " + msg);
                }

                return DataResult.success(pair);
            });
        }

        @Override
        public <O> DataResult<O> encode(InputParam<?, ?> input, DynamicOps<O> ops, O prefix) {
            return BASE_CODEC.encode(input, ops, prefix);
        }
    };

    private static final StreamCodec<RegistryFriendlyByteBuf, InputParam<?, ?>> BASE_STREAM_CODEC =
            ParamDispatch.streamCodec(
                    DEFAULT,
                    TYPE_FN,
                    BY_TYPE_ID,
                    BY_DISC,
                    ENTRY_INVALID,
                    Invalid::new,
                    TYPE_INVALID
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, InputParam<?, ?>> STREAM_CODEC =
            StreamCodec.of(
                    BASE_STREAM_CODEC,
                    buf -> {
                        InputParam<?, ?> v = BASE_STREAM_CODEC.decode(buf);

                        DataResult<?> vr = v.validate();
                        return vr.error().isPresent() ? new Invalid(TYPE_INVALID) : v;
                    }
            );

    // ---------------------------------------------------------------------
    // BUILT-IN BASE VARIANTS
    // ---------------------------------------------------------------------

    public record None() implements InputParam<None, Object> {

        public static final None INSTANCE = new None();

        public static final Codec<None> CODEC = Codec.unit(INSTANCE);
        public static final StreamCodec<RegistryFriendlyByteBuf, None> STREAM_CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public @NotNull ResourceLocation typeId() {
            return TYPE_NONE;
        }

        @Override
        public boolean matches(@NotNull WorldContext ctx, @Nullable Object subject) {
            return false;
        }

        @Override
        public @NotNull Codec<None> codec() {
            return CODEC;
        }

        @Override
        public @NotNull DataResult<None> validate() {
            return DataResult.success(this);
        }
    }

    public record Invalid(ResourceLocation unknownType) implements InputParam<Invalid, Object> {

        private ResourceLocation unknownTypeSafe() {
            return unknownType == null ? TYPE_INVALID : unknownType;
        }

        private static final Codec<Invalid> RAW_CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        ResourceLocation.CODEC
                                .optionalFieldOf(KEY_UNKNOWN_TYPE, TYPE_INVALID)
                                .forGetter(Invalid::unknownTypeSafe)
                ).apply(instance, Invalid::new));

        public static final Codec<Invalid> CODEC = RAW_CODEC;

        public static final StreamCodec<RegistryFriendlyByteBuf, Invalid> STREAM_CODEC =
                StreamCodec.composite(
                        ResourceLocation.STREAM_CODEC, Invalid::unknownTypeSafe,
                        Invalid::new
                );

        @Override
        public @NotNull ResourceLocation typeId() {
            return TYPE_INVALID;
        }

        @Override
        public boolean matches(@NotNull WorldContext ctx, @Nullable Object subject) {
            return false;
        }

        @Override
        public @NotNull Codec<Invalid> codec() {
            return CODEC;
        }

        @Override
        public @NotNull DataResult<Invalid> validate() {
            return DataResult.success(this);
        }
    }
}