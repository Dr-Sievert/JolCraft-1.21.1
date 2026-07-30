package net.sievert.jolcraft.integration.jei.util.render;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.gui.builder.ITooltipBuilder;
import mezz.jei.api.recipe.RecipeIngredientRole;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public final class JeiFluidRenderer {

    private JeiFluidRenderer() {
    }

    @SuppressWarnings("deprecation")
    public static void drawTinted(
            @NotNull GuiGraphics graphics,
            @NotNull FluidStack fluid,
            int x,
            int y,
            int z,
            int width,
            int height
    ) {
        IClientFluidTypeExtensions extensions =
                IClientFluidTypeExtensions.of(
                        fluid.getFluid()
                );

        ResourceLocation stillTexture =
                extensions.getStillTexture(
                        fluid
                );

        TextureAtlasSprite fluidSprite =
                Minecraft.getInstance()
                        .getTextureAtlas(
                                TextureAtlas.LOCATION_BLOCKS
                        )
                        .apply(
                                stillTexture
                        );

        int tint =
                extensions.getTintColor(
                        fluid
                );

        int red =
                tint >> 16
                        & 0xFF;

        int green =
                tint >> 8
                        & 0xFF;

        int blue =
                tint
                        & 0xFF;

        graphics.fill(
                x,
                y,
                x + width,
                y + height,
                0xFF000000
                        | red << 16
                        | green << 8
                        | blue
        );

        graphics.setColor(
                red / 255.0F,
                green / 255.0F,
                blue / 255.0F,
                1.0F
        );

        graphics.blit(
                x,
                y,
                z,
                width,
                height,
                fluidSprite
        );

        graphics.setColor(
                1.0F,
                1.0F,
                1.0F,
                1.0F
        );
    }

    public static void addSlot(
            @NotNull IRecipeLayoutBuilder builder,
            @NotNull RecipeIngredientRole role,
            int x,
            int y,
            @NotNull FluidStack fluid,
            int width,
            int height
    ) {
        IRecipeSlotBuilder slot =
                builder.addSlot(
                        role,
                        x,
                        y
                );

        slot.addFluidStack(
                        fluid.getFluid(),
                        fluid.getAmount(),
                        fluid.getComponentsPatch()
                )
                .setFluidRenderer(
                        1,
                        false,
                        width,
                        height
                );

        addPotionTooltip(
                slot,
                fluid
        );
    }

    private static void addPotionTooltip(
            @NotNull IRecipeSlotBuilder slot,
            @NotNull FluidStack fluid
    ) {
        PotionContents potionContents =
                fluid.getOrDefault(
                        DataComponents.POTION_CONTENTS,
                        PotionContents.EMPTY
                );

        if (!potionContents.hasEffects()) {
            return;
        }

        slot.addRichTooltipCallback(
                (
                        recipeSlot,
                        tooltip
                ) -> appendPotionTooltip(
                        tooltip,
                        potionContents
                )
        );
    }

    private static void appendPotionTooltip(
            @NotNull ITooltipBuilder tooltip,
            @NotNull PotionContents potionContents
    ) {
        PotionContents.addPotionTooltip(
                potionContents.getAllEffects(),
                tooltip::add,
                1.0F,
                20.0F
        );
    }
}