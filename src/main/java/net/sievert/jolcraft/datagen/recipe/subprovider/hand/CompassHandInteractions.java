package net.sievert.jolcraft.datagen.recipe.subprovider.hand;

import net.minecraft.core.HolderGetter;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.recipe.JolCraftRecipeHookIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.recipe.custom.base.ItemIngredientAction;
import net.sievert.jolcraft.data.recipe.custom.hand.HandInteractionRecipe;
import net.sievert.jolcraft.data.recipe.param.output.custom.SoundOutput;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.bridge.RecipeEmissionExecutor;
import net.sievert.jolcraft.datagen.recipe.build.custom.HandInteractionRecipeBuilder;
import net.sievert.jolcraft.datagen.recipe.build.param.input.item.ItemInputBuilder;
import net.sievert.jolcraft.datagen.recipe.build.param.output.base.OutputsBuilder;
import net.sievert.jolcraft.datagen.recipe.build.param.output.custom.SoundOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.build.param.output.custom.item.ItemOutputBuilder;
import net.sievert.jolcraft.datagen.recipe.build.param.output.custom.item.transform.ComponentTransformBuilder;
import net.sievert.jolcraft.datagen.recipe.build.param.output.custom.item.transform.ItemTransformsBuilder;
import net.sievert.jolcraft.datagen.recipe.build.param.output.hook.HookBuilder;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class CompassHandInteractions implements RecipeSubProvider {

    @Override
    public @NotNull String folder() {
        return JolCraftDictionary.MISC;
    }

    @Override
    public void registerRecipes(
            @NotNull RecipeEmissionExecutor executor,
            @NotNull RecipeOutput output,
            @NotNull HolderGetter<Item> items
    ) {
        executor.emit(
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
                        .failSound(SoundOutput.EMPTY)
                        .requireSneaking(false)
                        .buildValidated()
        );
    }
}