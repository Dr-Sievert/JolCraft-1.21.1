package net.sievert.jolcraft.datagen.client.model.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelBuilder;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelProvider;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelSubProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public record MaterialModelSubProvider(@NotNull JolCraftModelProvider parent) implements JolCraftModelSubProvider {

    private static final String SUB_PAPER = JolCraftStrings.slashed(
            JolCraftDictionary.MATERIAL,
            JolCraftDictionary.PAPER
    );

    private static final String SUB_ENTITY = JolCraftStrings.slashed(
            JolCraftDictionary.MATERIAL,
            JolCraftDictionary.ENTITY
    );

    private static final String SUB_GEODE = JolCraftStrings.slashed(
            JolCraftDictionary.MATERIAL,
            JolCraftDictionary.GEODE
    );

    private static final String SUB_DEEPSLATE = JolCraftStrings.slashed(
            JolCraftDictionary.MATERIAL,
            JolCraftDictionary.DEEPSLATE
    );

    private static final String SUB_MITHRIL = JolCraftStrings.slashed(
            JolCraftDictionary.MATERIAL,
            JolCraftDictionary.MITHRIL
    );

    @Override
    public @NotNull String id() {
        return JolCraftDictionary.MATERIAL;
    }

    @Override
    public void registerModels(
            @NotNull JolCraftModelBuilder builder,
            @NotNull JolCraftDataTracking tracking
    ) {
        builder.flatItem(JolCraftItems.PARCHMENT.get(), SUB_PAPER);
        builder.flatItem(JolCraftItems.QUILL_EMPTY.get(), SUB_PAPER);
        builder.flatItem(JolCraftItems.QUILL_SMALL.get(), SUB_PAPER);
        builder.flatItem(JolCraftItems.QUILL_HALF.get(), SUB_PAPER);
        builder.flatItem(JolCraftItems.QUILL_FULL.get(), SUB_PAPER);
        builder.flatItem(JolCraftItems.LEGENDARY_PAGE.get(), SUB_PAPER);

        builder.handheldItem(JolCraftItems.MUFFHORN_MILK_BUCKET.get(), SUB_ENTITY);
        builder.handheldItem(JolCraftItems.MUFFHORN_FUR.get(), SUB_ENTITY);

        builder.cubeAllWithItem(JolCraftBlocks.MUFFHORN_FUR_BLOCK.get(), SUB_ENTITY);

        builder.flatItem(JolCraftItems.GEODE_SMALL.get(), SUB_GEODE);
        builder.flatItem(JolCraftItems.GEODE_MEDIUM.get(), SUB_GEODE);
        builder.flatItem(JolCraftItems.GEODE_LARGE.get(), SUB_GEODE);

        builder.cubeAllWithItem(JolCraftBlocks.GEODE_BLOCK.get(), SUB_GEODE);

        builder.flatItem(JolCraftItems.DEEPSLATE_PLATE.get(), SUB_DEEPSLATE);
        builder.flatItem(JolCraftItems.DEEPSLATE_ROD.get(), SUB_DEEPSLATE);

        builder.cubeAllWithItem(JolCraftBlocks.DEEPSLATE_PLATE_BLOCK.get(), SUB_DEEPSLATE);

        builder.flatItem(JolCraftItems.IMPURE_MITHRIL.get(), SUB_MITHRIL);
        builder.flatItem(JolCraftItems.PURE_MITHRIL.get(), SUB_MITHRIL);
        builder.flatItem(JolCraftItems.MITHRIL_INGOT.get(), SUB_MITHRIL);
        builder.flatItem(JolCraftItems.MITHRIL_NUGGET.get(), SUB_MITHRIL);
        builder.flatItem(JolCraftItems.MITHRIL_CHAINWEAVE.get(), SUB_MITHRIL);

        builder.rotatedPillarWithHorizontalVariantAndItem(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get());
        builder.cubeAllWithItem(JolCraftBlocks.PURE_MITHRIL_BLOCK.get(), SUB_MITHRIL);
        builder.cubeAllWithItem(JolCraftBlocks.MITHRIL_BLOCK.get(), SUB_MITHRIL);
    }
}