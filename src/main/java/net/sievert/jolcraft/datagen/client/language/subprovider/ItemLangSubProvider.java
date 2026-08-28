package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.worldgen.JolCraftStructureIds;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;

import net.sievert.jolcraft.world.item.material.trim.JolCraftTrimPatterns;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.custom.alchemy.EssenceType;

@OnlyIn(Dist.CLIENT)
public final class ItemLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftStrings.plural(JolCraftDictionary.ITEM);
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }


    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        //Items

        for (EssenceType type : EssenceType.values()) {
            putManual(
                    translations,
                    type.translationKey(),
                    JolCraftStrings.toTitleCase(type.getId())
            );
        }

        putManual(translations, JolCraftItems.YEAST, "Brewing Yeast");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_BREWING_SPEED, "Brewing Speed: %sx");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_MAX_BREW_AGE, "Maximum Brew Age: %s");

        putSame(translations, "Ink and Quill",
                JolCraftItems.QUILL_FULL,
                JolCraftItems.QUILL_HALF,
                JolCraftItems.QUILL_SMALL
        );
        putManual(translations, JolCraftItems.QUILL_EMPTY, "Empty Ink and Quill");

        putSame(translations, "Reputation Tablet",
                JolCraftItems.REPUTATION_TABLET_0,
                JolCraftItems.REPUTATION_TABLET_1,
                JolCraftItems.REPUTATION_TABLET_2,
                JolCraftItems.REPUTATION_TABLET_3,
                JolCraftItems.REPUTATION_TABLET_4
        );

        putSame(translations, "Dwarven Tome",
                JolCraftItems.DWARVEN_TOME_COMMON,
                JolCraftItems.DWARVEN_TOME_UNCOMMON,
                JolCraftItems.DWARVEN_TOME_RARE,
                JolCraftItems.DWARVEN_TOME_EPIC
        );

        putSame(translations, "Ancient Dwarven Tome",
                JolCraftItems.ANCIENT_DWARVEN_TOME_COMMON,
                JolCraftItems.ANCIENT_DWARVEN_TOME_UNCOMMON,
                JolCraftItems.ANCIENT_DWARVEN_TOME_RARE,
                JolCraftItems.ANCIENT_DWARVEN_TOME_EPIC,
                JolCraftItems.ANCIENT_DWARVEN_TOME_LEGENDARY
        );

        putSame(translations, "Ancient Unidentified Dwarven Tome",
                JolCraftItems.UNIDENTIFIED_ANCIENT_DWARVEN_TOME,
                JolCraftItems.UNIDENTIFIED_LEGENDARY_ANCIENT_DWARVEN_TOME
        );

        putManual(translations, JolCraftItems.SCRAP_HEAP, "Heap of Scrap");

        putManualFlippedAll(translations, 
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

        // Rarities
        putManual(translations, JolCraftLanguageKeys.RARITY_COMMON, "Common");
        putManual(translations, JolCraftLanguageKeys.RARITY_UNCOMMON, "Uncommon");
        putManual(translations, JolCraftLanguageKeys.RARITY_RARE, "Rare");
        putManual(translations, JolCraftLanguageKeys.RARITY_EPIC, "Epic");
        putManual(translations, JolCraftLanguageKeys.RARITY_LEGENDARY, "Legendary");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_RARITY_NAME, "%1$s %2$s");

        // Tooltips
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_PESTLE_GRIND_SPEED, "Grinding Speed: %s");

        // Creative tabs
        putManual(translations, JolCraftLanguageKeys.JOLCRAFT_GENERAL_CREATIVE_TAB, JolCraft.MOD_NAME);

        // Structure maps
        putManual(translations, JolCraftStrings.dotted(BuiltInRegistries.ITEM.getKey(Items.FILLED_MAP).getPath(), JolCraftStructureIds.DWARVEN_FORTRESS),
                "Dwarven Fortress Map");

        //Trim Patterns
        addTrimTemplateItems(translations);

        for (DeferredHolder<?, ?> holder : JolCraftItems.ITEMS.getEntries()) {
            ResourceLocation id = holder.getId();

            String key = AbstractLanguageKeys.item(id.getPath());
            if (hasKey(translations, key)) continue;

            put(translations, key, JolCraftStrings.toTitleCase(id.getPath()));
        }
    }

    private void addTrimTemplateItems(@NotNull Map<String, String> translations) {
        String smithingTemplateName = JolCraftStrings.toTitleCase(
                JolCraftStrings.underscored(JolCraftDictionary.SMITHING, JolCraftDictionary.TEMPLATE)
        );

        for (JolCraftTrimPatterns.Entry entry : JolCraftTrimPatterns.entries()) {
            putItem(translations, entry.templateItem(), smithingTemplateName);
        }
    }
}