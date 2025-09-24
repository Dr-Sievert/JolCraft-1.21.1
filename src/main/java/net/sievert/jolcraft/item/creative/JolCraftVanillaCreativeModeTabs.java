package net.sievert.jolcraft.item.creative;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(modid = "jolcraft", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class JolCraftVanillaCreativeModeTabs {

    @SubscribeEvent
    public static void onBuildVanillaTabs(BuildCreativeModeTabContentsEvent event) {
        // Example: add to vanilla ingredients tab
        /*
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.accept(JolCraftItems.GOLD_COIN);
        }
        */
    }
}
