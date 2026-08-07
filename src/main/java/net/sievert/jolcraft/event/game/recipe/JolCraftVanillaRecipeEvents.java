package net.sievert.jolcraft.event.game.recipe;

import net.minecraft.core.Holder;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.potion.JolCraftPotions;

@SuppressWarnings({"removal", "SameParameterValue"})
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public final class JolCraftVanillaRecipeEvents {

    @SubscribeEvent
    public static void addVanillaBrewingRecipes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        int recipes = 0;

        builder.addMix(Potions.AWKWARD, Items.COOKED_BEEF.asItem(), JolCraftPotions.ENDURANCE); recipes++;
        builder.addMix(Potions.AWKWARD, Items.COOKED_PORKCHOP.asItem(), JolCraftPotions.ENDURANCE); recipes++;
        builder.addMix(Potions.AWKWARD, Items.PUMPKIN_PIE.asItem(), JolCraftPotions.ENDURANCE); recipes++;
        recipes += addLongStrong(builder, JolCraftPotions.ENDURANCE, JolCraftPotions.LONG_ENDURANCE, JolCraftPotions.STRONG_ENDURANCE);

        builder.addMix(Potions.AWKWARD, Items.IRON_BLOCK.asItem(), JolCraftPotions.ANCHOR); recipes++;
        recipes += addLong(builder, JolCraftPotions.ANCHOR, JolCraftPotions.LONG_ANCHOR);

        builder.addMix(Potions.AWKWARD, Items.WIND_CHARGE.asItem(), JolCraftPotions.MARKSMAN); recipes++;
        recipes += addLongStrong(builder, JolCraftPotions.MARKSMAN, JolCraftPotions.LONG_MARKSMAN, JolCraftPotions.STRONG_MARKSMAN);

        builder.addMix(Potions.AWKWARD, Items.AMETHYST_SHARD.asItem(), JolCraftPotions.ALCHEMIST_FOCUS); recipes++;
        recipes += addLongStrong(builder, JolCraftPotions.ALCHEMIST_FOCUS, JolCraftPotions.LONG_ALCHEMIST_FOCUS, JolCraftPotions.STRONG_ALCHEMIST_FOCUS);



        builder.addMix(Potions.AWKWARD, JolCraftItems.AEGISCORE_DUST.asItem(), JolCraftPotions.STONE_SKIN); recipes++;
        recipes += addLongStrong(builder, JolCraftPotions.STONE_SKIN, JolCraftPotions.LONG_STONE_SKIN, JolCraftPotions.STRONG_STONE_SKIN);

        builder.addMix(Potions.WATER, JolCraftItems.DEEPMARROW_DUST.get(), JolCraftPotions.ANCIENT_MEMORY); recipes++;
        recipes += addLong(builder, JolCraftPotions.ANCIENT_MEMORY, JolCraftPotions.LONG_ANCIENT_MEMORY);

        builder.addMix(Potions.AWKWARD, JolCraftItems.EARTHBLOOD_DUST.asItem(), JolCraftPotions.DWARVEN_HASTE); recipes++;
        recipes += addLongStrong(builder, JolCraftPotions.DWARVEN_HASTE, JolCraftPotions.LONG_DWARVEN_HASTE, JolCraftPotions.STRONG_DWARVEN_HASTE);

        builder.addMix(Potions.AWKWARD, JolCraftItems.GRIMSTONE_DUST.asItem(), JolCraftPotions.DWARVEN_RAGE); recipes++;
        recipes += addLongStrong(builder, JolCraftPotions.DWARVEN_RAGE, JolCraftPotions.LONG_DWARVEN_RAGE, JolCraftPotions.STRONG_DWARVEN_RAGE);

        builder.addMix(Potions.AWKWARD, JolCraftItems.IRONHEART_DUST.asItem(), JolCraftPotions.BULWARK); recipes++;
        recipes += addLongStrong(builder, JolCraftPotions.BULWARK, JolCraftPotions.LONG_BULWARK, JolCraftPotions.STRONG_BULWARK);

        builder.addMix(Potions.OOZING, JolCraftItems.RUSTAGATE_DUST.asItem(), JolCraftPotions.CORROSION); recipes++;
        recipes += addLongStrong(builder, JolCraftPotions.CORROSION, JolCraftPotions.LONG_CORROSION, JolCraftPotions.STRONG_CORROSION);

        builder.addMix(Potions.AWKWARD, JolCraftItems.SKYBURROW_DUST.asItem(), JolCraftPotions.DEXTERITY); recipes++;
        recipes += addLongStrong(builder, JolCraftPotions.DEXTERITY, JolCraftPotions.LONG_DEXTERITY, JolCraftPotions.STRONG_DEXTERITY);

        builder.addMix(Potions.AWKWARD, JolCraftItems.SUNGLEAM_DUST.asItem(), JolCraftPotions.LOCKPICKING); recipes++;
        recipes += addLongStrong(builder, JolCraftPotions.LOCKPICKING, JolCraftPotions.LONG_LOCKPICKING, JolCraftPotions.STRONG_LOCKPICKING);

        builder.addMix(Potions.AWKWARD, JolCraftItems.VERDANITE_DUST.asItem(), JolCraftPotions.POISON_RESISTANCE); recipes++;
        recipes += addLongStrong(builder, JolCraftPotions.POISON_RESISTANCE, JolCraftPotions.LONG_POISON_RESISTANCE, JolCraftPotions.STRONG_POISON_RESISTANCE);

        builder.addMix(Potions.AWKWARD, JolCraftItems.WOECRYSTAL_DUST.asItem(), JolCraftPotions.MAGIC_RESISTANCE); recipes++;
        recipes += addLongStrong(builder, JolCraftPotions.MAGIC_RESISTANCE, JolCraftPotions.LONG_MAGIC_RESISTANCE, JolCraftPotions.STRONG_MAGIC_RESISTANCE);



        builder.addMix(Potions.AWKWARD, JolCraftItems.BROKEN_COINS.get(), JolCraftPotions.UNLUCK); recipes++;
        recipes += addStrong(builder, JolCraftPotions.UNLUCK, JolCraftPotions.STRONG_UNLUCK);
        builder.addMix(JolCraftPotions.UNLUCK, JolCraftItems.INVERIX.get(), Potions.LUCK); recipes++;
        builder.addMix(JolCraftPotions.STRONG_UNLUCK, JolCraftItems.INVERIX.get(), JolCraftPotions.STRONG_LUCK); recipes++;
        recipes += addStrong(builder, Potions.LUCK, JolCraftPotions.STRONG_LUCK);
        builder.addMix(Potions.LUCK, JolCraftItems.INVERIX.get(), JolCraftPotions.UNLUCK); recipes++;
        builder.addMix(JolCraftPotions.STRONG_LUCK, JolCraftItems.INVERIX.get(), JolCraftPotions.STRONG_UNLUCK); recipes++;

        JolCraftLogs.info(JolCraftLogTags.RECIPE, "Registered {} brewing recipes", recipes);
    }

    private static int addLong(
            PotionBrewing.Builder builder,
            Holder<Potion> potion,
            Holder<Potion> longPotion
    ) {
        builder.addMix(potion, Items.REDSTONE, longPotion);
        return 1;
    }

    private static int addStrong(
            PotionBrewing.Builder builder,
            Holder<Potion> potion,
            Holder<Potion> strongPotion
    ) {
        builder.addMix(potion, Items.GLOWSTONE_DUST, strongPotion);
        return 1;
    }

    private static int addLongStrong(
            PotionBrewing.Builder builder,
            Holder<Potion> potion,
            Holder<Potion> longPotion,
            Holder<Potion> strongPotion
    ) {
        builder.addMix(potion, Items.REDSTONE, longPotion);
        builder.addMix(potion, Items.GLOWSTONE_DUST, strongPotion);
        return 2;
    }
}