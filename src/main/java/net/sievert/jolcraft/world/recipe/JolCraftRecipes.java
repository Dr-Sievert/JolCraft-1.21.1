package net.sievert.jolcraft.world.recipe;

import net.minecraft.world.item.crafting.*;
import net.sievert.jolcraft.JolCraft;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeIds;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyRewardRecipe;
import net.sievert.jolcraft.world.recipe.custom.bounty.BountyTaskRecipe;
import net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron.FermentingCauldronRecipe;
import net.sievert.jolcraft.world.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.world.recipe.custom.hand.HandInteractionRecipe;
import net.sievert.jolcraft.world.recipe.custom.lapidary_bench.LapidaryBenchRecipe;
import net.sievert.jolcraft.world.recipe.custom.vanilla.AttributeSmithingTrimRecipe;
import net.sievert.jolcraft.world.recipe.custom.vanilla.ComponentPreservingShapelessRecipe;

public final class JolCraftRecipes {

    private JolCraftRecipes(){}

    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, JolCraft.MOD_ID);

    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, JolCraft.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<AttributeSmithingTrimRecipe>> ATTRIBUTE_SMITHING_TRIM_SERIALIZER =
            SERIALIZERS.register(JolCraftRecipeIds.ATTRIBUTE_SMITHING_TRIM, AttributeSmithingTrimRecipe.Serializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<CraftingRecipe>> COMPONENT_PRESERVING_SHAPELESS_SERIALIZER =
            SERIALIZERS.register(JolCraftRecipeIds.COMPONENT_PRESERVING_SHAPELESS, ComponentPreservingShapelessRecipe.Serializer::new);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<HandInteractionRecipe>> HAND_INTERACTION_SERIALIZER =
            SERIALIZERS.register(JolCraftRecipeIds.HAND_INTERACTION, HandInteractionRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<HandInteractionRecipe>> HAND_INTERACTION_TYPE =
            TYPES.register(JolCraftRecipeIds.HAND_INTERACTION, () -> simpleType(JolCraftRecipeIds.HAND_INTERACTION));

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

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued {} recipe types",
                TYPES.getEntries().size()
        );
    }
}
