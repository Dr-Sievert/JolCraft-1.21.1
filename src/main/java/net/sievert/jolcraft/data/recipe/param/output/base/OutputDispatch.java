package net.sievert.jolcraft.data.recipe.param.output.base;

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
import net.sievert.jolcraft.data.recipe.param.output.hook.Hook;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public final class OutputDispatch {

    private OutputDispatch() {}

    // ---------------------------------------------------------------------
    // TYPE IDS
    // ---------------------------------------------------------------------

    public static final ResourceLocation TYPE_NONE =
            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.EMPTY, JolCraftDictionary.OUTPUT));

    public static final ResourceLocation TYPE_INVALID =
            JolCraft.location(JolCraftStrings.underscored(JolCraftDictionary.INVALID, JolCraftDictionary.OUTPUT));

    public static final ResourceLocation TYPE_MISSING =
            JolCraft.location(JolCraftStrings.underscored(
                    JolCraftDictionary.OUTPUT,
                    JolCraftParameterIds.TYPE,
                    JolCraftDictionary.MISSING
            ));

    public static final String KEY_UNKNOWN_TYPE =
            JolCraftStrings.underscored(JolCraftDictionary.UNKNOWN, JolCraftParameterIds.TYPE);

    // ---------------------------------------------------------------------
    // DISCRIMINATORS
    // ---------------------------------------------------------------------

    public static final byte DISC_NONE = 0;
    public static final byte DISC_INVALID = 127;

    private static final Map<ResourceLocation, ParamDispatch.Entry<OutputParam>> BY_TYPE_ID = new LinkedHashMap<>();
    private static final Map<Byte, ParamDispatch.Entry<OutputParam>> BY_DISC = new LinkedHashMap<>();

    private static final ParamDispatch.Entry<OutputParam> ENTRY_NONE =
            new ParamDispatch.Entry<>(TYPE_NONE, DISC_NONE, None.CODEC, None.STREAM_CODEC);

    private static final ParamDispatch.Entry<OutputParam> ENTRY_INVALID =
            new ParamDispatch.Entry<>(TYPE_INVALID, DISC_INVALID, Invalid.CODEC, Invalid.STREAM_CODEC);

    private static boolean FROZEN = false;

    static {
        BY_TYPE_ID.put(TYPE_NONE, ENTRY_NONE);
        BY_DISC.put(DISC_NONE, ENTRY_NONE);

        BY_TYPE_ID.put(TYPE_INVALID, ENTRY_INVALID);
        BY_DISC.put(DISC_INVALID, ENTRY_INVALID);
    }

    // ---------------------------------------------------------------------
    // REGISTRATION
    // ---------------------------------------------------------------------

    private static boolean isReservedDisc(byte disc) {
        return disc == DISC_NONE || disc == DISC_INVALID;
    }

    private static byte nextFreeDiscOrInvalid() {
        for (int i = 1; i <= 126; i++) {
            byte disc = (byte) i;
            if (!isReservedDisc(disc) && !BY_DISC.containsKey(disc)) {
                return disc;
            }
        }
        return DISC_INVALID;
    }

    public static boolean register(
            @NotNull ResourceLocation typeId,
            @NotNull Codec<? extends OutputParam> codec,
            @NotNull StreamCodec<RegistryFriendlyByteBuf, ? extends OutputParam> streamCodec
    ) {
        if (FROZEN) return false;
        if (BY_TYPE_ID.containsKey(typeId)) return false;
        if (typeId.equals(TYPE_NONE) || typeId.equals(TYPE_INVALID) || typeId.equals(TYPE_MISSING)) return false;

        byte discriminator = nextFreeDiscOrInvalid();
        if (discriminator == DISC_INVALID) return false;

        ParamDispatch.Entry<OutputParam> entry =
                new ParamDispatch.Entry<>(typeId, discriminator, codec, streamCodec);

        BY_TYPE_ID.put(typeId, entry);
        BY_DISC.put(discriminator, entry);
        return true;
    }

    public static void freeze() {
        FROZEN = true;
    }

    // ---------------------------------------------------------------------
    // BASE DISPATCH
    // ---------------------------------------------------------------------

    private static final OutputParam DEFAULT_VALUE = new Invalid(TYPE_MISSING);
    private static final Supplier<OutputParam> DEFAULT = () -> DEFAULT_VALUE;

    private static final Function<OutputParam, ResourceLocation> TYPE_FN =
            v -> v != null ? v.typeId() : TYPE_INVALID;

    private static final Codec<OutputParam> BASE_CODEC =
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

    private static final StreamCodec<RegistryFriendlyByteBuf, OutputParam> BASE_STREAM_CODEC =
            ParamDispatch.streamCodec(
                    DEFAULT,
                    TYPE_FN,
                    BY_TYPE_ID,
                    BY_DISC,
                    ENTRY_INVALID,
                    Invalid::new,
                    TYPE_INVALID
            );

    // ---------------------------------------------------------------------
    // UTIL
    // ---------------------------------------------------------------------

    private static final int MAX_HOOKS = 256;

    private static <T> List<T> sanitizeList(List<T> in) {
        if (in == null || in.isEmpty()) return List.of();
        ArrayList<T> safe = new ArrayList<>(in.size());
        for (T t : in) if (t != null) safe.add(t);
        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }

    private static @NotNull List<Hook> sanitizeHooks(List<Hook> hooks) {
        return sanitizeList(hooks);
    }

    private static @NotNull OutputParam unwrapForBase(@NotNull OutputParam v) {
        return OutputParam.unwrap(v);
    }

    // ---------------------------------------------------------------------
    // CODEC
    // ---------------------------------------------------------------------

    public static final Codec<OutputParam> CODEC = new Codec<>() {

        @Override
        public <T> DataResult<Pair<OutputParam, T>> decode(DynamicOps<T> ops, T input) {
            return BASE_CODEC.decode(ops, input).flatMap(basePair -> {
                OutputParam base = basePair.getFirst();
                T rest = basePair.getSecond();

                List<Hook> hooks =
                        ops.getMap(input).result()
                                .map(mapLike -> {
                                    T key = ops.createString(JolCraftParameterIds.HOOKS);
                                    T value = mapLike.get(key);
                                    if (value == null) return List.<Hook>of();

                                    return Hook.CODEC.listOf()
                                            .parse(ops, value)
                                            .result()
                                            .orElse(List.of());
                                })
                                .orElse(List.of());

                OutputParam wrapped = base.withHooks(sanitizeHooks(hooks));

                DataResult<?> vr = wrapped.validate();
                Optional<? extends DataResult.Error<?>> err = vr.error();
                return err.<DataResult<Pair<OutputParam, T>>>map(error -> DataResult.error(() ->
                        "output param invalid: " + error.message()
                )).orElseGet(() -> DataResult.success(Pair.of(wrapped, rest)));
            });
        }

        @Override
        public <T> DataResult<T> encode(OutputParam input, DynamicOps<T> ops, T prefix) {
            if (input == null) input = DEFAULT.get();

            OutputParam base = unwrapForBase(input);
            DataResult<T> baseEncoded = BASE_CODEC.encode(base, ops, prefix);

            List<Hook> hooks = sanitizeHooks(input.hooks());
            if (hooks.isEmpty()) {
                return baseEncoded;
            }

            return baseEncoded.flatMap(obj ->
                    Hook.CODEC.listOf()
                            .encodeStart(ops, hooks)
                            .flatMap(listValue ->
                                    ops.mergeToMap(
                                            obj,
                                            ops.createString(JolCraftParameterIds.HOOKS),
                                            listValue
                                    )
                            )
            );
        }
    };

    // ---------------------------------------------------------------------
    // STREAM
    // ---------------------------------------------------------------------

    public static final StreamCodec<RegistryFriendlyByteBuf, OutputParam> STREAM_CODEC =
            StreamCodec.of(
                    (buf, v) -> {


                        OutputParam base = unwrapForBase(v);
                        BASE_STREAM_CODEC.encode(buf, base);

                        List<Hook> hooks = sanitizeHooks(v.hooks());
                        int size = Math.min(hooks.size(), MAX_HOOKS);

                        buf.writeVarInt(size);
                        for (int i = 0; i < size; i++) {
                            Hook.STREAM_CODEC.encode(buf, hooks.get(i));
                        }
                    },
                    buf -> {
                        OutputParam base = BASE_STREAM_CODEC.decode(buf);

                        int size = buf.readVarInt();
                        if (size <= 0) return base;

                        int capped = Math.min(size, MAX_HOOKS);
                        ArrayList<Hook> hooks = new ArrayList<>(capped);

                        for (int i = 0; i < capped; i++) {
                            hooks.add(Hook.STREAM_CODEC.decode(buf));
                        }
                        for (int i = capped; i < size; i++) {
                            Hook.STREAM_CODEC.decode(buf);
                        }

                        return base.withHooks(sanitizeHooks(hooks));
                    }
            );

    // ---------------------------------------------------------------------
    // BUILT-IN TYPES
    // ---------------------------------------------------------------------

    public record None() implements OutputParam {

        public static final None INSTANCE = new None();
        public static final Codec<None> CODEC = Codec.unit(INSTANCE);
        public static final StreamCodec<RegistryFriendlyByteBuf, None> STREAM_CODEC = StreamCodec.unit(INSTANCE);

        @Override
        public @NotNull ResourceLocation typeId() {
            return TYPE_NONE;
        }

        @Override
        public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
            return List.of();
        }
    }

    public record Invalid(ResourceLocation unknownType) implements OutputParam {

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
        public @NotNull List<Output> generate(@NotNull WorldContext ctx) {
            return List.of();
        }
    }
}