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
import net.sievert.jolcraft.entity.client.model.dwarf.profession.DwarfScrapperModel;
import net.sievert.jolcraft.entity.custom.dwarf.profession.DwarfScrapperEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DwarfScrapperRenderer extends DwarfRenderer<DwarfScrapperEntity> {

    public DwarfScrapperRenderer(EntityRendererProvider.Context context) {
        super(context, new DwarfScrapperModel(context.bakeLayer(DwarfScrapperModel.LAYER_LOCATION)));
        this.addLayer(new DwarfBeardLayer(this));
        this.addLayer(new DwarfEyeLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DwarfRenderState entity) {
        return ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/dwarf_scrapper.png");
    }

}


