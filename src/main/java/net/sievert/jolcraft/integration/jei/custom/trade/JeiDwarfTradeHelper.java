package net.sievert.jolcraft.integration.jei.custom.trade;

import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.config.dwarf.DwarfProfessionConfigs;
import net.sievert.jolcraft.data.recipe.custom.DwarfTradeRecipe;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfTrades;
import net.sievert.jolcraft.world.item.JolCraftItems;

import java.util.ArrayList;
import java.util.List;

public final class JeiDwarfTradeHelper {

    private JeiDwarfTradeHelper() {}

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
        if (prof == DwarfProfession.NONE) {
            return Component.translatable("entity.jolcraft.dwarf").getString();
        }
        return Component.translatable("entity.jolcraft.dwarf_" + prof.getId()).getString();
    }

    /**
     * JEI should show trades exactly as data-driven content:
     * MAIN + POOL + RESTOCK_POOL recipes exist, but JEI should show all recipes
     * (pool type can be shown in tooltip later if you want).
     * This returns one entry per actual recipe (no legacy "rolled listings").
     */
    public static List<JeiDwarfTrade> getAllDwarfJeiTrades(DwarfProfession prof) {
        ServerLevel serverLevel = getClientServerLevelOrNull();
        if (serverLevel == null) {
            return List.of();
        }

        List<JeiDwarfTrade> out = new ArrayList<>();

        int maxLevel = 5;
        DwarfProfessionConfigs.get(prof);

        for (int lvl = 1; lvl <= maxLevel; lvl++) {
            for (var pool : DwarfTradeRecipe.TradePool.values()) {
                for (var holder : DwarfTrades.getTradeRecipesAtLevel(serverLevel, prof, pool, lvl)) {
                    out.add(new JeiDwarfTrade(holder.value(), getSpawnEggForProfession(prof)));
                }
            }
        }

        return out;
    }

    public static RegistryAccess getClientRegistryAccess() {
        var level = Minecraft.getInstance().level;
        return level != null ? level.registryAccess() : RegistryAccess.EMPTY;
    }

    private static ServerLevel getClientServerLevelOrNull() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSingleplayerServer() == null) return null;
        return mc.getSingleplayerServer().overworld();
    }
}
