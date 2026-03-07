package net.sievert.jolcraft.datagen.recipe.builder.base;

import com.mojang.serialization.DataResult;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.id.recipe.JolCraftParameterIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftEnumHelper;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Datagen-only fluent builder for deterministic recipe JSON names.
 *
 * Contract (dev/datagen):
 * - Builders choose semantics (tokens), this class owns mechanics (sanitize/join).
 * - Token normalization happens immediately when added.
 * - Never throws.
 * - Does NOT "heal" invalid tokens by inserting UNKNOWN.
 *   Instead, records errors and returns DataResult.error on build().
 *
 * Caller policy:
 * - Datagen should hard-stop on any error by unwrapping via JolCraftDataResults.require(...).
 */
public final class RecipeFileNameBuilder {

    private final List<String> tokens = new ArrayList<>();
    private final List<String> errors = new ArrayList<>();

    private RecipeFileNameBuilder() {}

    public static RecipeFileNameBuilder create() {
        return new RecipeFileNameBuilder();
    }

    // ---------------------------------------------------------------------
    // TOKEN ADDERS
    // ---------------------------------------------------------------------

    public RecipeFileNameBuilder word(String raw) {
        addToken(JolCraftDictionary.WORD, raw);
        return this;
    }

    public RecipeFileNameBuilder enumId(JolCraftEnumHelper.StringId id) {
        addToken(
                JolCraftStrings.underscored(JolCraftParameterIds.ENUM, JolCraftParameterIds.ID),
                id != null ? id.getId() : null
        );
        return this;
    }

    /**
     * Adds the ResourceLocation path only (no namespace), per dev-env naming rules.
     */
    public RecipeFileNameBuilder rl(ResourceLocation id) {
        addToken(JolCraftParameterIds.RESOURCE_LOCATION, id != null ? id.getPath() : null);
        return this;
    }

    // ---------------------------------------------------------------------
    // BUILD
    // ---------------------------------------------------------------------

    /**
     * Returns success(name) if all tokens were valid without required normalization.
     * Returns error(message, partialName) if any token was invalid or required normalization.
     */
    public DataResult<String> build() {
        if (tokens.isEmpty()) {
            return DataResult.error(() -> "recipeName: no tokens were provided");
        }

        String name = JolCraftStrings.underscored(tokens.toArray(String[]::new));

        if (name.isBlank()) {
            return DataResult.error(() -> "recipeName: built name is blank");
        }

        if (errors.isEmpty()) {
            return DataResult.success(name);
        }

        String msg = "recipeName: " + String.join("; ", errors);
        return DataResult.error(() -> msg, name);
    }

    // ---------------------------------------------------------------------
    // INTERNALS
    // ---------------------------------------------------------------------

    private void addToken(String kind, String raw) {
        String rawTrim = raw == null ? "" : raw.trim();
        if (rawTrim.isEmpty()) {
            errors.add(kind + " token is null/blank");
            return;
        }

        String normalized = normalize(rawTrim);
        if (normalized.isBlank()) {
            errors.add(kind + " token became blank after normalization");
            return;
        }

        if (!normalized.equals(rawTrim.toLowerCase(Locale.ROOT))) {
            errors.add(kind + " token required normalization");
        }

        tokens.add(normalized);
    }

    /**
     * Normalization:
     * - lower-case
     * - allow only [a-z0-9_]
     * - treat any other char as separator '_'
     * - collapse multiple '_' and trim leading/trailing '_'
     */
    private static String normalize(String rawTrim) {
        String s = rawTrim.toLowerCase(Locale.ROOT);
        if (s.isEmpty()) return "";

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
                continue;
            }

            if (!lastUnderscore) {
                out.append('_');
                lastUnderscore = true;
            }
        }

        int start = 0;
        int end = out.length();

        while (start < end && out.charAt(start) == '_') start++;
        while (end > start && out.charAt(end - 1) == '_') end--;

        return (start >= end) ? "" : out.substring(start, end);
    }
}