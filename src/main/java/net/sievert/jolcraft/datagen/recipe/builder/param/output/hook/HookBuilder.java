package net.sievert.jolcraft.datagen.recipe.builder.param.output.hook;

import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.recipe.param.condition.Conditions;
import net.sievert.jolcraft.data.recipe.param.output.hook.Hook;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.condition.ConditionsBuilder;

public final class HookBuilder implements ParamBuilder<Hook> {

    private ResourceLocation id;
    private Conditions conditions;

    private HookBuilder() {}

    public static HookBuilder create() {
        return new HookBuilder();
    }

    public HookBuilder id(ResourceLocation id) {
        this.id = id;
        return this;
    }

    public HookBuilder conditions(Conditions conditions) {
        this.conditions = conditions;
        return this;
    }

    public HookBuilder conditions(ConditionsBuilder builder) {
        this.conditions = builder != null ? builder.build() : null;
        return this;
    }

    @Override
    public Hook build() {
        return new Hook(id, conditions != null ? conditions : Conditions.EMPTY);
    }
}