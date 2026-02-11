package net.sievert.jolcraft.util.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class JolCraftFonts {

    private JolCraftFonts(){}

    /** Returns the current active Minecraft font renderer. */
    public static Font defaultFont() {
        return Minecraft.getInstance().font;
    }

    /** Standard Galactic Alphabet (enchantment table font). */
    public static final ResourceLocation SGA = ResourceLocation.withDefaultNamespace("alt");
}
