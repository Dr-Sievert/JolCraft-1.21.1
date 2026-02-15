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
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeIds;
import net.sievert.jolcraft.data.recipe.custom.*;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.data.recipe.custom.bounty.BountyTaskRecipe;
import net.sievert.jolcraft.data.recipe.custom.fermenting_cauldron.FermentingCauldronRecipe;
import net.sievert.jolcraft.data.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.data.recipe.custom.lapidary_bench.LapidaryBenchRecipe;

public final class JolCraftRecipes {

    private JolCraftRecipes(){}

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, JolCraft.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, JolCraft.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<SmithingTrimRecipe>> ATTRIBUTE_SMITHING_TRIM_SERIALIZER =
            SERIALIZERS.register(JolCraftRecipeIds.ATTRIBUTE_SMITHING_TRIM, AttributeSmithingTrimRecipe.Serializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<DwarfTradeRecipe>> DWARF_TRADE_SERIALIZER =
            SERIALIZERS.register(JolCraftRecipeIds.DWARF_TRADE, DwarfTradeRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<DwarfTradeRecipe>> DWARF_TRADE_TYPE =
            TYPES.register(JolCraftRecipeIds.DWARF_TRADE, () -> simpleType(JolCraftRecipeIds.DWARF_TRADE));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<LapidaryBenchRecipe>> LAPIDARY_BENCH_SERIALIZER =
            SERIALIZERS.register(JolCraftRecipeIds.LAPIDARY_BENCH, LapidaryBenchRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<LapidaryBenchRecipe>> LAPIDARY_BENCH_TYPE =
            TYPES.register(JolCraftRecipeIds.LAPIDARY_BENCH, () -> simpleType(JolCraftRecipeIds.LAPIDARY_BENCH));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<FermentingCauldronRecipe>> FERMENTING_CAULDRON_SERIALIZER =
            SERIALIZERS.register(JolCraftRecipeIds.FERMENTING_CAULDRON, FermentingCauldronRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<FermentingCauldronRecipe>> FERMENTING_CAULDRON_TYPE =
            TYPES.register(JolCraftRecipeIds.FERMENTING_CAULDRON, () -> simpleType(JolCraftRecipeIds.FERMENTING_CAULDRON));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BountyTaskRecipe>> BOUNTY_TASK_SERIALIZER =
            SERIALIZERS.register(JolCraftRecipeIds.BOUNTY_TASK, BountyTaskRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<BountyTaskRecipe>> BOUNTY_TASK_TYPE =
            TYPES.register(JolCraftRecipeIds.BOUNTY_TASK, () -> simpleType(JolCraftRecipeIds.BOUNTY_TASK));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BountyRewardRecipe>> BOUNTY_REWARD_SERIALIZER =
            SERIALIZERS.register(JolCraftRecipeIds.BOUNTY_REWARD, BountyRewardRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<BountyRewardRecipe>> BOUNTY_REWARD_TYPE =
            TYPES.register(JolCraftRecipeIds.BOUNTY_REWARD, () -> simpleType(JolCraftRecipeIds.BOUNTY_REWARD));

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
