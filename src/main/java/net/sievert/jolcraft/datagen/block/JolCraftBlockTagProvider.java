package net.sievert.jolcraft.datagen.block;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.data.JolCraftTags;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public final class JolCraftBlockTagProvider extends BlockTagsProvider {

    public JolCraftBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, JolCraft.MOD_ID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        //Vanilla
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
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
                .add(JolCraftBlocks.DEEPSLATE_MORTAR.get());

        tag(BlockTags.NEEDS_IRON_TOOL)
                .add(JolCraftBlocks.DEEPSLATE_BULBS_CROP.get())
                .add(JolCraftBlocks.DEEPSLATE_PLATE_BLOCK.get());

        tag(BlockTags.MINEABLE_WITH_HOE)
                .add(JolCraftBlocks.BARLEY_BLOCK.get());

        tag(BlockTags.MUSHROOM_GROW_BLOCK)
                .add(JolCraftBlocks.VERDANT_SOIL.get());

        //Common Neoforge
        tag(Tags.Blocks.CHESTS)
                .add(JolCraftBlocks.STRONGBOX.get());

        tag(Tags.Blocks.STORAGE_BLOCKS)
                .add(JolCraftBlocks.PURE_MITHRIL_BLOCK.get())
                .add(JolCraftBlocks.MITHRIL_BLOCK.get())
                .add(JolCraftBlocks.DEEPSLATE_PLATE_BLOCK.get())
                .add(JolCraftBlocks.MUFFHORN_FUR_BLOCK.get());

        tag(Tags.Blocks.NEEDS_NETHERITE_TOOL)
                .add(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get())
                .add(JolCraftBlocks.PURE_MITHRIL_BLOCK.get())
                .add(JolCraftBlocks.MITHRIL_BLOCK.get());

        tag(Tags.Blocks.ORES)
                .add(JolCraftBlocks.GEODE_BLOCK.get())
                .add(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get());

        tag(Tags.Blocks.ORE_RATES_SINGULAR)
                .add(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get());

        tag(Tags.Blocks.ORE_RATES_DENSE)
                .add(JolCraftBlocks.GEODE_BLOCK.get());

        tag(Tags.Blocks.ORES_IN_GROUND_DEEPSLATE)
                .add(JolCraftBlocks.DEEPSLATE_MITHRIL_ORE.get());

        //Custom
        tag(JolCraftTags.Blocks.DEEPSLATE_BULBS_PLANTABLE)
                .add(JolCraftBlocks.VERDANT_SOIL.get())
                .add(Blocks.DEEPSLATE)
                .add(Blocks.TUFF);

        tag(JolCraftTags.Blocks.HOPS_BOTTOM)
                .add(JolCraftBlocks.ASGARNIAN_CROP_BOTTOM.get())
                .add(JolCraftBlocks.DUSKHOLD_CROP_BOTTOM.get())
                .add(JolCraftBlocks.KRANDONIAN_CROP_BOTTOM.get())
                .add(JolCraftBlocks.YANILLIAN_CROP_BOTTOM.get());

        tag(JolCraftTags.Blocks.HOPS_TOP)
                .add(JolCraftBlocks.ASGARNIAN_CROP_TOP.get())
                .add(JolCraftBlocks.DUSKHOLD_CROP_TOP.get())
                .add(JolCraftBlocks.KRANDONIAN_CROP_TOP.get())
                .add(JolCraftBlocks.YANILLIAN_CROP_TOP.get());

        tag(JolCraftTags.Blocks.VERDANT)
                .add(JolCraftBlocks.VERDANT_SOIL.get())
                .add(JolCraftBlocks.VERDANT_FARMLAND.get());
    }

    @Override
    public @NotNull String getName() {
        return "JolCraft Block Tags";
    }
}