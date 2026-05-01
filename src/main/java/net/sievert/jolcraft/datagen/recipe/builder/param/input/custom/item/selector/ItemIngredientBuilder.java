package net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item.selector;

import com.mojang.datafixers.util.Either;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.world.recipe.param.input.custom.item.selector.ItemIngredient;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Datagen builder for {@link ItemIngredient}.
 *
 * Semantics:
 * - Ingredient is an OR-list of {@link ItemIngredient.Target}.
 * - Targets are either concrete item holder or item tag.
 *
 * Policy:
 * - Never throws
 * - Ignores nulls
 * - Deterministic build
 * - Leaves strict validation to {@link ItemIngredient#validate()}
 */
public final class ItemIngredientBuilder implements ParamBuilder<ItemIngredient> {

    private List<ItemIngredient.Target> targets;

    private ItemIngredientBuilder() {}

    public static ItemIngredientBuilder create() {
        return new ItemIngredientBuilder();
    }

    // ---------------------------------------------------------------------
    // BULK
    // ---------------------------------------------------------------------

    public ItemIngredientBuilder targets(List<ItemIngredient.Target> targets) {
        this.targets = targets;
        return this;
    }

    public ItemIngredientBuilder target(ItemIngredient.Target target) {
        if (target == null) return this;

        List<ItemIngredient.Target> list = this.targets;
        if (list == null || list.isEmpty()) {
            this.targets = new ArrayList<>(List.of(target));
            return this;
        }

        ArrayList<ItemIngredient.Target> next = new ArrayList<>(list.size() + 1);
        for (ItemIngredient.Target t : list) {
            if (t != null) next.add(t);
        }
        next.add(target);

        this.targets = next;
        return this;
    }

    // ---------------------------------------------------------------------
    // CONVENIENCE
    // ---------------------------------------------------------------------

    public ItemIngredientBuilder item(ItemLike item) {
        if (item == null) return this;
        return target(ItemIngredient.Target.of(item));
    }

    public ItemIngredientBuilder item(Item item) {
        if (item == null) return this;
        return target(ItemIngredient.Target.of(item));
    }

    public ItemIngredientBuilder item(Holder<Item> holder) {
        if (holder == null) return this;
        return target(new ItemIngredient.Target(Either.left(holder)));
    }

    public ItemIngredientBuilder tag(TagKey<Item> tag) {
        if (tag == null) return this;
        return target(ItemIngredient.Target.of(tag));
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public ItemIngredient build() {
        List<ItemIngredient.Target> list = this.targets;
        if (list == null || list.isEmpty()) {
            return ItemIngredient.ofTargets(List.of());
        }

        ArrayList<ItemIngredient.Target> safe = new ArrayList<>(list.size());
        for (ItemIngredient.Target t : list) {
            if (t != null) safe.add(t);
        }

        return ItemIngredient.ofTargets(safe);
    }
}