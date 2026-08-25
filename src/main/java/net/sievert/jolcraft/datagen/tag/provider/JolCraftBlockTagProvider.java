package net.sievert.jolcraft.datagen.tag.provider;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftTags;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.tag.JolCraftMainTagProvider;
import net.sievert.jolcraft.util.JolCraftStrings;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public final class JolCraftBlockTagProvider
        extends BlockTagsProvider
        implements JolCraftMainTagProvider<JolCraftBlockTagProvider> {

    private final @Nullable ExistingFileHelper existingFileHelper;

    public JolCraftBlockTagProvider(
            @NotNull PackOutput output,
            @NotNull CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper
    ) {
        super(output, lookupProvider, JolCraft.MOD_ID, existingFileHelper);
        this.existingFileHelper = existingFileHelper;
    }

    @Override
    public @NotNull String tagType() {
        return JolCraftDictionary.BLOCK;
    }

    @Override
    public @NotNull String getName() {
        return name();
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        generate(this, null, CompletableFuture.completedFuture(provider), existingFileHelper);

        JolCraftDataTracking.logExplicitCount(
                this,
                this.builders.size(),
                JolCraftStrings.spaced(tagType(), JolCraftStrings.plural(domain().getId()))
        );
    }

    @Override
    public void run(
            @NotNull JolCraftBlockTagProvider target,
            @Nullable PackOutput packOutput,
            @Nullable CompletableFuture<HolderLookup.Provider> lookupProvider,
            @Nullable ExistingFileHelper existingFileHelper,
            @NotNull JolCraftDataTracking tracking
    ) {
        // Custom

        target.tag(JolCraftTags.Blocks.DEEPSLATE_BULBS_PLANTABLE)
                .add(JolCraftBlocks.VERDANT_SOIL.get())
                .add(Blocks.DEEPSLATE);

        target.tag(JolCraftTags.Blocks.CYANELLA_PLANTABLE)
                .add(JolCraftBlocks.VERDANT_SOIL.get())
                .add(Blocks.WARPED_NYLIUM);

        target.tag(JolCraftTags.Blocks.HOPS_BOTTOM)
                .add(JolCraftBlocks.ASGARNIAN_CROP_BOTTOM.get())
                .add(JolCraftBlocks.DUSKHOLD_CROP_BOTTOM.get())
                .add(JolCraftBlocks.KRANDONIAN_CROP_BOTTOM.get())
                .add(JolCraftBlocks.YANILLIAN_CROP_BOTTOM.get());

        target.tag(JolCraftTags.Blocks.HOPS_TOP)
                .add(JolCraftBlocks.ASGARNIAN_CROP_TOP.get())
                .add(JolCraftBlocks.DUSKHOLD_CROP_TOP.get())
                .add(JolCraftBlocks.KRANDONIAN_CROP_TOP.get())
                .add(JolCraftBlocks.YANILLIAN_CROP_TOP.get());

        target.tag(JolCraftTags.Blocks.VERDANT)
                .add(JolCraftBlocks.VERDANT_SOIL.get())
                .add(JolCraftBlocks.VERDANT_FARMLAND.get());

        // Vanilla

        target.tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(JolCraftBlocks.TUFF_VITRIOL_ORE.get())
                .add(JolCraftBlocks.VITRIOL_BLOCK.get())
                .add(JolCraftBlocks.LAPIDARY_BENCH.get())
                .add(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get())
                .add(JolCraftBlocks.GEODE_BLOCK.get())
                .add(JolCraftBlocks.PURE_MITHRIL_BLOCK.get())
                .add(JolCraftBlocks.MITHRIL_BLOCK.get())
                .add(JolCraftBlocks.STRONGBOX.get())
                .add(JolCraftBlocks.DEEPSLATE_BULBS_CROP.get())
                .add(JolCraftBlocks.DEEPSLATE_PLATE_BLOCK.get())
                .add(JolCraftBlocks.HEARTH.get())
                .add(JolCraftBlocks.FERMENTING_CAULDRON.get())
                .add(JolCraftBlocks.MORTAR.get());

        target.tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(JolCraftBlocks.VERDANT_SOIL.get())
                .add(JolCraftBlocks.VERDANT_FARMLAND.get());

        target.tag(BlockTags.MINEABLE_WITH_AXE)
                .add(JolCraftBlocks.BLOODROOT.get())
                .add(JolCraftBlocks.DUSKCAP_BLOCK.get())
                .add(JolCraftBlocks.DUSKCAP_STEM.get())
                .add(JolCraftBlocks.FESTERLING_BLOCK.get())
                .add(JolCraftBlocks.FESTERLING_STEM.get())
                .add(JolCraftBlocks.FERMENTING_BARREL.get());

        target.tag(BlockTags.MINEABLE_WITH_HOE)
                .add(JolCraftBlocks.BARLEY_BLOCK.get());

        target.tag(BlockTags.SWORD_EFFICIENT)
                .add(JolCraftBlocks.BLOODROOT.get());

        target.tag(BlockTags.NEEDS_IRON_TOOL)
                .add(JolCraftBlocks.TUFF_VITRIOL_ORE.get())
                .add(JolCraftBlocks.VITRIOL_BLOCK.get())
                .add(JolCraftBlocks.GEODE_BLOCK.get())
                .add(JolCraftBlocks.DEEPSLATE_BULBS_CROP.get())
                .add(JolCraftBlocks.DEEPSLATE_PLATE_BLOCK.get());

        target.tag(BlockTags.CROPS)
                .add(JolCraftBlocks.BARLEY_CROP.get())
                .addTag(JolCraftTags.Blocks.HOPS_BOTTOM)
                .addTag(JolCraftTags.Blocks.HOPS_TOP)
                .add(JolCraftBlocks.FESTERLING_CROP.get())
                .add(JolCraftBlocks.DEEPSLATE_BULBS_CROP.get());

        target.tag(BlockTags.MAINTAINS_FARMLAND)
                .add(JolCraftBlocks.BARLEY_CROP.get())
                .addTag(JolCraftTags.Blocks.HOPS_BOTTOM);

        target.tag(BlockTags.MUSHROOM_GROW_BLOCK)
                .add(JolCraftBlocks.VERDANT_SOIL.get());

        target.tag(BlockTags.BAMBOO_PLANTABLE_ON)
                .add(JolCraftBlocks.VERDANT_SOIL.get());

        target.tag(BlockTags.AZALEA_GROWS_ON)
                .add(JolCraftBlocks.VERDANT_SOIL.get());

        target.tag(BlockTags.SMALL_FLOWERS)
                .add(JolCraftBlocks.CYANELLA.get())
                .add(JolCraftBlocks.SKYBELL.get());

        target.tag(BlockTags.FLOWER_POTS)
                .add(JolCraftBlocks.POTTED_CYANELLA.get())
                .add(JolCraftBlocks.POTTED_SKYBELL.get())
                .add(JolCraftBlocks.POTTED_DUSKCAP.get())
                .add(JolCraftBlocks.POTTED_FESTERLING.get());

        target.tag(BlockTags.CAULDRONS)
                .add(JolCraftBlocks.FERMENTING_CAULDRON.get());

        // Common NeoForge

        target.tag(Tags.Blocks.CHESTS)
                .add(JolCraftBlocks.STRONGBOX.get());

        target.tag(Tags.Blocks.STORAGE_BLOCKS)
                .add(JolCraftBlocks.VITRIOL_BLOCK.get())
                .add(JolCraftBlocks.PURE_MITHRIL_BLOCK.get())
                .add(JolCraftBlocks.MITHRIL_BLOCK.get())
                .add(JolCraftBlocks.DEEPSLATE_PLATE_BLOCK.get())
                .add(JolCraftBlocks.MUFFHORN_FUR_BLOCK.get());

        target.tag(Tags.Blocks.NEEDS_NETHERITE_TOOL)
                .add(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get())
                .add(JolCraftBlocks.PURE_MITHRIL_BLOCK.get())
                .add(JolCraftBlocks.MITHRIL_BLOCK.get());

        target.tag(Tags.Blocks.ORES)
                .add(JolCraftBlocks.TUFF_VITRIOL_ORE.get())
                .add(JolCraftBlocks.GEODE_BLOCK.get())
                .add(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get());

        target.tag(Tags.Blocks.ORE_RATES_SINGULAR)
                .add(JolCraftBlocks.TUFF_VITRIOL_ORE.get())
                .add(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get());

        target.tag(Tags.Blocks.ORE_RATES_DENSE)
                .add(JolCraftBlocks.GEODE_BLOCK.get());

        target.tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE)
                .add(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get());
    }
}