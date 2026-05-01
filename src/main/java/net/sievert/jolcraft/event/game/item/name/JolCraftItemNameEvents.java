package net.sievert.jolcraft.event.game.item.name;

import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEnchantItemEvent;
import net.sievert.jolcraft.JolCraft;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftItemNameEvents {

    private JolCraftItemNameEvents() {}

    @SubscribeEvent
    public static void onEnchantItem(PlayerEnchantItemEvent event) {
        ItemStack stack = event.getEnchantedItem();
        JolCraftItemNameHelper.applySpecialNameStyle(stack);
    }
}