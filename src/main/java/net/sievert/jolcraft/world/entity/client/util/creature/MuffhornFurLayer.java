package net.sievert.jolcraft.world.entity.client.util.creature;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.entity.client.model.creature.MuffhornModel;
import net.sievert.jolcraft.world.entity.custom.creature.MuffhornEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class MuffhornFurLayer extends RenderLayer<MuffhornEntity, MuffhornModel<MuffhornEntity>> {
    private static final ResourceLocation FUR_TEXTURE =
            JolCraftTextures.mod(JolCraftTextures.creature(JolCraftItemIds.MUFFHORN_FUR));

    private final MuffhornModel<MuffhornEntity> model;

    public MuffhornFurLayer(RenderLayerParent<MuffhornEntity, MuffhornModel<MuffhornEntity>> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new MuffhornModel<>(modelSet.bakeLayer(MuffhornModel.LAYER_LOCATION));
    }

    @Override
    public void render(
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffer,
            int packedLight,
            @NotNull MuffhornEntity entity,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        if (entity.isSheared() || entity.isInvisible()) {
            return;
        }

        this.getParentModel().copyPropertiesTo(this.model);
        this.model.prepareMobModel(entity, limbSwing, limbSwingAmount, partialTick);
        this.model.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.model.setFurVisible(true);
        this.model.renderToBuffer(
                poseStack,
                buffer.getBuffer(this.model.renderType(FUR_TEXTURE)),
                packedLight,
                OverlayTexture.NO_OVERLAY
        );
        this.model.setFurVisible(false);
    }
}