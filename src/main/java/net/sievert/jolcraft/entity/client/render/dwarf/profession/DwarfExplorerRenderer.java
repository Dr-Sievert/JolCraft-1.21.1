package net.sievert.jolcraft.entity.client.render.dwarf.profession;

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
import net.sievert.jolcraft.entity.client.model.dwarf.profession.DwarfExplorerModel;
import net.sievert.jolcraft.entity.custom.dwarf.profession.EntityExplorerEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DwarfExplorerRenderer extends DwarfRenderer<EntityExplorerEntity> {

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

}



