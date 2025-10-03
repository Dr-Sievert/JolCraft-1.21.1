package net.sievert.jolcraft.entity.client.render.dwarf.profession;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.render.dwarf.DwarfRenderer;
import net.sievert.jolcraft.entity.client.util.dwarf.layer.DwarfEyeLayer;
import net.sievert.jolcraft.entity.client.util.dwarf.DwarfRenderState;
import net.sievert.jolcraft.entity.client.model.dwarf.profession.DwarfGuildmasterModel;
import net.sievert.jolcraft.entity.custom.dwarf.profession.EntityGuildmasterEntity;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class DwarfGuildmasterRenderer extends DwarfRenderer<EntityGuildmasterEntity> {

    public DwarfGuildmasterRenderer(EntityRendererProvider.Context context) {
        super(context, new DwarfGuildmasterModel(context.bakeLayer(DwarfGuildmasterModel.LAYER_LOCATION)));
        this.addLayer(new DwarfEyeLayer(this));
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(DwarfRenderState entity) {
        return ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/dwarf/dwarf_guildmaster.png");
    }

}

