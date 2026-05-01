package net.sievert.jolcraft.util;

import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.registries.DeferredHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

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

    /** Join non-empty parts with '/' */
    public static String slashed(String... parts) {
        return join('/', parts);
    }

    /** Join non-empty parts with ' ' */
    public static String spaced(String... parts) {
        return join(' ', parts);
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

    public static String toTitleCase(String input) {
        if (input == null || input.isBlank()) {
            return input;
        }

        String[] parts = input.split("[._/\\s]+");

        StringBuilder result = new StringBuilder();

        for (String part : parts) {
            if (part.isEmpty()) continue;

            if (!result.isEmpty()) {
                result.append(" ");
            }

            result.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1).toLowerCase());
        }

        return result.toString();
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
    // Datagen normalization helpers
    // ---------------------------------------------------------------------

    @NotNull
    public static String normalizeUnderscored(@Nullable String raw) {
        String s = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty()) {
            return "";
        }

        StringBuilder out = new StringBuilder(s.length());
        boolean lastUnderscore = false;

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean ok =
                    (c >= 'a' && c <= 'z') ||
                            (c >= '0' && c <= '9');

            if (ok) {
                out.append(c);
                lastUnderscore = false;
            } else if (!lastUnderscore) {
                out.append('_');
                lastUnderscore = true;
            }
        }

        int start = 0;
        int end = out.length();

        while (start < end && out.charAt(start) == '_') {
            start++;
        }
        while (end > start && out.charAt(end - 1) == '_') {
            end--;
        }

        return start >= end ? "" : out.substring(start, end);
    }

    @NotNull
    public static String normalizeExtension(@Nullable String raw) {
        String s = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
        if (s.isEmpty()) {
            return "";
        }

        if (s.startsWith(".")) {
            s = s.substring(1);
        }

        String normalized = normalizeUnderscored(s).replace('_', '.');
        return normalized.isEmpty() ? "" : "." + normalized;
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