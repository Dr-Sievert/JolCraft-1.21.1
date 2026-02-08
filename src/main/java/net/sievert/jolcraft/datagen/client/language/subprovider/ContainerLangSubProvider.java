package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;

@OnlyIn(Dist.CLIENT)
public final class ContainerLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        // -----------------------------------------------------------------
        // Lapidary Bench
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.CONTAINER_LAPIDARY_BENCH, "Lapidary Bench");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_LAPIDARY_BENCH_LOCKED_CUT_GEMS, "You have no idea how to cut this gem without breaking it.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_GEODE, "Can be broken into dust using an artisan hammer at a lapidary bench.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_UNCUT_GEM, "Can be broken into dust using an artisan hammer or cut using a chisel at a lapidary bench.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_ARTISAN_HAMMER, "Can be used to break geodes and gems at a lapidary bench.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_CHISEL, "Can be used to cut gems at a lapidary bench.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_CUT_LOCKED, "You have not learned how to cut gems!");

        // -----------------------------------------------------------------
        // Fermenting Cauldron
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.TOOLTIP_FERMENTING_CAULDRON_INGREDIENT_MAX, "You already added the max amount of this ingredient to the brew.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_FERMENTING_CAULDRON_LOCKED_MULTI, "Adding more ingredients without proper knowledge would ruin the brew.");

        // -----------------------------------------------------------------
        // Strongbox / Locks
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.CONTAINER_STRONGBOX, "Strongbox");
        p.putManual(JolCraftLanguageKeys.CONTAINER_STRONGBOX_LOCKED, "Locked Strongbox");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_LOCKPICK, "Used to pick locks. Will break on failure. Lockpicking is easier when using potions.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_STRONGBOX_NOT_EMPTY, "This strongbox has items inside.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_STRONGBOX_LOOT, "This strongbox has loot inside.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_STRONGBOX_SET_LOCKED, "You have locked this strongbox.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_STRONGBOX_SET_UNLOCKED, "You have unlocked this strongbox.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_STRONGBOX_LOCKED, "This strongbox is locked.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_STRONGBOX_BUSY, "Someone else is trying to pick this lock.");

        // -----------------------------------------------------------------
        // Hearth
        // -----------------------------------------------------------------

        p.putManual(JolCraftLanguageKeys.TOOLTIP_HEARTH_COOLDOWN, "You must rest before light a hearth.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_HEARTH_NEED_COAL, "You need coal to light this.");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_HEARTH_NOT_SAFE, "Cannot light with monsters nearby!");
        p.putManual(JolCraftLanguageKeys.TOOLTIP_HEARTH_NO_BED_NEARBY, "No claimed bed nearby.");
    }
}