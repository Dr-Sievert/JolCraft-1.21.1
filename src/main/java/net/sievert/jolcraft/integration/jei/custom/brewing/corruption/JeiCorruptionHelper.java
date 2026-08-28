package net.sievert.jolcraft.integration.jei.custom.brewing.corruption;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionContents;
import net.sievert.jolcraft.config.custom.brewing.CorruptionEffectsConfig;
import net.sievert.jolcraft.config.custom.brewing.CorruptionEffectsConfigManager;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.custom.alchemy.EssenceType;
import net.sievert.jolcraft.world.recipe.custom.vanilla.JolCraftCorruptionBrewingRecipe;

import java.util.ArrayList;
import java.util.List;

public final class JeiCorruptionHelper {

    private JeiCorruptionHelper() {}

    public static List<JeiCorruptionRecipe> getRecipes() {
        List<CorruptionEffectsConfig.WeightedEffect> corruptionEffects =
                CorruptionEffectsConfigManager.INSTANCE.entries();

        if (corruptionEffects.isEmpty()) {
            return List.of();
        }

        List<ItemStack> inputs =
                new ArrayList<>();

        List<ItemStack> outputs =
                new ArrayList<>();

        BuiltInRegistries.POTION.holders()
                .forEach(potion -> {
                    ItemStack input =
                            PotionContents.createItemStack(
                                    Items.POTION,
                                    potion
                            );

                    if (!JolCraftCorruptionBrewingRecipe.hasBeneficialEffect(input)) {
                        return;
                    }

                    for (CorruptionEffectsConfig.WeightedEffect corruptionEffect : corruptionEffects) {
                        ItemStack output =
                                JolCraftCorruptionBrewingRecipe.createPreviewOutput(
                                        input,
                                        corruptionEffect.copyEffect()
                                );

                        if (output.isEmpty()) {
                            continue;
                        }

                        inputs.add(
                                input.copy()
                        );

                        outputs.add(
                                output
                        );
                    }
                });

        if (inputs.isEmpty()) {
            return List.of();
        }

        return List.of(
                new JeiCorruptionRecipe(
                        inputs,
                        JolCraftItems.ESSENCE.get()
                                .createStack(
                                        EssenceType.CORRUPTED
                                ),
                        outputs
                )
        );
    }
}
