package net.sievert.jolcraft.entity.client.render.animal;

import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.client.animal.MuffhornFurLayer;
import net.sievert.jolcraft.entity.client.animal.MuffhornRenderState;
import net.sievert.jolcraft.entity.client.model.animal.MuffhornModel;
import net.sievert.jolcraft.entity.custom.animal.MuffhornEntity;

@OnlyIn(Dist.CLIENT)
public class MuffhornRenderer extends AgeableMobRenderer<MuffhornEntity, MuffhornRenderState, MuffhornModel> {

    public MuffhornRenderer(EntityRendererProvider.Context context) {
        super(
                context,
                new MuffhornModel(context.bakeLayer(MuffhornModel.LAYER_LOCATION)),
                new MuffhornModel(context.bakeLayer(MuffhornModel.BABY_LAYER_LOCATION)),
                0.7f
        );
        this.addLayer(new MuffhornFurLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(MuffhornRenderState state) {
        return ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "textures/entity/animal/muffhorn.png");
    }

    public MuffhornRenderState createRenderState() {
        return new MuffhornRenderState();
    }

    @Override
    public void extractRenderState(MuffhornEntity entity, MuffhornRenderState state, float partialTick) {
        super.extractRenderState(entity, state, partialTick);
        state.setEntity(entity);
        state.isSheared = entity.isSheared();
    }

}

