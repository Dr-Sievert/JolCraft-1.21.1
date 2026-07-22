package net.sievert.jolcraft.datagen.tag;

import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.base.JolCraftDataDomain;
import net.sievert.jolcraft.datagen.base.JolCraftMainDataProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

public interface JolCraftMainTagProvider<TTarget> extends JolCraftMainDataProvider<TTarget> {

    @Override
    @NotNull
    default JolCraftDataDomain domain(){
        return JolCraftDataDomain.TAG;
    }

    @NotNull
    String tagType();

    @Override
    default @NotNull String id() {
        String type = tagType();

        if (type.isBlank()) {
            throw new IllegalStateException("tagType must not be blank: " + name());
        }

        if (!type.equals(type.toLowerCase())) {
            throw new IllegalStateException("tagType must be lowercase: " + name() + " -> " + type);
        }

        if (type.contains(" ") || type.contains("/")) {
            throw new IllegalStateException("tagType must be a simple id: " + name() + " -> " + type);
        }

        return JolCraftStrings.underscored(type, domain().getId());
    }

    @Override
    default @NotNull String name() {
        return JolCraft.MOD_NAME + " " + JolCraftStrings.toTitleCase(tagType()) + " " + JolCraftStrings.toTitleCase(JolCraftStrings.plural(domain().getId()));
    }
}