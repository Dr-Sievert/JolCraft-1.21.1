package net.sievert.jolcraft.event.game.entity.villager;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.id.worldgen.JolCraftStructureIds;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.registry.JolCraftMapDecorationTypes;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class JolCraftVillagerEvents {

    @SubscribeEvent
    public static void onVillagerCrateInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        JolCraftVillagerCrateEventsHelper.onInteract(event);
    }

    @SubscribeEvent
    public static void addVillagerTrades(final VillagerTradesEvent event) {
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
        int added = 0;

        if (event.getType() == VillagerProfession.LIBRARIAN) {
            trades.get(5).add((pTrader, pRandom) -> {
                int baseCost = 32 + pRandom.nextInt(33);
                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, baseCost),
                        new ItemStack(JolCraftItems.DWARVEN_LEXICON.get(), 1),
                        1, 1, 0.05f
                );
            });
            added++;
        }

        if (event.getType() == VillagerProfession.CARTOGRAPHER) {
            trades.get(5).add((pTrader, pRandom) -> {
                if (!(pTrader.level() instanceof ServerLevel serverLevel)) {
                    return null;
                }

                BlockPos fortressPos = serverLevel.findNearestMapStructure(
                        JolCraftTags.Structures.ON_DWARVEN_FORTRESS_EXPLORER_MAPS,
                        pTrader.blockPosition(),
                        100,
                        true
                );

                if (fortressPos == null) {
                    return null;
                }

                ItemStack map = MapItem.create(
                        serverLevel,
                        fortressPos.getX(),
                        fortressPos.getZ(),
                        (byte) 2,
                        true,
                        true
                );

                MapItem.renderBiomePreviewMap(serverLevel, map);
                MapItemSavedData.addTargetDecoration(
                        map,
                        fortressPos,
                        JolCraftStructureIds.DWARVEN_FORTRESS,
                        JolCraftMapDecorationTypes.DWARVEN
                );
                map.set(
                        DataComponents.ITEM_NAME,
                        Component.translatable(
                                JolCraftStrings.dotted(
                                        BuiltInRegistries.ITEM
                                                .getKey(Items.FILLED_MAP)
                                                .getPath(),
                                        JolCraftStructureIds.DWARVEN_FORTRESS
                                )
                        )
                );

                return new MerchantOffer(
                        new ItemCost(Items.EMERALD, 32 + pRandom.nextInt(33)),
                        Optional.of(new ItemCost(Items.MAP)),
                        map,
                        1,
                        30,
                        0.2F
                );
            });
            added++;
        }

        if (added == 0) {
            return;
        }

        JolCraftLogs.info(
                JolCraftLogTags.RECIPE,
                "Registered {} villager trades",
                added
        );
    }
}
