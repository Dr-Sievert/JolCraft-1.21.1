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
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.sievert.jolcraft.world.block.entity.custom.brewing.FermentingCauldronBlockEntity;
import net.sievert.jolcraft.world.block.entity.custom.util.FermentingCauldronColorHelper;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public final class FermentingCauldronRenderer implements BlockEntityRenderer<FermentingCauldronBlockEntity> {

    public FermentingCauldronRenderer(BlockEntityRendererProvider.Context ctx) {}

    @Override
    public void render(FermentingCauldronBlockEntity be, float partialTicks, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        Level level = be.getLevel();
        if (level == null) return;

        BlockState state = be.getBlockState();
        if (!state.hasProperty(LayeredCauldronBlock.LEVEL)) return;

        int lvl = state.getValue(LayeredCauldronBlock.LEVEL);
        if (lvl <= 0) return;

        float min = 2f / 16f;
        float max = 14f / 16f;
        float y = (6f + (lvl * 3f)) / 16f;

        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(Fluids.WATER);

        @SuppressWarnings("deprecation")
        TextureAtlasSprite sprite = Minecraft.getInstance()
                .getTextureAtlas(TextureAtlas.LOCATION_BLOCKS)
                .apply(props.getStillTexture());

        int argb = FermentingCauldronColorHelper.displayColor(
                level,
                partialTicks,
                be.getBrewStartTime(),
                be.getBlendTotalTicks(),
                be.getCurrentColor(),
                be.getStartColor(),
                be.getTargetColor()
        );

        float a = ((argb >>> 24) & 0xFF) / 255f;
        float r = ((argb >>> 16) & 0xFF) / 255f;
        float g = ((argb >>> 8) & 0xFF) / 255f;
        float b = (argb & 0xFF) / 255f;

        float alpha = Mth.clamp(a * 0.85f, 0f, 1f);

        float u0 = sprite.getU0();
        float u1 = sprite.getU1();
        float v0 = sprite.getV0();
        float v1 = sprite.getV1();

        VertexConsumer vc = buffer.getBuffer(RenderType.translucentMovingBlock());
        Matrix4f mat = poseStack.last().pose();

        vc.addVertex(mat, min, y, max).setColor(r, g, b, alpha).setUv(u0, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(0f, 1f, 0f);
        vc.addVertex(mat, max, y, max).setColor(r, g, b, alpha).setUv(u1, v1).setOverlay(packedOverlay).setLight(packedLight).setNormal(0f, 1f, 0f);
        vc.addVertex(mat, max, y, min).setColor(r, g, b, alpha).setUv(u1, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(0f, 1f, 0f);
        vc.addVertex(mat, min, y, min).setColor(r, g, b, alpha).setUv(u0, v0).setOverlay(packedOverlay).setLight(packedLight).setNormal(0f, 1f, 0f);
    }
}