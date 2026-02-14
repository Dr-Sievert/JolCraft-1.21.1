package net.sievert.jolcraft.util.client;

import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.directory.JolCraftDirectoryIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.util.JolCraftStrings;

public final class JolCraftTextures {

    private JolCraftTextures() {}

    /* ---------------------------------------------------------------------
     * Namespace selectors
     * ------------------------------------------------------------------ */

    public static ResourceLocation mod(String path) {
        return JolCraft.location(path);
    }

    public static ResourceLocation vanilla(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public static ResourceLocation jeiRl(String path) {
        return ResourceLocation.fromNamespaceAndPath(JolCraftDictionary.JEI, path);
    }

    public static ResourceLocation of(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    /* ---------------------------------------------------------------------
     * Path builders
     * ------------------------------------------------------------------ */

    private static String texture(String prefix, String... path) {
        return pngPath(JolCraftStrings.slashed(
                JolCraftDirectoryIds.TEXTURES,
                prefix,
                dir(path)
        ));
    }

    public static String block(String... path) {
        return texture(JolCraftDirectoryIds.BLOCK, path);
    }

    public static String item(String... path) {
        return texture(JolCraftDirectoryIds.ITEM, path);
    }

    public static String entity(String... path) {
        return texture(JolCraftDirectoryIds.ENTITY, path);
    }

    public static String container(String... path) {
        return texture(dir(JolCraftDirectoryIds.GUI, JolCraftDirectoryIds.CONTAINER), path);
    }

    public static String dwarf(String... path) {
        return entity(JolCraftDirectoryIds.DWARF, JolCraftStrings.slashed(path));
    }

    public static String creature(String... path) {
        return entity(JolCraftDirectoryIds.CREATURE, JolCraftStrings.slashed(path));
    }

    public static String object(String... path) {
        return entity(JolCraftDirectoryIds.OBJECT, JolCraftStrings.slashed(path));
    }

    public static String jei(String... path) {
        return pngPath(
                JolCraftStrings.slashed(
                        JolCraftDirectoryIds.TEXTURES,
                        JolCraftDirectoryIds.JEI,
                        JolCraftDirectoryIds.ATLAS,
                        JolCraftDirectoryIds.GUI,
                        JolCraftStrings.slashed(path)
                )
        );
    }

    public static String jeiIcon(String... path) {
        return jei(JolCraftDirectoryIds.ICONS, JolCraftStrings.slashed(path));
    }

    /* ---------------------------------------------------------------------
     * GUI sprite ids (blitSprite)
     * blitSprite looks up: textures/gui/sprites/<path>.png
     * ------------------------------------------------------------------ */

    public static ResourceLocation modSprite(String... path) {
        return mod(dir(path));
    }

    public static ResourceLocation modWidget(String... path) {
        return mod(dir(JolCraftDictionary.WIDGET, dir(path)));
    }

    public static ResourceLocation vanillaSprite(String... path) {
        return vanilla(dir(path));
    }

    /* ---------------------------------------------------------------------
     * Helpers
     * ------------------------------------------------------------------ */

    public static String pngPath(String path) {
        String extension = "." + JolCraftDirectoryIds.PNG;
        return path.endsWith(extension) ? path : (path + extension);
    }

    public static String dir(String... path) {
        return JolCraftStrings.slashed(path);
    }
}
