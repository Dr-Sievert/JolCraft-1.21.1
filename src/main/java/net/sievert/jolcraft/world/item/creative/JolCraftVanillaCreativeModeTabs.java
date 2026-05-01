package net.sievert.jolcraft.world.item.creative;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.sievert.jolcraft.JolCraft;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class JolCraftVanillaCreativeModeTabs {

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
