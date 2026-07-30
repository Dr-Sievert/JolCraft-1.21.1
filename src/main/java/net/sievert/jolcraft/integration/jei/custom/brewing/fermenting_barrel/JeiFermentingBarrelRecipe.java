package net.sievert.jolcraft.integration.jei.custom.brewing.fermenting_barrel;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.integration.jei.util.item.JeiStacks;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

public record JeiFermentingBarrelRecipe(
        @NotNull ResourceLocation id,
        @NotNull Process process
) {

    public JeiFermentingBarrelRecipe {
        Objects.requireNonNull(
                id,
                JolCraftDictionary.ID
        );

        Objects.requireNonNull(
                process,
                "process"
        );
    }

    public sealed interface Process
            permits AgingProcess,
            ExtractionProcess {
    }

    public record AgingProcess(
            @NotNull Stage input,
            @NotNull Stage output
    ) implements Process {

        public AgingProcess {
            Objects.requireNonNull(
                    input,
                    JolCraftDictionary.INPUT
            );

            Objects.requireNonNull(
                    output,
                    JolCraftDictionary.OUTPUT
            );
        }
    }

    public record Stage(
            @NotNull Component title,
            @NotNull FluidStack brew
    ) {

        public Stage {
            Objects.requireNonNull(
                    title,
                    JolCraftDictionary.TITLE
            );

            Objects.requireNonNull(
                    brew,
                    JolCraftDictionary.FLUID
            );

            if (brew.isEmpty()) {
                throw new IllegalArgumentException(
                        "Aging stage brew must not be empty"
                );
            }

            brew =
                    brew.copy();
        }
    }

    public record ExtractionProcess(
            @NotNull FluidStack brew,
            @NotNull List<ItemStack> containerExamples,
            @NotNull List<ItemStack> resultExamples
    ) implements Process {

        public ExtractionProcess {
            Objects.requireNonNull(
                    brew,
                    JolCraftDictionary.FLUID
            );

            if (brew.isEmpty()) {
                throw new IllegalArgumentException(
                        "Extraction brew must not be empty"
                );
            }

            brew =
                    brew.copy();

            containerExamples =
                    JeiStacks.copyRequired(
                            containerExamples,
                            "containerExamples"
                    );

            resultExamples =
                    JeiStacks.copyRequired(
                            resultExamples,
                            "resultExamples"
                    );
        }
    }
}