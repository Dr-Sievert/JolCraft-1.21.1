package net.sievert.jolcraft.data.recipe.param.output.custom.item.transform;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public sealed interface ComponentTransform
        extends SelfValidating<ComponentTransform>, RegistryIntrospectable, RegistryIntrospectionSource
        permits ComponentTransform.Config, ComponentTransform.Invalid {

    String SOURCE = JolCraftParameterIds.SOURCE;
    String KEEP = JolCraftParameterIds.KEEP;
    String REMOVE = JolCraftParameterIds.REMOVE;
    String PATCH = JolCraftParameterIds.PATCH;
    String REMOVE_ALL = JolCraftStrings.underscored(REMOVE, JolCraftDictionary.ALL);

    Codec<Holder<DataComponentType<?>>> COMPONENT_TYPE_HOLDER_CODEC =
            RegistryFixedCodec.create(Registries.DATA_COMPONENT_TYPE);

    StreamCodec<RegistryFriendlyByteBuf, Holder<DataComponentType<?>>> COMPONENT_TYPE_HOLDER_STREAM =
            ByteBufCodecs.holderRegistry(Registries.DATA_COMPONENT_TYPE);

    void apply(@Nullable ItemStack input, @NotNull ItemStack output);

    default boolean requiresInput() {
        return false;
    }

    @Override
    default @NotNull RegistryIntrospection introspection() {
        return switch (this) {
            case Config c -> {
                Holder<?> single = null;
                int holders = 0;

                holders = scanList(c.keep(), holders);
                if (holders == 1) single = firstNonNull(c.keep());

                int before = holders;
                holders = scanList(c.remove(), holders);

                if (holders == 1) {
                    single = (single != null) ? single : firstNonNull(c.remove());
                } else if (holders != before) {
                    single = null;
                }

                yield (single != null)
                        ? RegistryIntrospection.single(Registries.DATA_COMPONENT_TYPE, single)
                        : RegistryIntrospection.mixed(Registries.DATA_COMPONENT_TYPE, holders, false);
            }
            case Invalid ignored -> RegistryIntrospection.mixed(Registries.DATA_COMPONENT_TYPE, 0, false);
        };
    }

    @Override
    default @NotNull List<RegistryIntrospection> introspections() {
        return asList();
    }

    private static int scanList(@Nullable List<? extends Holder<?>> list, int holders) {
        if (list == null) return holders;
        for (Holder<?> h : list) {
            if (h != null) holders++;
        }
        return holders;
    }

    private static @Nullable Holder<?> firstNonNull(@Nullable List<? extends Holder<?>> list) {
        if (list == null) return null;
        for (Holder<?> h : list) {
            if (h != null) return h;
        }
        return null;
    }

    private static @Nullable String sanitizeSource(@Nullable String source) {
        if (source == null) return null;
        String s = source.trim();
        return s.isEmpty() ? null : s.toLowerCase(Locale.ROOT);
    }

    MapCodec<Config> RAW_MAP_CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING
                            .optionalFieldOf(SOURCE)
                            .forGetter(c -> java.util.Optional.ofNullable(c.source())),

                    Codec.BOOL
                            .optionalFieldOf(REMOVE_ALL, false)
                            .forGetter(Config::removeAll),

                    COMPONENT_TYPE_HOLDER_CODEC.listOf()
                            .optionalFieldOf(KEEP, List.of())
                            .forGetter(Config::keep),

                    COMPONENT_TYPE_HOLDER_CODEC.listOf()
                            .optionalFieldOf(REMOVE, List.of())
                            .forGetter(Config::remove),

                    DataComponentPatch.CODEC
                            .optionalFieldOf(PATCH, DataComponentPatch.EMPTY)
                            .forGetter(Config::patch)
            ).apply(instance, (source, removeAll, keep, remove, patch) ->
                    new Config(source.orElse(null), removeAll, keep, remove, patch)));

    MapCodec<ComponentTransform> MAP_CODEC =
            RAW_MAP_CODEC.xmap(v -> v, v -> (Config) v);

    Codec<ComponentTransform> CODEC =
            ParamCodecs.validated(MAP_CODEC.codec());

    int MAX_SOURCE_LENGTH = 256;
    int MAX_COMPONENT_TYPES = 256;

    StreamCodec<RegistryFriendlyByteBuf, Config> RAW_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.optional(ByteBufCodecs.stringUtf8(MAX_SOURCE_LENGTH)),
                    c -> java.util.Optional.ofNullable(c.source()),

                    ByteBufCodecs.BOOL, Config::removeAll,

                    ByteBufCodecs.collection(ArrayList::new, COMPONENT_TYPE_HOLDER_STREAM, MAX_COMPONENT_TYPES),
                    Config::keep,

                    ByteBufCodecs.collection(ArrayList::new, COMPONENT_TYPE_HOLDER_STREAM, MAX_COMPONENT_TYPES),
                    Config::remove,

                    DataComponentPatch.STREAM_CODEC, Config::patch,

                    (source, removeAll, keep, remove, patch) ->
                            new Config(source.orElse(null), removeAll, keep, remove, patch)
            );

    StreamCodec<RegistryFriendlyByteBuf, ComponentTransform> STREAM_CODEC =
            StreamCodec.of(
                    (buf, v) -> RAW_STREAM_CODEC.encode(buf, v instanceof Config c ? c : Config.EMPTY),
                    RAW_STREAM_CODEC::decode
            );

    @Override
    default DataResult<ComponentTransform> validate() {
        if (this instanceof Config c) return c.validate();
        if (this instanceof Invalid i) return i.validate();
        return SelfValidating.invalid("unknown component transform");
    }

    static ComponentTransform config(
            @Nullable String source,
            boolean removeAll,
            List<Holder<DataComponentType<?>>> keep,
            List<Holder<DataComponentType<?>>> remove,
            DataComponentPatch patch
    ) {
        return new Config(source, removeAll, keep, remove, patch);
    }

    record Invalid() implements ComponentTransform {
        public static final Invalid INSTANCE = new Invalid();

        @Override
        public void apply(@Nullable ItemStack input, @NotNull ItemStack output) {
        }

        @Override
        public DataResult<ComponentTransform> validate() {
            return SelfValidating.invalid("invalid component transform");
        }
    }

    record Config(
            @Nullable String source,
            boolean removeAll,
            List<Holder<DataComponentType<?>>> keep,
            List<Holder<DataComponentType<?>>> remove,
            DataComponentPatch patch
    ) implements ComponentTransform {

        public static final Config EMPTY =
                new Config(null, false, List.of(), List.of(), DataComponentPatch.EMPTY);

        public Config(
                @Nullable String source,
                boolean removeAll,
                List<Holder<DataComponentType<?>>> keep,
                List<Holder<DataComponentType<?>>> remove,
                DataComponentPatch patch
        ) {
            this.source = sanitizeSource(source);
            this.removeAll = removeAll;
            this.keep = sanitizeHolders(keep);
            this.remove = sanitizeHolders(remove);
            this.patch = patch;
        }

        private boolean hasCopyRules() {
            return removeAll || !remove.isEmpty();
        }

        @Override
        public boolean requiresInput() {
            return hasCopyRules();
        }

        @Override
        public void apply(@Nullable ItemStack input, @NotNull ItemStack output) {
            if (output.isEmpty()) return;

            if (input != null && !input.isEmpty()) {
                if (removeAll) {
                    HashSet<DataComponentType<?>> keepSet = new HashSet<>(keep.size());
                    for (Holder<DataComponentType<?>> h : keep) {
                        if (h != null) {
                            keepSet.add(h.value());
                        }
                    }

                    for (TypedDataComponent<?> typed : input.getComponents()) {
                        if (keepSet.contains(typed.type())) {
                            copyTypedComponent(output, typed);
                        }
                    }
                } else if (!remove.isEmpty()) {
                    HashSet<DataComponentType<?>> removeSet = new HashSet<>(remove.size());
                    for (Holder<DataComponentType<?>> h : remove) {
                        if (h != null) {
                            removeSet.add(h.value());
                        }
                    }

                    for (TypedDataComponent<?> typed : input.getComponents()) {
                        if (!removeSet.contains(typed.type())) {
                            copyTypedComponent(output, typed);
                        }
                    }
                }
            }

            if (!patch.isEmpty()) {
                output.applyComponents(patch);
            }
        }

        @Override
        public DataResult<ComponentTransform> validate() {
            if (removeAll) {
                if (!remove.isEmpty()) {
                    return SelfValidating.invalid("'" + REMOVE + "' is not allowed when '" + REMOVE_ALL + "' is true");
                }
            } else {
                if (!keep.isEmpty()) {
                    return SelfValidating.invalid("'" + KEEP + "' is only allowed when '" + REMOVE_ALL + "' is true");
                }
            }

            boolean hasCopyRules = hasCopyRules();
            if (hasCopyRules && source == null) {
                return SelfValidating.invalid("'" + SOURCE + "' is required when copy/filter component rules are used");
            }

            for (int i = 0; i < keep.size(); i++) {
                Holder<DataComponentType<?>> h = keep.get(i);
                if (h == null) {
                    return SelfValidating.invalid(KEEP + "[" + i + "] is null");
                }
                h.value();
            }

            for (int i = 0; i < remove.size(); i++) {
                Holder<DataComponentType<?>> h = remove.get(i);
                if (h == null) {
                    return SelfValidating.invalid(REMOVE + "[" + i + "] is null");
                }
                h.value();
            }

            for (var e : patch.entrySet()) {
                if (e == null) continue;
                var v = e.getValue();
                if (v == null || v.isEmpty()) {
                    return SelfValidating.invalid("'" + PATCH + "' must not remove components; use '" + REMOVE + "' / '" + REMOVE_ALL + "' instead");
                }
            }

            return SelfValidating.ok(this);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void copyTypedComponent(@NotNull ItemStack output, @NotNull TypedDataComponent<?> typed) {
        output.set((DataComponentType) typed.type(), typed.value());
    }

    private static <T> List<T> sanitizeHolders(@Nullable List<T> in) {
        if (in == null || in.isEmpty()) return List.of();

        ArrayList<T> out = new ArrayList<>(in.size());
        for (T t : in) {
            if (t != null) out.add(t);
        }

        return out.isEmpty() ? List.of() : List.copyOf(out);
    }
}