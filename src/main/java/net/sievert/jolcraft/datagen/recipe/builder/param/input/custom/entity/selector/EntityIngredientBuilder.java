package net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.entity.selector;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.sievert.jolcraft.world.recipe.param.input.custom.entity.selector.EntityIngredient;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Datagen builder for {@link EntityIngredient}.
 *
 * Mirrors the param contract:
 * - An ingredient is an OR-list of {@link EntityIngredient.Target}.
 * - No gating/conditions here (that belongs to higher-level selectors/inputs).
 *
 * Policy:
 * - Never throws
 * - Ignores nulls
 * - Deterministic build
 * - Leaves strict validation to {@link EntityIngredient#validate()}
 */
public final class EntityIngredientBuilder implements ParamBuilder<EntityIngredient> {

    private List<EntityIngredient.Target> targets;

    private EntityIngredientBuilder() {}

    public static EntityIngredientBuilder create() {
        return new EntityIngredientBuilder();
    }

    // ---------------------------------------------------------------------
    // BULK
    // ---------------------------------------------------------------------

    public EntityIngredientBuilder targets(List<EntityIngredient.Target> targets) {
        this.targets = targets;
        return this;
    }

    public EntityIngredientBuilder target(EntityIngredient.Target target) {
        if (target == null) return this;

        List<EntityIngredient.Target> list = this.targets;
        if (list == null || list.isEmpty()) {
            this.targets = new ArrayList<>(List.of(target));
            return this;
        }

        ArrayList<EntityIngredient.Target> next = new ArrayList<>(list.size() + 1);
        for (EntityIngredient.Target t : list) {
            if (t != null) next.add(t);
        }
        next.add(target);

        this.targets = next;
        return this;
    }

    // ---------------------------------------------------------------------
    // CONVENIENCE
    // ---------------------------------------------------------------------

    public EntityIngredientBuilder entity(EntityType<?> type) {
        if (type == null) return this;
        return target(EntityIngredient.Target.of(type));
    }

    public EntityIngredientBuilder entity(Holder<EntityType<?>> holder) {
        if (holder == null) return this;
        return target(new EntityIngredient.Target(Either.left(holder)));
    }

    public EntityIngredientBuilder tag(TagKey<EntityType<?>> tag) {
        if (tag == null) return this;
        return target(EntityIngredient.Target.of(tag));
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public EntityIngredient build() {
        List<EntityIngredient.Target> list = this.targets;
        if (list == null || list.isEmpty()) {
            return EntityIngredient.ofTargets(List.of());
        }

        ArrayList<EntityIngredient.Target> safe = new ArrayList<>(list.size());
        for (EntityIngredient.Target t : list) {
            if (t != null) safe.add(t);
        }

        return EntityIngredient.ofTargets(safe);
    }
}