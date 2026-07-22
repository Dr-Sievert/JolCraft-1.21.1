package net.sievert.jolcraft.world.entity.client.util.layer;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class EmissiveLayer<T extends Entity, M extends EntityModel<T>> extends EyesLayer<T, M> {

    private final RenderType renderType;

    public EmissiveLayer(RenderLayerParent<T, M> renderer, ResourceLocation emissiveLayerLocation) {
        super(renderer);
        this.renderType = RenderType.eyes(emissiveLayerLocation);
    }

    @Override
    public @NotNull RenderType renderType() {
        return this.renderType;
    }
}