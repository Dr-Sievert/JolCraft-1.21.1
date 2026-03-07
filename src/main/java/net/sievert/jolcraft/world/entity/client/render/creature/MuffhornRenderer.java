package net.sievert.jolcraft.world.entity.client.render.creature;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.entity.AgeableMobRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.id.entity.creature.JolCraftCreatureIds;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.entity.client.util.creature.MuffhornFurLayer;
import net.sievert.jolcraft.world.entity.client.util.creature.MuffhornRenderState;
import net.sievert.jolcraft.world.entity.client.model.creature.MuffhornModel;
import net.sievert.jolcraft.world.entity.custom.creature.MuffhornEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("deprecation")
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
        return JolCraftTextures.mod(JolCraftTextures.creature(JolCraftCreatureIds.MUFFHORN));
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

