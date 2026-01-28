package net.sievert.jolcraft.datagen.language.subprovider;

import net.sievert.jolcraft.datagen.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.datagen.language.util.JolCraftLanguageKeys;

public final class MiscLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        // Attachments – reputation tiers
        p.putManual(JolCraftLanguageKeys.reputationTier(0), "Stranger");
        p.putManual(JolCraftLanguageKeys.reputationTier(1), "Known Face");
        p.putManual(JolCraftLanguageKeys.reputationTier(2), "Trusted");
        p.putManual(JolCraftLanguageKeys.reputationTier(3), "Respected");
        p.putManual(JolCraftLanguageKeys.reputationTier(4), "Blood-Kin");

        // Creative tabs
        p.putManual(JolCraftLanguageKeys.itemGroup("jolcraft_items_tab"), "JolCraft");
        p.putManual(JolCraftLanguageKeys.itemGroup("jolcraft_egg_tab"), "JolCraft Spawn Eggs");

        // Containers
        p.putManual(JolCraftLanguageKeys.container("lapidary_bench"), "Lapidary Bench");
        p.putManual(JolCraftLanguageKeys.container("strongbox"), "Strongbox");
        p.putManual(JolCraftLanguageKeys.container("strongbox_locked"), "Locked Strongbox");

        // Stats
        p.putManual(JolCraftLanguageKeys.stat("structures_discovered"), "Structures Discovered");

        // Structure maps
        p.putManual("filled_map.forge", "Map to a Dwarven Forge");
    }
}