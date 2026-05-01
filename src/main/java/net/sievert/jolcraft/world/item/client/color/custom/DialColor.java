package net.sievert.jolcraft.world.item.client.color.custom;

import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassDialColor;
import net.sievert.jolcraft.world.item.component.custom.compass.DeepslateCompassStructureGroup;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public final class DialColor {

    private static final int DEFAULT = 0xFFFF0000;

    private DialColor() {}

    public static int color(@NotNull ItemStack stack) {
        DeepslateCompassDialColor color = stack.get(
                JolCraftDataComponents.DEEPSLATE_COMPASS_DIAL_COLOR.get()
        );

        return color != null ? color.color() : DEFAULT;
    }

    public static int color(String groupId) {
        return DeepslateCompassStructureGroup.color(groupId, DEFAULT);
    }
}