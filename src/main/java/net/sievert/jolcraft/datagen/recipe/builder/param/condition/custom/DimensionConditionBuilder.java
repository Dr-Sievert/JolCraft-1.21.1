package net.sievert.jolcraft.datagen.recipe.builder.param.condition.custom;

import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.Level;
import net.sievert.jolcraft.data.recipe.param.condition.Condition;
import net.sievert.jolcraft.data.recipe.param.condition.custom.DimensionCondition;
import net.sievert.jolcraft.datagen.recipe.builder.base.ValidatedBuilder;

import java.util.Optional;

/**
 * Datagen-only builder for {@link DimensionCondition}.
 *
 * Policy:
 * - No throwing, no logging.
 * - Single-assignment selection: exactly one of dimension(...) or tag(...) may be chosen (first wins).
 * - Inconsistent calls are ignored (fail-closed).
 * - invert(boolean) is always allowed (inherited).
 *
 * Validation:
 * - Delegates to {@link DimensionCondition#validate()} (enforces exactly one of id/tag).
 */
public final class DimensionConditionBuilder extends AbstractConditionBuilder<DimensionConditionBuilder> implements ValidatedBuilder<Condition> {

    private enum Kind { ID, TAG }

    private Kind kind;

    private ResourceKey<Level> dimension;
    private TagKey<Level> tag;

    private DimensionConditionBuilder() {}

    public static DimensionConditionBuilder create() {
        return new DimensionConditionBuilder();
    }

    // ---------------------------------------------------------------------
    // MODE SELECTION
    // ---------------------------------------------------------------------

    public DimensionConditionBuilder dimension(ResourceKey<Level> dimension) {
        if (this.kind != null) return this;
        this.kind = Kind.ID;
        this.dimension = dimension;
        return this;
    }

    public DimensionConditionBuilder tag(TagKey<Level> tag) {
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
        Optional<ResourceKey<Level>> d =
                (kind == Kind.ID && dimension != null) ? Optional.of(dimension) : Optional.empty();

        Optional<TagKey<Level>> t =
                (kind == Kind.TAG && tag != null) ? Optional.of(tag) : Optional.empty();

        DimensionCondition built = new DimensionCondition(d, t, invert());
        return built.validate().map(v -> v);
    }
}