package net.sievert.jolcraft.world.recipe.param.introspection;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@SuppressWarnings({"OptionalUsedAsFieldOrParameterType", "unchecked", "BooleanMethodIsAlwaysInverted"})
public interface RegistryIntrospectionSource {

    @NotNull List<RegistryIntrospection> introspections();

    // ---------------------------------------------------------------------
    // Small builders
    // ---------------------------------------------------------------------

    /**
     * Builds a single-registry report from either:
     * - exactly one concrete holder, or
     * - exactly one tag.
     */
    default @NotNull List<RegistryIntrospection> fromConcreteOrTag(
            @NotNull ResourceKey<? extends Registry<?>> registryKey,
            @NotNull Optional<? extends Holder<?>> concrete,
            @NotNull Optional<? extends TagKey<?>> tag
    ) {
        boolean hasConcrete = concrete.isPresent();
        boolean hasTag = tag.isPresent();

        if (hasConcrete == hasTag) {
            return List.of(RegistryIntrospection.mixed(registryKey, 0, hasTag));
        }
        if (hasTag) {
            return List.of(RegistryIntrospection.singleTag(registryKey, tag.get()));
        }
        return List.of(RegistryIntrospection.single(registryKey, concrete.get()));
    }

    /**
     * Builds a single-registry report from either:
     * - exactly one resource key, or
     * - exactly one tag.
     */
    default @NotNull List<RegistryIntrospection> fromKeyOrTag(
            @NotNull ResourceKey<? extends Registry<?>> registryKey,
            @NotNull Optional<? extends ResourceKey<?>> key,
            @NotNull Optional<? extends TagKey<?>> tag
    ) {
        boolean hasKey = key.isPresent();
        boolean hasTag = tag.isPresent();

        if (hasKey == hasTag) {
            return List.of(RegistryIntrospection.mixed(registryKey, 0, hasTag));
        }
        if (hasTag) {
            return List.of(RegistryIntrospection.singleTag(registryKey, tag.get()));
        }
        return List.of(RegistryIntrospection.singleKey(registryKey, key.get()));
    }

    // ---------------------------------------------------------------------
    // Merge
    // ---------------------------------------------------------------------

    /**
     * Merges child introspections by registry key.
     * A single report is preserved; multiple reports collapse to mixed(...).
     */
    static @NotNull List<RegistryIntrospection> mergeByRegistry(
            @NotNull Iterable<? extends RegistryIntrospectionSource> sources
    ) {
        HashMap<ResourceKey<? extends Registry<?>>, RegistryIntrospection> merged = new HashMap<>(8);
        HashMap<ResourceKey<? extends Registry<?>>, Integer> counts = new HashMap<>(8);

        for (RegistryIntrospectionSource src : sources) {
            if (src == null) continue;

            for (RegistryIntrospection ri : src.introspections()) {
                if (ri == null) continue;

                ResourceKey<? extends Registry<?>> key = ri.registryKey();

                int n = counts.merge(key, 1, Integer::sum);
                if (n == 1) {
                    merged.put(key, ri);
                    continue;
                }

                RegistryIntrospection prev = merged.get(key);
                int holders = (prev != null ? prev.holderCount() : 0) + ri.holderCount();
                boolean tag = (prev != null && prev.hasAnyTag()) || ri.hasAnyTag();

                merged.put(key, RegistryIntrospection.mixed(key, holders, tag));
            }
        }

        return merged.isEmpty() ? List.of() : List.copyOf(merged.values());
    }

    // ---------------------------------------------------------------------
    // Queries
    // ---------------------------------------------------------------------

    default <T> @NotNull Optional<Holder<T>> singleConcrete(
            @NotNull ResourceKey<? extends Registry<T>> registryKey
    ) {
        for (RegistryIntrospection ri : introspections()) {
            if (!ri.registryKey().equals(registryKey)) continue;
            if (!ri.exactlyOneConcrete()) continue;
            return ri.singleConcreteOpt().map(h -> (Holder<T>) h);
        }
        return Optional.empty();
    }

    default <T> boolean exactlyOneConcrete(@NotNull ResourceKey<? extends Registry<T>> registryKey) {
        return singleConcrete(registryKey).isPresent();
    }

    default <T> @NotNull Optional<TagKey<T>> singleTag(
            @NotNull ResourceKey<? extends Registry<T>> registryKey
    ) {
        for (RegistryIntrospection ri : introspections()) {
            if (!ri.registryKey().equals(registryKey)) continue;
            if (!ri.exactlyOneTag()) continue;
            return ri.singleTagOpt().map(t -> (TagKey<T>) t);
        }
        return Optional.empty();
    }

    default <T> boolean exactlyOneTag(@NotNull ResourceKey<? extends Registry<T>> registryKey) {
        return singleTag(registryKey).isPresent();
    }

    default <T> @NotNull Optional<ResourceKey<T>> singleKey(
            @NotNull ResourceKey<? extends Registry<T>> registryKey
    ) {
        for (RegistryIntrospection ri : introspections()) {
            if (!ri.registryKey().equals(registryKey)) continue;
            if (!ri.exactlyOneKey()) continue;
            return ri.singleKeyOpt().map(k -> (ResourceKey<T>) k);
        }
        return Optional.empty();
    }

    default <T> boolean exactlyOneKey(@NotNull ResourceKey<? extends Registry<T>> registryKey) {
        return singleKey(registryKey).isPresent();
    }

    default <T> boolean anyIntrospection(
            @NotNull ResourceKey<? extends Registry<T>> registryKey,
            @NotNull java.util.function.Predicate<Holder<T>> test
    ) {
        for (RegistryIntrospection ri : introspections()) {
            if (!ri.registryKey().equals(registryKey)) continue;

            if (ri.exactlyOneConcrete()) {
                Optional<Holder<?>> h = ri.singleConcreteOpt();
                if (h.isPresent()) {
                    @SuppressWarnings("unchecked")
                    Holder<T> ht = (Holder<T>) h.get();
                    if (test.test(ht)) return true;
                }
            }
        }
        return false;
    }
}