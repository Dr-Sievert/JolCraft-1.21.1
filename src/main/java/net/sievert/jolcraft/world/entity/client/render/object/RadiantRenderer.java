package net.sievert.jolcraft.world.entity.client.render.object;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.id.entity.object.JolCraftEntityObjectIds;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.entity.client.model.object.RadiantModel;
import net.sievert.jolcraft.world.entity.custom.object.RadiantEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RadiantRenderer extends EntityRenderer<RadiantEntity> {
    private static final ResourceLocation TEXTURE =
            JolCraftTextures.mod(JolCraftTextures.object(JolCraftEntityObjectIds.RADIANT));

    private final RadiantModel model;

    public RadiantRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.model = new RadiantModel(context.bakeLayer(RadiantModel.LAYER_LOCATION));
    }

    @Override
    public void render(@NotNull RadiantEntity entity, float entityYaw, float partialTick, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        poseStack.scale(-1.0F, -1.0F, 1.0F);
        poseStack.translate(-0.01F, -1.501F, 0.0F);

        this.model.setupAnim(entity, 0.0F, 0.0F, entity.tickCount + partialTick, 0.0F, 0.0F);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.entityCutout(getTextureLocation(entity)));
        this.model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);

        poseStack.popPose();

        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull RadiantEntity entity) {
        return TEXTURE;
    }

    @Override
    protected int getBlockLightLevel(@NotNull RadiantEntity entity, @NotNull BlockPos pos) {
        return 15;
    }
}