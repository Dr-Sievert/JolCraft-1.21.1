package net.sievert.jolcraft.datagen.recipe.subprovider.hand;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.HandInteractionRecipeBuilder;
import net.sievert.jolcraft.world.recipe.base.ItemIngredientAction;
import net.sievert.jolcraft.world.recipe.input.ItemInput;
import net.sievert.jolcraft.world.recipe.output.EffectOutput;
import net.sievert.jolcraft.world.recipe.output.EntityOutput;
import net.sievert.jolcraft.world.recipe.output.ItemOutputs;
import net.sievert.jolcraft.world.recipe.output.SoundOutputs;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("SameParameterValue")
public record TestHandInteractions(
        JolCraftDataProvider<RecipeOutput> parent
) implements RecipeSubProvider {

    public TestHandInteractions(
            @NotNull JolCraftDataProvider<RecipeOutput> parent
    ) {
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
        supportedOutputsTest(
                output,
                tracking
        );
    }

    private void supportedOutputsTest(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataTracking tracking
    ) {
        emit(
                output,
                tracking,
                HandInteractionRecipeBuilder.create()
                        .id("supported_outputs")
                        .ingredientA(
                                ItemInput.item(
                                        Items.OAK_LOG
                                )
                        )
                        .actionA(
                                new ItemIngredientAction(
                                        ItemIngredientAction.Type.CONSUME,
                                        1
                                )
                        )
                        .ingredientB(
                                ItemInput.item(
                                        Items.BIRCH_LOG
                                )
                        )
                        .actionB(
                                new ItemIngredientAction(
                                        ItemIngredientAction.Type.CONSUME,
                                        1
                                )
                        )

                        // Item outputs
                        .output(
                                ItemOutputs.item(
                                        LootItem.lootTableItem(
                                                Items.DIAMOND
                                        )
                                )
                        )
                        .output(
                                ItemOutputs.item(
                                        LootItem.lootTableItem(
                                                Items.EMERALD
                                        )
                                )
                        )

                        // Entity output
                        .output(
                                EntityOutput.of(
                                        EntityType.COW
                                )
                        )

                        // Effect output
                        .output(
                                EffectOutput.of(
                                        new MobEffectInstance(
                                                MobEffects.REGENERATION,
                                                200,
                                                0
                                        )
                                )
                        )

                        // Sound output
                        .output(
                                SoundOutputs.sound(
                                        SoundEvents.AMETHYST_BLOCK_CHIME,
                                        SoundSource.PLAYERS,
                                        ConstantValue.exactly(1.0F),
                                        ConstantValue.exactly(1.0F)
                                )
                        )

                        // Interaction result sounds
                        .successSound(
                                SoundOutputs.sound(
                                        SoundEvents.EXPERIENCE_ORB_PICKUP,
                                        SoundSource.PLAYERS,
                                        ConstantValue.exactly(1.0F),
                                        ConstantValue.exactly(1.0F)
                                )
                        )
                        .failSound(
                                SoundOutputs.sound(
                                        SoundEvents.VILLAGER_NO,
                                        SoundSource.PLAYERS,
                                        ConstantValue.exactly(0.8F),
                                        ConstantValue.exactly(1.0F)
                                )
                        )
                        .requireSneaking(false)
                        .buildValidated()
        );
    }
}