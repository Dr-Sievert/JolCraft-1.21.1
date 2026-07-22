package net.sievert.jolcraft.datagen.recipe.subprovider.hand;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeHookIds;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.HandInteractionRecipeBuilder;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.recipe.base.input.ItemInputAction;
import net.sievert.jolcraft.world.recipe.base.input.ItemInput;
import net.sievert.jolcraft.world.recipe.base.output.custom.ItemOutput;
import net.sievert.jolcraft.world.recipe.base.output.custom.SoundOutput;
import org.jetbrains.annotations.NotNull;

import static net.minecraft.sounds.SoundEvents.METAL_HIT;
import static net.minecraft.sounds.SoundSource.PLAYERS;
import static net.minecraft.world.level.storage.loot.providers.number.ConstantValue.exactly;

public record CompassHandInteractions(
        JolCraftDataProvider<RecipeOutput> parent
) implements RecipeSubProvider {

    public CompassHandInteractions(
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
        return JolCraftItemIds.DEEPSLATE_COMPASS;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeOutput output,
            @NotNull JolCraftDataLookups lookups,
            @NotNull JolCraftDataTracking tracking
    ) {
        emit(
                output,
                tracking,
                HandInteractionRecipeBuilder.create()
                        .id("assemble")
                        .ingredientA(
                                ItemInput.item(
                                        JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get()
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
                                        JolCraftItems.DEEPSLATE_COMPASS_DIAL.get()
                                )
                        )
                        .actionB(
                                new ItemInputAction(
                                        ItemInputAction.Type.CONSUME,
                                        1
                                )
                        )
                        .output(
                                ItemOutput.item(
                                        LootItem.lootTableItem(
                                                JolCraftItems.DEEPSLATE_COMPASS.get()
                                        )
                                ).applyHooks(
                                        JolCraft.location(
                                                JolCraftRecipeHookIds.DEEPSLATE_COMPASS
                                        )
                                )
                        )
                        .successSound(
                                SoundOutput.sound(
                                        METAL_HIT,
                                        PLAYERS,
                                        exactly(1.0F),
                                        exactly(1.4F)
                                )
                        )
                        .failSound(
                                SoundOutput.sound(
                                        METAL_HIT,
                                        PLAYERS,
                                        exactly(0.4F),
                                        exactly(1.6F)
                                )
                        )
                        .requireSneaking(false)
                        .buildValidated()
        );
    }
}