package net.sievert.jolcraft.entity.client.dwarf;

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
import net.sievert.jolcraft.entity.custom.dwarf.variation.DwarfBeardColor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class DwarfBeardLayer extends RenderLayer<DwarfRenderState, DwarfModel> {

    private static final Map<DwarfBeardColor, ResourceLocation> LOCATION_BY_BEARD =
            Util.make(Maps.newEnumMap(DwarfBeardColor.class), map -> {
                map.put(DwarfBeardColor.BROWN,
                        ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/beard/beard_brown.png"));
                map.put(DwarfBeardColor.RED,
                        ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/beard/beard_red.png"));
                map.put(DwarfBeardColor.BLACK,
                        ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/beard/beard_black.png"));
                map.put(DwarfBeardColor.GRAY,
                        ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/beard/beard_gray.png"));
            });

    public DwarfBeardLayer(RenderLayerParent<DwarfRenderState, DwarfModel> parent) {
        super(parent);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight,
                       DwarfRenderState state, float yRot, float xRot) {

        if (state.dwarf == null || state.beard == null) return;

        DwarfModel model = this.getParentModel();
        ModelPart beard = model.getHead().getChild("beard");
        ModelPart right_eyebrow = model.getHead().getChild("right_eyebrow");
        ModelPart left_eyebrow = model.getHead().getChild("left_eyebrow");
        beard.visible = true;
        right_eyebrow.visible = true;
        left_eyebrow.visible = true;

        model.setupAnim(state);
        ResourceLocation texture = LOCATION_BY_BEARD.get(state.beard);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);

        // Prevents it from bleeding into other layers
        beard.visible = false;
        right_eyebrow.visible = false;
        left_eyebrow.visible = false;

    }
}