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
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.entity.client.model.dwarf.DwarfModel;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractBreedingEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.variant.DwarfBeardColor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class DwarfBeardLayer<T extends AbstractDwarfEntity> extends RenderLayer<T, DwarfModel<T>> {

    private static final Map<DwarfBeardColor, ResourceLocation> LOCATION_BY_BEARD =
            Util.make(Maps.newEnumMap(DwarfBeardColor.class), map -> {
                for (DwarfBeardColor color : DwarfBeardColor.values()) {
                    map.put(color, texture(color));
                }
            });

    public DwarfBeardLayer(RenderLayerParent<T, DwarfModel<T>> parent) {
        super(parent);
    }

    @Override
    public void render(
            @NotNull PoseStack poseStack,
            @NotNull MultiBufferSource buffer,
            int packedLight,
            @NotNull T dwarf,
            float limbSwing,
            float limbSwingAmount,
            float partialTick,
            float ageInTicks,
            float netHeadYaw,
            float headPitch
    ) {
        DwarfBeardColor beardColor = DwarfBeardColor.byId(dwarf.getData(AbstractBreedingEntity.BEARD_COLOR));
        ResourceLocation texture = LOCATION_BY_BEARD.get(beardColor);
        if (texture == null) {
            return;
        }

        DwarfModel<T> model = this.getParentModel();
        model.setupAnim(dwarf, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        ModelPart beard = model.beard;
        ModelPart rightEyebrow = model.right_eyebrow;
        ModelPart leftEyebrow = model.left_eyebrow;

        boolean prevBeard = beard.visible;
        boolean prevRightEyebrow = rightEyebrow.visible;
        boolean prevLeftEyebrow = leftEyebrow.visible;

        beard.visible = true;
        rightEyebrow.visible = true;
        leftEyebrow.visible = true;

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);

        beard.visible = prevBeard;
        rightEyebrow.visible = prevRightEyebrow;
        leftEyebrow.visible = prevLeftEyebrow;
    }

    private static @NotNull ResourceLocation texture(@NotNull DwarfBeardColor color) {
        return JolCraftTextures.mod(
                JolCraftTextures.entity(
                        JolCraftDirectoryIds.DWARF,
                        JolCraftDirectoryIds.BEARD,
                        JolCraftStrings.underscored(JolCraftDirectoryIds.BEARD, color.getId())
                )
        );
    }
}
