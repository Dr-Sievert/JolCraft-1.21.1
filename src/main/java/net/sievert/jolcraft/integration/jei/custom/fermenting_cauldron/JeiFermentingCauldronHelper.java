package net.sievert.jolcraft.integration.jei.custom.fermenting_cauldron;

import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.sievert.jolcraft.world.item.registry.JolCraftBrewingItems;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.integration.jei.util.recipe.ItemInputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeAccess;
import net.sievert.jolcraft.world.block.fluid.JolCraftFluids;
import net.sievert.jolcraft.world.block.fluid.util.brewing.BrewingColors;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
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
                        holder -> List.of(
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

        JeiFermentingCauldronRecipe.PreviousInput previousInput =
                recipe.lastIngredient()
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

        JeiFermentingCauldronRecipe.Result result =
                recipe.effect()
                        .<JeiFermentingCauldronRecipe.Result>map(
                                effect ->
                                        new JeiFermentingCauldronRecipe.EffectResult(
                                                effect.effect()
                                        )
                        )
                        .orElseGet(
                                () ->
                                        new JeiFermentingCauldronRecipe.FluidResult(
                                                createRecipeOutputFluid(
                                                        recipe
                                                )
                                        )
                        );

        return new JeiFermentingCauldronRecipe(
                holder.id(),
                previousInput,
                ItemInputJeiTranslator.translate(
                        recipe.ingredient()
                ),
                result
        );
    }

    private static @NotNull FluidStack createRecipeOutputFluid(
            @NotNull FermentingCauldronRecipe recipe
    ) {
        return switch (recipe.outputFluid()) {
            case DWARVEN_BREW -> createBrewFluid(
                    recipe.finalizeBrew()
                            ? JolCraftFluids.DWARVEN_BREW.get()
                            : JolCraftFluids.UNFINISHED_DWARVEN_BREW.get(),
                    recipe.finalizeBrew()
                            ? BrewingColors.DWARVEN_BREW
                            : BrewingColors.UNFINISHED_DWARVEN_BREW
            );

            case YEAST -> createYeastFluid(
                    recipe.finalizeBrew()
                            ? JolCraftFluids.YEAST.get()
                            : JolCraftFluids.UNFINISHED_YEAST.get(),
                    recipe.finalizeBrew()
                            ? BrewingColors.YEAST
                            : BrewingColors.UNFINISHED_YEAST
            );
        };
    }

    private static @NotNull FluidStack createBrewFluid(
            @NotNull Fluid fluid,
            int color
    ) {
        FluidStack result =
                createColoredFluid(
                        fluid,
                        color
                );

        result.set(
                DataComponents.POTION_CONTENTS,
                PotionContents.EMPTY
        );

        if (fluid == JolCraftFluids.DWARVEN_BREW.get()) {
            result.set(
                    JolCraftDataComponents.BREW_AGE.get(),
                    0L
            );
        }

        return result;
    }

    private static @NotNull FluidStack createYeastFluid(
            @NotNull Fluid fluid,
            int color
    ) {
        return createColoredFluid(
                fluid,
                color
        );
    }

    private static @NotNull FluidStack createColoredFluid(
            @NotNull Fluid fluid,
            int color
    ) {
        FluidStack result =
                new FluidStack(
                        fluid,
                        FluidType.BUCKET_VOLUME
                );

        result.set(
                JolCraftDataComponents.BREW_COLOR.get(),
                color
        );

        return result;
    }

    private static void addExtractionRecipes(
            @NotNull List<JeiFermentingCauldronRecipe> result
    ) {
        FluidStack brew =
                createBrewFluid(
                        JolCraftFluids.DWARVEN_BREW.get(),
                        BrewingColors.DWARVEN_BREW
                );

        FluidStack yeast =
                createYeastFluid(
                        JolCraftFluids.YEAST.get(),
                        BrewingColors.YEAST
                );

        result.add(
                new JeiFermentingCauldronRecipe(
                        BREW_MUG_EXTRACTION_ID,
                        new JeiFermentingCauldronRecipe.FluidInput(
                                withAmount(
                                        brew,
                                        DwarvenBrewFluidHelper.MUG_VOLUME
                                )
                        ),
                        List.of(
                                new ItemStack(
                                        JolCraftItems.GLASS_MUG.get()
                                )
                        ),
                        new JeiFermentingCauldronRecipe.ItemResult(
                                List.of(
                                        new ItemStack(
                                                JolCraftItems.DWARVEN_BREW.get()
                                        )
                                )
                        )
                )
        );

        result.add(
                new JeiFermentingCauldronRecipe(
                        BREW_BUCKET_EXTRACTION_ID,
                        new JeiFermentingCauldronRecipe.FluidInput(
                                brew
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
                        )
                )
        );

        result.add(
                new JeiFermentingCauldronRecipe(
                        YEAST_BOTTLE_EXTRACTION_ID,
                        new JeiFermentingCauldronRecipe.FluidInput(
                                withAmount(
                                        yeast,
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
                        )
                )
        );
    }

    private static @NotNull FluidStack withAmount(
            @NotNull FluidStack fluid,
            int amount
    ) {
        FluidStack result = fluid.copy();
        result.setAmount(amount);
        return result;
    }
}