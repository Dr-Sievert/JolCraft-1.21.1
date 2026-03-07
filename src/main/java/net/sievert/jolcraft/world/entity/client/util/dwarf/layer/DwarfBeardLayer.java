package net.sievert.jolcraft.world.entity.client.util.dwarf.layer;

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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.id.directory.JolCraftDirectoryIds;
import net.sievert.jolcraft.data.id.model.JolCraftModelPartIds;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.entity.client.model.dwarf.DwarfModel;
import net.sievert.jolcraft.world.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.world.entity.custom.dwarf.variant.DwarfBeardColor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class DwarfBeardLayer extends RenderLayer<DwarfRenderState, DwarfModel> {

    private static final Map<DwarfBeardColor, ResourceLocation> LOCATION_BY_BEARD =
            Util.make(Maps.newEnumMap(DwarfBeardColor.class), map -> {
                map.put(DwarfBeardColor.BROWN, texture(DwarfBeardColor.BROWN));
                map.put(DwarfBeardColor.RED, texture(DwarfBeardColor.RED));
                map.put(DwarfBeardColor.BLACK, texture(DwarfBeardColor.BLACK));
                map.put(DwarfBeardColor.GRAY, texture(DwarfBeardColor.GRAY));
            });

    public DwarfBeardLayer(RenderLayerParent<DwarfRenderState, DwarfModel> parent) {
        super(parent);
    }

    @Override
    public void render(
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffer,
            int packedLight,
            DwarfRenderState state,
            float yRot,
            float xRot
    ) {
        if (state.dwarf == null || state.beard == null) {
            return;
        }

        DwarfModel model = getParentModel();
        ModelPart head = model.getHead();
        ModelPart beard = head.getChild(JolCraftModelPartIds.Creature.Humanoid.Dwarf.BEARD);
        ModelPart rightEyebrow = head.getChild(JolCraftModelPartIds.Creature.Humanoid.RIGHT_EYEBROW);
        ModelPart leftEyebrow = head.getChild(JolCraftModelPartIds.Creature.Humanoid.LEFT_EYEBROW);

        beard.visible = true;
        rightEyebrow.visible = true;
        leftEyebrow.visible = true;

        model.setupAnim(state);

        ResourceLocation texture = LOCATION_BY_BEARD.get(state.beard);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);

        beard.visible = false;
        rightEyebrow.visible = false;
        leftEyebrow.visible = false;
    }

    private static ResourceLocation texture(DwarfBeardColor color) {
        return JolCraftTextures.mod(
                JolCraftTextures.entity(
                        JolCraftDirectoryIds.DWARF,
                        JolCraftDirectoryIds.BEARD,
                        JolCraftDirectoryIds.BEARD + "_" + color.getId()
                )
        );
    }
}