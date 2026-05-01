package net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.transform;

import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.sievert.jolcraft.world.recipe.param.output.custom.item.transform.EnchantmentTransform;
import net.sievert.jolcraft.world.recipe.param.quantity.IntRange;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

/**
 * Datagen-only builder for {@link EnchantmentTransform}.
 *
 * Policy:
 * - Single-assignment mode selection: first mode call wins, subsequent mode calls are ignored.
 * - No throwing, no logging.
 * - level(...) applies to DIRECT/TAGGED only.
 *   - If PROVIDER mode is selected, level(...) is ignored (fail-closed).
 *   - Selecting PROVIDER clears any previously set level (fail-closed).
 */
public final class EnchantmentTransformBuilder implements ParamBuilder<EnchantmentTransform> {

    private enum Kind { DIRECT, TAGGED, PROVIDER }

    private Kind kind;

    private Holder<Enchantment> enchantment;
    private TagKey<Enchantment> tag;
    private Holder<EnchantmentProvider> provider;

    private IntRange level;

    private EnchantmentTransformBuilder() {}

    public static EnchantmentTransformBuilder create() {
        return new EnchantmentTransformBuilder();
    }

    // ---------------------------------------------------------------------
    // MODE SELECTION
    // ---------------------------------------------------------------------

    public EnchantmentTransformBuilder enchantment(Holder<Enchantment> enchantment) {
        if (this.kind != null) return this;
        this.kind = Kind.DIRECT;
        this.enchantment = enchantment;
        return this;
    }

    public EnchantmentTransformBuilder tag(TagKey<Enchantment> tag) {
        if (this.kind != null) return this;
        this.kind = Kind.TAGGED;
        this.tag = tag;
        return this;
    }

    public EnchantmentTransformBuilder provider(Holder<EnchantmentProvider> provider) {
        if (this.kind != null) return this;
        this.kind = Kind.PROVIDER;
        this.provider = provider;

        this.level = null;

        return this;
    }

    // ---------------------------------------------------------------------
    // OPTIONALS
    // ---------------------------------------------------------------------

    public EnchantmentTransformBuilder level(IntRange level) {
        if (this.kind == Kind.PROVIDER) return this;

        this.level = level;
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    @Override
    public EnchantmentTransform build() {
        if (kind == null) return EnchantmentTransform.Invalid.INSTANCE;

        IntRange lvl = level == null ? IntRange.ONE : level;

        return switch (kind) {
            case DIRECT -> new EnchantmentTransform.Direct(enchantment, lvl);
            case TAGGED -> new EnchantmentTransform.Tagged(tag, lvl);
            case PROVIDER -> new EnchantmentTransform.Provider(provider);
        };
    }
}