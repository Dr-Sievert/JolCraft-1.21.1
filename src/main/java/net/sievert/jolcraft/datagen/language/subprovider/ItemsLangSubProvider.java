package net.sievert.jolcraft.datagen.language.subprovider;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.world.item.JolCraftItems;

public final class ItemsLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    public static final String JOLCRAFT_GENERAL_CREATIVE_TAB = "jolcraft_items_tab";
    public static final String JOLCRAFT_GENERAL_CREATIVE_TAB_KEY = itemGroup(JOLCRAFT_GENERAL_CREATIVE_TAB);
    public static final String JOLCRAFT_EGG_CREATIVE_TAB = "jolcraft_egg_tab";
    public static final String JOLCRAFT_EGG_CREATIVE_TAB_KEY = itemGroup(JOLCRAFT_EGG_CREATIVE_TAB);

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        // Creative tabs
        p.putManual(JOLCRAFT_GENERAL_CREATIVE_TAB_KEY, "JolCraft");
        p.putManual(JOLCRAFT_EGG_CREATIVE_TAB_KEY, "JolCraft Spawn Eggs");

        // Structure maps
        p.putManual("filled_map.forge", "Map to a Dwarven Forge");

        //Items

        p.putManual(JolCraftItems.YEAST, "Brewing Yeast");

        p.putSame("Ink and Quill",
                JolCraftItems.QUILL_FULL,
                JolCraftItems.QUILL_HALF,
                JolCraftItems.QUILL_SMALL
        );
        p.putManual(JolCraftItems.QUILL_EMPTY, "Empty Ink and Quill");

        p.putSame("Reputation Tablet",
                JolCraftItems.REPUTATION_TABLET_0,
                JolCraftItems.REPUTATION_TABLET_1,
                JolCraftItems.REPUTATION_TABLET_2,
                JolCraftItems.REPUTATION_TABLET_3,
                JolCraftItems.REPUTATION_TABLET_4
        );

        p.putSame("Dwarven Tome",
                JolCraftItems.DWARVEN_TOME_COMMON,
                JolCraftItems.DWARVEN_TOME_UNCOMMON,
                JolCraftItems.DWARVEN_TOME_RARE,
                JolCraftItems.DWARVEN_TOME_EPIC
        );

        p.putSame("Ancient Dwarven Tome",
                JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON,
                JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON,
                JolCraftItems.ANCIENT_DWARVEN_TOME_RARE,
                JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC,
                JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY
        );

        p.putSame("Ancient Unidentified Dwarven Tome",
                JolCraftItems.ANCIENT_UNIDENTIFIED_DWARVEN_TOME,
                JolCraftItems.LEGENDARY_ANCIENT_UNIDENTIFIED_DWARVEN_TOME
        );

        p.putManual(JolCraftItems.SCRAP_HEAP, "Heap of Scrap");
        p.putManual(JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE, "Forge Armor Trim");

        p.putManualFlippedAll(
                JolCraftItems.CONTRACT_BLANK,
                JolCraftItems.CONTRACT_WRITTEN,
                JolCraftItems.CONTRACT_SIGNED,
                JolCraftItems.CONTRACT_GUILDMASTER,
                JolCraftItems.CONTRACT_HISTORIAN,
                JolCraftItems.CONTRACT_MERCHANT,
                JolCraftItems.CONTRACT_SCRAPPER,
                JolCraftItems.CONTRACT_BREWMASTER,
                JolCraftItems.CONTRACT_GUARD,
                JolCraftItems.CONTRACT_KEEPER,
                JolCraftItems.CONTRACT_ARTISAN,
                JolCraftItems.CONTRACT_EXPLORER,
                JolCraftItems.CONTRACT_MINER,
                JolCraftItems.CONTRACT_ALCHEMIST,
                JolCraftItems.CONTRACT_ARCANIST,
                JolCraftItems.CONTRACT_PRIEST,
                JolCraftItems.CONTRACT_BLACKSMITH,
                JolCraftItems.CONTRACT_CHAMPION,
                JolCraftItems.CONTRACT_SMELTER,

                JolCraftItems.GEODE_SMALL,
                JolCraftItems.GEODE_MEDIUM,
                JolCraftItems.GEODE_LARGE,

                JolCraftItems.AEGISCORE_CUT,
                JolCraftItems.ASHFANG_CUT,
                JolCraftItems.DEEPMARROW_CUT,
                JolCraftItems.EARTHBLOOD_CUT,
                JolCraftItems.EMBERGLASS_CUT,
                JolCraftItems.FROSTVEIN_CUT,
                JolCraftItems.GRIMSTONE_CUT,
                JolCraftItems.IRONHEART_CUT,
                JolCraftItems.LUMIERE_CUT,
                JolCraftItems.MOONSHARD_CUT,
                JolCraftItems.RUSTAGATE_CUT,
                JolCraftItems.SKYBURROW_CUT,
                JolCraftItems.SUNGLEAM_CUT,
                JolCraftItems.VERDANITE_CUT,
                JolCraftItems.WOECRYSTAL_CUT
        );

        for (DeferredHolder<?, ?> holder : JolCraftItems.ITEMS.getEntries()) {
            ResourceLocation id = holder.getId();

            String key = "item." + id.getNamespace() + "." + id.getPath();
            if (p.hasKey(key)) continue;

            p.put(key, AbstractLanguageProvider.toTitleCase(id.getPath()));
        }
    }

    public static String itemGroup(String path) { return "itemGroup." + JolCraft.MOD_ID + "." + path; }

}
