package net.sievert.jolcraft.world.item.client.color.custom;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.util.client.JolCraftColors;
import net.sievert.jolcraft.world.item.component.custom.alchemy.EssenceType;
import net.sievert.jolcraft.world.item.potion.JolCraftPotions;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public final class PotionColor {

    private PotionColor() {}

    public static int color(@NotNull ItemStack stack) {
        PotionContents contents = stack.getOrDefault(
                DataComponents.POTION_CONTENTS,
                PotionContents.EMPTY
        );

        if (contents.is(JolCraftPotions.INFUSED)) {
            return essenceColor(EssenceType.INFUSED);
        }

        if (contents.is(JolCraftPotions.REFINED)) {
            return essenceColor(EssenceType.REFINED);
        }

        if (contents.is(JolCraftPotions.EXALTED)) {
            return essenceColor(EssenceType.EXALTED);
        }

        return JolCraftColors.toArgb(
                contents.getColor()
        );
    }

    private static int essenceColor(EssenceType type) {
        return JolCraftColors.argb(
                type.color()
        );
    }
}