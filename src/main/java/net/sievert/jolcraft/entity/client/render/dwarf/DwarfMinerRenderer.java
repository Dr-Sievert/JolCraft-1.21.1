package net.sievert.jolcraft.entity.client.render.dwarf;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfBeardLayer;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfEyeLayer;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.entity.client.model.dwarf.DwarfMinerModel;
import net.sievert.jolcraft.entity.custom.dwarf.DwarfMinerEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DwarfMinerRenderer extends DwarfRenderer<DwarfMinerEntity> {

    public DwarfMinerRenderer(EntityRendererProvider.Context context) {
        super(context, new DwarfMinerModel(context.bakeLayer(DwarfMinerModel.LAYER_LOCATION)));
        this.addLayer(new DwarfEyeLayer(this));
        this.addLayer(new DwarfBeardLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DwarfRenderState entity) {
        return ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/dwarf_miner.png");
    }

    @Override
    public void extractRenderState(@NotNull DwarfMinerEntity entity, @NotNull DwarfRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
    }

}





