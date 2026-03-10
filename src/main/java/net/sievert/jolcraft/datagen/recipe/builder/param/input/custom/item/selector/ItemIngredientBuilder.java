package net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item.selector;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.sievert.jolcraft.data.recipe.param.input.custom.item.selector.ItemIngredient;
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
 * - Never throws during mutation
 * - Ignores nulls
 * - Deterministic build
 *
 * Note:
 * - Since runtime no longer uses EMPTY sentinels, build() now fails fast
 *   if no valid targets were provided.
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
        return item(holder.value());
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
            throw new IllegalStateException("ItemIngredient requires at least one target");
        }

        ArrayList<ItemIngredient.Target> safe = new ArrayList<>(list.size());
        for (ItemIngredient.Target t : list) {
            if (t != null) safe.add(t);
        }

        if (safe.isEmpty()) {
            throw new IllegalStateException("ItemIngredient requires at least one target");
        }

        return ItemIngredient.ofTargets(safe);
    }
}