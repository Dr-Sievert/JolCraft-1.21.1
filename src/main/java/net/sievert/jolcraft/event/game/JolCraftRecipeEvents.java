package net.sievert.jolcraft.event.game;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.item.potion.JolCraftPotions;
import net.sievert.jolcraft.world.item.JolCraftItems;

import java.util.*;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftRecipeEvents {

    @SubscribeEvent
    public static void onBrewingRecipeRegister(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        int recipes = 0;

        builder.addMix(Potions.WATER, JolCraftItems.DEEPMARROW_DUST.get(), JolCraftPotions.ANCIENT_MEMORY); recipes++;
        builder.addMix(JolCraftPotions.ANCIENT_MEMORY, Items.REDSTONE, JolCraftPotions.LONG_ANCIENT_MEMORY); recipes++;

        builder.addMix(Potions.AWKWARD, JolCraftItems.SUNGLEAM_DUST.asItem(), JolCraftPotions.LOCKPICKING); recipes++;
        builder.addMix(JolCraftPotions.LOCKPICKING, Items.REDSTONE, JolCraftPotions.LONG_LOCKPICKING); recipes++;
        builder.addMix(JolCraftPotions.LOCKPICKING, Items.GLOWSTONE_DUST, JolCraftPotions.STRONG_LOCKPICKING); recipes++;

        builder.addMix(Potions.AWKWARD, JolCraftItems.EARTHBLOOD_DUST.asItem(), JolCraftPotions.DWARVEN_HASTE); recipes++;
        builder.addMix(JolCraftPotions.DWARVEN_HASTE, Items.REDSTONE, JolCraftPotions.LONG_DWARVEN_HASTE); recipes++;
        builder.addMix(JolCraftPotions.DWARVEN_HASTE, Items.GLOWSTONE_DUST, JolCraftPotions.STRONG_DWARVEN_HASTE); recipes++;

        builder.addMix(Potions.OOZING, JolCraftItems.RUSTAGATE_DUST.asItem(), JolCraftPotions.CORROSION); recipes++;
        builder.addMix(JolCraftPotions.CORROSION, Items.REDSTONE, JolCraftPotions.LONG_CORROSION); recipes++;
        builder.addMix(JolCraftPotions.CORROSION, Items.GLOWSTONE_DUST, JolCraftPotions.STRONG_CORROSION); recipes++;

        JolCraftLogs.info(JolCraftLogTags.RECIPE, "Registered {} brewing recipes", recipes);
    }

    @SubscribeEvent
    public static void registerCustomTrades(final VillagerTradesEvent event) {
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
