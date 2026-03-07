package net.sievert.jolcraft.data.recipe.param.input.custom.item.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.recipe.param.ParamCodecs;
import net.sievert.jolcraft.data.recipe.param.SelfValidating;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospection;
import net.sievert.jolcraft.data.recipe.param.introspection.RegistryIntrospectionSource;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Aggregates atomic item requirements.
 *
 * All requirements must match.
 * Empty requirements = always matches.
 *
 * Strict server-only runtime:
 * - No registry params in matching; decoding resolves holders where needed.
 *
 * - No throws in ctor/stream decode.
 * - Invalid child requirements are representable and fail-closed at runtime.
 */
@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public record ItemRequirements(
        List<EnchantmentRequirement> enchantments,
        Optional<ComponentRequirement> componentRequirement
) implements SelfValidating<ItemRequirements>, RegistryIntrospectionSource {

    public static final ItemRequirements EMPTY = new ItemRequirements(List.of(), Optional.empty());

    private static final Codec<List<EnchantmentRequirement>> ENCHANTMENTS_CODEC =
            EnchantmentRequirement.CODEC.listOf();

    private static final Codec<ItemRequirements> RAW_CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    ENCHANTMENTS_CODEC
                            .optionalFieldOf(JolCraftParameterIds.ENCHANTMENTS, List.of())
                            .forGetter(ItemRequirements::enchantments),

                    ComponentRequirement.CODEC
                            .optionalFieldOf(JolCraftParameterIds.COMPONENTS)
                            .forGetter(ItemRequirements::componentRequirement)
            ).apply(instance, ItemRequirements::new));

    public static final Codec<ItemRequirements> CODEC = ParamCodecs.validated(RAW_CODEC);

    /**
     * StreamCodec:
     * - No validate() calls (no allocations).
     * - Encodes deterministically.
     * - Null entries are sanitized on encode (fail-closed).
     */
    public static final StreamCodec<RegistryFriendlyByteBuf, ItemRequirements> STREAM_CODEC =
            StreamCodec.of(
                    (buf, v) -> {
                        List<EnchantmentRequirement> ench = v.enchantments == null ? List.of() : v.enchantments;

                        ArrayList<EnchantmentRequirement> safe = new ArrayList<>(ench.size());
                        for (EnchantmentRequirement r : ench) {
                            if (r != null) safe.add(r);
                        }

                        ByteBufCodecs.collection(ArrayList::new, EnchantmentRequirement.STREAM_CODEC)
                                .encode(buf, safe);

                        Optional<ComponentRequirement> comp =
                                v.componentRequirement == null ? Optional.empty() : v.componentRequirement;

                        ByteBufCodecs.optional(ComponentRequirement.STREAM_CODEC)
                                .encode(buf, comp);
                    },
                    buf -> {
                        List<EnchantmentRequirement> ench =
                                ByteBufCodecs.collection(ArrayList::new, EnchantmentRequirement.STREAM_CODEC).decode(buf);

                        Optional<ComponentRequirement> comp =
                                ByteBufCodecs.optional(ComponentRequirement.STREAM_CODEC).decode(buf);

                        return new ItemRequirements(sanitizeList(ench), comp);
                    }
            );

    /**
     * No throws. Nulls are normalized.
     */
    public ItemRequirements(
            List<EnchantmentRequirement> enchantments,
            Optional<ComponentRequirement> componentRequirement
    ) {
        this.enchantments = enchantments == null ? List.of() : sanitizeList(enchantments);
        this.componentRequirement = componentRequirement == null ? Optional.empty() : componentRequirement;
    }

    @Override
    public @NotNull DataResult<ItemRequirements> validate() {
        for (int i = 0; i < enchantments.size(); i++) {
            EnchantmentRequirement req = enchantments.get(i);
            if (req == null) {
                return SelfValidating.invalid("enchantments[" + i + "] is null");
            }

            DataResult<EnchantmentRequirement> res = req.validate();
            if (res.error().isPresent()) {
                String msg = res.error().map(DataResult.Error::message).orElse("");
                return SelfValidating.invalid("enchantments[" + i + "] invalid: " + msg);
            }
        }

        Optional<ComponentRequirement> comp = componentRequirement == null ? Optional.empty() : componentRequirement;
        if (comp.isPresent()) {
            DataResult<ComponentRequirement> res = comp.get().validate();
            if (res.error().isPresent()) {
                String msg = res.error().map(DataResult.Error::message).orElse("");
                return SelfValidating.invalid(JolCraftParameterIds.COMPONENTS + " invalid: " + msg);
            }
        }

        return SelfValidating.ok(this);
    }

    // ---------------------------------------------------------------------
    // INTROSPECTION
    // ---------------------------------------------------------------------

    @Override
    public @NotNull List<RegistryIntrospection> introspections() {
        ArrayList<RegistryIntrospection> all = new ArrayList<>(4);

        for (EnchantmentRequirement r : enchantments) {
            if (r == null) continue;
            all.addAll(r.asList());
        }

        Optional<ComponentRequirement> comp = componentRequirement == null ? Optional.empty() : componentRequirement;
        comp.ifPresent(r -> all.addAll(r.asList()));

        if (all.isEmpty()) return List.of();

        List<RegistryIntrospection> merged = mergeByRegistry(all);
        merged.sort(Comparator.comparing(a -> a.registryKey().location()));
        return List.copyOf(merged);
    }

    private static @NotNull List<RegistryIntrospection> mergeByRegistry(@NotNull List<RegistryIntrospection> in) {
        if (in.isEmpty()) return List.of();

        ArrayList<RegistryIntrospection> out = new ArrayList<>(Math.min(in.size(), 8));

        for (RegistryIntrospection info : in) {
            if (info == null) continue;

            int idx = indexOf(out, info.registryKey());
            if (idx < 0) {
                out.add(info);
                continue;
            }

            RegistryIntrospection prev = out.get(idx);
            out.set(idx, merge(prev, info));
        }

        return out;
    }

    private static int indexOf(
            @NotNull List<RegistryIntrospection> list,
            @NotNull ResourceKey<? extends Registry<?>> key
    ) {
        for (int i = 0; i < list.size(); i++) {
            RegistryIntrospection r = list.get(i);
            if (r != null && r.registryKey().equals(key)) return i;
        }
        return -1;
    }

    private static @NotNull RegistryIntrospection merge(
            @NotNull RegistryIntrospection a,
            @NotNull RegistryIntrospection b
    ) {
        ResourceKey<? extends Registry<?>> key = a.registryKey();

        int holders = Math.max(0, a.holderCount()) + Math.max(0, b.holderCount());
        boolean anyTag = a.hasAnyTag() || b.hasAnyTag();

        Holder<?> singleConcrete = null;
        TagKey<?> singleTag = null;
        ResourceKey<?> singleKey = null;

        if (!anyTag && holders == 1) {
            singleConcrete = (a.singleConcrete() != null) ? a.singleConcrete() : b.singleConcrete();
        }

        if (holders == 0 && anyTag) {
            if (a.exactlyOneTag() && b.exactlyOneTag() && a.singleTag() != null && a.singleTag().equals(b.singleTag())) {
                singleTag = a.singleTag();
            }
        }

        if (!anyTag && holders == 0) {
            if (a.exactlyOneKey() && b.exactlyOneKey() && a.singleKey() != null && a.singleKey().equals(b.singleKey())) {
                singleKey = a.singleKey();
            }
        }

        if (singleConcrete != null) return RegistryIntrospection.single(key, singleConcrete);
        if (singleTag != null) return RegistryIntrospection.singleTag(key, singleTag);
        if (singleKey != null) return RegistryIntrospection.singleKey(key, singleKey);

        if (holders == 0 && anyTag) return RegistryIntrospection.anyTag(key);
        return RegistryIntrospection.mixed(key, holders, anyTag);
    }

    // ---------------------------------------------------------------------
    // MATCHING
    // ---------------------------------------------------------------------

    public boolean matches(ItemStack stack) {
        List<EnchantmentRequirement> ench = this.enchantments;
        if (ench == null) ench = List.of();

        Optional<ComponentRequirement> comp = this.componentRequirement;
        if (comp == null) comp = Optional.empty();

        if (ench.isEmpty() && comp.isEmpty()) return true;

        if (stack.isEmpty()) return false;

        for (EnchantmentRequirement requirement : ench) {
            if (requirement == null || !requirement.matches(stack)) return false;
        }

        return comp.map(r -> r.matches(stack)).orElse(true);
    }

    public boolean isEmpty() {
        return enchantments.isEmpty() && componentRequirement.isEmpty();
    }

    private static <T> List<T> sanitizeList(List<T> in) {
        if (in == null || in.isEmpty()) return List.of();
        ArrayList<T> safe = new ArrayList<>(in.size());
        for (T t : in) if (t != null) safe.add(t);
        return safe.isEmpty() ? List.of() : List.copyOf(safe);
    }
}