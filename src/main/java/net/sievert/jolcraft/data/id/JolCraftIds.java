package net.sievert.jolcraft.data.id;

import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.JolCraftStrings;

/**
 * Base class for all JolCraft id holders.
 *
 * Subclasses store path strings only (e.g. "dwarven_lexicon").
 * This class provides helper methods for converting them.
 */
public abstract class JolCraftIds {

    protected JolCraftIds() {}

    /** Convert a path into "jolcraft:<path>". */
    public static String full(String path) {
        return JolCraft.MOD_ID + ":" + path;
    }

    /** Create a vanilla (minecraft namespace) id. */
    public static ResourceLocation vanilla(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    /** Join non-empty parts with '_' (no namespace). */
    protected static String joined(String... parts) {
        return JolCraftStrings.underscored(parts);
    }

    /** Convenience: "<modid>_<a>_<b>_...". */
    protected static String modJoined(String... parts) {
        String[] all = new String[parts.length + 1];
        all[0] = JolCraft.MOD_ID;
        System.arraycopy(parts, 0, all, 1, parts.length);
        return JolCraftStrings.underscored(all);
    }
}
