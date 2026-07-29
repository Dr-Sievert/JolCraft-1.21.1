package net.sievert.jolcraft.integration.jei.util.gui.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.alchemy.PotionContents;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class JeiEffectRenderer {

    private JeiEffectRenderer() {
    }

    public static void draw(
            @NotNull GuiGraphics graphics,
            @NotNull MobEffectInstance effect,
            int x,
            int y,
            int z,
            int width,
            int height
    ) {
        TextureAtlasSprite sprite =
                Minecraft.getInstance()
                        .getMobEffectTextures()
                        .get(
                                effect.getEffect()
                        );

        graphics.blit(
                x,
                y,
                z,
                width,
                height,
                sprite
        );
    }

    @SuppressWarnings("removal")
    public static @NotNull List<Component> tooltip(
            @NotNull MobEffectInstance effect
    ) {
        List<Component> tooltip =
                new ArrayList<>();

        PotionContents.addPotionTooltip(
                List.of(
                        effect
                ),
                tooltip::add,
                1.0F,
                20.0F
        );

        return tooltip;
    }
}
