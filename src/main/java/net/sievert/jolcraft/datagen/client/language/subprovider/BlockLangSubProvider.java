package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.data.language.util.AbstractLanguageKeys;
import net.sievert.jolcraft.datagen.client.language.LanguageSubProvider;
import net.sievert.jolcraft.datagen.base.JolCraftDataProvider;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.block.JolCraftBlocks;

@OnlyIn(Dist.CLIENT)
public final class BlockLangSubProvider implements LanguageSubProvider {

    @Override
    public @NotNull String id() {
        return JolCraftStrings.plural(JolCraftDictionary.BLOCK);
    }

    @Override
    public @NotNull JolCraftDataProvider<Map<String, String>> parent() {
        return languageProvider();
    }


    @Override
    public void addTranslations(@NotNull Map<String, String> translations) {

        putManual(translations, JolCraftBlocks.BARLEY_CROP, "Barley Crops");
        putManual(translations, JolCraftBlocks.BARLEY_BLOCK, "Barley Hay Bale");

        putSame(translations, "Asgarnian Hops",
                JolCraftBlocks.ASGARNIAN_CROP_BOTTOM,
                JolCraftBlocks.ASGARNIAN_CROP_TOP
        );
        putSame(translations, "Duskhold Hops",
                JolCraftBlocks.DUSKHOLD_CROP_BOTTOM,
                JolCraftBlocks.DUSKHOLD_CROP_TOP
        );
        putSame(translations, "Krandonian Hops",
                JolCraftBlocks.KRANDONIAN_CROP_BOTTOM,
                JolCraftBlocks.KRANDONIAN_CROP_TOP
        );
        putSame(translations, "Yanillian Hops",
                JolCraftBlocks.YANILLIAN_CROP_BOTTOM,
                JolCraftBlocks.YANILLIAN_CROP_TOP
        );

        putManual(translations, JolCraftBlocks.DEEPSLATE_BULBS_CROP, "Deepslate Bulbs");
        putManual(translations, JolCraftBlocks.FESTERLING_CROP, "Cultivated Festerling");
        putManual(translations, JolCraftBlocks.MUFFHORN_FUR_BLOCK, "Muffhorn Fur Bundle");
        putManual(translations, JolCraftBlocks.GEODE_BLOCK, "Basalt Geode Cluster");

        for (DeferredHolder<?, ?> holder : JolCraftBlocks.BLOCKS.getEntries()) {
            ResourceLocation id = holder.getId();
            String key = AbstractLanguageKeys.block(id.getPath());
            if (hasKey(translations, key)) continue;

            put(translations, key, JolCraftStrings.toTitleCase(id.getPath()));
        }
    }
}
