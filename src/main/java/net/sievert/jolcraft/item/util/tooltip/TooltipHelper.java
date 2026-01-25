package net.sievert.jolcraft.item.util.tooltip;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.sievert.jolcraft.network.proxy.JolCraftProxy;

import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Supplier;

public final class TooltipHelper {

    private TooltipHelper() {}

    public static Component altKey() {
        Component key = JolCraftProxy.access().getAltKeyComponent();
        if (key == null) {
            return Component.literal("Alt").withStyle(ChatFormatting.BLUE);
        }
        return key;
    }

    public static void addAltTooltip(List<Component> tooltip, Component mainLine, List<Component> fallbackLines) {
        if (JolCraftProxy.access().isAltDown()) {
            tooltip.add(mainLine);
        } else {
            tooltip.addAll(fallbackLines);
            tooltip.add(Component.translatable("tooltip.jolcraft.hold_key", altKey()).withStyle(ChatFormatting.DARK_GRAY));
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