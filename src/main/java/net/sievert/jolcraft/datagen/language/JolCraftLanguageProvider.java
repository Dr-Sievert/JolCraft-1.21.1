package net.sievert.jolcraft.datagen.language;

import net.minecraft.data.PackOutput;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.language.subprovider.*;
import net.sievert.jolcraft.datagen.language.util.AbstractLanguageProvider;

import java.util.List;

public final class JolCraftLanguageProvider extends AbstractLanguageProvider {

    private final List<LangSubProvider> subs;

    public JolCraftLanguageProvider(PackOutput output) {
        super(output, JolCraft.MOD_ID, "en_us");

        this.subs = List.of(
                new AdvancementsLangSubProvider(),
                new AttributesLangSubProvider(),
                new BlocksLangSubProvider(),
                new BountyLangSubProvider(),
                new CompassLangSubProvider(),
                new ContainerLangSubProvider(),
                new DwarfLangSubProvider(),
                new EffectsLangSubProvider(),
                new EntitiesLangSubProvider(),
                new ItemsLangSubProvider(),
                new JeiLangSubProvider(),
                new LoreLangSubProvider(),
                new MiscLangSubProvider(),
                new PotionsLangSubProvider(),
                new ReputationLangSubProvider(),
                new StatsLangSubProvider(),
                new SubtitlesLangSubProvider(),
                new TrimsLangSubProvider()
        );
    }

    @Override
    protected void addTranslations() {
        runAll(subs);
    }
}
