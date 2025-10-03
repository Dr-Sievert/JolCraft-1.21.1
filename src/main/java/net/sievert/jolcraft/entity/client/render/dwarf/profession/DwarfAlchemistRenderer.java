package net.sievert.jolcraft.entity.client.render.dwarf.profession;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.model.dwarf.profession.DwarfAlchemistModel;
import net.sievert.jolcraft.entity.client.render.dwarf.DwarfRenderer;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.entity.client.util.dwarf.layer.DwarfBeardLayer;
import net.sievert.jolcraft.entity.custom.dwarf.profession.EntityAlchemistEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DwarfAlchemistRenderer extends DwarfRenderer<EntityAlchemistEntity> {

    public DwarfAlchemistRenderer(EntityRendererProvider.Context context) {
        super(context, new DwarfAlchemistModel(context.bakeLayer(DwarfAlchemistModel.LAYER_LOCATION)));
        this.addLayer(new DwarfBeardLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DwarfRenderState entity) {
        return ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/dwarf_alchemist.png");
    }
}

