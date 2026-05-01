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
import net.sievert.jolcraft.world.entity.custom.dwarf.variant.DwarfEyeColor;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

@OnlyIn(Dist.CLIENT)
public final class DwarfEyeLayer<T extends AbstractDwarfEntity> extends RenderLayer<T, DwarfModel<T>> {

    private static String eyeTextureName(@NotNull DwarfEyeColor color) {
        return JolCraftStrings.underscored(JolCraftDirectoryIds.EYE, color.getId());
    }

    private static final Map<DwarfEyeColor, ResourceLocation> LOCATION_BY_EYE =
            Util.make(Maps.newEnumMap(DwarfEyeColor.class), map -> {
                for (DwarfEyeColor color : DwarfEyeColor.values()) {
                    map.put(
                            color,
                            JolCraftTextures.mod(
                                    JolCraftTextures.entity(
                                            JolCraftDirectoryIds.DWARF,
                                            JolCraftDirectoryIds.EYE,
                                            eyeTextureName(color)
                                    )
                            )
                    );
                }
            });

    public DwarfEyeLayer(RenderLayerParent<T, DwarfModel<T>> parent) {
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
        DwarfEyeColor eyeColor = DwarfEyeColor.byId(dwarf.getData(AbstractBreedingEntity.EYE_COLOR));
        ResourceLocation texture = LOCATION_BY_EYE.get(eyeColor);
        if (texture == null) {
            return;
        }

        DwarfModel<T> model = this.getParentModel();
        model.setupAnim(dwarf, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        ModelPart rightEye = model.right_eye;
        ModelPart leftEye = model.left_eye;

        boolean prevRight = rightEye.visible;
        boolean prevLeft = leftEye.visible;

        rightEye.visible = true;
        leftEye.visible = true;

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        model.renderToBuffer(poseStack, consumer, packedLight, OverlayTexture.NO_OVERLAY);

        rightEye.visible = prevRight;
        leftEye.visible = prevLeft;
    }
}
