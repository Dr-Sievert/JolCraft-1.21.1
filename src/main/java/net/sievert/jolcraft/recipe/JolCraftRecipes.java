package net.sievert.jolcraft.recipe;

import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.sievert.jolcraft.JolCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.recipe.custom.JolSmithingTrimRecipe;

public class JolCraftRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, JolCraft.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, JolCraft.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SmithingTrimRecipe>> SMITHING_TRIM_SERIALIZER =
            SERIALIZERS.register("jolcraft_smithing_trim", JolSmithingTrimRecipe.Serializer::new);

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
