package net.sievert.jolcraft.datagen.recipe.subprovider;

import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.data.id.block.JolCraftBlockIds;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;
import net.sievert.jolcraft.datagen.base.builder.JolCraftDataLookups;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.recipe.RecipeSubProvider;
import net.sievert.jolcraft.datagen.recipe.builder.MortarRecipeBuilder;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.custom.alchemy.EssenceType;
import org.jetbrains.annotations.NotNull;

public record MortarRecipesSubProvider(
        JolCraftDataProvider<RecipeOutput> parent
) implements RecipeSubProvider {

    public MortarRecipesSubProvider(
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
        return JolCraftBlockIds.MORTAR;
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
                MortarRecipeBuilder.create()
                        .input(JolCraftBlocks.FESTERLING)
                        .essenceOutput(EssenceType.INFUSED)
                        .grindingWork(100)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .essenceInput(EssenceType.INFUSED)
                        .input(JolCraftBlocks.CYANELLA)
                        .essenceOutput(EssenceType.REFINED)
                        .grindingWork(200)
                        .toolDamage(2)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .essenceInput(EssenceType.REFINED)
                        .input(JolCraftItems.VITRIOL)
                        .input(Items.CHORUS_FRUIT)
                        .essenceOutput(EssenceType.EXALTED)
                        .grindingWork(300)
                        .toolDamage(3)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .essenceInput(EssenceType.EXALTED)
                        .input(JolCraftItems.BLOODROOT)
                        .input(JolCraftItems.INVERIX)
                        .essenceOutput(EssenceType.CORRUPTED)
                        .grindingWork(400)
                        .toolDamage(4)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(JolCraftItems.MUFFHORN_MILK_BUCKET)
                        .input(Items.CHARCOAL, 3)
                        .input(Items.BONE_MEAL, 5)
                        .result(JolCraftItems.INVERIX, 3)
                        .grindingWork(50)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(JolCraftItems.MUFFHORN_MILK_BUCKET)
                        .input(Items.COAL, 3)
                        .input(Items.BONE_MEAL, 5)
                        .result(JolCraftItems.INVERIX, 3)
                        .grindingWork(50)
                        .toolDamage(1)
                        .buildValidated()
        );

        //Vanilla extras

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.BONE)
                        .result(Items.BONE_MEAL, 5)
                        .grindingWork(200)
                        .toolDamage(2)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.BREEZE_ROD)
                        .result(Items.WIND_CHARGE, 8)
                        .grindingWork(200)
                        .toolDamage(2)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.BLAZE_ROD)
                        .result(Items.BLAZE_POWDER, 3)
                        .grindingWork(300)
                        .toolDamage(3)
                        .buildValidated()
        );

        //Dyes

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.LILY_OF_THE_VALLEY)
                        .result(Items.WHITE_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.BONE_MEAL)
                        .result(Items.WHITE_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.COCOA_BEANS)
                        .result(Items.BROWN_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.ORANGE_TULIP)
                        .result(Items.ORANGE_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.TORCHFLOWER)
                        .result(Items.ORANGE_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.SEA_PICKLE)
                        .result(Items.LIME_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.PITCHER_PLANT)
                        .result(Items.CYAN_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.LAPIS_LAZULI)
                        .result(Items.BLUE_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.CORNFLOWER)
                        .result(Items.BLUE_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.ALLIUM)
                        .result(Items.MAGENTA_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.LILAC)
                        .result(Items.MAGENTA_DYE, 4)
                        .grindingWork(20)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.AZURE_BLUET)
                        .result(Items.LIGHT_GRAY_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.OXEYE_DAISY)
                        .result(Items.LIGHT_GRAY_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.WHITE_TULIP)
                        .result(Items.LIGHT_GRAY_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.INK_SAC)
                        .result(Items.BLACK_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.WITHER_ROSE)
                        .result(Items.BLACK_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.POPPY)
                        .result(Items.RED_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.RED_TULIP)
                        .result(Items.RED_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.BEETROOT)
                        .result(Items.RED_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.ROSE_BUSH)
                        .result(Items.RED_DYE, 4)
                        .grindingWork(20)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.DANDELION)
                        .result(Items.YELLOW_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.SUNFLOWER)
                        .result(Items.YELLOW_DYE, 4)
                        .grindingWork(20)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.CACTUS)
                        .result(Items.GREEN_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.BLUE_ORCHID)
                        .result(Items.LIGHT_BLUE_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.PINK_TULIP)
                        .result(Items.PINK_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );

        emit(
                output,
                tracking,
                MortarRecipeBuilder.create()
                        .input(Items.PINK_PETALS)
                        .result(Items.PINK_DYE, 2)
                        .grindingWork(10)
                        .toolDamage(1)
                        .buildValidated()
        );
    }
}