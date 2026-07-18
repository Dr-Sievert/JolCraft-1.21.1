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
import net.sievert.jolcraft.world.recipe.base.input.ItemInputAction;
import net.sievert.jolcraft.world.recipe.base.input.ItemInput;
import net.sievert.jolcraft.world.recipe.base.output.custom.EffectOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.EntityOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.SoundOutput;
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
                                new ItemInputAction(
                                        ItemInputAction.Type.CONSUME,
                                        1
                                )
                        )
                        .ingredientB(
                                ItemInput.item(
                                        Items.BIRCH_LOG
                                )
                        )
                        .actionB(
                                new ItemInputAction(
                                        ItemInputAction.Type.CONSUME,
                                        1
                                )
                        )

                        // Item outputs
                        .output(
                                ItemOutput.item(
                                        LootItem.lootTableItem(
                                                Items.DIAMOND
                                        )
                                )
                        )

                        .output(
                                ItemOutput.item(
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

                        // Interaction result sounds
                        .successSound(
                                SoundOutput.sound(
                                        SoundEvents.EXPERIENCE_ORB_PICKUP,
                                        SoundSource.PLAYERS,
                                        ConstantValue.exactly(1.0F),
                                        ConstantValue.exactly(1.0F)
                                )
                        )
                        .failSound(
                                SoundOutput.sound(
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