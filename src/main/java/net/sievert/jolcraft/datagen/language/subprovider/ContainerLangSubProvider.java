package net.sievert.jolcraft.datagen.language.subprovider;

import net.sievert.jolcraft.datagen.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.datagen.language.util.JolCraftLanguageCategory;
import net.sievert.jolcraft.datagen.language.util.JolCraftLanguageKeys;

public final class ContainerLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    // ---------------------------------------------------------------------
    // Lapidary Bench tooltips
    // ---------------------------------------------------------------------

    public static final String LAPIDARY_BENCH = "lapidary_bench";
    public static final String CONTAINER_LAPIDARY_BENCH = JolCraftLanguageKeys.container(LAPIDARY_BENCH);
    public static final String TOOLTIP_LAPIDARY_BENCH_LOCKED_CUT_GEMS = JolCraftLanguageKeys.tooltip(LAPIDARY_BENCH, "locked_cut_gems");
    public static final String TOOLTIP_GEODE = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "geode");
    public static final String TOOLTIP_UNCUT_GEM = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "uncut_gem");
    public static final String TOOLTIP_ARTISAN_HAMMER = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "artisan_hammer");
    public static final String TOOLTIP_CUT_GEM = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "cut_gem");
    public static final String TOOLTIP_CHISEL = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "chisel");
    public static final String TOOLTIP_CUT_LOCKED = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "cut_locked");

    // ---------------------------------------------------------------------
    // Fermenting Cauldron tooltips (tooltip.<modid>.fermenting_cauldron.<path>)
    // ---------------------------------------------------------------------

    public static final String FERMENTING_CAULDRON = "fermenting_cauldron";
    public static final String TOOLTIP_FERMENTING_CAULDRON_INGREDIENT_MAX = JolCraftLanguageKeys.tooltip(FERMENTING_CAULDRON, "ingredient_max");
    public static final String TOOLTIP_FERMENTING_CAULDRON_LOCKED_MULTI = JolCraftLanguageKeys.tooltip(FERMENTING_CAULDRON, "locked_multi");

    // ---------------------------------------------------------------------
    // Strongbox tooltips (tooltip.<modid>.strongbox.<path>)
    // ---------------------------------------------------------------------

    public static final String STRONGBOX = "strongbox";
    public static final String CONTAINER_STRONGBOX = JolCraftLanguageKeys.container(STRONGBOX);
    public static final String CONTAINER_STRONGBOX_LOCKED = JolCraftLanguageKeys.container("strongbox_locked");
    public static final String TOOLTIP_LOCKPICK = JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "lockpick");
    public static final String TOOLTIP_STRONGBOX_NOT_EMPTY = JolCraftLanguageKeys.tooltip(STRONGBOX, "not_empty");
    public static final String TOOLTIP_STRONGBOX_LOOT = JolCraftLanguageKeys.tooltip(STRONGBOX, "loot");
    public static final String TOOLTIP_STRONGBOX_SET_LOCKED = JolCraftLanguageKeys.tooltip(STRONGBOX, "set_locked");
    public static final String TOOLTIP_STRONGBOX_SET_UNLOCKED = JolCraftLanguageKeys.tooltip(STRONGBOX, "set_unlocked");
    public static final String TOOLTIP_STRONGBOX_LOCKED = JolCraftLanguageKeys.tooltip(STRONGBOX, "locked");
    public static final String TOOLTIP_STRONGBOX_BUSY = JolCraftLanguageKeys.tooltip(STRONGBOX, "busy");

    // ---------------------------------------------------------------------
    // Hearth tooltips (tooltip.<modid>.hearth.<path>)
    // ---------------------------------------------------------------------

    public static final String HEARTH = "hearth";
    public static final String TOOLTIP_HEARTH_COOLDOWN = JolCraftLanguageKeys.tooltip(HEARTH, "cooldown");
    public static final String TOOLTIP_HEARTH_NEED_COAL = JolCraftLanguageKeys.tooltip(HEARTH, "need_coal");
    public static final String TOOLTIP_HEARTH_NOT_SAFE = JolCraftLanguageKeys.tooltip(HEARTH, "not_safe");
    public static final String TOOLTIP_HEARTH_NO_BED_NEARBY = JolCraftLanguageKeys.tooltip(HEARTH, "no_bed_nearby");

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        // -----------------------------------------------------------------
        // Lapidary Bench
        // -----------------------------------------------------------------

        p.putManual(CONTAINER_LAPIDARY_BENCH, "Lapidary Bench");
        p.putManual(TOOLTIP_LAPIDARY_BENCH_LOCKED_CUT_GEMS, "You have no idea how to cut this gem without breaking it.");
        p.putManual(TOOLTIP_GEODE, "Can be broken into dust using an artisan hammer at a lapidary bench.");
        p.putManual(TOOLTIP_UNCUT_GEM, "Can be broken into dust using an artisan hammer or cut using a chisel at a lapidary bench.");
        p.putManual(TOOLTIP_ARTISAN_HAMMER, "Can be used to break geodes and gems at a lapidary bench.");
        p.putManual(TOOLTIP_CUT_GEM, "Can be used to trim armor for bonus stats. Applying additional cosmetic trims does not override given stats.");
        p.putManual(TOOLTIP_CHISEL, "Can be used to cut gems at a lapidary bench.");
        p.putManual(TOOLTIP_CUT_LOCKED, "You have not learned how to cut gems!");

        // -----------------------------------------------------------------
        // Fermenting Cauldron
        // -----------------------------------------------------------------

        p.putManual(TOOLTIP_FERMENTING_CAULDRON_INGREDIENT_MAX, "You already added the max amount of this ingredient to the brew.");
        p.putManual(TOOLTIP_FERMENTING_CAULDRON_LOCKED_MULTI, "Adding more ingredients without proper knowledge would ruin the brew.");

        // -----------------------------------------------------------------
        // Strongbox / Locks
        // -----------------------------------------------------------------

        p.putManual(CONTAINER_STRONGBOX, "Strongbox");
        p.putManual(CONTAINER_STRONGBOX_LOCKED, "Locked Strongbox");
        p.putManual(TOOLTIP_LOCKPICK, "Used to pick locks. Will break on failure. Lockpicking is easier when using potions.");
        p.putManual(TOOLTIP_STRONGBOX_NOT_EMPTY, "This strongbox has items inside.");
        p.putManual(TOOLTIP_STRONGBOX_LOOT, "This strongbox has loot inside.");
        p.putManual(TOOLTIP_STRONGBOX_SET_LOCKED, "You have locked this strongbox.");
        p.putManual(TOOLTIP_STRONGBOX_SET_UNLOCKED, "You have unlocked this strongbox.");
        p.putManual(TOOLTIP_STRONGBOX_LOCKED, "This strongbox is locked.");
        p.putManual(TOOLTIP_STRONGBOX_BUSY, "Someone else is trying to pick this lock.");

        // -----------------------------------------------------------------
        // Hearth
        // -----------------------------------------------------------------

        p.putManual(TOOLTIP_HEARTH_COOLDOWN, "You must rest before light a hearth.");
        p.putManual(TOOLTIP_HEARTH_NEED_COAL, "You need coal to light this.");
        p.putManual(TOOLTIP_HEARTH_NOT_SAFE, "Cannot light with monsters nearby!");
        p.putManual(TOOLTIP_HEARTH_NO_BED_NEARBY, "No claimed bed nearby.");
    }
}
