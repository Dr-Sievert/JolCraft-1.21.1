package net.sievert.jolcraft.integration.jei.custom.trade;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.entity.custom.dwarf.*;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfTrades;

import java.util.ArrayList;
import java.util.List;

public class DwarfTradeJeiHelper {

    public static List<DwarfTradeRecipe> getAllDwarfJeiTrades() {
        List<DwarfTradeRecipe> recipes = new ArrayList<>();
        for (DwarfProfession prof : PROFESSIONS) {
            Int2ObjectMap<DwarfTrades.ItemListing[]> trades = prof.trades();
            if (trades == null) continue;
            for (int level = 1; level <= 5; ++level) {
                DwarfTrades.ItemListing[] tradeArr = trades.get(level);
                if (tradeArr == null) continue;
                for (DwarfTrades.ItemListing listing : tradeArr) {
                    var inputA = DwarfTrades.getExampleInputA(listing);
                    var inputB = DwarfTrades.getExampleInputB(listing);
                    var output = DwarfTrades.getExampleOutput(listing);

                    int[] a = getInputAMinMax(listing);
                    int[] b = getInputBMinMax(listing);
                    int[] o = getOutputMinMax(listing);

                    if ((!inputA.isEmpty() || (inputB != null && !inputB.isEmpty())) && !output.isEmpty()) {
                        recipes.add(new DwarfTradeRecipe(
                                prof.displayName(), level, inputA, inputB, output, prof.spawnEgg(),
                                a[0], a[1], b[0], b[1], o[0], o[1]
                        ));
                    }
                }
            }
        }
        return recipes;
    }

    public record DwarfProfession(
            String id,
            String displayName,
            Int2ObjectMap<DwarfTrades.ItemListing[]> trades,
            DeferredItem<Item> spawnEgg
    ) {}

    public static final List<DwarfProfession> PROFESSIONS = List.of(
            new DwarfProfession(
                    "dwarf",
                    "Dwarf",
                    DwarfEntity.createRandomizedDwarfTrades(),
                    JolCraftItems.DWARF_SPAWN_EGG
            ),
            new DwarfProfession(
                    "guildmaster",
                    "Guildmaster",
                    DwarfGuildmasterEntity.createRandomizedGuildmasterTrades(),
                    JolCraftItems.DWARF_GUILDMASTER_SPAWN_EGG
            ),
            new DwarfProfession(
                    "historian",
                    "Historian",
                    DwarfHistorianEntity.createRandomizedHistorianTrades(),
                    JolCraftItems.DWARF_HISTORIAN_SPAWN_EGG
            ),
            new DwarfProfession(
                    "merchant",
                    "Merchant",
                    DwarfMerchantEntity.getAllJeiTrades(),
                    JolCraftItems.DWARF_MERCHANT_SPAWN_EGG
            ),
            new DwarfProfession(
                    "scrapper",
                    "Scrapper",
                    DwarfScrapperEntity.getAllJeiTrades(),
                    JolCraftItems.DWARF_SCRAPPER_SPAWN_EGG
            ),
            new DwarfProfession(
                    "brewmaster",
                    "Brewmaster",
                    DwarfBrewmasterEntity.createRandomizedBrewmasterTrades(),
                    JolCraftItems.DWARF_BREWMASTER_SPAWN_EGG
            ),
            new DwarfProfession(
                    "guard",
                    "Guard",
                    DwarfGuardEntity.createRandomizedGuardTrades(),
                    JolCraftItems.DWARF_GUARD_SPAWN_EGG
            ),
            new DwarfProfession(
                    "keeper",
                    "Keeper",
                    DwarfKeeperEntity.createRandomizedKeeperTrades(),
                    JolCraftItems.DWARF_KEEPER_SPAWN_EGG
            ),
            new DwarfProfession(
                    "artisan",
                    "Artisan",
                    DwarfArtisanEntity.createRandomizedArtisanTrades(),
                    JolCraftItems.DWARF_ARTISAN_SPAWN_EGG
            ),
            new DwarfProfession(
                    "explorer",
                    "Explorer",
                    DwarfExplorerEntity.createRandomizedExplorerTrades(),
                    JolCraftItems.DWARF_EXPLORER_SPAWN_EGG
            ),
            new DwarfProfession(
                    "miner",
                    "Miner",
                    DwarfMinerEntity.createRandomizedMinerTrades(),
                    JolCraftItems.DWARF_MINER_SPAWN_EGG
            )
    );

    private static int[] getInputAMinMax(DwarfTrades.ItemListing listing) {
        if (listing instanceof DwarfTrades.ItemsForGold t) {
            return new int[]{t.minGoldCost, t.maxGoldCost};
        } else if (listing instanceof DwarfTrades.GoldForItems t) {
            return new int[]{t.minInputCount, t.maxInputCount};
        } else if (listing instanceof DwarfTrades.ItemsAndGoldToItems t) {
            return new int[]{t.minGoldCost, t.maxGoldCost};
        } else if (listing instanceof DwarfTrades.ItemsWithDataForGold t) {
            return new int[]{t.minGoldCost, t.maxGoldCost};
        } else if (listing instanceof DwarfTrades.ItemsAndGoldToItemsWithData t) {
            return new int[]{t.minGoldCost, t.maxGoldCost};
        } else if (listing instanceof DwarfTrades.ItemForItemWithData t) {
            return new int[]{t.minInputCount, t.maxInputCount};
        } else if (listing instanceof DwarfTrades.TreasureMapForGold t) {
            return new int[]{t.goldCost, t.goldCost};
        }
        return new int[]{1, 1};
    }

    private static int[] getInputBMinMax(DwarfTrades.ItemListing listing) {
        if (listing instanceof DwarfTrades.ItemsAndGoldToItems t) {
            return new int[]{t.minInputCount, t.maxInputCount};
        } else if (listing instanceof DwarfTrades.ItemsAndGoldToItemsWithData t) {
            return new int[]{t.minInputCount, t.maxInputCount};
        } else if (listing instanceof DwarfTrades.TreasureMapForGold) {
            return new int[]{1, 1};
        }
        return new int[]{0, 0};
    }

    private static int[] getOutputMinMax(DwarfTrades.ItemListing listing) {
        if (listing instanceof DwarfTrades.ItemsForGold t) {
            return new int[]{t.minItemCount, t.maxItemCount};
        } else if (listing instanceof DwarfTrades.GoldForItems t) {
            return new int[]{t.minGoldAmount, t.maxGoldAmount};
        } else if (listing instanceof DwarfTrades.ItemsAndGoldToItems t) {
            return new int[]{t.minOutputCount, t.maxOutputCount};
        } else if (listing instanceof DwarfTrades.ItemsWithDataForGold t) {
            return new int[]{t.minItemCount, t.maxItemCount};
        } else if (listing instanceof DwarfTrades.ItemsAndGoldToItemsWithData t) {
            return new int[]{t.minOutputCount, t.maxOutputCount};
        } else if (listing instanceof DwarfTrades.ItemForItemWithData t) {
            return new int[]{t.minOutputCount, t.maxOutputCount};
        } else if (listing instanceof DwarfTrades.TreasureMapForGold) {
            return new int[]{1, 1};
        }
        return new int[]{1, 1};
    }

}
