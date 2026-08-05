package net.sievert.jolcraft.integration.jei.custom.brewing.fermenting_barrel;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.neoforge.fluids.FluidType;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.integration.jei.util.fluid.JeiBrewingFluids;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewFluidHelper;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class JeiFermentingBarrelHelper {

    private static final ResourceLocation FRESH_TO_AGED_ID =
            JolCraft.location(
                    "jei/fermenting_barrel/fresh_to_aged"
            );

    private static final ResourceLocation AGED_TO_MATURE_ID =
            JolCraft.location(
                    "jei/fermenting_barrel/aged_to_mature"
            );

    private static final ResourceLocation MATURE_TO_VINTAGE_ID =
            JolCraft.location(
                    "jei/fermenting_barrel/mature_to_vintage"
            );

    private static final ResourceLocation BREW_MUG_EXTRACTION_ID =
            JolCraft.location(
                    "jei/fermenting_barrel/extract_brew_mug"
            );

    private static final ResourceLocation BREW_BUCKET_EXTRACTION_ID =
            JolCraft.location(
                    "jei/fermenting_barrel/extract_brew_bucket"
            );

    private JeiFermentingBarrelHelper() {
    }

    public static @NotNull List<JeiFermentingBarrelRecipe> getRecipes() {
        List<JeiFermentingBarrelRecipe> result =
                new ArrayList<>();

        addAgingRecipes(
                result,
                FRESH_TO_AGED_ID,
                DwarvenBrewAge.FRESH,
                DwarvenBrewAge.AGED
        );

        addAgingRecipes(
                result,
                AGED_TO_MATURE_ID,
                DwarvenBrewAge.AGED,
                DwarvenBrewAge.MATURED
        );

        addAgingRecipes(
                result,
                MATURE_TO_VINTAGE_ID,
                DwarvenBrewAge.MATURED,
                DwarvenBrewAge.VINTAGE
        );

        result.add(
                mugExtractionRecipe()
        );

        result.add(
                bucketExtractionRecipe()
        );

        return List.copyOf(
                result
        );
    }

    private static void addAgingRecipes(
            @NotNull List<JeiFermentingBarrelRecipe> result,
            @NotNull ResourceLocation id,
            @NotNull DwarvenBrewAge inputAge,
            @NotNull DwarvenBrewAge outputAge
    ) {
        result.add(
                agingRecipe(
                        id,
                        inputAge,
                        outputAge,
                        DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED
                )
        );
    }

    private static @NotNull JeiFermentingBarrelRecipe agingRecipe(
            @NotNull ResourceLocation id,
            @NotNull DwarvenBrewAge inputAge,
            @NotNull DwarvenBrewAge outputAge,
            float brewingSpeed
    ) {
        return new JeiFermentingBarrelRecipe(
                id,
                new JeiFermentingBarrelRecipe.AgingProcess(
                        stage(
                                inputAge,
                                outputAge,
                                brewingSpeed
                        ),
                        stage(
                                outputAge,
                                outputAge,
                                brewingSpeed
                        )
                )
        );
    }

    private static @NotNull JeiFermentingBarrelRecipe.Stage stage(
            @NotNull DwarvenBrewAge age,
            @NotNull DwarvenBrewAge maxAge,
            float brewingSpeed
    ) {
        return new JeiFermentingBarrelRecipe.Stage(
                JeiBrewingFluids.dwarvenBrew(
                        age,
                        maxAge,
                        brewingSpeed,
                        JeiBrewingFluids.displayStrengthEffect(
                                age.amplifierBonus()
                        )
                )
        );
    }

    private static @NotNull JeiFermentingBarrelRecipe mugExtractionRecipe() {
        return new JeiFermentingBarrelRecipe(
                BREW_MUG_EXTRACTION_ID,
                new JeiFermentingBarrelRecipe.ExtractionProcess(
                        JeiBrewingFluids.dwarvenBrew(
                                DwarvenBrewFluidHelper.MUG_VOLUME,
                                DwarvenBrewAge.VINTAGE,
                                DwarvenBrewAge.VINTAGE,
                                DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED,
                                PotionContents.EMPTY.withEffectAdded(
                                        JeiBrewingFluids.displayStrengthEffect(
                                                DwarvenBrewAge.VINTAGE.amplifierBonus()
                                        )
                                )
                        ),
                        List.of(
                                new ItemStack(
                                        JolCraftItems.GLASS_MUG.get()
                                )
                        ),
                        List.of(
                                JeiBrewingFluids.dwarvenBrewItem(
                                        DwarvenBrewAge.VINTAGE,
                                        DwarvenBrewAge.VINTAGE,
                                        DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED
                                )
                        )
                )
        );
    }

    private static @NotNull JeiFermentingBarrelRecipe bucketExtractionRecipe() {
        return new JeiFermentingBarrelRecipe(
                BREW_BUCKET_EXTRACTION_ID,
                new JeiFermentingBarrelRecipe.ExtractionProcess(
                        JeiBrewingFluids.dwarvenBrew(
                                FluidType.BUCKET_VOLUME,
                                DwarvenBrewAge.VINTAGE,
                                DwarvenBrewAge.VINTAGE,
                                DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED,
                                PotionContents.EMPTY
                        ),
                        List.of(
                                new ItemStack(
                                        Items.BUCKET
                                )
                        ),
                        List.of(
                                JeiBrewingFluids.dwarvenBrewBucket(
                                        DwarvenBrewAge.VINTAGE,
                                        DwarvenBrewAge.VINTAGE,
                                        DwarvenBrewFluidHelper.DEFAULT_BREWING_SPEED
                                )
                        )
                )
        );
    }
}