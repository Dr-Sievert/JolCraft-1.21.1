package net.sievert.jolcraft.integration.jei.custom.brewing.fermenting_barrel;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.integration.jei.util.fluid.JeiBrewingFluids;
import net.sievert.jolcraft.world.block.fluid.util.brewing.DwarvenBrewAge;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

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
        return List.of(
                agingRecipe(
                        FRESH_TO_AGED_ID,
                        DwarvenBrewAge.FRESH,
                        DwarvenBrewAge.AGED
                ),
                agingRecipe(
                        AGED_TO_MATURE_ID,
                        DwarvenBrewAge.AGED,
                        DwarvenBrewAge.MATURED
                ),
                agingRecipe(
                        MATURE_TO_VINTAGE_ID,
                        DwarvenBrewAge.MATURED,
                        DwarvenBrewAge.VINTAGE
                ),
                mugExtractionRecipe(),
                bucketExtractionRecipe()
        );
    }

    private static @NotNull JeiFermentingBarrelRecipe agingRecipe(
            @NotNull ResourceLocation id,
            @NotNull DwarvenBrewAge inputAge,
            @NotNull DwarvenBrewAge outputAge
    ) {
        return new JeiFermentingBarrelRecipe(
                id,
                new JeiFermentingBarrelRecipe.AgingProcess(
                        stage(
                                inputAge
                        ),
                        stage(
                                outputAge
                        )
                )
        );
    }

    private static @NotNull JeiFermentingBarrelRecipe.Stage stage(
            @NotNull DwarvenBrewAge age
    ) {
        return new JeiFermentingBarrelRecipe.Stage(
                Component.translatable(
                        age.translationKey()
                ),
                JeiBrewingFluids.dwarvenBrew(
                        age,
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
                        JeiBrewingFluids.dwarvenBrewMug(),
                        List.of(
                                new ItemStack(
                                        JolCraftItems.GLASS_MUG.get()
                                )
                        ),
                        List.of(
                                JeiBrewingFluids.dwarvenBrewItem()
                        )
                )
        );
    }

    private static @NotNull JeiFermentingBarrelRecipe bucketExtractionRecipe() {
        return new JeiFermentingBarrelRecipe(
                BREW_BUCKET_EXTRACTION_ID,
                new JeiFermentingBarrelRecipe.ExtractionProcess(
                        JeiBrewingFluids.dwarvenBrew(),
                        List.of(
                                new ItemStack(
                                        Items.BUCKET
                                )
                        ),
                        List.of(
                                new ItemStack(
                                        JolCraftItems.DWARVEN_BREW_BUCKET.get()
                                )
                        )
                )
        );
    }
}