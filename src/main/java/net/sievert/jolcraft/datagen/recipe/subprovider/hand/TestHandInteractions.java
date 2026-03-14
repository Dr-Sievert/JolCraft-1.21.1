package net.sievert.jolcraft.datagen.recipe.subprovider.hand;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.custom.base.ItemIngredientAction;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.builder.base.RecipeLookups;
import net.sievert.jolcraft.datagen.recipe.builder.custom.HandInteractionRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.condition.ConditionsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.condition.custom.*;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item.ItemInputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.base.OutputsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.SoundOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.ItemOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.pool.PoolBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.pool.PoolEntryBuilder;
import org.jetbrains.annotations.NotNull;

public final class TestHandInteractions implements RecipeSubProvider {

    @Override
    public @NotNull String folder() {
        return JolCraftDictionary.TEST;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull RecipeLookups lookups
    ) {
        
        ConditionsBuilder condition = ConditionsBuilder.create().condition(
                BiomeConditionBuilder.create().lookups(lookups).biome(Biomes.PLAINS)
        );

        /*
         * SAME IDEA, FOUR GATE SCOPES:
         *
         * 1) top-level Outputs gate
         *    - whole results param is disabled if condition fails
         *
         * 2) pool gate
         *    - pool is removed before rolling
         *
         * 3) pool-entry gate
         *    - entry is removed before weighted selection
         *
         * 4) output gate
         *    - output is selected/resolved, but can still produce nothing
         *
         * These use slightly different inputs only so recipe file names do not collide.
         * You said you will manually change the inputs, so just swap them however you want.
         */

        // -----------------------------------------------------------------
        // 1) TOP-LEVEL RESULTS GATE
        // Shape goal:
        // "results": {
        //   "biome": "minecraft:condition",
        //   ...
        // }
        // -----------------------------------------------------------------
        executor.emit(
                HandInteractionRecipeBuilder.create()
                        .ingredientA(
                                ItemInputBuilder.create()
                                        .item(Items.OAK_LOG)
                                        .build()
                        )
                        .actionA(new ItemIngredientAction(ItemIngredientAction.Type.CONSUME, 1))
                        .ingredientB(
                                ItemInputBuilder.create()
                                        .item(Items.OAK_LOG)
                                        .build()
                        )
                        .actionB(new ItemIngredientAction(ItemIngredientAction.Type.CONSUME, 1))
                        .output(
                                OutputsBuilder.create()
                                        .conditions(condition)
                                        .wrapSingle(
                                                ItemOutputBuilder.create()
                                                        .result(Items.DIAMOND, 1)
                                                        .build()
                                        )
                                        .wrapSingle(
                                                ItemOutputBuilder.create()
                                                        .result(Items.EMERALD, 1)
                                                        .build()
                                        )
                                        .build()
                        )
                        .successSound(
                                SoundOutputBuilder.create()
                                        .sound(SoundEvents.EXPERIENCE_ORB_PICKUP)
                                        .volume(1.0F)
                                        .pitch(1.0F)
                                        .build()
                        )
                        .failSound(
                                SoundOutputBuilder.create()
                                        .sound(SoundEvents.VILLAGER_NO)
                                        .volume(0.8F)
                                        .pitch(1.0F)
                                        .build()
                        )
                        .requireSneaking(false)
                        .buildValidated()
        );

        // -----------------------------------------------------------------
        // 2) POOL GATE
        // Shape goal:
        // "results": [
        //   {
        //     "biome": "minecraft:condition",
        //     "entries": [ ... ]
        //   }
        // ]
        //
        // Added emerald as a second pool with no gate.
        // -----------------------------------------------------------------
        executor.emit(
                HandInteractionRecipeBuilder.create()
                        .ingredientA(
                                ItemInputBuilder.create()
                                        .item(Items.BIRCH_LOG)
                                        .build()
                        )
                        .actionA(new ItemIngredientAction(ItemIngredientAction.Type.CONSUME, 1))
                        .ingredientB(
                                ItemInputBuilder.create()
                                        .item(Items.BIRCH_LOG)
                                        .build()
                        )
                        .actionB(new ItemIngredientAction(ItemIngredientAction.Type.CONSUME, 1))
                        .output(
                                OutputsBuilder.create()
                                        .pool(
                                                PoolBuilder.create()
                                                        .conditions(condition)
                                                        .entry(
                                                                PoolEntryBuilder.create()
                                                                        .output(
                                                                                ItemOutputBuilder.create()
                                                                                        .result(Items.DIAMOND, 1)
                                                                        )
                                                                        .weight(1)
                                                        )
                                        )
                                        .pool(
                                                PoolBuilder.create()
                                                        .entry(
                                                                PoolEntryBuilder.create()
                                                                        .output(
                                                                                ItemOutputBuilder.create()
                                                                                        .result(Items.EMERALD, 1)
                                                                        )
                                                                        .weight(1)
                                                        )
                                        )
                                        .build()
                        )
                        .successSound(
                                SoundOutputBuilder.create()
                                        .sound(SoundEvents.EXPERIENCE_ORB_PICKUP)
                                        .volume(1.0F)
                                        .pitch(1.0F)
                                        .build()
                        )
                        .failSound(
                                SoundOutputBuilder.create()
                                        .sound(SoundEvents.VILLAGER_NO)
                                        .volume(0.8F)
                                        .pitch(1.0F)
                                        .build()
                        )
                        .requireSneaking(false)
                        .buildValidated()
        );

        // -----------------------------------------------------------------
        // 3) POOL ENTRY GATE
        // Shape goal:
        // "results": [
        //   [
        //     {
        //       "biome": "minecraft:condition",
        //       "type": "jolcraft:item_output",
        //       "item": "minecraft:diamond"
        //     },
        //     {
        //       "type": "jolcraft:item_output",
        //       "item": "minecraft:emerald"
        //     }
        //   ]
        // ]
        //
        // Diamond entry is gated. Emerald entry is always present in same pool.
        // -----------------------------------------------------------------
        executor.emit(
                HandInteractionRecipeBuilder.create()
                        .ingredientA(
                                ItemInputBuilder.create()
                                        .item(Items.SPRUCE_LOG)
                                        .build()
                        )
                        .actionA(new ItemIngredientAction(ItemIngredientAction.Type.CONSUME, 1))
                        .ingredientB(
                                ItemInputBuilder.create()
                                        .item(Items.SPRUCE_LOG)
                                        .build()
                        )
                        .actionB(new ItemIngredientAction(ItemIngredientAction.Type.CONSUME, 1))
                        .output(
                                OutputsBuilder.create()
                                        .pool(
                                                PoolBuilder.create()
                                                        .entry(
                                                                PoolEntryBuilder.create()
                                                                        .conditions(condition)
                                                                        .output(
                                                                                ItemOutputBuilder.create()
                                                                                        .result(Items.DIAMOND, 1)
                                                                        )
                                                                        .weight(1)
                                                        )
                                                        .entry(
                                                                PoolEntryBuilder.create()
                                                                        .output(
                                                                                ItemOutputBuilder.create()
                                                                                        .result(Items.EMERALD, 1)
                                                                        )
                                                                        .weight(1)
                                                        )
                                        )
                                        .build()
                        )
                        .successSound(
                                SoundOutputBuilder.create()
                                        .sound(SoundEvents.EXPERIENCE_ORB_PICKUP)
                                        .volume(1.0F)
                                        .pitch(1.0F)
                                        .build()
                        )
                        .failSound(
                                SoundOutputBuilder.create()
                                        .sound(SoundEvents.VILLAGER_NO)
                                        .volume(0.8F)
                                        .pitch(1.0F)
                                        .build()
                        )
                        .requireSneaking(false)
                        .buildValidated()
        );

// -----------------------------------------------------------------
// 4) OUTPUT PARAM GATE
// Shape goal:
// diamond is leaf-gated, emerald is ungated in the same pool
// -----------------------------------------------------------------
        executor.emit(
                HandInteractionRecipeBuilder.create()
                        .ingredientA(
                                ItemInputBuilder.create()
                                        .item(Items.JUNGLE_LOG)
                                        .build()
                        )
                        .actionA(new ItemIngredientAction(ItemIngredientAction.Type.CONSUME, 1))
                        .ingredientB(
                                ItemInputBuilder.create()
                                        .item(Items.JUNGLE_LOG)
                                        .build()
                        )
                        .actionB(new ItemIngredientAction(ItemIngredientAction.Type.CONSUME, 1))
                        .output(
                                OutputsBuilder.create()
                                        .pool(
                                                PoolBuilder.create()
                                                        .entry(
                                                                PoolEntryBuilder.create()
                                                                        .output(
                                                                                ItemOutputBuilder.create()
                                                                                        .conditions(condition)
                                                                                        .result(Items.DIAMOND, 1)
                                                                                        .buildParam()
                                                                        )
                                                                        .weight(1)
                                                        )
                                                        .entry(
                                                                PoolEntryBuilder.create()
                                                                        .output(
                                                                                ItemOutputBuilder.create()
                                                                                        .result(Items.EMERALD, 1)
                                                                                        .buildParam()
                                                                        )
                                                                        .weight(1)
                                                        )
                                        )
                                        .build()
                        )
                        .successSound(
                                SoundOutputBuilder.create()
                                        .sound(SoundEvents.EXPERIENCE_ORB_PICKUP)
                                        .volume(1.0F)
                                        .pitch(1.0F)
                                        .build()
                        )
                        .failSound(
                                SoundOutputBuilder.create()
                                        .sound(SoundEvents.VILLAGER_NO)
                                        .volume(0.8F)
                                        .pitch(1.0F)
                                        .build()
                        )
                        .requireSneaking(false)
                        .buildValidated()
        );
    }
}