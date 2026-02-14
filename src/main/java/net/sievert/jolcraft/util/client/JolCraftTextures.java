package net.sievert.jolcraft.util.client;

import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.texture.JolCraftTextureIds;
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

    public static String block(String... path) {
        return pngPath(
                JolCraftStrings.slashed(
                        JolCraftTextureIds.TEXTURES,
                        JolCraftTextureIds.BLOCK,
                        JolCraftStrings.slashed(path)
                )
        );
    }

    public static String jei(String... path) {
        return pngPath(
                JolCraftStrings.slashed(
                        JolCraftTextureIds.TEXTURES,
                        JolCraftTextureIds.JEI,
                        JolCraftTextureIds.ATLAS,
                        JolCraftTextureIds.GUI,
                        JolCraftStrings.slashed(path)
                )
        );
    }

    public static String jeiIcon(String... path) {
        return jei(JolCraftTextureIds.ICONS, JolCraftStrings.slashed(path));
    }

    private static String pngPath(String path) {
        String extension = "." + JolCraftTextureIds.PNG;
        return path.endsWith(extension) ? path : (path + extension);
    }
}
