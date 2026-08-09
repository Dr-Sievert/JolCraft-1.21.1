package net.sievert.jolcraft.world.entity.player.advancement.util;

import net.minecraft.advancements.critereon.SimpleCriterionTrigger;
import net.minecraft.resources.ResourceLocation;

public abstract class JolCraftSimpleTrigger<T extends SimpleCriterionTrigger.SimpleInstance>
        extends SimpleCriterionTrigger<T> {

    private final ResourceLocation id;

    protected JolCraftSimpleTrigger(ResourceLocation id) {
        this.id = id;
    }

    public final ResourceLocation id() {
        return this.id;
    }
}