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
import net.sievert.jolcraft.data.id.model.JolCraftModelPartIds;
import net.sievert.jolcraft.data.id.directory.JolCraftDirectoryIds;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.entity.client.model.dwarf.DwarfModel;
import net.sievert.jolcraft.world.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.variation.DwarfBeardColor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class DwarfBeardLayer extends RenderLayer<DwarfRenderState, DwarfModel> {

    private static final Map<DwarfBeardColor, ResourceLocation> LOCATION_BY_BEARD =
            Util.make(Maps.newEnumMap(DwarfBeardColor.class), map -> {
                map.put(DwarfBeardColor.BROWN,
                        JolCraftTextures.mod(
                                JolCraftTextures.entity(
                                        JolCraftDirectoryIds.DWARF,
                                        JolCraftDirectoryIds.BEARD,
                                        JolCraftDirectoryIds.BEARD + "_" + DwarfBeardColor.BROWN.name().toLowerCase()
                                )
                        ));

                map.put(DwarfBeardColor.RED,
                        JolCraftTextures.mod(
                                JolCraftTextures.entity(
                                        JolCraftDirectoryIds.DWARF,
                                        JolCraftDirectoryIds.BEARD,
                                        JolCraftDirectoryIds.BEARD + "_" + DwarfBeardColor.RED.name().toLowerCase()
                                )
                        ));

                map.put(DwarfBeardColor.BLACK,
                        JolCraftTextures.mod(
                                JolCraftTextures.entity(
                                        JolCraftDirectoryIds.DWARF,
                                        JolCraftDirectoryIds.BEARD,
                                        JolCraftDirectoryIds.BEARD + "_" + DwarfBeardColor.BLACK.name().toLowerCase()
                                )
                        ));

                map.put(DwarfBeardColor.GRAY,
                        JolCraftTextures.mod(
                                JolCraftTextures.entity(
                                        JolCraftDirectoryIds.DWARF,
                                        JolCraftDirectoryIds.BEARD,
                                        JolCraftDirectoryIds.BEARD + "_" + DwarfBeardColor.GRAY.name().toLowerCase()
                                )
                        ));
            });

    public DwarfBeardLayer(RenderLayerParent<DwarfRenderState, DwarfModel> parent) {
        super(parent);
    }

    @Override
    public void render(@NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight,
                       DwarfRenderState state, float yRot, float xRot) {

        if (state.dwarf == null || state.beard == null) return;

        DwarfModel model = this.getParentModel();
        ModelPart beard = model.getHead().getChild(JolCraftModelPartIds.Creature.Humanoid.Dwarf.BEARD);
        ModelPart right_eyebrow = model.getHead().getChild(JolCraftModelPartIds.Creature.Humanoid.RIGHT_EYEBROW);
        ModelPart left_eyebrow = model.getHead().getChild(JolCraftModelPartIds.Creature.Humanoid.LEFT_EYEBROW);
        beard.visible = true;
        right_eyebrow.visible = true;
        left_eyebrow.visible = true;

        model.setupAnim(state);
        ResourceLocation texture = LOCATION_BY_BEARD.get(state.beard);
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);

        beard.visible = false;
        right_eyebrow.visible = false;
        left_eyebrow.visible = false;

    }
}