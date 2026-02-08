package net.sievert.jolcraft.data.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.SmithingTrimRecipe;
import net.sievert.jolcraft.JolCraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.data.recipe.custom.DwarfTradeRecipe;
import net.sievert.jolcraft.data.recipe.custom.FermentingCauldronRecipe;
import net.sievert.jolcraft.data.recipe.custom.AttributeSmithingTrimRecipe;
import net.sievert.jolcraft.data.recipe.custom.LapidaryBenchRecipe;

public class JolCraftRecipes {

    public static final String ATTRIBUTE_SMITHING_TRIM_ID = "attribute_smithing_trim";
    public static final String DWARF_TRADE_ID = "dwarf_trade";
    public static final String LAPIDARY_BENCH_ID = "lapidary_bench";
    public static final String FERMENTING_CAULDRON_ID = "fermenting_cauldron";

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, JolCraft.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, JolCraft.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SmithingTrimRecipe>> ATTRIBUTE_SMITHING_TRIM_SERIALIZER =
            SERIALIZERS.register(ATTRIBUTE_SMITHING_TRIM_ID, AttributeSmithingTrimRecipe.Serializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DwarfTradeRecipe>> DWARF_TRADE_SERIALIZER =
            SERIALIZERS.register(DWARF_TRADE_ID, DwarfTradeRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<DwarfTradeRecipe>> DWARF_TRADE_TYPE =
            TYPES.register(DWARF_TRADE_ID, () -> simpleType(DWARF_TRADE_ID));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LapidaryBenchRecipe>> LAPIDARY_BENCH_SERIALIZER =
            SERIALIZERS.register(LAPIDARY_BENCH_ID, LapidaryBenchRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<LapidaryBenchRecipe>> LAPIDARY_BENCH_TYPE =
            TYPES.register(LAPIDARY_BENCH_ID, () -> simpleType(LAPIDARY_BENCH_ID));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FermentingCauldronRecipe>> FERMENTING_CAULDRON_SERIALIZER =
            SERIALIZERS.register(FERMENTING_CAULDRON_ID, FermentingCauldronRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<FermentingCauldronRecipe>> FERMENTING_CAULDRON_TYPE =
            TYPES.register(FERMENTING_CAULDRON_ID, () -> simpleType(FERMENTING_CAULDRON_ID));

    private static <T extends Recipe<?>> RecipeType<T> simpleType(String id) {
        return new RecipeType<>() {
            @Override
            public String toString() {
                return JolCraft.location(id).toString();
            }
        };
    }

    public static void register(IEventBus eventBus) {
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
