package net.sievert.jolcraft.data.recipe.param.introspection;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Structural registry report.
 */
public record RegistryIntrospection(
        @NotNull ResourceKey<? extends Registry<?>> registryKey,
        int holderCount,
        boolean hasAnyTag,
        @Nullable Holder<?> singleConcrete,
        @Nullable TagKey<?> singleTag,
        @Nullable ResourceKey<?> singleKey
) {

    // ---------------------------------------------------------------------
    // Queries
    // ---------------------------------------------------------------------

    public boolean exactlyOneConcrete() {
        return singleConcrete != null
                && holderCount == 1
                && !hasAnyTag
                && singleTag == null
                && singleKey == null;
    }

    public @NotNull Optional<Holder<?>> singleConcreteOpt() {
        return Optional.ofNullable(singleConcrete);
    }

    public boolean exactlyOneTag() {
        return singleTag != null
                && hasAnyTag
                && holderCount == 0
                && singleConcrete == null
                && singleKey == null;
    }

    public @NotNull Optional<TagKey<?>> singleTagOpt() {
        return Optional.ofNullable(singleTag);
    }

    public boolean exactlyOneKey() {
        return singleKey != null
                && !hasAnyTag
                && holderCount == 0
                && singleConcrete == null
                && singleTag == null;
    }

    public @NotNull Optional<ResourceKey<?>> singleKeyOpt() {
        return Optional.ofNullable(singleKey);
    }

    // ---------------------------------------------------------------------
    // Factories
    // ---------------------------------------------------------------------

    public static @NotNull RegistryIntrospection single(
            @NotNull ResourceKey<? extends Registry<?>> key,
            @NotNull Holder<?> holder
    ) {
        return new RegistryIntrospection(key, 1, false, holder, null, null);
    }

    public static @NotNull RegistryIntrospection anyTag(
            @NotNull ResourceKey<? extends Registry<?>> key
    ) {
        return new RegistryIntrospection(key, 0, true, null, null, null);
    }

    public static @NotNull RegistryIntrospection singleTag(
            @NotNull ResourceKey<? extends Registry<?>> key,
            @NotNull TagKey<?> tagKey
    ) {
        return new RegistryIntrospection(key, 0, true, null, tagKey, null);
    }

    public static @NotNull RegistryIntrospection singleKey(
            @NotNull ResourceKey<? extends Registry<?>> key,
            @NotNull ResourceKey<?> resourceKey
    ) {
        return new RegistryIntrospection(key, 0, false, null, null, resourceKey);
    }

    public static @NotNull RegistryIntrospection mixed(
            @NotNull ResourceKey<? extends Registry<?>> key,
            int holderCount,
            boolean hasAnyTag
    ) {
        return new RegistryIntrospection(key, Math.max(0, holderCount), hasAnyTag, null, null, null);
    }
}