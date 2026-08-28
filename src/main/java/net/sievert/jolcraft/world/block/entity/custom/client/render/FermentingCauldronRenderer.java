package net.sievert.jolcraft.world.block.entity.custom.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.fluids.FluidType;
import net.sievert.jolcraft.util.client.JolCraftColors;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingCauldronBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.brewing.util.FermentingCauldronColorHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public final class FermentingCauldronRenderer implements BlockEntityRenderer<FermentingCauldronBlockEntity> {

    private static final float MIN_XZ = 2.0F / 16.0F;
    private static final float MAX_XZ = 14.0F / 16.0F;

    /*
     * The visible liquid occupies the interior between the lowest useful
     * surface and the full-cauldron surface.
     */
    private static final float MIN_Y = 6.0F / 16.0F;
    private static final float MAX_Y = 15.0F / 16.0F;

    public FermentingCauldronRenderer(
            BlockEntityRendererProvider.Context context
    ) {}

    @Override
    public void render(
            FermentingCauldronBlockEntity blockEntity,
            float partialTicks,
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffer,
            int packedLight,
            int packedOverlay
    ) {
        Level level =
                blockEntity.getLevel();

        if (level == null) {
            return;
        }

        int amount =
                blockEntity.getBrewAmount();

        if (amount <= 0) {
            return;
        }

        float fillFraction =
                Mth.clamp(
                        amount / (float) FluidType.BUCKET_VOLUME,
                        0.0F,
                        1.0F
                );

        float y =
                Mth.lerp(
                        fillFraction,
                        MIN_Y,
                        MAX_Y
                );

        IClientFluidTypeExtensions properties =
                IClientFluidTypeExtensions.of(
                        Fluids.WATER
                );

        @SuppressWarnings("deprecation")
        TextureAtlasSprite sprite =
                Minecraft.getInstance()
                        .getTextureAtlas(
                                TextureAtlas.LOCATION_BLOCKS
                        )
                        .apply(
                                properties.getStillTexture()
                        );

        int argb =
                FermentingCauldronColorHelper.displayColor(
                        level,
                        partialTicks,
                        blockEntity.getBrewStartTime(),
                        blockEntity.getBlendTotalTicks(),
                        blockEntity.getCurrentColor(),
                        blockEntity.getStartColor(),
                        blockEntity.getTargetColor()
                );

        float alpha =
                Mth.clamp(
                        JolCraftColors.alpha(argb) / 255.0F * 0.85F,
                        0.0F,
                        1.0F
                );

        float red =
                JolCraftColors.red(argb) / 255.0F;

        float green =
                JolCraftColors.green(argb) / 255.0F;

        float blue =
                JolCraftColors.blue(argb) / 255.0F;

        float u0 =
                sprite.getU0();

        float u1 =
                sprite.getU1();

        float v0 =
                sprite.getV0();

        float v1 =
                sprite.getV1();

        VertexConsumer consumer =
                buffer.getBuffer(
                        RenderType.translucentMovingBlock()
                );

        Matrix4f matrix =
                poseStack.last()
                        .pose();

        consumer.addVertex(
                        matrix,
                        MIN_XZ,
                        y,
                        MAX_XZ
                )
                .setColor(
                        red,
                        green,
                        blue,
                        alpha
                )
                .setUv(
                        u0,
                        v1
                )
                .setOverlay(
                        packedOverlay
                )
                .setLight(
                        packedLight
                )
                .setNormal(
                        0.0F,
                        1.0F,
                        0.0F
                );

        consumer.addVertex(
                        matrix,
                        MAX_XZ,
                        y,
                        MAX_XZ
                )
                .setColor(
                        red,
                        green,
                        blue,
                        alpha
                )
                .setUv(
                        u1,
                        v1
                )
                .setOverlay(
                        packedOverlay
                )
                .setLight(
                        packedLight
                )
                .setNormal(
                        0.0F,
                        1.0F,
                        0.0F
                );

        consumer.addVertex(
                        matrix,
                        MAX_XZ,
                        y,
                        MIN_XZ
                )
                .setColor(
                        red,
                        green,
                        blue,
                        alpha
                )
                .setUv(
                        u1,
                        v0
                )
                .setOverlay(
                        packedOverlay
                )
                .setLight(
                        packedLight
                )
                .setNormal(
                        0.0F,
                        1.0F,
                        0.0F
                );

        consumer.addVertex(
                        matrix,
                        MIN_XZ,
                        y,
                        MIN_XZ
                )
                .setColor(
                        red,
                        green,
                        blue,
                        alpha
                )
                .setUv(
                        u0,
                        v0
                )
                .setOverlay(
                        packedOverlay
                )
                .setLight(
                        packedLight
                )
                .setNormal(
                        0.0F,
                        1.0F,
                        0.0F
                );
    }
}
