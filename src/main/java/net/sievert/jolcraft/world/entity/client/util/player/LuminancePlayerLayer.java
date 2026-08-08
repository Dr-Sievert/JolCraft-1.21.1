package net.sievert.jolcraft.world.entity.client.util.player;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.id.attribute.JolCraftAttributeIds;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import net.sievert.jolcraft.world.entity.client.model.object.LuminanceModel;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public final class LuminancePlayerLayer
        extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final ResourceLocation TEXTURE =
            JolCraftTextures.mod(
                    JolCraftTextures.object(JolCraftAttributeIds.LUMINANCE)
            );

    private static final float RIGHT_OFFSET = -6.5F / 16.0F;
    private static final float UP_OFFSET = -2.5F / 16.0F;
    private static final float BACK_OFFSET = 3.0F / 16.0F;

    private final LuminanceModel model;

    public LuminancePlayerLayer(
            RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer,
            EntityModelSet modelSet
    ) {
        super(renderer);
        this.model = new LuminanceModel(
                modelSet.bakeLayer(LuminanceModel.LAYER_LOCATION)
        );
    }

    @Override
    public void render(
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffer,
            int packedLight,
            @NotNull AbstractClientPlayer player,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (player.getAttributeValue(JolCraftAttributes.LUMINANCE) <= 0.0D) {
            return;
        }

        poseStack.pushPose();

        this.getParentModel().body.translateAndRotate(poseStack);
        poseStack.translate(RIGHT_OFFSET, UP_OFFSET, BACK_OFFSET);

        this.model.setupAnim(
                player,
                limbSwing,
                limbSwingAmount,
                ageInTicks,
                netHeadYaw,
                headPitch
        );

        VertexConsumer consumer = buffer.getBuffer(
                RenderType.entityCutout(TEXTURE)
        );

        this.model.renderToBuffer(
                poseStack,
                consumer,
                LightTexture.FULL_BRIGHT,
                OverlayTexture.NO_OVERLAY
        );

        poseStack.popPose();
    }
}
