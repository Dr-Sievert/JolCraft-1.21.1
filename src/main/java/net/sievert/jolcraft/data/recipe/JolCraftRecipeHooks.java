package net.sievert.jolcraft.data.recipe;

import net.sievert.jolcraft.data.recipe.param.output.hook.custom.DeepslateCompassHook;

public final class JolCraftRecipeHooks {

    private JolCraftRecipeHooks() {}

    public static void registerAll() {
        DeepslateCompassHook.register();
    }

}