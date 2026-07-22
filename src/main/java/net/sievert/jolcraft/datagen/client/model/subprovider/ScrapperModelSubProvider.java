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
public record ScrapperModelSubProvider(@NotNull JolCraftModelProvider parent) implements JolCraftModelSubProvider {

    private static final String SUB_SCRAP = "material/scrap";
    private static final String SUB_SALVAGE = "material/salvage";

    @Override
    public @NotNull String id() {
        return JolCraftDictionary.SCRAPPER;
    }

    @Override
    public void registerModels(
            @NotNull JolCraftModelBuilder builder,
            @NotNull JolCraftDataTracking tracking
    ) {
        builder.flatItem(JolCraftItems.SCRAP.get(), SUB_SCRAP);
        builder.flatItem(JolCraftItems.SCRAP_HEAP.get(), SUB_SCRAP);

        builder.handheldItem(JolCraftItems.BROKEN_PICKAXE.get(), SUB_SALVAGE);
        builder.flatItem(JolCraftItems.BROKEN_AMULET.get(), SUB_SALVAGE);
        builder.flatItem(JolCraftItems.BROKEN_BELT.get(), SUB_SALVAGE);
        builder.flatItem(JolCraftItems.BROKEN_COINS.get(), SUB_SALVAGE);
        builder.flatItem(JolCraftItems.DEEPSLATE_MUG.get(), SUB_SALVAGE);
        builder.flatItem(JolCraftItems.BROKEN_TABLET.get(), SUB_SALVAGE);
        builder.flatItem(JolCraftItems.EXPIRED_POTION.get(), SUB_SALVAGE);
        builder.flatItem(JolCraftItems.INGOT_MOULD.get(), SUB_SALVAGE);
        builder.flatItem(JolCraftItems.OLD_FABRIC.get(), SUB_SALVAGE);
        builder.flatItem(JolCraftItems.RUSTY_TONGS.get(), SUB_SALVAGE);

        builder.flatItem(JolCraftItems.MITHRIL_SCRAP.get(), SUB_SALVAGE);
        builder.flatItem(JolCraftItems.BROKEN_MITHRIL_PLATE.get(), SUB_SALVAGE);
        builder.handheldItem(JolCraftItems.BROKEN_MITHRIL_SWORD.get(), SUB_SALVAGE);

        builder.flatItem(JolCraftItems.BROKEN_DEEPSLATE_PLATES.get(), SUB_SALVAGE);
        builder.flatItem(JolCraftItems.BROKEN_DEEPSLATE_GEAR.get(), SUB_SALVAGE);
        builder.flatItem(JolCraftItems.BROKEN_DEEPSLATE_PICKAXE_HEAD.get(), SUB_SALVAGE);
    }
}