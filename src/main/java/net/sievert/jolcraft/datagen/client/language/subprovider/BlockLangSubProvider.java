package net.sievert.jolcraft.datagen.client.language.subprovider;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.sievert.jolcraft.datagen.client.language.util.AbstractLanguageProvider;
import net.sievert.jolcraft.world.block.JolCraftBlocks;

@OnlyIn(Dist.CLIENT)
public final class BlockLangSubProvider implements AbstractLanguageProvider.LangSubProvider {

    @Override
    public void addTranslations(AbstractLanguageProvider p) {

        p.putManual(JolCraftBlocks.BARLEY_CROP, "Barley Crops");
        p.putManual(JolCraftBlocks.BARLEY_BLOCK, "Barley Hay Bale");

        p.putSame("Asgarnian Hops",
                JolCraftBlocks.ASGARNIAN_CROP_BOTTOM,
                JolCraftBlocks.ASGARNIAN_CROP_TOP
        );
        p.putSame("Duskhold Hops",
                JolCraftBlocks.DUSKHOLD_CROP_BOTTOM,
                JolCraftBlocks.DUSKHOLD_CROP_TOP
        );
        p.putSame("Krandonian Hops",
                JolCraftBlocks.KRANDONIAN_CROP_BOTTOM,
                JolCraftBlocks.KRANDONIAN_CROP_TOP
        );
        p.putSame("Yanillian Hops",
                JolCraftBlocks.YANILLIAN_CROP_BOTTOM,
                JolCraftBlocks.YANILLIAN_CROP_TOP
        );

        p.putManual(JolCraftBlocks.DEEPSLATE_BULBS_CROP, "Deepslate Bulbs");
        p.putManual(JolCraftBlocks.FESTERLING_CROP, "Cultivated Festerling");
        p.putManual(JolCraftBlocks.MUFFHORN_FUR_BLOCK, "Muffhorn Fur Bundle");
        p.putManual(JolCraftBlocks.GEODE_BLOCK, "Basalt Geode Cluster");

        for (DeferredHolder<?, ?> holder : JolCraftBlocks.BLOCKS.getEntries()) {
            ResourceLocation id = holder.getId();

            String key = "block." + id.getNamespace() + "." + id.getPath();
            if (p.hasKey(key)) continue;

            p.put(key, AbstractLanguageProvider.toTitleCase(id.getPath()));
        }
    }
}
