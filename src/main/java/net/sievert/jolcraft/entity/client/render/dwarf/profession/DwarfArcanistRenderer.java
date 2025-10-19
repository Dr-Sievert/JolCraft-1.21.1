package net.sievert.jolcraft.entity.client.render.dwarf.profession;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.model.dwarf.profession.DwarfArcanistModel;
import net.sievert.jolcraft.entity.client.render.dwarf.DwarfRenderer;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.entity.client.util.dwarf.layer.DwarfBeardLayer;
import net.sievert.jolcraft.entity.client.util.layer.EmissiveLayer;
import net.sievert.jolcraft.entity.custom.dwarf.profession.DwarfArcanistEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DwarfArcanistRenderer extends DwarfRenderer<DwarfArcanistEntity> {

    public DwarfArcanistRenderer(EntityRendererProvider.Context context) {
        super(context, new DwarfArcanistModel(context.bakeLayer(DwarfArcanistModel.LAYER_LOCATION)));
        this.addLayer(new DwarfBeardLayer(this));
        this.addLayer(new EmissiveLayer<>(this, JolCraft.location("textures/entity/dwarf/dwarf_arcanist_emissive.png")));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DwarfRenderState entity) {
        return JolCraft.location("textures/entity/dwarf/dwarf_arcanist.png");
    }
}
