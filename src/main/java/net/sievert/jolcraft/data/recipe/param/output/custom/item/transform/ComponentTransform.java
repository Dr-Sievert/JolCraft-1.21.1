package net.sievert.jolcraft.data.recipe.param.output.custom.item.transform;

import com.mojang.datafixers.util.Pair;
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
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
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
import java.util.Optional;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public sealed interface ComponentTransform
        extends SelfValidating<ComponentTransform>, RegistryIntrospectable, RegistryIntrospectionSource
        permits ComponentTransform.Config, ComponentTransform.Invalid {

    String SOURCE = JolCraftParameterIds.SOURCE;
    String KEEP = JolCraftParameterIds.KEEP;
    String REMOVE = JolCraftParameterIds.REMOVE;
    String SET = JolCraftDictionary.SET;
    String REMOVE_ALL = JolCraftStrings.underscored(REMOVE, JolCraftDictionary.ALL);

    int MAX_SOURCE_LENGTH = 256;
    int MAX_COMPONENT_TYPES = 256;

    Codec<Holder<DataComponentType<?>>> COMPONENT_TYPE_HOLDER_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Holder<DataComponentType<?>>, T>> decode(
                com.mojang.serialization.DynamicOps<T> ops,
                T input
        ) {
            return ResourceLocation.CODEC.decode(ops, input).flatMap(pair -> {
                ResourceLocation id = pair.getFirst();
                T rest = pair.getSecond();

                if (!(ops instanceof RegistryOps<T> registryOps)) {
                    return DataResult.error(() ->
                            "component transform requires RegistryOps for '" +
                                    Registries.DATA_COMPONENT_TYPE.location() + "'"
                    );
                }

                var lookupOpt = registryOps.lookupProvider.lookup(Registries.DATA_COMPONENT_TYPE);
                if (lookupOpt.isEmpty()) {
                    return DataResult.error(() ->
                            "missing registry info for '" +
                                    Registries.DATA_COMPONENT_TYPE.location() + "'"
                    );
                }

                ResourceKey<DataComponentType<?>> key =
                        ResourceKey.create(Registries.DATA_COMPONENT_TYPE, id);

                Optional<Holder.Reference<DataComponentType<?>>> holderOpt =
                        lookupOpt.get().getter().get(key);

                return holderOpt.<DataResult<Pair<Holder<DataComponentType<?>>, T>>>map(dataComponentTypeReference ->
                        DataResult.success(Pair.of(dataComponentTypeReference, rest))).orElseGet(() -> DataResult.error(() ->
                        "unknown data component type '" + id + "'"
                ));

            });
        }

        @Override
        public <T> DataResult<T> encode(
                Holder<DataComponentType<?>> input,
                com.mojang.serialization.DynamicOps<T> ops,
                T prefix
        ) {

            return input.unwrapKey()
                    .map(ResourceKey::location)
                    .map(id -> ResourceLocation.CODEC.encode(id, ops, prefix))
                    .orElseGet(() -> DataResult.error(() -> "unkeyed data component holder"));
        }
    };

    StreamCodec<RegistryFriendlyByteBuf, Holder<DataComponentType<?>>> COMPONENT_TYPE_HOLDER_STREAM =
            ByteBufCodecs.holderRegistry(Registries.DATA_COMPONENT_TYPE);

    MapCodec<Config> RAW_MAP_CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    Codec.STRING
                            .optionalFieldOf(SOURCE)
                            .forGetter(c -> Optional.ofNullable(c.source())),

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
                            .optionalFieldOf(SET, DataComponentPatch.EMPTY)
                            .forGetter(Config::set)
            ).apply(instance, (source, removeAll, keep, remove, set) ->
                    new Config(
                            source.orElse(null),
                            removeAll,
                            keep != null ? keep : List.of(),
                            remove != null ? remove : List.of(),
                            set != null ? set : DataComponentPatch.EMPTY
                    )));

    MapCodec<ComponentTransform> MAP_CODEC =
            RAW_MAP_CODEC.xmap(v -> v, v -> (Config) v);

    Codec<ComponentTransform> CODEC =
            ParamCodecs.validated(MAP_CODEC.codec());

    StreamCodec<RegistryFriendlyByteBuf, Config> RAW_STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.optional(ByteBufCodecs.stringUtf8(MAX_SOURCE_LENGTH)),
                    c -> Optional.ofNullable(c.source()),

                    ByteBufCodecs.BOOL,
                    Config::removeAll,

                    ByteBufCodecs.collection(ArrayList::new, COMPONENT_TYPE_HOLDER_STREAM, MAX_COMPONENT_TYPES),
                    Config::keep,

                    ByteBufCodecs.collection(ArrayList::new, COMPONENT_TYPE_HOLDER_STREAM, MAX_COMPONENT_TYPES),
                    Config::remove,

                    DataComponentPatch.STREAM_CODEC,
                    Config::set,

                    (source, removeAll, keep, remove, set) ->
                            new Config(
                                    source.orElse(null),
                                    removeAll,
                                    keep != null ? keep : List.of(),
                                    remove != null ? remove : List.of(),
                                    set != null ? set : DataComponentPatch.EMPTY
                            )
            );

    StreamCodec<RegistryFriendlyByteBuf, ComponentTransform> STREAM_CODEC =
            StreamCodec.of(
                    (buf, value) -> RAW_STREAM_CODEC.encode(buf, value instanceof Config c ? c : Config.EMPTY),
                    RAW_STREAM_CODEC::decode
            );

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
                if (holders == 1) {
                    single = firstNonNull(c.keep());
                }

                int before = holders;
                holders = scanList(c.remove(), holders);

                if (holders == 1) {
                    if (single == null) {
                        single = firstNonNull(c.remove());
                    }
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

    @Override
    default DataResult<ComponentTransform> validate() {
        if (this instanceof Config c) {
            return c.validate();
        }
        if (this instanceof Invalid i) {
            return i.validate();
        }
        return SelfValidating.invalid("unknown component transform");
    }

    static ComponentTransform config(
            @Nullable String source,
            boolean removeAll,
            List<Holder<DataComponentType<?>>> keep,
            List<Holder<DataComponentType<?>>> remove,
            @Nullable DataComponentPatch set
    ) {
        return new Config(source, removeAll, keep, remove, set);
    }

    private static int scanList(@Nullable List<? extends Holder<?>> list, int holders) {
        if (list == null) {
            return holders;
        }
        for (Holder<?> holder : list) {
            if (holder != null) {
                holders++;
            }
        }
        return holders;
    }

    private static @Nullable Holder<?> firstNonNull(@Nullable List<? extends Holder<?>> list) {
        if (list == null) {
            return null;
        }
        for (Holder<?> holder : list) {
            if (holder != null) {
                return holder;
            }
        }
        return null;
    }

    private static @Nullable String sanitizeSource(@Nullable String source) {
        if (source == null) {
            return null;
        }
        String s = source.trim();
        if (s.isEmpty()) {
            return null;
        }
        return s.toLowerCase(Locale.ROOT);
    }

    private static <T> @NotNull List<T> sanitizeList(@Nullable List<T> in) {
        if (in == null || in.isEmpty()) {
            return List.of();
        }

        ArrayList<T> out = new ArrayList<>(in.size());
        for (T value : in) {
            if (value != null) {
                out.add(value);
            }
        }

        return out.isEmpty() ? List.of() : List.copyOf(out);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void copyTypedComponent(@NotNull ItemStack output, @NotNull TypedDataComponent<?> typed) {
        output.set((DataComponentType) typed.type(), typed.value());
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
            DataComponentPatch set
    ) implements ComponentTransform {

        public static final Config EMPTY =
                new Config(null, false, List.of(), List.of(), DataComponentPatch.EMPTY);

        public Config(
                @Nullable String source,
                boolean removeAll,
                List<Holder<DataComponentType<?>>> keep,
                List<Holder<DataComponentType<?>>> remove,
                @Nullable DataComponentPatch set
        ) {
            this.source = sanitizeSource(source);
            this.removeAll = removeAll;
            this.keep = sanitizeList(keep);
            this.remove = sanitizeList(remove);
            this.set = set != null ? set : DataComponentPatch.EMPTY;
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
            if (output.isEmpty()) {
                return;
            }

            if (input != null && !input.isEmpty()) {
                if (removeAll) {
                    HashSet<DataComponentType<?>> keepSet = new HashSet<>(keep.size());
                    for (Holder<DataComponentType<?>> holder : keep) {
                        keepSet.add(holder.value());
                    }

                    for (TypedDataComponent<?> typed : input.getComponents()) {
                        if (keepSet.contains(typed.type())) {
                            copyTypedComponent(output, typed);
                        }
                    }
                } else if (!remove.isEmpty()) {
                    HashSet<DataComponentType<?>> removeSet = new HashSet<>(remove.size());
                    for (Holder<DataComponentType<?>> holder : remove) {
                        removeSet.add(holder.value());
                    }

                    for (TypedDataComponent<?> typed : input.getComponents()) {
                        if (!removeSet.contains(typed.type())) {
                            copyTypedComponent(output, typed);
                        }
                    }
                }
            }

            if (!set.isEmpty()) {
                output.applyComponents(set);
            }
        }

        @Override
        public DataResult<ComponentTransform> validate() {
            if (removeAll) {
                if (!remove.isEmpty()) {
                    return SelfValidating.invalid(
                            "'" + REMOVE + "' is not allowed when '" + REMOVE_ALL + "' is true"
                    );
                }
            } else if (!keep.isEmpty()) {
                return SelfValidating.invalid(
                        "'" + KEEP + "' is only allowed when '" + REMOVE_ALL + "' is true"
                );
            }

            if (hasCopyRules() && source == null) {
                return SelfValidating.invalid(
                        "'" + SOURCE + "' is required when copy/filter component rules are used"
                );
            }

            for (int i = 0; i < keep.size(); i++) {
                Holder<DataComponentType<?>> holder = keep.get(i);
                if (holder == null) {
                    return SelfValidating.invalid(KEEP + "[" + i + "] is null");
                }
                holder.value();
            }

            for (int i = 0; i < remove.size(); i++) {
                Holder<DataComponentType<?>> holder = remove.get(i);
                if (holder == null) {
                    return SelfValidating.invalid(REMOVE + "[" + i + "] is null");
                }
                holder.value();
            }

            for (var entry : set.entrySet()) {
                if (entry == null) {
                    continue;
                }

                var value = entry.getValue();
                if (value == null || value.isEmpty()) {
                    return SelfValidating.invalid(
                            "'" + SET + "' must not remove components; use '" +
                                    REMOVE + "' / '" + REMOVE_ALL + "' instead"
                    );
                }
            }

            return SelfValidating.ok(this);
        }
    }
}