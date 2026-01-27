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

        // Trim patterns
        p.putManual(JolCraftLanguageKeys.trimPattern("forge"), "Forge Armor Trim");

        // Trim materials
        p.putManual(JolCraftLanguageKeys.trimMaterial("deepslate"), "Deepslate");
        p.putManual(JolCraftLanguageKeys.trimMaterial("mithril"), "Mithril");
        p.putManual(JolCraftLanguageKeys.trimMaterial("aegiscore"), "Aegiscore");
        p.putManual(JolCraftLanguageKeys.trimMaterial("ashfang"), "Ashfang");
        p.putManual(JolCraftLanguageKeys.trimMaterial("deepmarrow"), "Deepmarrow");
        p.putManual(JolCraftLanguageKeys.trimMaterial("earthblood"), "Earthblood");
        p.putManual(JolCraftLanguageKeys.trimMaterial("emberglass"), "Emberglass");
        p.putManual(JolCraftLanguageKeys.trimMaterial("frostvein"), "Frostvein");
        p.putManual(JolCraftLanguageKeys.trimMaterial("grimstone"), "Grimstone");
        p.putManual(JolCraftLanguageKeys.trimMaterial("ironheart"), "Ironheart");
        p.putManual(JolCraftLanguageKeys.trimMaterial("lumiere"), "Lumiere");
        p.putManual(JolCraftLanguageKeys.trimMaterial("moonshard"), "Moonshard");
        p.putManual(JolCraftLanguageKeys.trimMaterial("rustagate"), "Rustagate");
        p.putManual(JolCraftLanguageKeys.trimMaterial("skyburrow"), "Skyburrow");
        p.putManual(JolCraftLanguageKeys.trimMaterial("sungleam"), "Sungleam");
        p.putManual(JolCraftLanguageKeys.trimMaterial("verdanite"), "Verdanite");
        p.putManual(JolCraftLanguageKeys.trimMaterial("woecrystal"), "Woecrystal");

        // Stats
        p.putManual(JolCraftLanguageKeys.stat("structures_discovered"), "Structures Discovered");

        // Structure maps
        p.putManual("filled_map.forge", "Map to a Dwarven Forge");
    }
}