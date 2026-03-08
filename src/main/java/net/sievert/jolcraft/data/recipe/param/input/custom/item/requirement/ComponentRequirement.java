package net.sievert.jolcraft.data.recipe.param.input.custom.item.requirement;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.param.base.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.base.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectable;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record ComponentRequirement(
        List<DataComponentPredicate> predicates,
        List<Holder<DataComponentType<?>>> has
) implements SelfValidating<ComponentRequirement>, RegistryIntrospectable {

    /**
     * Decode/encode "has" as ids, but resolve to holders during decode using RegistryOps.
     * Matching is holder-based.
     */
    private static final Codec<List<Holder<DataComponentType<?>>>> HAS_CODEC = new Codec<>() {
        @Override
        public <T> DataResult<Pair<List<Holder<DataComponentType<?>>>, T>> decode(DynamicOps<T> ops, T input) {
            final DataResult<Pair<List<ResourceLocation>, T>> idsRes =
                    ResourceLocation.CODEC.listOf().decode(ops, input);

            return idsRes.flatMap(pair -> {
                final List<ResourceLocation> ids = pair.getFirst();
                final T rest = pair.getSecond();

                if (!(ops instanceof RegistryOps<T> rops)) {
                    return DataResult.error(() ->
                            "ComponentRequirement '" + JolCraftDictionary.HAS + "' requires RegistryOps"
                    );
                }

                final var infoOpt = rops.lookupProvider.lookup(Registries.DATA_COMPONENT_TYPE);
                if (infoOpt.isEmpty()) {
                    return DataResult.error(() ->
                            "missing registry info for '" + Registries.DATA_COMPONENT_TYPE.location() + "'"
                    );
                }

                final var getter = infoOpt.get().getter();
                final ArrayList<Holder<DataComponentType<?>>> out = new ArrayList<>(ids.size());

                for (int i = 0; i < ids.size(); i++) {
                    final ResourceLocation id = ids.get(i);
                    if (id == null) {
                        final int idx = i;
                        return DataResult.error(() ->
                                "'" + JolCraftDictionary.HAS + "' contains null at index " + idx
                        );
                    }

                    final ResourceKey<DataComponentType<?>> key =
                            ResourceKey.create(Registries.DATA_COMPONENT_TYPE, id);

                    final Optional<Holder.Reference<DataComponentType<?>>> refOpt = getter.get(key);
                    if (refOpt.isEmpty()) {
                        return DataResult.error(() ->
                                "unknown data component type '" + id + "' in '" + JolCraftDictionary.HAS + "'"
                        );
                    }

                    out.add(refOpt.get());
                }

                return DataResult.success(Pair.of(sanitizeList(out), rest));
            });
        }

        @Override
        public <T> DataResult<T> encode(List<Holder<DataComponentType<?>>> input, DynamicOps<T> ops, T prefix) {
            final List<Holder<DataComponentType<?>>> list = (input == null) ? List.of() : input;
            final ArrayList<ResourceLocation> ids = new ArrayList<>(list.size());

            for (int i = 0; i < list.size(); i++) {
                final Holder<DataComponentType<?>> h = list.get(i);
                if (h == null) {
                    final int idx = i;
                    return DataResult.error(() -> "'" + JolCraftDictionary.HAS + "' contains null at index " + idx);
                }

                final Optional<ResourceKey<DataComponentType<?>>> keyOpt = h.unwrapKey();
                if (keyOpt.isEmpty()) {
                    final int idx = i;
                    return DataResult.error(() ->
                            "unkeyed data component holder in '" + JolCraftDictionary.HAS + "' at index " + idx
                    );
                }

                ids.add(keyOpt.get().location());
            }

            return ResourceLocation.CODEC.listOf().encode(ids, ops, prefix);
        }
    };

    private static final Codec<ComponentRequirement> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    DataComponentPredicate.CODEC.listOf()
                            .optionalFieldOf(JolCraftParameterIds.PREDICATES, List.of())
                            .forGetter(ComponentRequirement::predicates),

                    HAS_CODEC
                            .optionalFieldOf(JolCraftDictionary.HAS, List.of())
                            .forGetter(ComponentRequirement::has
                            )
            ).apply(instance, ComponentRequirement::new));

    public static final Codec<ComponentRequirement> CODEC = ParamCodecs.validated(RAW_CODEC);

    private static final StreamCodec<RegistryFriendlyByteBuf, List<DataComponentPredicate>> PRED_LIST_STREAM =
            ByteBufCodecs.collection(ArrayList::new, DataComponentPredicate.STREAM_CODEC);

    private static final StreamCodec<RegistryFriendlyByteBuf, Holder<DataComponentType<?>>> HAS_HOLDER_STREAM =
            ByteBufCodecs.holderRegistry(Registries.DATA_COMPONENT_TYPE);

    private static final StreamCodec<RegistryFriendlyByteBuf, List<Holder<DataComponentType<?>>>> HAS_LIST_STREAM =
            ByteBufCodecs.collection(ArrayList::new, HAS_HOLDER_STREAM);

    public static final StreamCodec<RegistryFriendlyByteBuf, ComponentRequirement> STREAM_CODEC =
            StreamCodec.of(
                    (buf, v) -> {
                        final List<DataComponentPredicate> preds = (v.predicates == null) ? List.of() : v.predicates;
                        final ArrayList<DataComponentPredicate> safePreds = new ArrayList<>(preds.size());
                        for (DataComponentPredicate p : preds) if (p != null) safePreds.add(p);
                        PRED_LIST_STREAM.encode(buf, safePreds);

                        final List<Holder<DataComponentType<?>>> hasList = (v.has == null) ? List.of() : v.has;
                        final ArrayList<Holder<DataComponentType<?>>> safeHas = new ArrayList<>(hasList.size());
                        for (Holder<DataComponentType<?>> h : hasList) if (h != null) safeHas.add(h);
                        HAS_LIST_STREAM.encode(buf, safeHas);
                    },
                    buf -> {
                        final List<DataComponentPredicate> preds = PRED_LIST_STREAM.decode(buf);
                        final List<Holder<DataComponentType<?>>> hasList = HAS_LIST_STREAM.decode(buf);
                        return new ComponentRequirement(sanitizeList(preds), sanitizeList(hasList));
                    }
            );

    public ComponentRequirement(List<DataComponentPredicate> predicates, List<Holder<DataComponentType<?>>> has) {
        this.predicates = predicates == null ? List.of() : sanitizeList(predicates);
        this.has = has == null ? List.of() : sanitizeList(has);
    }

    @Override
    public @NotNull RegistryIntrospection introspection() {
        int holders = 0;
        Holder<?> single = null;

        List<Holder<DataComponentType<?>>> list = (has == null) ? List.of() : has;
        for (Holder<DataComponentType<?>> h : list) {
            if (h == null) continue;
            holders++;
            if (holders == 1) single = h;
            else single = null;
        }

        return (single != null)
                ? RegistryIntrospection.single(Registries.DATA_COMPONENT_TYPE, single)
                : RegistryIntrospection.mixed(Registries.DATA_COMPONENT_TYPE, holders, false);
    }

    @Override
    public @NotNull DataResult<ComponentRequirement> validate() {
        if (predicates.isEmpty() && has.isEmpty()) {
            return SelfValidating.invalid(
                    "missing or empty '" + JolCraftParameterIds.PREDICATES + "' and '" + JolCraftDictionary.HAS + "'"
            );
        }

        for (int i = 0; i < predicates.size(); i++) {
            if (predicates.get(i) == null) {
                return SelfValidating.invalid("'" + JolCraftParameterIds.PREDICATES + "' contains null at index " + i);
            }
        }

        for (int i = 0; i < has.size(); i++) {
            final Holder<DataComponentType<?>> h = has.get(i);
            if (h == null) {
                return SelfValidating.invalid("'" + JolCraftDictionary.HAS + "' contains null at index " + i);
            }
            h.value();
        }

        return SelfValidating.ok(this);
    }

    // ---------------------------------------------------------------------
    // MATCHING
    // ---------------------------------------------------------------------

    public boolean matches(ItemStack stack) {
        if (stack.isEmpty()) return false;

        for (Holder<DataComponentType<?>> h : has) {
            if (h == null) return false;
            if (!stack.has(h.value())) return false;
        }

        for (DataComponentPredicate p : predicates) {
            if (p == null || !p.test(stack)) return false;
        }

        return !(has.isEmpty() && predicates.isEmpty());
    }

    private static <T> List<T> sanitizeList(List<T> in) {
        if (in == null || in.isEmpty()) return List.of();
        ArrayList<T> safe = new ArrayList<>(in.size());
        for (T t : in) if (t != null) safe.add(t);
        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }
}