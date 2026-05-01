package net.sievert.jolcraft.datagen.recipe.subprovider.hand;

import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.biome.Biomes;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.world.recipe.custom.base.ItemIngredientAction;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.custom.HandInteractionRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.condition.ConditionsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.condition.custom.*;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item.ItemInputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.base.OutputsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.EffectOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.SoundOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.TextOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.entity.EntityOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.entity.EntitySpawnConfigBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.entity.EntitySpecBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.ItemOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.particle.ParticleOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.pool.PoolBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.pool.PoolEntryBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.quantity.IntRangeBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("deprecation")
public record TestHandInteractions(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public TestHandInteractions(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
        this.parent = parent;
    }

    @Override
    public @NotNull JolCraftDataProvider<RecipeOutput> parent() {
        return parent;
    }

    @Override
    public @NotNull String id() {
        return folder();
    }

    @Override
    public @NotNull String folder() {
        return JolCraftDictionary.TEST;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
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
        emit(output, tracking,
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

                                        // -------------------------
                                        // ITEM OUTPUTS
                                        // -------------------------

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

                                        // -------------------------
                                        // ENTITY OUTPUT
                                        // -------------------------

                                        .wrapSingle(
                                                EntityOutputBuilder.builder()
                                                        .result(
                                                                Objects.requireNonNull(
                                                                        EntitySpecBuilder.builder()
                                                                                .entity(EntityType.COW.builtInRegistryHolder())
                                                                                .countFixed(1)
                                                                                .name(
                                                                                        Component.literal("Half Moo")
                                                                                                .withStyle(ChatFormatting.GOLD)
                                                                                )
                                                                                .nameVisible(true)
                                                                                .attribute(Attributes.MAX_HEALTH, 10.0)
                                                                                .spawn(
                                                                                        EntitySpawnConfigBuilder.builder()
                                                                                                .offset(0, 1, 0)
                                                                                                .persistent(true)
                                                                                                .buildOrNull()
                                                                                )
                                                                                .buildOrNull()
                                                                )
                                                        )
                                                        .buildOrNull()
                                        )

                                        // -------------------------
                                        // PARTICLE OUTPUT
                                        // -------------------------

                                        .wrapSingle(
                                                ParticleOutputBuilder.builder()
                                                        .lookups(lookups)
                                                        .particle(ParticleTypes.HAPPY_VILLAGER)
                                                        .count(IntRangeBuilder.between(4, 8))
                                                        .speed(0.2)
                                                        .offset(0.0, 2.5, 0.0)
                                                        .spread(0.25, 0.05, 0.25)
                                                        .build()
                                        )

                                        // -------------------------
                                        // EFFECT OUTPUT
                                        // -------------------------

                                        .wrapSingle(
                                                EffectOutputBuilder.builder()
                                                        .id(MobEffects.REGENERATION)
                                                        .duration(200)
                                                        .amplifier(0)
                                                        .targetPlayer()
                                                        .build()
                                        )

                                        // -------------------------
                                        // TEXT OUTPUT
                                        // -------------------------

                                        .wrapSingle(
                                                TextOutputBuilder.builder()
                                                        .text("Test success")
                                                        .addStyle(ChatFormatting.GREEN)
                                                        .build()
                                                        .getOrThrow()
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
        emit(output, tracking,
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
        emit(output, tracking,
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
        emit(output, tracking,
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