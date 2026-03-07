package net.sievert.jolcraft.integration.jei.custom.dwarf_trade;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.sievert.jolcraft.data.recipe.custom.dwarf_trade.DwarfTradeRecipe;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfession;
import net.sievert.jolcraft.world.entity.custom.dwarf.profession.DwarfProfessionHelper;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfMerchantData;
import net.sievert.jolcraft.world.entity.custom.dwarf.trade.DwarfTrades;

import java.util.ArrayList;
import java.util.List;

public final class JeiDwarfTradeHelper {

    private JeiDwarfTradeHelper() {}

    public static List<JeiDwarfTrade> getAllDwarfJeiTrades(DwarfProfession profession) {
        ServerLevel serverLevel = getClientServerLevelOrNull();
        if (serverLevel == null) {
            return List.of();
        }

        List<JeiDwarfTrade> out = new ArrayList<>();

        for (DwarfMerchantData.Level level : DwarfMerchantData.Level.values()) {
            List<RecipeHolder<DwarfTradeRecipe>> holders =
                    DwarfTrades.getTradeRecipesAtLevel(serverLevel, profession, level);

            for (RecipeHolder<DwarfTradeRecipe> holder : holders) {
                out.add(new JeiDwarfTrade(
                        holder.value(),
                        DwarfProfessionHelper.getSpawnEgg(profession)
                ));
            }
        }

        return List.copyOf(out);
    }

    private static ServerLevel getClientServerLevelOrNull() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSingleplayerServer() == null) {
            return null;
        }
        return mc.getSingleplayerServer().overworld();
    }
}