package net.sievert.jolcraft.datagen.client.language;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.common.extensions.ILevelExtension;
import net.sievert.jolcraft.data.id.param.JolCraftParameterIds;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.JolCraftSubDataProvider;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public interface LanguageSubProvider extends JolCraftSubDataProvider<Map<String, String>> {

    void addTranslations(@NotNull Map<String, String> translations);

    @Override
    default void run(
            @NotNull Map<String, String> target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        addTranslations(target);
    }

    default @NotNull JolCraftLanguageProvider languageProvider() {
        for (JolCraftDataProvider<?> current : chain()) {
            if (current instanceof JolCraftLanguageProvider provider) {
                return provider;
            }
        }

        throw new IllegalStateException("No JolCraftLanguageProvider found in provider chain: " + name());
    }

    default void put(@NotNull Map<String, String> target, @NotNull String key, @NotNull String value) {
        Objects.requireNonNull(target, JolCraftParameterIds.TARGET);
        Objects.requireNonNull(key, JolCraftParameterIds.KEY);
        Objects.requireNonNull(value, JolCraftParameterIds.VALUE);

        if (target.put(key, value) != null) {
            throw new IllegalStateException("Duplicate translation key " + key);
        }
    }

    default boolean hasKey(@NotNull Map<String, String> target, @NotNull String key) {
        return target.containsKey(key);
    }

    default void putBlock(@NotNull Map<String, String> target, @NotNull Supplier<? extends Block> key, @NotNull String name) {
        put(target, key.get().getDescriptionId(), name);
    }

    default void put(@NotNull Map<String, String> target, @NotNull Block key, @NotNull String name) {
        put(target, key.getDescriptionId(), name);
    }

    default void putItem(@NotNull Map<String, String> target, @NotNull Supplier<? extends Item> key, @NotNull String name) {
        put(target, key.get().getDescriptionId(), name);
    }

    default void put(@NotNull Map<String, String> target, @NotNull Item key, @NotNull String name) {
        put(target, key.getDescriptionId(), name);
    }

    default void putItemStack(@NotNull Map<String, String> target, @NotNull Supplier<ItemStack> key, @NotNull String name) {
        put(target, key.get().getDescriptionId(), name);
    }

    default void put(@NotNull Map<String, String> target, @NotNull ItemStack key, @NotNull String name) {
        put(target, key.getDescriptionId(), name);
    }

    default void putEffect(@NotNull Map<String, String> target, @NotNull Supplier<? extends MobEffect> key, @NotNull String name) {
        put(target, key.get().getDescriptionId(), name);
    }

    default void put(@NotNull Map<String, String> target, @NotNull MobEffect key, @NotNull String name) {
        put(target, key.getDescriptionId(), name);
    }

    default void putEntityType(@NotNull Map<String, String> target, @NotNull Supplier<? extends EntityType<?>> key, @NotNull String name) {
        put(target, key.get().getDescriptionId(), name);
    }

    default void put(@NotNull Map<String, String> target, @NotNull EntityType<?> key, @NotNull String name) {
        put(target, key.getDescriptionId(), name);
    }

    default void putTag(@NotNull Map<String, String> target, @NotNull Supplier<? extends TagKey<?>> key, @NotNull String name) {
        put(target, Tags.getTagTranslationKey(key.get()), name);
    }

    default void put(@NotNull Map<String, String> target, @NotNull TagKey<?> tagKey, @NotNull String name) {
        put(target, Tags.getTagTranslationKey(tagKey), name);
    }

    default void putDimension(@NotNull Map<String, String> target, @NotNull ResourceKey<Level> dimension, @NotNull String value) {
        put(target, dimension.location().toLanguageKey(ILevelExtension.TRANSLATION_PREFIX), value);
    }

    default void putManual(@NotNull Map<String, String> target, @NotNull Object thing, @NotNull String value) {
        put(target, JolCraftStrings.resolveLangKey(thing), value);
    }

    default void putSame(@NotNull Map<String, String> target, @NotNull String value, @NotNull Object... things) {
        for (Object thing : things) {
            put(target, JolCraftStrings.resolveLangKey(thing), value);
        }
    }

    default void putManualFlipped(@NotNull Map<String, String> target, @NotNull Object thing) {
        String key = JolCraftStrings.resolveLangKey(thing);
        String path = key.substring(key.lastIndexOf('.') + 1);
        put(target, key, JolCraftStrings.flipAndTitle(path));
    }

    default void putManualFlippedAll(@NotNull Map<String, String> target, @NotNull Object... things) {
        for (Object thing : things) {
            putManualFlipped(target, thing);
        }
    }
}