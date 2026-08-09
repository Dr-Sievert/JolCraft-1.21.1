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
        clearSpecialRecipes();

        int recipes = 0;

        // Endurance

        recipes += addStartMix(builder,
                List.of(
                        Items.COOKED_BEEF,
                        Items.COOKED_PORKCHOP,
                        Items.PUMPKIN_PIE
                ),
                JolCraftPotions.ENDURANCE
        );

        // Anchor

        recipes += addStartMix(builder,
                Items.IRON_BLOCK,
                JolCraftPotions.ANCHOR
        );

        // Marksman

        recipes += addStartMix(builder,
                Items.WIND_CHARGE,
                JolCraftPotions.MARKSMAN
        );

        // Alchemist Focus

        recipes += addStartMix(builder,
                Items.AMETHYST_SHARD,
                JolCraftPotions.ALCHEMIST_FOCUS
        );

        // Lockpicking

        recipes += addStartMix(builder,
                JolCraftBlocks.DUSKCAP,
                JolCraftPotions.LOCKPICKING
        );

        // Stone Skin

        recipes += addStartMix(builder,
                JolCraftItems.AEGISCORE_DUST,
                JolCraftPotions.STONE_SKIN
        );

        // Might

        recipes += addStartMix(builder,
                JolCraftItems.ASHFANG_DUST,
                JolCraftPotions.MIGHT
        );

        // Ancient Memory

        recipes += addStartMix(builder,
                JolCraftItems.DEEPMARROW_DUST,
                JolCraftPotions.ANCIENT_MEMORY
        );

        // Dwarven Haste

        recipes += addStartMix(builder,
                JolCraftItems.EARTHBLOOD_DUST,
                JolCraftPotions.DWARVEN_HASTE
        );

        // Slow Resistance

        recipes += addStartMix(builder,
                JolCraftItems.FROSTVEIN_DUST,
                JolCraftPotions.SLOW_RESISTANCE
        );

        // Dwarven Rage

        recipes += addStartMix(builder,
                JolCraftItems.GRIMSTONE_DUST,
                JolCraftPotions.DWARVEN_RAGE
        );

        // Bulwark

        recipes += addStartMix(builder,
                JolCraftItems.IRONHEART_DUST,
                JolCraftPotions.BULWARK
        );

        // Corrosion

        recipes += addStartMix(builder,
                JolCraftItems.RUSTAGATE_DUST,
                JolCraftPotions.CORROSION
        );

        // Piercing

        recipes += addFamilyMix(builder,
                Potions.OOZING,
                JolCraftItems.RUSTAGATE_DUST,
                JolCraftPotions.PIERCING
        );

        // Dexterity

        recipes += addStartMix(builder,
                JolCraftItems.SKYBURROW_DUST,
                JolCraftPotions.DEXTERITY
        );

        // Hoard

        recipes += addFamilyMix(builder,
                Potions.LUCK,
                JolCraftItems.SUNGLEAM_DUST,
                JolCraftPotions.HOARD
        );

        // Wisdom

        recipes += addFamilyMix(builder,
                Items.EXPERIENCE_BOTTLE,
                JolCraftItems.DEEPMARROW_DUST,
                JolCraftPotions.WISDOM
        );

        // Poison Resistance

        recipes += addFamilyMix(builder,
                Items.HONEY_BOTTLE,
                JolCraftItems.VERDANITE_DUST,
                JolCraftPotions.POISON_RESISTANCE
        );

        // Frost Resistance

        recipes += addFamilyMix(builder,
                Items.HONEY_BOTTLE,
                JolCraftItems.FROSTVEIN_DUST,
                JolCraftPotions.FROST_RESISTANCE
        );

        // Wither Resistance

        recipes += addFamilyMix(builder,
                Items.HONEY_BOTTLE,
                Items.WITHER_ROSE,
                JolCraftPotions.WITHER_RESISTANCE
        );

        // Explosion Resistance

        recipes += addFamilyMix(builder,
                Potions.STRENGTH,
                JolCraftItems.AEGISCORE_DUST,
                JolCraftPotions.EXPLOSION_RESISTANCE
        );

        // Fire Resistance (Strong only)

        recipes += addStrong(builder,
                Potions.FIRE_RESISTANCE,
                JolCraftPotions.STRONG_FIRE_RESISTANCE
        );

        // Harvest

        recipes += addStartMix(builder,
                JolCraftItems.VERDANITE_DUST,
                JolCraftPotions.HARVEST
        );

        // Lunar

        recipes += addStartMix(builder,
                JolCraftItems.MOONSHARD_DUST,
                JolCraftPotions.LUNAR
        );

        // Conflagration

        recipes += addStartMix(builder,
                JolCraftItems.SUNGLEAM_DUST,
                JolCraftPotions.CONFLAGRATION
        );

        // Sunfire

        recipes += addFamilyMix(builder,
                Potions.HARMING,
                JolCraftItems.SUNGLEAM_DUST,
                JolCraftPotions.SUNFIRE
        );

        // Vitality

        recipes += addStartMix(builder,
                JolCraftItems.EMBERGLASS_DUST,
                JolCraftPotions.OVERHEAL
        );

        // Luminance

        recipes += addStartMix(builder,
                JolCraftItems.LUMIERE_DUST,
                JolCraftPotions.LUMINANCE
        );


        // Magic Resistance

        recipes += addStartMix(builder,
                JolCraftItems.WOECRYSTAL_DUST,
                JolCraftPotions.MAGIC_RESISTANCE
        );

        // Tenacity

        recipes += addStartMix(builder,
                Items.SHULKER_SHELL,
                JolCraftPotions.TENACITY
        );

        // Vulnerabilities

        recipes += addFamilyMix(builder,
                JolCraftPotions.EXPLOSION_RESISTANCE,
                JolCraftItems.INVERIX,
                JolCraftPotions.EXPLOSION_VULNERABILITY
        );

        recipes += addFamilyMix(builder,
                Potions.FIRE_RESISTANCE,
                JolCraftItems.INVERIX,
                JolCraftPotions.FIRE_VULNERABILITY
        );

        recipes += addFamilyMix(builder,
                JolCraftPotions.FROST_RESISTANCE,
                JolCraftItems.INVERIX,
                JolCraftPotions.FROST_VULNERABILITY
        );

        recipes += addFamilyMix(builder,
                JolCraftPotions.MAGIC_RESISTANCE,
                JolCraftItems.INVERIX,
                JolCraftPotions.MAGIC_VULNERABILITY
        );

        recipes += addFamilyMix(builder,
                JolCraftPotions.POISON_RESISTANCE,
                JolCraftItems.INVERIX,
                JolCraftPotions.POISON_VULNERABILITY
        );

        recipes += addFamilyMix(builder,
                JolCraftPotions.SLOW_RESISTANCE,
                JolCraftItems.INVERIX,
                JolCraftPotions.SLOW_VULNERABILITY
        );

        recipes += addFamilyMix(builder,
                JolCraftPotions.WITHER_RESISTANCE,
                JolCraftItems.INVERIX,
                JolCraftPotions.WITHER_VULNERABILITY
        );

        // Unluck

        recipes += addStartMix(builder,
                JolCraftItems.BROKEN_COINS,
                JolCraftPotions.UNLUCK
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