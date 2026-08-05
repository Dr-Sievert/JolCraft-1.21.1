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
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;
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

    private static final ResourceLocation TANNIN_BOTTLE_EXTRACTION_ID =
            JolCraft.location(
                    "jei/fermenting_cauldron/extract_tannin_bottle"
            );

    private static final ResourceLocation REFINED_TANNIN_BOTTLE_EXTRACTION_ID =
            JolCraft.location(
                    "jei/fermenting_cauldron/extract_refined_tannin_bottle"
            );

    private JeiFermentingCauldronHelper() {
    }

    public static @NotNull List<JeiFermentingCauldronRecipe> getRecipes() {
        if (!JeiRecipeAccess.isAvailable()) {
            return List.of();
        }

        List<JeiFermentingCauldronRecipe> result =
                new ArrayList<>(JeiRecipeAccess.translateSorted(
                        JolCraftRecipes
                                .FERMENTING_CAULDRON_TYPE
                                .get(),
                        JeiFermentingCauldronHelper::translateAll
                ));

        addExtractionRecipes(
                result
        );

        return List.copyOf(
                result
        );
    }

    private static @NotNull List<JeiFermentingCauldronRecipe> translateAll(
            @NotNull RecipeHolder<FermentingCauldronRecipe> holder
    ) {
        FermentingCauldronRecipe recipe = holder.value();

        if (recipe.outputFluid() == FermentingCauldronRecipe.OutputFluid.YEAST) {
            if (recipe.finalizeBrew()) {
                return DwarvenBrewFluidHelper.BREWING_SPEED_TIERS
                        .stream()
                        .map(speed -> translateYeastFinalize(
                                holder,
                                speed
                        ))
                        .toList();
            }

            if (isYeastCultureIngredient(recipe)) {
                return DwarvenBrewFluidHelper.BREWING_SPEED_TIERS
                        .stream()
                        .map(speed -> translateYeastStart(
                                holder,
                                speed
                        ))
                        .toList();
            }
        }

        if (recipe.outputFluid() == FermentingCauldronRecipe.OutputFluid.TANNIN
                && recipe.finalizeBrew()) {
            return List.of(
                    translateTanninFinalize(
                            holder,
                            DwarvenBrewAge.MATURED,
                            "_regular"
                    ),
                    translateTanninFinalize(
                            holder,
                            DwarvenBrewAge.VINTAGE,
                            "_refined"
                    )
            );
        }

        if (recipe.outputFluid() == FermentingCauldronRecipe.OutputFluid.DWARVEN_BREW) {
            if (recipe.finalizeBrew()
                    && isYeastIngredient(recipe)) {
                return DwarvenBrewFluidHelper.BREWING_SPEED_TIERS
                        .stream()
                        .map(speed -> translateBrewFinalize(
                                holder,
                                speed
                        ))
                        .toList();
            }

            if (!recipe.finalizeBrew()
                    && isTanninIngredient(recipe)) {
                return List.of(
                        translateBrewTannin(
                                holder,
                                DwarvenBrewAge.MATURED,
                                "_regular"
                        ),
                        translateBrewTannin(
                                holder,
                                DwarvenBrewAge.VINTAGE,
                                "_refined"
                        )
                );
            }
        }

        return List.of(
                translate(holder)
        );
    }

    private static boolean isTanninIngredient(
            FermentingCauldronRecipe recipe
    ) {
        return ItemInputJeiTranslator.translate(
                        recipe.ingredient()
                )
                .stream()
                .anyMatch(stack -> stack.is(
                        JolCraftItems.TANNIN.get()
                ));
    }

    private static boolean isYeastCultureIngredient(
            FermentingCauldronRecipe recipe
    ) {
        return ItemInputJeiTranslator.translate(
                        recipe.ingredient()
                )
                .stream()
                .anyMatch(stack -> stack.is(
                        JolCraftItems.YEAST_CULTURE.get()
                ));
    }

    private static boolean isYeastIngredient(
            FermentingCauldronRecipe recipe
    ) {
        return ItemInputJeiTranslator.translate(
                        recipe.ingredient()
                )
                .stream()
                .anyMatch(stack -> stack.is(
                        JolCraftItems.YEAST.get()
                ));
    }

    private static @NotNull JeiFermentingCauldronRecipe translateBrewFinalize(
            @NotNull RecipeHolder<FermentingCauldronRecipe> holder,
            float brewingSpeed
    ) {
        FermentingCauldronRecipe recipe = holder.value();

        return new JeiFermentingCauldronRecipe(
                holder.id().withSuffix(
                        yeastSpeedSuffix(
                                brewingSpeed
                        )
                ),
                new JeiFermentingCauldronRecipe.FluidInput(
                        strengthPreviewUnfinishedBrew()
                ),
                List.of(
                        JeiBrewingFluids.yeastItem(
                                brewingSpeed
                        )
                ),
                new JeiFermentingCauldronRecipe.FluidResult(
                        JeiBrewingFluids.dwarvenBrew(
                                FluidType.BUCKET_VOLUME,
                                DwarvenBrewAge.FRESH,
                                DwarvenBrewFluidHelper.DEFAULT_MAX_AGE,
                                brewingSpeed,
                                PotionContents.EMPTY.withEffectAdded(
                                        JeiBrewingFluids.displayStrengthEffect()
                                )
                        )
                ),
                scaledBrewTicks(
                        recipe.brewTicks(),
                        brewingSpeed
                )
        );
    }

    private static FluidStack strengthPreviewUnfinishedBrew() {
        FluidStack fluid = JeiBrewingFluids.unfinishedDwarvenBrew();

        fluid.set(
                DataComponents.POTION_CONTENTS,
                PotionContents.EMPTY.withEffectAdded(
                        JeiBrewingFluids.displayStrengthEffect()
                )
        );

        return fluid;
    }

    private static int scaledBrewTicks(
            int brewTicks,
            float brewingSpeed
    ) {
        return Math.max(
                1,
                (int) Math.ceil(
                        brewTicks
                                / (double) brewingSpeed
                )
        );
    }

    private static @NotNull JeiFermentingCauldronRecipe translateBrewTannin(
            @NotNull RecipeHolder<FermentingCauldronRecipe> holder,
            @NotNull DwarvenBrewAge maxAge,
            @NotNull String idSuffix
    ) {
        return new JeiFermentingCauldronRecipe(
                holder.id().withSuffix(idSuffix),
                new JeiFermentingCauldronRecipe.FluidInput(
                        JeiBrewingFluids.unfinishedDwarvenBrew()
                ),
                List.of(
                        JeiBrewingFluids.tanninItem(
                                maxAge
                        )
                ),
                new JeiFermentingCauldronRecipe.FluidResult(
                        JeiBrewingFluids.unfinishedDwarvenBrew(
                                maxAge,
                                DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED
                        )
                ),
                holder.value().brewTicks()
        );
    }

    private static @NotNull JeiFermentingCauldronRecipe translateYeastStart(
            @NotNull RecipeHolder<FermentingCauldronRecipe> holder,
            float brewingSpeed
    ) {
        FermentingCauldronRecipe recipe = holder.value();

        return new JeiFermentingCauldronRecipe(
                holder.id().withSuffix(
                        yeastSpeedSuffix(
                                brewingSpeed
                        )
                ),
                new JeiFermentingCauldronRecipe.FluidInput(
                        new FluidStack(
                                Fluids.WATER,
                                FluidType.BUCKET_VOLUME
                        )
                ),
                List.of(
                        JeiBrewingFluids.yeastCultureItem(
                                brewingSpeed
                        )
                ),
                new JeiFermentingCauldronRecipe.FluidResult(
                        JeiBrewingFluids.unfinishedYeast(
                                brewingSpeed
                        )
                ),
                recipe.brewTicks()
        );
    }

    private static @NotNull JeiFermentingCauldronRecipe translateYeastFinalize(
            @NotNull RecipeHolder<FermentingCauldronRecipe> holder,
            float brewingSpeed
    ) {
        FermentingCauldronRecipe recipe = holder.value();

        return new JeiFermentingCauldronRecipe(
                holder.id().withSuffix(
                        yeastSpeedSuffix(
                                brewingSpeed
                        )
                ),
                new JeiFermentingCauldronRecipe.FluidInput(
                        JeiBrewingFluids.unfinishedYeast(
                                brewingSpeed
                        )
                ),
                ItemInputJeiTranslator.translate(
                        recipe.ingredient()
                ),
                new JeiFermentingCauldronRecipe.FluidResult(
                        JeiBrewingFluids.yeast(
                                brewingSpeed
                        )
                ),
                recipe.brewTicks()
        );
    }

    private static String yeastSpeedSuffix(
            float brewingSpeed
    ) {
        return "_"
                + Float.toString(
                        brewingSpeed
                )
                .replace(
                        '.',
                        '_'
                )
                + "x";
    }

    private static @NotNull JeiFermentingCauldronRecipe translateTanninFinalize(
            @NotNull RecipeHolder<FermentingCauldronRecipe> holder,
            @NotNull DwarvenBrewAge maxAge,
            @NotNull String idSuffix
    ) {
        FermentingCauldronRecipe recipe = holder.value();

        return new JeiFermentingCauldronRecipe(
                holder.id().withSuffix(idSuffix),
                new JeiFermentingCauldronRecipe.FluidInput(
                        JeiBrewingFluids.unfinishedTannin(
                                maxAge
                        )
                ),
                ItemInputJeiTranslator.translate(
                        recipe.ingredient()
                ),
                new JeiFermentingCauldronRecipe.FluidResult(
                        JeiBrewingFluids.tannin(
                                maxAge
                        )
                ),
                recipe.brewTicks()
        );
    }

    private static @NotNull JeiFermentingCauldronRecipe translate(
            @NotNull RecipeHolder<FermentingCauldronRecipe> holder
    ) {
        FermentingCauldronRecipe recipe =
                holder.value();

        boolean usesUnfinishedFluidInput =
                recipe.effect().isPresent()
                        || (
                        recipe.outputFluid()
                                == FermentingCauldronRecipe.OutputFluid.DWARVEN_BREW
                                && recipe.finalizeBrew()
                                && recipe.lastIngredient().isPresent()
                )
                        || (
                        recipe.outputFluid()
                                == FermentingCauldronRecipe.OutputFluid.TANNIN
                                && recipe.lastIngredient().isPresent()
                )
                        || (
                        recipe.outputFluid()
                                == FermentingCauldronRecipe.OutputFluid.YEAST
                                && recipe.lastIngredient().isPresent()
                );

        JeiFermentingCauldronRecipe.PreviousInput previousInput =
                usesUnfinishedFluidInput
                        ? new JeiFermentingCauldronRecipe.FluidInput(
                        createPreviousFluid(
                                recipe
                        )
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

    private static @NotNull FluidStack createPreviousFluid(
            FermentingCauldronRecipe recipe
    ) {
        if (recipe.outputFluid()
                == FermentingCauldronRecipe.OutputFluid.YEAST) {
            return JeiBrewingFluids.unfinishedYeast(
                    DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED
            );
        }

        if (recipe.outputFluid()
                == FermentingCauldronRecipe.OutputFluid.TANNIN) {
            return JeiBrewingFluids.unfinishedTannin(
                    resolvePreviousTanninMaxAge(
                            recipe
                    )
            );
        }

        return JeiBrewingFluids.unfinishedDwarvenBrew();
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
                                    ? JeiBrewingFluids.yeast(
                                    resolveYeastOutputSpeed(
                                            recipe
                                    )
                            )
                                    : JeiBrewingFluids.unfinishedYeast(
                                    resolveYeastOutputSpeed(
                                            recipe
                                    )
                            );

                    case TANNIN ->
                            recipe.finalizeBrew()
                                    ? JeiBrewingFluids.tannin(
                                    resolveTanninOutputMaxAge(
                                            recipe
                                    )
                            )
                                    : JeiBrewingFluids.unfinishedTannin(
                                    resolveTanninOutputMaxAge(
                                            recipe
                                    )
                            );
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

    private static float resolveYeastOutputSpeed(
            FermentingCauldronRecipe recipe
    ) {
        return recipe.brewingSpeed()
                .orElse(
                        DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED
                );
    }

    private static DwarvenBrewAge resolveTanninOutputMaxAge(
            FermentingCauldronRecipe recipe
    ) {
        return recipe.maxBrewAge()
                .orElseGet(
                        () -> resolvePreviousTanninMaxAge(
                                recipe
                        )
                );
    }

    private static DwarvenBrewAge resolvePreviousTanninMaxAge(
            FermentingCauldronRecipe recipe
    ) {
        boolean premium = recipe.lastIngredient()
                .map(ItemInputJeiTranslator::translate)
                .orElseGet(List::of)
                .stream()
                .anyMatch(
                        stack -> stack.is(
                                Items.CHORUS_FRUIT
                        )
                );

        return premium
                ? DwarvenBrewAge.VINTAGE
                : DwarvenBrewAge.MATURED;
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

        addYeastExtractionRecipe(
                result
        );

        addTanninExtractionRecipe(
                result,
                TANNIN_BOTTLE_EXTRACTION_ID,
                DwarvenBrewAge.MATURED
        );

        addTanninExtractionRecipe(
                result,
                REFINED_TANNIN_BOTTLE_EXTRACTION_ID,
                DwarvenBrewAge.VINTAGE
        );
    }

    private static void addYeastExtractionRecipe(
            List<JeiFermentingCauldronRecipe> result
    ) {
        for (float brewingSpeed : DwarvenBrewFluidHelper.BREWING_SPEED_TIERS) {
            result.add(
                    new JeiFermentingCauldronRecipe(
                            YEAST_BOTTLE_EXTRACTION_ID.withSuffix(
                                    yeastSpeedSuffix(
                                            brewingSpeed
                                    )
                            ),
                            new JeiFermentingCauldronRecipe.FluidInput(
                                    JeiBrewingFluids.yeast(
                                            JolCraftBrewingItems.BOTTLE_VOLUME,
                                            brewingSpeed
                                    )
                            ),
                            List.of(
                                    new ItemStack(
                                            Items.GLASS_BOTTLE
                                    )
                            ),
                            new JeiFermentingCauldronRecipe.ItemResult(
                                    List.of(
                                            JeiBrewingFluids.yeastItem(
                                                    brewingSpeed
                                            )
                                    )
                            ),
                            0
                    )
            );
        }
    }

    private static void addTanninExtractionRecipe(
            List<JeiFermentingCauldronRecipe> result,
            ResourceLocation id,
            DwarvenBrewAge maxAge
    ) {
        result.add(
                new JeiFermentingCauldronRecipe(
                        id,
                        new JeiFermentingCauldronRecipe.FluidInput(
                                JeiBrewingFluids.tannin(
                                        JolCraftBrewingItems.BOTTLE_VOLUME,
                                        maxAge
                                )
                        ),
                        List.of(
                                new ItemStack(
                                        Items.GLASS_BOTTLE
                                )
                        ),
                        new JeiFermentingCauldronRecipe.ItemResult(
                                List.of(
                                        JeiBrewingFluids.tanninItem(
                                                maxAge
                                        )
                                )
                        ),
                        0
                )
        );
    }
}