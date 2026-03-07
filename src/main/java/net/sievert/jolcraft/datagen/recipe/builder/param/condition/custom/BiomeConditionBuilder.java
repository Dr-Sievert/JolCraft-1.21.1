package net.sievert.jolcraft.datagen.recipe.builder.param.condition.custom;

import com.mojang.serialization.DataResult;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.BiomeCondition;
import net.sievert.jolcraft.datagen.recipe.builder.base.ValidatedBuilder;

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
public final class BiomeConditionBuilder extends AbstractConditionBuilder<BiomeConditionBuilder> implements ValidatedBuilder<Condition> {

    private enum Kind { BIOME, TAG }

    private Kind kind;

    private Holder<Biome> biome;
    private TagKey<Biome> tag;

    private BiomeConditionBuilder() {}

    public static BiomeConditionBuilder create() {
        return new BiomeConditionBuilder();
    }

    // ---------------------------------------------------------------------
    // MODE SELECTION
    // ---------------------------------------------------------------------

    public BiomeConditionBuilder biome(Holder<Biome> biome) {
        if (this.kind != null) return this;
        this.kind = Kind.BIOME;
        this.biome = biome;
        return this;
    }

    public BiomeConditionBuilder tag(TagKey<Biome> tag) {
        if (this.kind != null) return this;
        this.kind = Kind.TAG;
        this.tag = tag;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public DataResult<Condition> buildValidated() {
        Optional<Holder<Biome>> b =
                (kind == Kind.BIOME && biome != null) ? Optional.of(biome) : Optional.empty();

        Optional<TagKey<Biome>> t =
                (kind == Kind.TAG && tag != null) ? Optional.of(tag) : Optional.empty();

        BiomeCondition built = new BiomeCondition(b, t, invert());

        return built.validate();
    }
}