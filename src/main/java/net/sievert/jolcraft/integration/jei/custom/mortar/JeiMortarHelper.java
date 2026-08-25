package net.sievert.jolcraft.integration.jei.custom.mortar;

import net.minecraft.world.item.ItemStack;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.integration.jei.util.recipe.ItemInputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.recipe.ItemOutputJeiTranslator;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiItemOutcome;
import net.sievert.jolcraft.integration.jei.util.recipe.JeiRecipeAccess;
import net.sievert.jolcraft.world.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.recipe.base.input.ItemInput;
import net.sievert.jolcraft.world.recipe.custom.mortar.MortarRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public final class JeiMortarHelper {

    private JeiMortarHelper() {
    }

    public static @NotNull List<JeiMortarRecipe> getRecipes() {
        return JeiRecipeAccess.translateSorted(
                JolCraftRecipes.MORTAR_TYPE.get(),
                holder -> translate(
                        holder.value()
                )
        );
    }

    private static @NotNull List<JeiMortarRecipe> translate(
            @NotNull MortarRecipe recipe
    ) {
        List<List<ItemStack>> inputs =
                recipe.inputs()
                        .stream()
                        .map(
                                ItemInputJeiTranslator::translate
                        )
                        .toList();

        List<ItemStack> pestles =
                ItemInputJeiTranslator.translate(
                        ItemInput.tag(
                                JolCraftTags.Items.PESTLES
                        )
                );

        List<JeiMortarRecipe> result =
                new ArrayList<>();

        for (JeiItemOutcome outcome :
                ItemOutputJeiTranslator.translate(
                        recipe.result()
                )) {
            result.add(
                    new JeiMortarRecipe(
                            recipe,
                            inputs,
                            pestles,
                            outcome
                    )
            );
        }

        return List.copyOf(
                result
        );
    }
}