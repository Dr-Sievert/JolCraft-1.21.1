package net.sievert.jolcraft.event.game.recipe;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.item.potion.JolCraftPotions;
import net.sievert.jolcraft.world.item.JolCraftItems;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftVanillaRecipeEvents {

    @SubscribeEvent
    public static void addVanillaBrewingRecipes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        int recipes = 0;

        builder.addMix(Potions.AWKWARD, JolCraftItems.BROKEN_COINS.get(), JolCraftPotions.UNLUCK); recipes++;
        builder.addMix(JolCraftPotions.UNLUCK, Items.GLOWSTONE_DUST, JolCraftPotions.STRONG_UNLUCK); recipes++;
        builder.addMix(JolCraftPotions.UNLUCK, JolCraftItems.INVERIX.get(), Potions.LUCK); recipes++;
        builder.addMix(JolCraftPotions.STRONG_UNLUCK, JolCraftItems.INVERIX.get(), JolCraftPotions.STRONG_LUCK); recipes++;
        builder.addMix(Potions.LUCK, Items.GLOWSTONE_DUST, JolCraftPotions.STRONG_LUCK); recipes++;
        builder.addMix(Potions.LUCK, JolCraftItems.INVERIX.get(), JolCraftPotions.UNLUCK); recipes++;
        builder.addMix(JolCraftPotions.STRONG_LUCK, JolCraftItems.INVERIX.get(), JolCraftPotions.STRONG_UNLUCK); recipes++;

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
}
