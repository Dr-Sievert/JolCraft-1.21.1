package net.sievert.jolcraft.world.item.client.color.custom;

import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.util.client.JolCraftColors;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.alchemy.EssenceType;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public final class EssenceColor {

    private EssenceColor() {}

    public static int color(@NotNull ItemStack stack) {
        return JolCraftColors.argb(
                stack.getOrDefault(
                        JolCraftDataComponents.ESSENCE_TYPE.get(),
                        EssenceType.INFUSED
                ).color()
        );
    }
}
