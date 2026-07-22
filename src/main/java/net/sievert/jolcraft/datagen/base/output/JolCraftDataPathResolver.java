package net.sievert.jolcraft.datagen.base.output;

import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class JolCraftDataPathResolver {

    private JolCraftDataPathResolver() {}

    @NotNull
    public static String resolveFolder(@NotNull JolCraftDataProvider<?> provider) {
        Objects.requireNonNull(provider, JolCraftDictionary.PROVIDER);

        List<String> parts = new ArrayList<>();

        for (JolCraftDataProvider<?> current : provider.chain()) {
            String folder = current.validatedFolder();
            if (!folder.isEmpty()) {
                parts.add(folder);
            }
        }

        return JolCraftStrings.slashed(parts.toArray(String[]::new));
    }

    @NotNull
    public static String resolvePath(
            @NotNull JolCraftDataProvider<?> provider,
            @NotNull String fileName
    ) {
        Objects.requireNonNull(provider, JolCraftDictionary.PROVIDER);
        Objects.requireNonNull(fileName, JolCraftDictionary.NAME);

        String folder = resolveFolder(provider);

        return folder.isEmpty()
                ? fileName
                : JolCraftStrings.slashed(folder, fileName);
    }
}