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
                        .input(Items.CHORUS_FRUIT)
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
                        .input(Items.GOLD_INGOT)
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
    }
}