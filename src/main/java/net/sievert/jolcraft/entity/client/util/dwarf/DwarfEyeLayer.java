package net.sievert.jolcraft.entity.client.util.dwarf;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.Util;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.model.dwarf.DwarfModel;
import net.sievert.jolcraft.entity.custom.dwarf.variation.DwarfEyeColor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DwarfEyeLayer extends RenderLayer<DwarfRenderState, DwarfModel> {

    private static final Map<DwarfEyeColor, ResourceLocation> LOCATION_BY_EYE =
            Util.make(Maps.newEnumMap(DwarfEyeColor.class), map -> {
                map.put(DwarfEyeColor.BROWN,
                        ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/eye/eye_brown.png"));
                map.put(DwarfEyeColor.DARK_BROWN,
                        ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/eye/eye_dark_brown.png"));
                map.put(DwarfEyeColor.BLUE,
                        ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/eye/eye_blue.png"));
                map.put(DwarfEyeColor.GREEN,
                        ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/eye/eye_green.png"));
                map.put(DwarfEyeColor.GRAY,
                        ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/eye/eye_gray.png"));
            });

    public DwarfEyeLayer(RenderLayerParent<DwarfRenderState, DwarfModel> parent) {
        super(parent);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight,
                       DwarfRenderState state, float yRot, float xRot) {

        if (state.dwarf == null || state.eye == null) return;

        DwarfModel model = this.getParentModel();
        ModelPart right_eye = model.getHead().getChild("right_eye");
        ModelPart left_eye = model.getHead().getChild("left_eye");
        right_eye.visible = true;
        left_eye.visible = true;

        model.setupAnim(state);
        ResourceLocation texture = LOCATION_BY_EYE.get(state.eye);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);

        right_eye.visible = false;
        left_eye.visible = false;
    }
}