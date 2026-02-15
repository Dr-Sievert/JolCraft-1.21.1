package net.sievert.jolcraft.data.recipe.util;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class RecipeStackTransformations {

    private RecipeStackTransformations() {}

    /**
     * Canonical transform order:
     * 1) enchantmentProvider (server-authoritative; needs Level + DifficultyInstance)
     * 2) stackModifier (id -> Consumer)
     * 3) resultPatch
     * JEI-safe: level/contextEntity may be null (e.g. multiplayer client).
     * Returns the same stack for chaining.
     */
    public static ItemStack apply(
            ItemStack base,
            @Nullable Level level,
            @Nullable Entity contextEntity,
            RandomSource random,
            Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider,
            Optional<String> stackModifierId,
            Optional<DataComponentPatch> resultPatch,
            Consumer<String> stackModifierIdApplier
    ) {
        if (base.isEmpty()) return base;

        // 1) enchantmentProvider
        if (level != null && contextEntity != null && enchantmentProvider.isPresent()) {
            EnchantmentHelper.enchantItemFromProvider(
                    base,
                    level.registryAccess(),
                    enchantmentProvider.get(),
                    level.getCurrentDifficultyAt(contextEntity.blockPosition()),
                    random
            );
        }

        // 2) stackModifierId
        if (stackModifierId.isPresent()) {
            String id = stackModifierId.get();
            if (!id.isBlank()) {
                stackModifierIdApplier.accept(id);
            }
        }

        // 3) resultPatch
        resultPatch.ifPresent(base::applyComponents);

        return base;
    }

    /**
     * Convenience for the common "id -> Consumer<ItemStack>" registry pattern.
     */
    public static void applyWithResolver(
            ItemStack base,
            @Nullable Level level,
            @Nullable Entity contextEntity,
            RandomSource random,
            Optional<ResourceKey<EnchantmentProvider>> enchantmentProvider,
            Optional<String> stackModifierId,
            Optional<DataComponentPatch> resultPatch,
            Function<String, Consumer<ItemStack>> resolver
    ) {
        apply(
                base,
                level,
                contextEntity,
                random,
                enchantmentProvider,
                stackModifierId,
                resultPatch,
                id -> resolver.apply(id).accept(base)
        );
    }
}