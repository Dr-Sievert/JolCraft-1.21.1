package net.sievert.jolcraft.datagen.client.language.util;

import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.util.log.JolCraftLogs;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Shared base for language providers with:
 * - tracked keys (to avoid accidental duplicates / support "auto add missing")
 * - common string helpers
 * - convenience methods for adding translations via DeferredHolders
 */
@OnlyIn(Dist.CLIENT)
public abstract class AbstractLanguageProvider extends LanguageProvider {

    private final Set<String> addedKeys = new HashSet<>();

    protected AbstractLanguageProvider(PackOutput output, String modId, String locale) {
        super(output, modId, locale);
    }

    /** Returns true if the key has already been added in this provider run. */
    public final boolean hasKey(String key) {
        return addedKeys.contains(key);
    }

    /** Convenience: add and track. Prefer using this over calling {@link #add(String, String)} directly. */
    public final void put(String key, String value) {
        if (!addedKeys.add(key)) {
            throw new IllegalStateException("Duplicate lang key added: '" + key + "' (provider: " + getClass().getSimpleName() + ")");
        }
        add(key, value);
    }

    /** Convenience: add many keys with the same display value. */
    public final void putSame(String value, Object... things) {
        for (Object t : things) {
            put(resolveKey(t), value);
        }
    }

    /** Convenience: add a DeferredHolder (or String key) with an explicit value. */
    public final void putManual(Object thing, String value) {
        put(resolveKey(thing), value);
    }

    /** Convenience: flip two-word ids ("contract_blank" -> "Blank Contract") else title-case. */
    protected final void putManualFlipped(Object thing) {
        String key = resolveKey(thing);
        String path = key.substring(key.lastIndexOf('.') + 1);
        put(key, flipAndTitle(path));
    }

    /** Batch flipped. */
    public final void putManualFlippedAll(Object... things) {
        for (Object t : things) putManualFlipped(t);
    }

    /**
     * Resolve a translation key from supported inputs.
     * Supported:
     * - String: treated as the translation key directly
     * - DeferredHolder: "registry.namespace.path"
     */
    protected final String resolveKey(Object thing) {
        if (thing instanceof String str) {
            return str;
        }
        if (thing instanceof DeferredHolder<?, ?> deferred) {
            var resourceKey = deferred.getKey();
            return resourceKey.registry().getPath() + "."
                    + resourceKey.location().getNamespace() + "."
                    + resourceKey.location().getPath();
        }

        throw new IllegalArgumentException("Unsupported key source: " + thing + " (class: " + thing.getClass() + ")");
    }

    protected static String capitalize(String s) {
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

    public static String flipAndTitle(String path) {
        if (path == null || path.isEmpty()) return "";
        String[] words = path.split("_");
        if (words.length == 2) {
            return capitalize(words[1]) + " " + capitalize(words[0]);
        }
        return toTitleCase(path);
    }

    /** Simple subprovider contract. */
    public interface LangSubProvider {
        void addTranslations(AbstractLanguageProvider provider);
    }

    /** Called by subclasses to run all subproviders. */
    protected final void runAll(List<? extends LangSubProvider> subs) {
        int beforeTotal = addedKeys.size();

        for (LangSubProvider sub : subs) {
            int before = addedKeys.size();

            sub.addTranslations(this);

            int added = addedKeys.size() - before;
            String name = sub.getClass().getSimpleName();

            JolCraftLogs.debug(
                    "Datagen",
                    "Lang subprovider {}: +{} keys",
                    name,
                    added
            );

            if (added == 0) {
                JolCraftLogs.warn(
                        "Datagen",
                        "Lang subprovider {} added 0 keys.",
                        name
                );
            }
        }

        int totalAdded = addedKeys.size() - beforeTotal;
        JolCraftLogs.debug(
                "Datagen",
                "Total language keys generated: {} ({} subproviders)",
                totalAdded,
                subs.size()
        );
    }
}