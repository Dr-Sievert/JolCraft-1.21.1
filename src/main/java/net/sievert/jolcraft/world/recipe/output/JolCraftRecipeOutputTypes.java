package net.sievert.jolcraft.world.recipe.output;

import com.mojang.serialization.Codec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftRegistries;

import java.util.List;

public final class JolCraftRecipeOutputTypes {

    public static final DeferredRegister<RecipeOutputType> OUTPUT_TYPES =
            DeferredRegister.create(
                    JolCraftRegistries.RECIPE_OUTPUT_TYPE,
                    JolCraft.MOD_ID
            );

    public static final Codec<RecipeOutput> CODEC =
            JolCraftRegistries.RECIPE_OUTPUT_TYPE
                    .byNameCodec()
                    .dispatch(
                            "type",
                            RecipeOutput::getType,
                            RecipeOutputType::codec
                    );

    public static final Codec<List<RecipeOutput>> LIST_CODEC =
            CODEC.listOf();

    public static final DeferredHolder<RecipeOutputType, RecipeOutputType> ITEM =
            OUTPUT_TYPES.register(
                    "item",
                    () -> new RecipeOutputType(ItemOutput.CODEC)
            );

    public static final DeferredHolder<RecipeOutputType, RecipeOutputType> ENTITY =
            OUTPUT_TYPES.register(
                    "entity",
                    () -> new RecipeOutputType(EntityOutput.CODEC)
            );

    public static final DeferredHolder<RecipeOutputType, RecipeOutputType> SOUND =
            OUTPUT_TYPES.register(
                    "sound",
                    () -> new RecipeOutputType(SoundOutput.CODEC)
            );

    public static final DeferredHolder<RecipeOutputType, RecipeOutputType> EFFECT =
            OUTPUT_TYPES.register(
                    "effect",
                    () -> new RecipeOutputType(EffectOutput.CODEC)
            );

    private JolCraftRecipeOutputTypes() {}

    public static void register(IEventBus eventBus) {
        OUTPUT_TYPES.register(eventBus);
    }
}