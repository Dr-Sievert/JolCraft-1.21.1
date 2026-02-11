package net.sievert.jolcraft.datagen.client.language.util;

import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.util.JolCraftStrings;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Shared base for language providers with:
 * - tracked keys (to avoid accidental duplicates / support "auto add missing")
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
            put(JolCraftStrings.resolveLangKey(t), value);
        }
    }

    /** Convenience: add a DeferredHolder (or String key) with an explicit value. */
    public final void putManual(Object thing, String value) {
        put(JolCraftStrings.resolveLangKey(thing), value);
    }

    /** Convenience: flip two-word ids ("contract_blank" -> "Blank Contract") else title-case. */
    protected final void putManualFlipped(Object thing) {
        String key = JolCraftStrings.resolveLangKey(thing);
        String path = key.substring(key.lastIndexOf('.') + 1);
        put(key, JolCraftStrings.flipAndTitle(path));
    }

    /** Batch flipped. */
    public final void putManualFlippedAll(Object... things) {
        for (Object t : things) putManualFlipped(t);
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
                    JolCraftLogTags.DATAGEN,
                    "Lang subprovider {}: +{} keys",
                    name,
                    added
            );

            if (added == 0) {
                JolCraftLogs.warn(
                        JolCraftLogTags.DATAGEN,
                        "Lang subprovider {} added 0 keys.",
                        name
                );
            }
        }

        int totalAdded = addedKeys.size() - beforeTotal;
        JolCraftLogs.debug(
                JolCraftLogTags.DATAGEN,
                "Total language keys generated: {} ({} subproviders)",
                totalAdded,
                subs.size()
        );
    }
}