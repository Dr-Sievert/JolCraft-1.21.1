package net.sievert.jolcraft.integration.jei.custom.trade;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.entity.custom.dwarf.*;
import net.sievert.jolcraft.entity.custom.dwarf.profession.*;
import net.sievert.jolcraft.entity.util.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.entity.util.dwarf.trade.DwarfTrades;

import java.util.ArrayList;
import java.util.List;

public class DwarfTradeJeiHelper {

    public static Int2ObjectMap<DwarfTrades.ItemListing[]> getTradesForProfession(DwarfProfession prof) {
        return switch (prof) {
            case NONE -> DwarfEntity.createRandomizedDwarfTrades();
            case GUILDMASTER -> DwarfGuildmasterEntity.createRandomizedGuildmasterTrades();
            case HISTORIAN -> DwarfHistorianEntity.createRandomizedHistorianTrades();
            case MERCHANT -> DwarfMerchantEntity.getAllJeiTrades();
            case SCRAPPER -> DwarfScrapperEntity.getAllJeiTrades();
            case BREWMASTER -> DwarfBrewmasterEntity.createRandomizedBrewmasterTrades();
            case GUARD -> DwarfGuardEntity.createRandomizedGuardTrades();
            case KEEPER -> DwarfKeeperEntity.createRandomizedKeeperTrades();
            case ARTISAN -> DwarfArtisanEntity.createRandomizedArtisanTrades();
            case EXPLORER -> DwarfExplorerEntity.createRandomizedExplorerTrades();
            case MINER -> DwarfMinerEntity.createRandomizedMinerTrades();
            case ALCHEMIST -> DwarfAlchemistEntity.createRandomizedAlchemistTrades();
            case ARCANIST -> DwarfArcanistEntity.createRandomizedArcanistTrades();
            case PRIEST -> DwarfPriestEntity.createRandomizedArcanistTrades();
        };
    }

    public static DeferredItem<Item> getSpawnEggForProfession(DwarfProfession prof) {
        return switch (prof) {
            case NONE -> JolCraftItems.DWARF_SPAWN_EGG;
            case GUILDMASTER -> JolCraftItems.DWARF_GUILDMASTER_SPAWN_EGG;
            case HISTORIAN -> JolCraftItems.DWARF_HISTORIAN_SPAWN_EGG;
            case MERCHANT -> JolCraftItems.DWARF_MERCHANT_SPAWN_EGG;
            case SCRAPPER -> JolCraftItems.DWARF_SCRAPPER_SPAWN_EGG;
            case BREWMASTER -> JolCraftItems.DWARF_BREWMASTER_SPAWN_EGG;
            case GUARD -> JolCraftItems.DWARF_GUARD_SPAWN_EGG;
            case KEEPER -> JolCraftItems.DWARF_KEEPER_SPAWN_EGG;
            case ARTISAN -> JolCraftItems.DWARF_ARTISAN_SPAWN_EGG;
            case EXPLORER -> JolCraftItems.DWARF_EXPLORER_SPAWN_EGG;
            case MINER -> JolCraftItems.DWARF_MINER_SPAWN_EGG;
            case ALCHEMIST -> JolCraftItems.DWARF_ALCHEMIST_SPAWN_EGG;
            case ARCANIST -> JolCraftItems.DWARF_ARCANIST_SPAWN_EGG;
            case PRIEST -> JolCraftItems.DWARF_PRIEST_SPAWN_EGG;
        };
    }

    public static String getDisplayName(DwarfProfession prof) {
        if(prof.equals(DwarfProfession.NONE)) return Component.translatable("entity.jolcraft.dwarf").getString();
        return Component.translatable("entity.jolcraft.dwarf_" + prof.getId()).getString();
    }

    public static List<DwarfTradeRecipe> getAllDwarfJeiTrades(DwarfProfession prof) {
        List<DwarfTradeRecipe> recipes = new ArrayList<>();
        var trades = getTradesForProfession(prof);
        if (trades == null) return recipes;
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
                            prof, level, inputA, inputB, output, getSpawnEggForProfession(prof),
                            a[0], a[1], b[0], b[1], o[0], o[1]
                    ));
                }
            }
        }
        return recipes;
    }


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