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
                new AttributeLangSubProvider(),
                new BlockLangSubProvider(),
                new BountyLangSubProvider(),
                new CompassLangSubProvider(),
                new ContainerLangSubProvider(),
                new DwarfLangSubProvider(),
                new EffectLangSubProvider(),
                new EntityLangSubProvider(),
                new ItemLangSubProvider(),
                new JeiLangSubProvider(),
                new LoreLangSubProvider(),
                new MiscLangSubProvider(),
                new PotionLangSubProvider(),
                new ReputationLangSubProvider(),
                new StatLangSubProvider(),
                new SubtitleLangSubProvider(),
                new TrimLangSubProvider()
        );
    }

    @Override
    protected void addTranslations() {
        runAll(subs);
    }
}
