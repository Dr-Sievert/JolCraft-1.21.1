package net.sievert.jolcraft.entity.client.util.animal;

import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.sievert.jolcraft.entity.custom.animal.MuffhornEntity;

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
