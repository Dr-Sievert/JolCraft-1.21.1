package net.sievert.jolcraft.datagen.recipe.util;

import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.sievert.jolcraft.data.recipe.custom.dwarf_trade.DwarfTradeRecipe;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public final class RecipeValidation {

    private RecipeValidation() {}

    /**
     * Single entrypoint for ALL datagen-only recipe validation.
     * Throwing here is correct: it fails the datagen run loudly and deterministically.
     */
    public static void validateRecipes(Map<ResourceKey<Recipe<?>>, Recipe<?>> allRecipes) {
        assertNoDuplicateDwarfTradeOrders(collectDwarfTrades(allRecipes));
    }

    private static Map<ResourceLocation, DwarfTradeRecipe> collectDwarfTrades(
            Map<ResourceKey<Recipe<?>>, Recipe<?>> allRecipes
    ) {
        Map<ResourceLocation, DwarfTradeRecipe> out = new LinkedHashMap<>();

        for (var e : allRecipes.entrySet()) {
            Recipe<?> r = e.getValue();
            if (!(r instanceof DwarfTradeRecipe trade)) continue;

            ResourceLocation id = e.getKey().location();
            out.put(id, trade);
        }

        return out;
    }

    private static void assertNoDuplicateDwarfTradeOrders(Map<ResourceLocation, DwarfTradeRecipe> recipesById) {
        record Key(String professionId, int level, DwarfTradeRecipe.TradePool pool, int order) {}

        Map<Key, ResourceLocation> seen = new HashMap<>();

        for (var e : recipesById.entrySet()) {
            ResourceLocation id = e.getKey();
            DwarfTradeRecipe r = e.getValue();
            if (r == null || r.order().isEmpty()) continue;

            Key key = new Key(r.profession().getId(), r.merchantLevel(), r.pool(), r.order().getAsInt());
            ResourceLocation prev = seen.putIfAbsent(key, id);

            if (prev != null) {
                throw new IllegalStateException(
                        "Duplicate dwarf trade order: profession=" + key.professionId()
                                + ", level=" + key.level()
                                + ", pool=" + key.pool().name().toLowerCase()
                                + ", order=" + key.order()
                                + " -> " + prev + " and " + id
                );
            }
        }
    }
}