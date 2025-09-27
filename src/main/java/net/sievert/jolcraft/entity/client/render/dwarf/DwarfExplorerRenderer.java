package net.sievert.jolcraft.entity.client.render.dwarf;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.dwarf.DwarfArmorLayer;
import net.sievert.jolcraft.entity.client.dwarf.DwarfBeardLayer;
import net.sievert.jolcraft.entity.client.dwarf.DwarfEyeLayer;
import net.sievert.jolcraft.entity.client.dwarf.DwarfRenderState;
import net.sievert.jolcraft.entity.client.model.dwarf.DwarfExplorerModel;
import net.sievert.jolcraft.entity.custom.dwarf.DwarfExplorerEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DwarfExplorerRenderer extends DwarfRenderer<DwarfExplorerEntity> {

    public DwarfExplorerRenderer(EntityRendererProvider.Context context) {
        super(context, new DwarfExplorerModel(context.bakeLayer(DwarfExplorerModel.LAYER_LOCATION)));
        addLayer(new DwarfEyeLayer(this));
        addLayer(new DwarfBeardLayer(this));
        addLayer(new DwarfArmorLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DwarfRenderState entity) {
        return ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/dwarf_explorer.png");
    }

    @Override
    public void extractRenderState(@NotNull DwarfExplorerEntity entity, @NotNull DwarfRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
    }

}



