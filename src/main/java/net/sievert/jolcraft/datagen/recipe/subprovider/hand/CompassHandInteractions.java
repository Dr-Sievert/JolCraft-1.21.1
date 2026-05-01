package net.sievert.jolcraft.datagen.recipe.subprovider.hand;

import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeHookIds;
import net.sievert.jolcraft.world.recipe.custom.base.ItemIngredientAction;
import net.sievert.jolcraft.world.recipe.custom.hand.HandInteractionRecipe;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.builder.custom.HandInteractionRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.input.custom.item.ItemInputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.base.OutputsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.SoundOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.ItemOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.transform.ComponentTransformBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.custom.item.transform.ItemTransformsBuilder;
import net.sievert.jolcraft.datagen.recipe.builder.param.output.hook.HookBuilder;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record CompassHandInteractions(JolCraftDataProvider<RecipeOutput> parent) implements RecipeSubProvider {

    public CompassHandInteractions(@NotNull JolCraftDataProvider<RecipeOutput> parent) {
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
        emit(output, tracking,
                HandInteractionRecipeBuilder.create()
                        .ingredientA(
                                ItemInputBuilder.create()
                                        .item(JolCraftItems.EMPTY_DEEPSLATE_COMPASS.get())
                                        .build()
                        )
                        .actionA(new ItemIngredientAction(ItemIngredientAction.Type.CONSUME, 1))
                        .ingredientB(
                                ItemInputBuilder.create()
                                        .item(JolCraftItems.DEEPSLATE_COMPASS_DIAL.get())
                                        .build()
                        )
                        .actionB(new ItemIngredientAction(ItemIngredientAction.Type.CONSUME, 1))
                        .output(
                                OutputsBuilder.create()
                                        .wrapSingle(
                                                ItemOutputBuilder.create()
                                                        .result(JolCraftItems.DEEPSLATE_COMPASS.get().asItem(), 1)
                                                        .transforms(
                                                                ItemTransformsBuilder.create()
                                                                        .component(
                                                                                ComponentTransformBuilder.create()
                                                                                        .source(HandInteractionRecipe.SOURCE_INGREDIENT_A)
                                                                                        .removeAll(true)
                                                                                        .keep(BuiltInRegistries.DATA_COMPONENT_TYPE.wrapAsHolder(DataComponents.DYED_COLOR))
                                                                        )
                                                                        .build()
                                                        )
                                                        .build()
                                                        .withHooks(List.of(
                                                                HookBuilder.create()
                                                                        .id(JolCraft.location(JolCraftRecipeHookIds.DEEPSLATE_COMPASS))
                                                                        .build()
                                                        ))
                                        )
                                        .build()
                        )
                        .successSound(
                                SoundOutputBuilder.create()
                                        .sound(SoundEvents.METAL_HIT)
                                        .volume(1.0F)
                                        .pitch(1.4F)
                                        .build()
                        )
                        .failSound(
                                SoundOutputBuilder.create()
                                        .sound(SoundEvents.METAL_HIT)
                                        .volume(0.4F)
                                        .pitch(1.6F)
                                        .build()
                        )
                        .requireSneaking(false)
                        .buildValidated()
        );
    }
}