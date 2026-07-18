package net.sievert.jolcraft.world.recipe.base.output.hook;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeInput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeHookIds;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.recipe.base.output.hook.custom.DeepslateCompassHook;

import java.util.Map;

public final class JolCraftRecipeHooks {

    public static final ResourceLocation DEEPSLATE_COMPASS =
            JolCraft.location(
                    JolCraftRecipeHookIds.DEEPSLATE_COMPASS
            );

    private static final Map<ResourceLocation, RecipeHook> HOOKS =
            Map.of(
                    DEEPSLATE_COMPASS,
                    new DeepslateCompassHook()
            );

    private JolCraftRecipeHooks() {}

    public static boolean apply(
            ResourceLocation id,
            LootContext context,
            Object generatedOutput,
            RecipeInput recipeInput
    ) {
        RecipeHook hook =
                HOOKS.get(id);

        if (hook == null) {
            JolCraftLogs.error(
                    JolCraftLogTags.RECIPE,
                    "Unknown recipe hook id={}",
                    id
            );

            return false;
        }

        return hook.apply(
                context,
                generatedOutput,
                recipeInput
        );
    }
}