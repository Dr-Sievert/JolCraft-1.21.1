package net.sievert.jolcraft.world.recipe.context;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.param.JolCraftContextParamIds;

public final class JolCraftRecipeContextParams {

    public static final LootContextParam<ItemStack> INPUT_ITEM =
            create(JolCraftContextParamIds.INPUT_ITEM);

    private JolCraftRecipeContextParams() {}

    private static <T> LootContextParam<T> create(String path) {
        return new LootContextParam<>(
                ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, path)
        );
    }
}