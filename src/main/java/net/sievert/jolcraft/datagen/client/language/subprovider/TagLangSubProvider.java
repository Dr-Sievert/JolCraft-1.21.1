package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.tags.TagKey;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class TagLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftStrings.plural(
                JolCraftDictionary.TAG
        );
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }

    @Override
    public void addTranslations(
            @NotNull Map<String, String> translations
    ) {
        List<TagKey<?>> tags = collectTags(
        );

        tags.sort(
                Comparator.comparing(
                        TagLangSubProvider::translationKey
                )
        );

        for (TagKey<?> tag : tags) {
            String key = translationKey(
                    tag
            );

            if (hasKey(
                    translations,
                    key
            )) {
                continue;
            }

            put(
                    translations,
                    key,
                    JolCraftStrings.toTitleCase(
                            tag.location().getPath()
                    )
            );
        }
    }

    private static @NotNull List<TagKey<?>> collectTags(
            ) {
        List<TagKey<?>> tags = new ArrayList<>();

        collectTags(
                JolCraftTags.class,
                tags
        );

        return tags;
    }

    private static void collectTags(
            @NotNull Class<?> type,
            @NotNull List<TagKey<?>> tags
    ) {
        for (Field field : type.getDeclaredFields()) {
            if (
                    !Modifier.isStatic(
                            field.getModifiers()
                    )
                            || !TagKey.class.isAssignableFrom(
                            field.getType()
                    )
            ) {
                continue;
            }

            try {
                Object value = field.get(
                        null
                );

                if (
                        value instanceof TagKey<?> tag
                                && !tags.contains(
                                tag
                        )
                ) {
                    tags.add(
                            tag
                    );
                }
            } catch (IllegalAccessException exception) {
                throw new IllegalStateException(
                        "Unable to read tag field "
                                + type.getName()
                                + "."
                                + field.getName(),
                        exception
                );
            }
        }

        for (Class<?> nested : type.getDeclaredClasses()) {
            collectTags(
                    nested,
                    tags
            );
        }
    }

    private static @NotNull String translationKey(
            @NotNull TagKey<?> tag
    ) {
        return JolCraftStrings.dotted(
                JolCraftDictionary.TAG,
                tag.registry().location().getPath(),
                tag.location().getNamespace(),
                tag.location()
                        .getPath()
                        .replace(
                                '/',
                                '.'
                        )
        );
    }
}