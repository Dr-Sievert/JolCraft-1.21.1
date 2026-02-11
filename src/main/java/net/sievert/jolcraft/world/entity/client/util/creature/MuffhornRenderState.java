package net.sievert.jolcraft.world.entity.client.util.creature;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.entity.custom.creature.MuffhornEntity;

@OnlyIn(Dist.CLIENT)
public class MuffhornRenderState extends LivingEntityRenderState {
    private MuffhornEntity entity;
    public boolean isSheared;
    public boolean isBaby;

    public void setEntity(MuffhornEntity entity) {
        this.entity = entity;
        this.isBaby = entity.isBaby();
    }

    public MuffhornEntity getEntity() {
        return entity;
    }
}
