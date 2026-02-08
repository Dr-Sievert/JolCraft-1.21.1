package net.sievert.jolcraft.datagen.client.language;

import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.datagen.client.language.subprovider.*;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;

import java.util.List;

@OnlyIn(Dist.CLIENT)
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
