package net.sievert.jolcraft.data.id;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.JolCraftStrings;

/**
 * Base class for all JolCraft name holders.
 * Subclasses store path strings only (e.g. "dwarven_lexicon").
 * This class provides helper methods for converting them.
 */
public abstract class JolCraftIds {

    protected JolCraftIds() {}

    /** Pluralize by adding 's'. */
    public static String plural(String singular) {
        return JolCraftStrings.plural(singular);
    }

    /** Create a vanilla (minecraft namespace) name. */
    public static ResourceLocation vanilla(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    /** Join non-empty parts with '_' (no namespace). */
    protected static String join(String... parts) {
        return JolCraftStrings.underscored(parts);
    }

    /** Convenience: "<modid>_<a>_<b>_...". */
    protected static String modJoin(String... parts) {
        String[] all = new String[parts.length + 1];
        all[0] = JolCraft.MOD_ID;
        System.arraycopy(parts, 0, all, 1, parts.length);
        return JolCraftStrings.underscored(all);
    }

    /**
     * Returns the last path segment of the key's ResourceLocation.
     */
    public static String lastPathSegment(ResourceKey<?> key) {
        String path = key.location().getPath();
        int idx = path.lastIndexOf('/');
        return idx >= 0 ? path.substring(idx + 1) : path;
    }
}
