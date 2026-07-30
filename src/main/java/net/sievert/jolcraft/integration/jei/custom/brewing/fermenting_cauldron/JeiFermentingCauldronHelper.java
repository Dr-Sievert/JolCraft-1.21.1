package net.sievert.jolcraft.integration.jei.custom.brewing.fermenting_cauldron;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.integration.jei.util.fluid.JeiBrewingFluids;
import net.sievert.jolcraft.integration.jei.util.recipe.ItemInputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeAccess;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.registry.JolCraftBrewingItems;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.custom.fermenting_cauldron.FermentingCauldronRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class JeiFermentingCauldronHelper {

    private static final ResourceLocation BREW_MUG_EXTRACTION_ID =
            JolCraft.location(
                    "jei/fermenting_cauldron/extract_brew_mug"
            );

    private static final ResourceLocation BREW_BUCKET_EXTRACTION_ID =
            JolCraft.location(
                    "jei/fermenting_cauldron/extract_brew_bucket"
            );

    private static final ResourceLocation YEAST_BOTTLE_EXTRACTION_ID =
            JolCraft.location(
                    "jei/fermenting_cauldron/extract_yeast_bottle"
            );

    private JeiFermentingCauldronHelper() {
    }

    public static @NotNull List<JeiFermentingCauldronRecipe> getRecipes() {
        if (!JeiRecipeAccess.isAvailable()) {
            return List.of();
        }

        List<JeiFermentingCauldronRecipe> result =
                new ArrayList<>();

        result.addAll(
                JeiRecipeAccess.translateSorted(
                        JolCraftRecipes
                                .FERMENTING_CAULDRON_TYPE
                                .get(),
                        holder ->
                                List.of(
                                        translate(
                                                holder
                                        )
                                )
                )
        );

        addExtractionRecipes(
                result
        );

        return List.copyOf(
                result
        );
    }

    private static @NotNull JeiFermentingCauldronRecipe translate(
            @NotNull RecipeHolder<FermentingCauldronRecipe> holder
    ) {
        FermentingCauldronRecipe recipe =
                holder.value();

        boolean usesUnfinishedBrewInput =
                recipe.effect().isPresent()
                        || (
                        recipe.outputFluid()
                                == FermentingCauldronRecipe.OutputFluid.DWARVEN_BREW
                                && recipe.finalizeBrew()
                                && recipe.lastIngredient().isPresent()
                );

        JeiFermentingCauldronRecipe.PreviousInput previousInput =
                usesUnfinishedBrewInput
                        ? new JeiFermentingCauldronRecipe.FluidInput(
                        JeiBrewingFluids.unfinishedDwarvenBrew()
                )
                        : recipe.lastIngredient()
                        .<JeiFermentingCauldronRecipe.PreviousInput>map(
                                input ->
                                        new JeiFermentingCauldronRecipe.ItemInput(
                                                ItemInputJeiTranslator.translate(
                                                        input
                                                )
                                        )
                        )
                        .orElseGet(
                                () ->
                                        new JeiFermentingCauldronRecipe.FluidInput(
                                                new FluidStack(
                                                        Fluids.WATER,
                                                        FluidType.BUCKET_VOLUME
                                                )
                                        )
                        );

        return new JeiFermentingCauldronRecipe(
                holder.id(),
                previousInput,
                ItemInputJeiTranslator.translate(
                        recipe.ingredient()
                ),
                new JeiFermentingCauldronRecipe.FluidResult(
                        createRecipeOutputFluid(
                                recipe
                        )
                ),
                recipe.brewTicks()
        );
    }

    private static @NotNull FluidStack createRecipeOutputFluid(
            @NotNull FermentingCauldronRecipe recipe
    ) {
        FluidStack result =
                switch (recipe.outputFluid()) {
                    case DWARVEN_BREW ->
                            recipe.finalizeBrew()
                                    ? JeiBrewingFluids.dwarvenBrew()
                                    : JeiBrewingFluids.unfinishedDwarvenBrew();

                    case YEAST ->
                            recipe.finalizeBrew()
                                    ? JeiBrewingFluids.yeast()
                                    : JeiBrewingFluids.unfinishedYeast();
                };

        recipe.effect()
                .ifPresent(
                        effect ->
                                result.set(
                                        DataComponents.POTION_CONTENTS,
                                        result.getOrDefault(
                                                        DataComponents.POTION_CONTENTS,
                                                        PotionContents.EMPTY
                                                )
                                                .withEffectAdded(
                                                        effect.effect()
                                                )
                                )
                );

        return result;
    }

    private static void addExtractionRecipes(
            @NotNull List<JeiFermentingCauldronRecipe> result
    ) {
        result.add(
                new JeiFermentingCauldronRecipe(
                        BREW_MUG_EXTRACTION_ID,
                        new JeiFermentingCauldronRecipe.FluidInput(
                                JeiBrewingFluids.dwarvenBrewMug()
                        ),
                        List.of(
                                new ItemStack(
                                        JolCraftItems.GLASS_MUG.get()
                                )
                        ),
                        new JeiFermentingCauldronRecipe.ItemResult(
                                List.of(
                                        JeiBrewingFluids.dwarvenBrewItem()
                                )
                        ),
                        0
                )
        );

        result.add(
                new JeiFermentingCauldronRecipe(
                        BREW_BUCKET_EXTRACTION_ID,
                        new JeiFermentingCauldronRecipe.FluidInput(
                                JeiBrewingFluids.dwarvenBrew()
                        ),
                        List.of(
                                new ItemStack(
                                        Items.BUCKET
                                )
                        ),
                        new JeiFermentingCauldronRecipe.ItemResult(
                                List.of(
                                        new ItemStack(
                                                JolCraftItems.DWARVEN_BREW_BUCKET.get()
                                        )
                                )
                        ),
                        0
                )
        );

        result.add(
                new JeiFermentingCauldronRecipe(
                        YEAST_BOTTLE_EXTRACTION_ID,
                        new JeiFermentingCauldronRecipe.FluidInput(
                                JeiBrewingFluids.yeast(
                                        JolCraftBrewingItems.YEAST_BOTTLE_VOLUME
                                )
                        ),
                        List.of(
                                new ItemStack(
                                        Items.GLASS_BOTTLE
                                )
                        ),
                        new JeiFermentingCauldronRecipe.ItemResult(
                                List.of(
                                        new ItemStack(
                                                JolCraftItems.YEAST.get()
                                        )
                                )
                        ),
                        0
                )
        );
    }
}