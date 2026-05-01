package net.sievert.jolcraft.datagen.recipe.builder.param.condition.custom;

import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.sievert.jolcraft.world.recipe.param.condition.Condition;
import net.sievert.jolcraft.world.recipe.param.condition.custom.BiomeCondition;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.builder.JolCraftValidatedBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Datagen-only builder for {@link BiomeCondition}.
 *
 * Policy:
 * - No throwing, no logging.
 * - Single-assignment selection: exactly one of biome(...) or tag(...) may be chosen (first wins).
 * - Inconsistent calls are ignored (fail-closed).
 * - invert(boolean) is always allowed (inherited).
 *
 * Validation:
 * - Delegates to {@link BiomeCondition#validate()} to enforce the param invariants (exactly one of biome/tag).
 */
public final class BiomeConditionBuilder extends AbstractConditionBuilder<BiomeConditionBuilder>
        implements JolCraftValidatedBuilder<Condition> {

    private enum Kind { BIOME, TAG }

    private @Nullable Kind kind;

    private @Nullable Holder<Biome> biome;
    private @Nullable ResourceKey<Biome> biomeKey;
    private @Nullable JolCraftDataLookups lookups;
    private @Nullable TagKey<Biome> tag;

    private BiomeConditionBuilder() {}

    public static @NotNull BiomeConditionBuilder create() {
        return new BiomeConditionBuilder();
    }

    public @NotNull BiomeConditionBuilder lookups(@Nullable JolCraftDataLookups lookups) {
        this.lookups = lookups;
        return this;
    }

    public @NotNull BiomeConditionBuilder biome(@Nullable Holder<Biome> biome) {
        if (this.kind != null) return this;
        this.kind = Kind.BIOME;
        this.biome = biome;
        return this;
    }

    public @NotNull BiomeConditionBuilder biome(@Nullable ResourceKey<Biome> biomeKey) {
        if (this.kind != null) return this;
        this.kind = Kind.BIOME;
        this.biomeKey = biomeKey;
        return this;
    }

    public @NotNull BiomeConditionBuilder tag(@Nullable TagKey<Biome> tag) {
        if (this.kind != null) return this;
        this.kind = Kind.TAG;
        this.tag = tag;
        return this;
    }

    @Override
    public @NotNull DataResult<Condition> buildValidated() {
        Optional<Holder<Biome>> b = Optional.empty();

        if (kind == Kind.BIOME) {
            if (biome != null) {
                b = Optional.of(biome);
            } else if (biomeKey != null) {
                if (lookups == null) {
                    return DataResult.error(() ->
                            "BiomeConditionBuilder requires recipe lookups to resolve biome key '" +
                                    biomeKey.location() + "'"
                    );
                }

                Optional<Holder.Reference<Biome>> resolved = lookups.biomes().get(biomeKey);
                if (resolved.isEmpty()) {
                    return DataResult.error(() ->
                            "Unknown biome '" + biomeKey.location() + "'"
                    );
                }

                b = Optional.of(resolved.get());
            }
        }

        Optional<TagKey<Biome>> t =
                (kind == Kind.TAG && tag != null) ? Optional.of(tag) : Optional.empty();

        BiomeCondition built = new BiomeCondition(b, t, invert());
        return built.validate();
    }
}