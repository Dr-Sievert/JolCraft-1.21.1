package net.sievert.jolcraft.entity.client.render.dwarf.profession;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.render.dwarf.DwarfRenderer;
import net.sievert.jolcraft.entity.client.util.dwarf.layer.DwarfArmorLayer;
import net.sievert.jolcraft.entity.client.util.dwarf.layer.DwarfBeardLayer;
import net.sievert.jolcraft.entity.client.util.dwarf.layer.DwarfEyeLayer;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.entity.client.model.dwarf.profession.DwarfGuardModel;
import net.sievert.jolcraft.entity.custom.dwarf.profession.DwarfGuardEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DwarfGuardRenderer extends DwarfRenderer<DwarfGuardEntity> {

    public DwarfGuardRenderer(EntityRendererProvider.Context context) {
        super(context, new DwarfGuardModel(context.bakeLayer(DwarfGuardModel.LAYER_LOCATION)));
        addLayer(new DwarfArmorLayer(this));
        addLayer(new DwarfBeardLayer(this));
        addLayer(new DwarfEyeLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DwarfRenderState entity) {
        return ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/dwarf_guard.png");
    }

    @Override
    public void render(DwarfRenderState renderState, @NotNull PoseStack poseStack, @NotNull MultiBufferSource bufferSource, int packedLight) {
        poseStack.scale(1.1f, 1.1f, 1.1f);
        super.render(renderState, poseStack, bufferSource, packedLight);
    }

}
