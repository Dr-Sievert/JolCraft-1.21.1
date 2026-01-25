package net.sievert.jolcraft.world.entity.client.util.layer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class EmissiveLayer<S extends EntityRenderState, M extends EntityModel<S>> extends EyesLayer<S, M> {

    private final RenderType renderType;

    public EmissiveLayer(RenderLayerParent<S, M> renderer, ResourceLocation emissiveLayerLocation) {
        super(renderer);
        this.renderType = RenderType.eyes(emissiveLayerLocation);
    }

    @Override
    public @NotNull RenderType renderType() {
        return renderType;
    }
}
