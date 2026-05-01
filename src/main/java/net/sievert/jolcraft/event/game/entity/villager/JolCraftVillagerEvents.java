package net.sievert.jolcraft.event.game.entity.villager;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.item.JolCraftItems;

import java.util.List;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class JolCraftVillagerEvents {

    @SubscribeEvent
    public static void onVillagerCrateInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        JolCraftVillagerCrateHelper.onInteract(event);
    }

    @SubscribeEvent
    public static void addVillagerTrades(final VillagerTradesEvent event) {
        if (event.getType() != VillagerProfession.LIBRARIAN) return;

        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();

        int added = 0;

        trades.get(5).add((pTrader, pRandom) -> {
            int baseCost = 32 + pRandom.nextInt(33);
            return new MerchantOffer(
                    new ItemCost(Items.EMERALD, baseCost),
                    new ItemStack(JolCraftItems.DWARVEN_LEXICON.get(), 1),
                    1, 1, 0.05f
            );
        });
        added++;

        JolCraftLogs.info(
                JolCraftLogTags.RECIPE,
                "Registered {} villager trades",
                added
        );
    }
}
