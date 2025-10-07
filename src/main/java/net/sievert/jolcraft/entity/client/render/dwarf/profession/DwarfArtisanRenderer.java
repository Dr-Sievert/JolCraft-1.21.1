package net.sievert.jolcraft.entity.client.render.dwarf.profession;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.render.dwarf.DwarfRenderer;
import net.sievert.jolcraft.entity.client.util.dwarf.layer.DwarfBeardLayer;
import net.sievert.jolcraft.entity.client.util.dwarf.layer.DwarfEyeLayer;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.entity.client.model.dwarf.profession.DwarfArtisanModel;
import net.sievert.jolcraft.entity.custom.dwarf.profession.DwarfArtisanEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DwarfArtisanRenderer extends DwarfRenderer<DwarfArtisanEntity> {

    public DwarfArtisanRenderer(EntityRendererProvider.Context context) {
        super(context, new DwarfArtisanModel(context.bakeLayer(DwarfArtisanModel.LAYER_LOCATION)));
        this.addLayer(new DwarfEyeLayer(this));
        this.addLayer(new DwarfBeardLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DwarfRenderState entity) {
        return JolCraft.location("textures/entity/dwarf/dwarf_artisan.png");
    }

}


