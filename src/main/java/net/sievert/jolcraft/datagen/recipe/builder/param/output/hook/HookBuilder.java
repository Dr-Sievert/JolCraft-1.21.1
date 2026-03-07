package net.sievert.jolcraft.datagen.recipe.builder.param.output.hook;

import net.minecraft.resources.ResourceLocation;
import net.sievert.jolcraft.data.recipe.param.output.hook.Hook;
import net.sievert.jolcraft.datagen.recipe.builder.base.ParamBuilder;

public final class HookBuilder implements ParamBuilder<Hook> {

    private ResourceLocation id;

    private HookBuilder() {}

    public static HookBuilder create() {
        return new HookBuilder();
    }

    public HookBuilder id(ResourceLocation id) {
        this.id = id;
        return this;
    }

    @Override
    public Hook build() {
        return new Hook(id);
    }
}