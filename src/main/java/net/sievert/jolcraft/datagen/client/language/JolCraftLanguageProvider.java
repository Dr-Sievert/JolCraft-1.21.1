package net.sievert.jolcraft.datagen.client.language;

import com.google.gson.JsonObject;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.JolCraftDataDomain;
import net.sievert.jolcraft.datagen.base.JolCraftMainDataProvider;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.client.language.subprovider.*;
import net.sievert.jolcraft.util.JolCraftStrings;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;

@OnlyIn(Dist.CLIENT)
public final class JolCraftLanguageProvider
        implements DataProvider, JolCraftMainDataProvider<Map<String, String>> {

    public static final String LOCALE = "en_us";

    private final PackOutput output;
    private final List<LanguageSubProvider> subProviders;

    public JolCraftLanguageProvider(@NotNull PackOutput output) {
        this.output = output;
        this.subProviders = List.of(
                new AdvancementsLangSubProvider(),
                new AttributeLangSubProvider(),
                new BlockLangSubProvider(),
                new BountyLangSubProvider(),
                new CompassLangSubProvider(),
                new ContainerLangSubProvider(),
                new DamageTypeLangSubProvider(),
                new DwarfLangSubProvider(),
                new EffectLangSubProvider(),
                new EntityLangSubProvider(),
                new ItemLangSubProvider(),
                new JeiLangSubProvider(),
                new LoreLangSubProvider(),
                new MiscLangSubProvider(),
                new PotionLangSubProvider(),
                new DwarvenReputationLangSubProvider(),
                new StatLangSubProvider(),
                new SubtitleLangSubProvider(),
                new TrimLangSubProvider()
        );
    }

    @Override
    public @NotNull JolCraftDataDomain domain() {
        return JolCraftDataDomain.LANGUAGE;
    }

    @Override
    public @NotNull String id() {
        return domain().getId();
    }

    @Override
    public @NotNull String folder() {
        return "lang";
    }

    @Override
    public @NotNull List<LanguageSubProvider> subProviders() {
        return subProviders;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        Map<String, String> translations = new TreeMap<>();
        generate(translations, output, null, null);

        JsonObject json = new JsonObject();
        translations.forEach(json::addProperty);

        Path path = output.getOutputFolder(PackOutput.Target.RESOURCE_PACK)
                .resolve(JolCraft.MOD_ID)
                .resolve(validatedFolder())
                .resolve(LOCALE + ".json");

        return DataProvider.saveStable(cache, json, path)
                .thenRun(() -> JolCraftDataTracking.logExplicitCount(
                        this,
                        translations.size(),
                        JolCraftStrings.plural(JolCraftDictionary.TRANSLATION)
                ));
    }

    @Override
    public void run(
            @NotNull Map<String, String> target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        tracking.record(this, validatedFolder() + "/" + LOCALE + ".json");
    }

    @Override
    public @NotNull String getName() {
        return name();
    }
}