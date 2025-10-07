package net.sievert.jolcraft.entity.client.render.dwarf.profession;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.model.dwarf.profession.DwarfPriestModel;
import net.sievert.jolcraft.entity.client.render.dwarf.DwarfRenderer;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.entity.client.util.dwarf.layer.DwarfBeardLayer;
import net.sievert.jolcraft.entity.custom.dwarf.profession.DwarfPriestEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DwarfPriestRenderer extends DwarfRenderer<DwarfPriestEntity> {

    public DwarfPriestRenderer(EntityRendererProvider.Context context) {
        super(context, new DwarfPriestModel(context.bakeLayer(DwarfPriestModel.LAYER_LOCATION)));
        this.addLayer(new DwarfBeardLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DwarfRenderState entity) {
        return JolCraft.location("textures/entity/dwarf/dwarf_priest.png");
    }
}

