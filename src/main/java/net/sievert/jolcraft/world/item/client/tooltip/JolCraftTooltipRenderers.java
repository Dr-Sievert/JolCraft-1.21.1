package net.sievert.jolcraft.world.item.client.tooltip;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.item.client.tooltip.coin.CoinPouchTooltip;

import java.util.function.Function;

public final class JolCraftTooltipRenderers {

    private JolCraftTooltipRenderers() {}

    public static int register(RegisterClientTooltipComponentFactoriesEvent event) {
        int count = 0;

        count += register(event, CoinPouchTooltip.class, CoinPouchTooltip.Renderer::new);

        JolCraftLogs.info(JolCraftLogTags.INIT, "Registered {} tooltip factories", count);
        return count;
    }

    private static <T extends TooltipComponent> int register(
            RegisterClientTooltipComponentFactoriesEvent event,
            Class<T> type,
            Function<T, ? extends ClientTooltipComponent> factory
    ) {
        event.register(type, factory);
        return 1;
    }
}