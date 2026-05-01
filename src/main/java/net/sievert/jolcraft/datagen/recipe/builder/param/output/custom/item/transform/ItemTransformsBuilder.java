package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.transform;

import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.ComponentTransform;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.EnchantmentTransform;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.ItemTransforms;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Datagen-only builder for {@link ItemTransforms}.
 *
 * Policy:
 * - No throwing, no logging.
 * - Simple list-assembler for enchantments and component transforms.
 * - Null entries are ignored fail-closed.
 * - Obvious no-op sentinels are ignored to keep output canonical:
 *   - {@link EnchantmentTransform.Invalid#INSTANCE} is not added.
 *   - {@link ComponentTransform.Config#EMPTY} is not added.
 * - Order is preserved (runtime semantics depend on order).
 *
 * Semantics:
 * - Enchantment transforms mutate the output stack using world/runtime context.
 * - Component transforms mutate the output stack using:
 *   - fixed patch/set operations, and/or
 *   - input-to-output component copy rules.
 */
public final class ItemTransformsBuilder implements ParamBuilder<ItemTransforms> {

    private final ArrayList<EnchantmentTransform> enchantments = new ArrayList<>();
    private final ArrayList<ComponentTransform> components = new ArrayList<>();

    private ItemTransformsBuilder() {}

    public static ItemTransformsBuilder create() {
        return new ItemTransformsBuilder();
    }

    public ItemTransformsBuilder enchantment(EnchantmentTransform transform) {
        if (transform == null) return this;
        if (transform == EnchantmentTransform.Invalid.INSTANCE) return this;
        enchantments.add(transform);
        return this;
    }

    public ItemTransformsBuilder enchantment(EnchantmentTransformBuilder builder) {
        if (builder == null) return this;
        return enchantment(builder.build());
    }

    public ItemTransformsBuilder component(ComponentTransform transform) {
        if (transform == null) return this;
        if (transform == ComponentTransform.Config.EMPTY) return this;
        components.add(transform);
        return this;
    }

    public ItemTransformsBuilder component(ComponentTransformBuilder builder) {
        if (builder == null) return this;
        return component(builder.build());
    }

    @Override
    public ItemTransforms build() {
        if (enchantments.isEmpty() && components.isEmpty()) {
            return ItemTransforms.EMPTY;
        }

        return new ItemTransforms(
                List.copyOf(enchantments),
                List.copyOf(components)
        );
    }
}