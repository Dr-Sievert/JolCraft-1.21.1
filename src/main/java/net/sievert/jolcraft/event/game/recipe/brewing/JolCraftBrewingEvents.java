package net.sievert.jolcraft.event.game.recipe.brewing;

import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.potion.JolCraftPotions;

import java.util.List;

import static net.sievert.jolcraft.event.game.recipe.brewing.JolCraftBrewingEventHelper.*;

public final class JolCraftBrewingEvents {

    private JolCraftBrewingEvents() {}

    public static void register(PotionBrewing.Builder builder) {
        int recipes = 0;

        // Endurance

        recipes += addStartMix(builder,
                List.of(
                        Items.COOKED_BEEF,
                        Items.COOKED_PORKCHOP,
                        Items.PUMPKIN_PIE
                ),
                JolCraftPotions.ENDURANCE,
                JolCraftPotions.LONG_ENDURANCE,
                JolCraftPotions.STRONG_ENDURANCE
        );

        // Anchor

        recipes += addStartMix(builder,
                Items.IRON_BLOCK,
                JolCraftPotions.ANCHOR,
                JolCraftPotions.LONG_ANCHOR
        );

        // Marksman

        recipes += addStartMix(builder,
                Items.WIND_CHARGE,
                JolCraftPotions.MARKSMAN,
                JolCraftPotions.LONG_MARKSMAN,
                JolCraftPotions.STRONG_MARKSMAN
        );

        // Alchemist Focus

        recipes += addStartMix(builder,
                Items.AMETHYST_SHARD,
                JolCraftPotions.ALCHEMIST_FOCUS,
                JolCraftPotions.LONG_ALCHEMIST_FOCUS,
                JolCraftPotions.STRONG_ALCHEMIST_FOCUS
        );

        // Lockpicking

        recipes += addStartMix(builder,
                JolCraftBlocks.DUSKCAP,
                JolCraftPotions.LOCKPICKING,
                JolCraftPotions.LONG_LOCKPICKING,
                JolCraftPotions.STRONG_LOCKPICKING
        );

        // Stone Skin

        recipes += addStartMix(builder,
                JolCraftItems.AEGISCORE_DUST,
                JolCraftPotions.STONE_SKIN,
                JolCraftPotions.LONG_STONE_SKIN,
                JolCraftPotions.STRONG_STONE_SKIN
        );

        // Might

        recipes += addStartMix(builder,
                JolCraftItems.ASHFANG_DUST,
                JolCraftPotions.MIGHT,
                JolCraftPotions.LONG_MIGHT,
                JolCraftPotions.STRONG_MIGHT
        );

        // Ancient Memory

        recipes += addStartMix(builder,
                JolCraftItems.DEEPMARROW_DUST,
                JolCraftPotions.ANCIENT_MEMORY,
                JolCraftPotions.LONG_ANCIENT_MEMORY
        );

        // Dwarven Haste

        recipes += addStartMix(builder,
                JolCraftItems.EARTHBLOOD_DUST,
                JolCraftPotions.DWARVEN_HASTE,
                JolCraftPotions.LONG_DWARVEN_HASTE,
                JolCraftPotions.STRONG_DWARVEN_HASTE
        );

        // Slow Resistance

        recipes += addStartMix(builder,
                JolCraftItems.FROSTVEIN_DUST,
                JolCraftPotions.SLOW_RESISTANCE,
                JolCraftPotions.LONG_SLOW_RESISTANCE,
                JolCraftPotions.STRONG_SLOW_RESISTANCE
        );

        // Dwarven Rage

        recipes += addStartMix(builder,
                JolCraftItems.GRIMSTONE_DUST,
                JolCraftPotions.DWARVEN_RAGE,
                JolCraftPotions.LONG_DWARVEN_RAGE,
                JolCraftPotions.STRONG_DWARVEN_RAGE
        );

        // Bulwark

        recipes += addStartMix(builder,
                JolCraftItems.IRONHEART_DUST,
                JolCraftPotions.BULWARK,
                JolCraftPotions.LONG_BULWARK,
                JolCraftPotions.STRONG_BULWARK
        );

        // Corrosion

        recipes += addStartMix(builder,
                JolCraftItems.RUSTAGATE_DUST,
                JolCraftPotions.CORROSION,
                JolCraftPotions.LONG_CORROSION,
                JolCraftPotions.STRONG_CORROSION
        );

        // Piercing

        recipes += addMix(builder,
                Potions.OOZING,
                JolCraftItems.RUSTAGATE_DUST,
                JolCraftPotions.PIERCING,
                JolCraftPotions.LONG_PIERCING,
                JolCraftPotions.STRONG_PIERCING
        );

        // Dexterity

        recipes += addStartMix(builder,
                JolCraftItems.SKYBURROW_DUST,
                JolCraftPotions.DEXTERITY,
                JolCraftPotions.LONG_DEXTERITY,
                JolCraftPotions.STRONG_DEXTERITY
        );

        // Hoard

        recipes += addMix(builder,
                Potions.LUCK,
                JolCraftItems.SUNGLEAM_DUST,
                JolCraftPotions.HOARD,
                JolCraftPotions.LONG_HOARD,
                JolCraftPotions.STRONG_HOARD
        );

        // Wisdom

        recipes += addMix(builder,
                Items.EXPERIENCE_BOTTLE,
                JolCraftItems.DEEPMARROW_DUST,
                JolCraftPotions.WISDOM,
                JolCraftPotions.LONG_WISDOM,
                JolCraftPotions.STRONG_WISDOM
        );

        // Poison Resistance

        recipes += addMix(builder,
                Items.HONEY_BOTTLE,
                JolCraftItems.VERDANITE_DUST,
                JolCraftPotions.POISON_RESISTANCE,
                JolCraftPotions.LONG_POISON_RESISTANCE,
                JolCraftPotions.STRONG_POISON_RESISTANCE
        );

        // Harvest

        recipes += addStartMix(builder,
                JolCraftItems.VERDANITE_DUST,
                JolCraftPotions.HARVEST,
                JolCraftPotions.LONG_HARVEST,
                JolCraftPotions.STRONG_HARVEST
        );

        // Lunar

        recipes += addStartMix(builder,
                JolCraftItems.MOONSHARD_DUST,
                JolCraftPotions.LUNAR,
                JolCraftPotions.LONG_LUNAR,
                JolCraftPotions.STRONG_LUNAR
        );

        // Sunfire

        recipes += addMix(builder,
                Potions.FIRE_RESISTANCE,
                JolCraftItems.SUNGLEAM_DUST,
                JolCraftPotions.SUNFIRE,
                JolCraftPotions.LONG_SUNFIRE,
                JolCraftPotions.STRONG_SUNFIRE
        );

        // Vitality

        recipes += addStartMix(builder,
                JolCraftItems.EMBERGLASS_DUST,
                JolCraftPotions.VITALITY,
                JolCraftPotions.LONG_VITALITY,
                JolCraftPotions.STRONG_VITALITY
        );

        // Radiant

        recipes += addStartMix(builder,
                JolCraftItems.LUMIERE_DUST,
                JolCraftPotions.RADIANT,
                JolCraftPotions.LONG_RADIANT,
                JolCraftPotions.STRONG_RADIANT
        );


        // Magic Resistance

        recipes += addStartMix(builder,
                JolCraftItems.WOECRYSTAL_DUST,
                JolCraftPotions.MAGIC_RESISTANCE,
                JolCraftPotions.LONG_MAGIC_RESISTANCE,
                JolCraftPotions.STRONG_MAGIC_RESISTANCE
        );

        // Tenacity

        recipes += addStartMix(builder,
                Items.SHULKER_SHELL,
                JolCraftPotions.TENACITY,
                JolCraftPotions.LONG_TENACITY,
                JolCraftPotions.STRONG_TENACITY
        );

        // Unluck

        recipes += addStartStrongMix(builder,
                JolCraftItems.BROKEN_COINS,
                JolCraftPotions.UNLUCK,
                JolCraftPotions.STRONG_UNLUCK
        );

        // Luck

        recipes += addMix(builder,
                JolCraftPotions.UNLUCK,
                JolCraftItems.INVERIX,
                Potions.LUCK
        );
        recipes += addMix(builder,
                JolCraftPotions.STRONG_UNLUCK,
                JolCraftItems.INVERIX,
                JolCraftPotions.STRONG_LUCK
        );
        recipes += addStrong(builder,
                Potions.LUCK,
                JolCraftPotions.STRONG_LUCK
        );
        recipes += addMix(builder,
                Potions.LUCK,
                JolCraftItems.INVERIX,
                JolCraftPotions.UNLUCK
        );
        recipes += addMix(builder,
                JolCraftPotions.STRONG_LUCK,
                JolCraftItems.INVERIX,
                JolCraftPotions.STRONG_UNLUCK
        );

        JolCraftLogs.info(JolCraftLogTags.RECIPE, "Registered {} brewing recipes", recipes);
    }
}