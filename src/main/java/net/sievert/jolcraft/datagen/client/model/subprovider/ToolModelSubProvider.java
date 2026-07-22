package net.sievert.jolcraft.datagen.client.model.subprovider;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.data.language.JolCraftDictionary;
import net.sievert.jolcraft.datagen.base.report.JolCraftDataTracking;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelBuilder;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelProvider;
import net.sievert.jolcraft.datagen.client.model.JolCraftModelSubProvider;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public record ToolModelSubProvider(@NotNull JolCraftModelProvider parent) implements JolCraftModelSubProvider {

    private static final String TOOL = "tool";
    private static final String WEAPON = "weapon";

    private static final String DEEPSLATE = "deepslate";
    private static final String MITHRIL = "mithril";

    private static final String WEAPON_DEEPSLATE = WEAPON + "/" + DEEPSLATE;
    private static final String WEAPON_MITHRIL = WEAPON + "/" + MITHRIL;
    private static final String TOOL_DEEPSLATE = TOOL + "/" + DEEPSLATE;
    private static final String TOOL_MITHRIL = TOOL + "/" + MITHRIL;

    @Override
    public @NotNull String id() {
        return JolCraftDictionary.TOOL;
    }

    @Override
    public void registerModels(
            @NotNull JolCraftModelBuilder builder,
            @NotNull JolCraftDataTracking tracking
    ) {
        builder.handheldItem(JolCraftItems.DEEPSLATE_SWORD.get(), WEAPON_DEEPSLATE);
        builder.handheldItem(JolCraftItems.DEEPSLATE_WARHAMMER.get(), WEAPON_DEEPSLATE);

        builder.handheldItem(JolCraftItems.MITHRIL_SWORD.get(), WEAPON_MITHRIL);
        builder.handheldItem(JolCraftItems.MITHRIL_WARHAMMER.get(), WEAPON_MITHRIL);

        builder.handheldItem(JolCraftItems.DEEPSLATE_PICKAXE.get(), TOOL_DEEPSLATE);
        builder.handheldItem(JolCraftItems.DEEPSLATE_SHOVEL.get(), TOOL_DEEPSLATE);
        builder.handheldItem(JolCraftItems.DEEPSLATE_AXE.get(), TOOL_DEEPSLATE);
        builder.handheldItem(JolCraftItems.DEEPSLATE_HOE.get(), TOOL_DEEPSLATE);
        builder.handheldItem(JolCraftItems.DEEPSLATE_ARTISAN_HAMMER.get(), TOOL_DEEPSLATE);
        builder.handheldItem(JolCraftItems.DEEPSLATE_CHISEL.get(), TOOL_DEEPSLATE);

        builder.handheldItem(JolCraftItems.MITHRIL_PICKAXE.get(), TOOL_MITHRIL);
        builder.handheldItem(JolCraftItems.MITHRIL_SHOVEL.get(), TOOL_MITHRIL);
        builder.handheldItem(JolCraftItems.MITHRIL_AXE.get(), TOOL_MITHRIL);
        builder.handheldItem(JolCraftItems.MITHRIL_HOE.get(), TOOL_MITHRIL);
        builder.handheldItem(JolCraftItems.MITHRIL_ARTISAN_HAMMER.get(), TOOL_MITHRIL);
        builder.handheldItem(JolCraftItems.MITHRIL_CHISEL.get(), TOOL_MITHRIL);

        builder.handheldItem(JolCraftItems.COPPER_SPANNER.get(), TOOL);
        builder.handheldItem(JolCraftItems.IRON_SPANNER.get(), TOOL);
    }
}