package net.sievert.jolcraft.item.util.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import com.mojang.blaze3d.platform.InputConstants;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class TooltipHelper {

    private TooltipHelper() {}

    public static final Component ALT_KEY   = InputConstants.getKey(InputConstants.KEY_LALT, -1)
            .getDisplayName().copy().withStyle(ChatFormatting.BLUE);

    public static void addAltTooltip(List<Component> tooltip, Component mainLine, List<Component> fallbackLines) {
        if (Screen.hasAltDown()) {
            tooltip.add(mainLine);
        } else {
            tooltip.addAll(fallbackLines);
            tooltip.add(Component.translatable("tooltip.jolcraft.hold_key", ALT_KEY)
                    .withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    public static void addAltTooltipCustom(
            List<Component> tooltip,
            Supplier<Component> mainSupplier,
            Supplier<List<Component>> fallbackSupplier,
            BooleanSupplier showMainPredicate,
            Supplier<Component> holdHintSupplier
    ) {
        if (showMainPredicate.getAsBoolean()) {
            tooltip.add(mainSupplier.get());
        } else {
            List<Component> fallbacks = fallbackSupplier.get();
            if (fallbacks != null && !fallbacks.isEmpty()) {
                tooltip.addAll(fallbacks);
            }
            if (holdHintSupplier != null) {
                tooltip.add(holdHintSupplier.get());
            }
        }
    }

}
