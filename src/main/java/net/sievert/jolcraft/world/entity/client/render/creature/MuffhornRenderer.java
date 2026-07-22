package net.sievert.jolcraft.world.entity.client.render.creature;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.id.entity.creature.JolCraftCreatureIds;
import net.sievert.jolcraft.util.client.JolCraftTextures;
import net.sievert.jolcraft.world.entity.client.model.creature.MuffhornModel;
import net.sievert.jolcraft.world.entity.client.util.creature.MuffhornFurLayer;
import net.sievert.jolcraft.world.entity.custom.creature.MuffhornEntity;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@OnlyIn(Dist.CLIENT)
public class MuffhornRenderer extends MobRenderer<MuffhornEntity, MuffhornModel<MuffhornEntity>> {

    public MuffhornRenderer(EntityRendererProvider.Context context) {
        super(context, new MuffhornModel<>(context.bakeLayer(MuffhornModel.LAYER_LOCATION)), 0.7F);
        this.addLayer(new MuffhornFurLayer(this, context.getModelSet()));
    }

    @Override
    public ResourceLocation getTextureLocation(MuffhornEntity entity) {
        return JolCraftTextures.mod(JolCraftTextures.creature(JolCraftCreatureIds.MUFFHORN));
    }
}