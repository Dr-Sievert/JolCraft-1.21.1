package net.sievert.jolcraft.world.item.client.color.custom;

import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public final class BrewColor {

    private static final int DEFAULT = 0xFF9A652B;

    private BrewColor() {}

    public static int color(@NotNull ItemStack stack) {
        Integer argb = stack.get(JolCraftDataComponents.BREW_COLOR.get());
        return argb != null ? (0xFF000000 | argb) : DEFAULT;
    }
}