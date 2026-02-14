package net.sievert.jolcraft.integration.jei.custom.trade;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.sievert.jolcraft.config.custom.dwarf.DwarfProfessionConfigs;
import net.sievert.jolcraft.data.recipe.custom.DwarfTradeRecipe;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.profession.DwarfProfessionHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.trade.DwarfTrades;

import java.util.ArrayList;
import java.util.List;

public final class JeiDwarfTradeHelper {

    private JeiDwarfTradeHelper() {}

    public static List<JeiDwarfTrade> getAllDwarfJeiTrades(DwarfProfession prof) {
        ServerLevel serverLevel = getClientServerLevelOrNull();
        if (serverLevel == null) return List.of();

        DwarfProfessionConfigs.get(prof);

        List<JeiDwarfTrade> out = new ArrayList<>();
        int maxLevel = 5;

        for (int lvl = 1; lvl <= maxLevel; lvl++) {
            for (var pool : DwarfTradeRecipe.TradePool.values()) {
                for (var holder : DwarfTrades.getTradeRecipesAtLevel(serverLevel, prof, pool, lvl)) {
                    out.add(new JeiDwarfTrade(holder.value(), DwarfProfessionHelper.getSpawnEgg(prof)));
                }
            }
        }

        return out;
    }

    private static ServerLevel getClientServerLevelOrNull() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSingleplayerServer() == null) return null;
        return mc.getSingleplayerServer().overworld();
    }
}