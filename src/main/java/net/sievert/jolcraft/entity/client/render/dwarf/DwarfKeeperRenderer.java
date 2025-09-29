package net.sievert.jolcraft.entity.client.render.dwarf;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfBeardLayer;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfEyeLayer;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.entity.client.model.dwarf.DwarfKeeperModel;
import net.sievert.jolcraft.entity.custom.dwarf.DwarfKeeperEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DwarfKeeperRenderer extends DwarfRenderer<DwarfKeeperEntity> {

    public DwarfKeeperRenderer(EntityRendererProvider.Context context) {
        super(context, new DwarfKeeperModel(context.bakeLayer(DwarfKeeperModel.LAYER_LOCATION)));
        this.addLayer(new DwarfEyeLayer(this));
        this.addLayer(new DwarfBeardLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DwarfRenderState entity) {
        return ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/dwarf_keeper.png");
    }

    @Override
    public void extractRenderState(@NotNull DwarfKeeperEntity entity, @NotNull DwarfRenderState reusedState, float partialTick) {
        super.extractRenderState(entity, reusedState, partialTick);
    }

}

