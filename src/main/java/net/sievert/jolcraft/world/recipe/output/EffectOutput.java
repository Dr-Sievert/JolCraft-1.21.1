package net.sievert.jolcraft.world.recipe.output;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.storage.loot.LootContext;

import java.util.List;
import java.util.function.Consumer;

public record EffectOutput(
        MobEffectInstance effect,
        List<ResourceLocation> hooks
) implements RecipeOutput {

    public static final MapCodec<EffectOutput> CODEC =
            RecordCodecBuilder.mapCodec(instance -> instance.group(
                    MobEffectInstance.CODEC
                            .fieldOf("effect")
                            .forGetter(EffectOutput::effect),

                    ResourceLocation.CODEC
                            .listOf()
                            .optionalFieldOf("hooks", List.of())
                            .forGetter(EffectOutput::hooks)
            ).apply(instance, EffectOutput::new));

    public EffectOutput {
        hooks = hooks == null
                ? List.of()
                : List.copyOf(hooks);
    }

    @Override
    public RecipeOutputType getType() {
        return JolCraftRecipeOutputTypes.EFFECT.get();
    }

    public void generate(
            LootContext context,
            RecipeInput recipeInput,
            Consumer<MobEffectInstance> output
    ) {
        MobEffectInstance generated =
                new MobEffectInstance(effect);

        if (applyHooks(
                context,
                generated,
                recipeInput
        )) {
            output.accept(generated);
        }
    }

    public static EffectOutput of(
            MobEffectInstance effect
    ) {
        return new EffectOutput(
                effect,
                List.of()
        );
    }

    public EffectOutput applyHook(
            ResourceLocation hook
    ) {
        List<ResourceLocation> updated =
                new java.util.ArrayList<>(hooks);

        updated.add(hook);

        return new EffectOutput(
                effect,
                updated
        );
    }
}