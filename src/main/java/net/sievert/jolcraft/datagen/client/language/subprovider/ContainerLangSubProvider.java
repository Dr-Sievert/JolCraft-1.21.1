package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;

import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;

import java.util.Map;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;

@OnlyIn(Dist.CLIENT)
public final class ContainerLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftStrings.plural(JolCraftDictionary.CONTAINER);
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }


    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        // -----------------------------------------------------------------
        // Lapidary Bench
        // -----------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.CONTAINER_LAPIDARY_BENCH, "Lapidary Bench");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_LAPIDARY_BENCH_CUT_GEMS_LOCKED, "You have no idea how to cut this gem without breaking it.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_CUT_LOCKED, "You have not learned how to cut gems!");

        // -----------------------------------------------------------------
        // Fermenting Cauldron
        // -----------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_FERMENTING_CAULDRON_INGREDIENT_MAX, "You have already added the maximum amount of this ingredient to the brew.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_FERMENTING_CAULDRON_LOCKED_MULTI, "Adding more ingredients without proper knowledge would ruin the brew.");

        // -----------------------------------------------------------------
        // Strongbox / Locks
        // -----------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.CONTAINER_STRONGBOX, "Strongbox");
        putManual(translations, JolCraftLanguageKeys.CONTAINER_STRONGBOX_LOCKED, "Locked Strongbox");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_LOCKPICK, "Used to lockpick strongboxes.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_STRONGBOX_NOT_EMPTY, "This strongbox has items inside.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_STRONGBOX_LOOT, "This strongbox has loot inside.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_STRONGBOX_SET_LOCKED, "You have locked this strongbox.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_STRONGBOX_SET_UNLOCKED, "You have unlocked this strongbox.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_STRONGBOX_LOCKED, "This strongbox is locked.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_STRONGBOX_BUSY, "Someone else is trying to pick this lock.");

        // -----------------------------------------------------------------
        // Hearth
        // -----------------------------------------------------------------

        putManual(translations, JolCraftLanguageKeys.TOOLTIP_HEARTH_OWNER, "You are not the owner of this hearth.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_HEARTH_COOLDOWN, "You can only light a hearth once per day.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_HEARTH_NEED_FUEL, "You need fuel with a long burn time to light this.");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_HEARTH_NOT_SAFE, "You cannot light this with monsters nearby!");
        putManual(translations, JolCraftLanguageKeys.TOOLTIP_HEARTH_NO_BED_NEARBY, "You need a claimed bed nearby to light this.");
    }
}