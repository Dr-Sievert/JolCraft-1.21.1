package net.sievert.jolcraft.datagen.model.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.sievert.jolcraft.datagen.model.util.AbstractModelProvider;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class ToolModelSubProvider implements AbstractModelProvider.ModelSubProvider {

    // Root folders
    private static final String TOOL = "tool";
    private static final String WEAPON = "weapon";

    // Materials
    private static final String DEEPSLATE = "deepslate";
    private static final String MITHRIL = "mithril";

    @Override
    public void addModels(@NotNull BlockModelGenerators blocks, @NotNull ItemModelGenerators items) {

        // Weapons — deepslate
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.DEEPSLATE_SWORD.get(),
                AbstractModelProvider.subFolder(WEAPON, DEEPSLATE)
        );
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.DEEPSLATE_WARHAMMER.get(),
                AbstractModelProvider.subFolder(WEAPON, DEEPSLATE)
        );

        // Weapons — mithril
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.MITHRIL_SWORD.get(),
                AbstractModelProvider.subFolder(WEAPON, MITHRIL)
        );
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.MITHRIL_WARHAMMER.get(),
                AbstractModelProvider.subFolder(WEAPON, MITHRIL)
        );

        // Tools — deepslate
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.DEEPSLATE_PICKAXE.get(),
                AbstractModelProvider.subFolder(TOOL, DEEPSLATE)
        );
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.DEEPSLATE_SHOVEL.get(),
                AbstractModelProvider.subFolder(TOOL, DEEPSLATE)
        );
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.DEEPSLATE_AXE.get(),
                AbstractModelProvider.subFolder(TOOL, DEEPSLATE)
        );
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.DEEPSLATE_HOE.get(),
                AbstractModelProvider.subFolder(TOOL, DEEPSLATE)
        );
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.DEEPSLATE_ARTISAN_HAMMER.get(),
                AbstractModelProvider.subFolder(TOOL, DEEPSLATE)
        );
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.DEEPSLATE_CHISEL.get(),
                AbstractModelProvider.subFolder(TOOL, DEEPSLATE)
        );

        // Tools — mithril
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.MITHRIL_PICKAXE.get(),
                AbstractModelProvider.subFolder(TOOL, MITHRIL)
        );
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.MITHRIL_SHOVEL.get(),
                AbstractModelProvider.subFolder(TOOL, MITHRIL)
        );
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.MITHRIL_AXE.get(),
                AbstractModelProvider.subFolder(TOOL, MITHRIL)
        );
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.MITHRIL_HOE.get(),
                AbstractModelProvider.subFolder(TOOL, MITHRIL)
        );
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.MITHRIL_ARTISAN_HAMMER.get(),
                AbstractModelProvider.subFolder(TOOL, MITHRIL)
        );
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.MITHRIL_CHISEL.get(),
                AbstractModelProvider.subFolder(TOOL, MITHRIL)
        );

        // Spanners (no material subfolder)
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.COPPER_SPANNER.get(),
                TOOL
        );
        AbstractModelProvider.generateHandheldItem(
                items,
                JolCraftItems.IRON_SPANNER.get(),
                TOOL
        );
    }
}