package net.sievert.jolcraft.util;

import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;

public final class JolCraftStrings {

    private JolCraftStrings() {}

    /** Pluralize by adding 's'. */
    public static String plural(String singular) {
        return singular.endsWith("s") ? singular : singular + "s";
    }

    // ---------------------------------------------------------------------
    // Join helpers
    // ---------------------------------------------------------------------

    /** Join non-empty parts with '.' */
    public static String dotted(String... parts) {
        return join('.', parts);
    }

    /** Join non-empty parts with '_' */
    public static String underscored(String... parts) {
        return join('_', parts);
    }

    /** Join non-empty parts with '/' (textures, folders, resource paths) */
    public static String slashed(String... parts) {
        return join('/', parts);
    }

    private static String join(char separator, String... parts) {
        StringBuilder out = new StringBuilder();
        for (String part : parts) {
            if (part == null || part.isBlank()) continue;
            if (!out.isEmpty()) out.append(separator);
            out.append(part);
        }
        return out.toString();
    }

    // ---------------------------------------------------------------------
    // Title / display helpers
    // ---------------------------------------------------------------------

    public static String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    public static String toTitleCase(String path) {
        if (path == null || path.isEmpty()) return "";
        String[] words = path.split("_");
        StringBuilder result = new StringBuilder();
        for (String w : words) {
            if (!w.isEmpty()) result.append(capitalize(w)).append(" ");
        }
        return result.toString().trim();
    }

    /** Two-word flip: "contract_blank" -> "Blank Contract", else title-case. */
    public static String flipAndTitle(String path) {
        if (path == null || path.isEmpty()) return "";
        String[] words = path.split("_");
        if (words.length == 2) {
            return capitalize(words[1]) + " " + capitalize(words[0]);
        }
        return toTitleCase(path);
    }

    // ---------------------------------------------------------------------
    // Language key resolving (datagen convenience)
    // ---------------------------------------------------------------------

    /**
     * Resolve a translation key from supported inputs.
     * Supported:
     * - String: treated as the translation key directly
     * - DeferredHolder: "registry.namespace.path"
     */
    public static String resolveLangKey(Object thing) {
        if (thing instanceof String str) {
            return str;
        }
        if (thing instanceof DeferredHolder<?, ?> deferred) {
            ResourceKey<?> resourceKey = deferred.getKey();
            return resourceKey.registry().getPath() + "."
                    + resourceKey.location().getNamespace() + "."
                    + resourceKey.location().getPath();
        }
        throw new IllegalArgumentException("Unsupported key source: " + thing + " (class: " + thing.getClass() + ")");
    }
}