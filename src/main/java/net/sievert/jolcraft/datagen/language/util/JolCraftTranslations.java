package net.sievert.jolcraft.datagen.language.util;

/**
 * Centralized reusable translation keys.
 * These are the "named strings" used across code and datagen.
 */
public final class JolCraftTranslations {

    private JolCraftTranslations() {}

    // ---------------------------------------------------------------------
    // Tooltips
    // ---------------------------------------------------------------------

    public static final String TOOLTIP_HOLD_KEY =
            JolCraftLanguageKeys.category(JolCraftLanguageCategory.TOOLTIP, "hold_key");

    public static String tooltipStructure(String structureId) {
        return JolCraftLanguageKeys.tooltip("structure", structureId);
    }

    public static String tooltipDeepslateCompassDial(String dialId) {
        return JolCraftLanguageKeys.tooltip("deepslate_compass_dial", dialId);
    }

    // ---------------------------------------------------------------------
    // Containers
    // ---------------------------------------------------------------------

    public static final String CONTAINER_STRONGBOX =
            JolCraftLanguageKeys.container("strongbox");

    public static final String CONTAINER_STRONGBOX_LOCKED =
            JolCraftLanguageKeys.container("strongbox_locked");

    public static final String CONTAINER_LAPIDARY_BENCH =
            JolCraftLanguageKeys.container("lapidary_bench");

    // ---------------------------------------------------------------------
    // JEI
    // ---------------------------------------------------------------------

    public static String jeiInfoPage(String id) {
        return JolCraftLanguageKeys.jei("info_page." + id);
    }

    // ---------------------------------------------------------------------
    // Creative tabs
    // ---------------------------------------------------------------------

    public static final String TAB_ITEMS =
            JolCraftLanguageKeys.itemGroup("jolcraft_items_tab");

    public static final String TAB_EGGS =
            JolCraftLanguageKeys.itemGroup("jolcraft_egg_tab");

    // ---------------------------------------------------------------------
    // Stats
    // ---------------------------------------------------------------------

    public static final String STAT_STRUCTURES_DISCOVERED =
            JolCraftLanguageKeys.stat("structures_discovered");
}
