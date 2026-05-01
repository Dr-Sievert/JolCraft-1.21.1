package net.sievert.jolcraft.datagen.client.model.subprovider;

import net.minecraft.data.models.model.ModelLocationUtils;
import net.minecraft.resources.ResourceLocation;
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
public record EggModelSubProvider(@NotNull JolCraftModelProvider parent) implements JolCraftModelSubProvider {

    private static final ResourceLocation SPAWN_EGG_MODEL = ModelLocationUtils.decorateItemModelLocation("template_spawn_egg");

    @Override
    public @NotNull String id() {
        return JolCraftDictionary.EGG;
    }

    @Override
    public void registerModels(
            @NotNull JolCraftModelBuilder builder,
            @NotNull JolCraftDataTracking tracking
    ) {
        builder.delegateItemModel(JolCraftItems.DWARF_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        builder.delegateItemModel(JolCraftItems.DWARF_GUILDMASTER_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        builder.delegateItemModel(JolCraftItems.DWARF_HISTORIAN_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        builder.delegateItemModel(JolCraftItems.DWARF_MERCHANT_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        builder.delegateItemModel(JolCraftItems.DWARF_SCRAPPER_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        builder.delegateItemModel(JolCraftItems.DWARF_BREWMASTER_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        builder.delegateItemModel(JolCraftItems.DWARF_GUARD_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        builder.delegateItemModel(JolCraftItems.DWARF_KEEPER_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        builder.delegateItemModel(JolCraftItems.DWARF_ARTISAN_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        builder.delegateItemModel(JolCraftItems.DWARF_EXPLORER_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        builder.delegateItemModel(JolCraftItems.DWARF_MINER_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        builder.delegateItemModel(JolCraftItems.DWARF_ALCHEMIST_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        builder.delegateItemModel(JolCraftItems.DWARF_ARCANIST_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
        builder.delegateItemModel(JolCraftItems.DWARF_PRIEST_SPAWN_EGG.get(), SPAWN_EGG_MODEL);

        builder.delegateItemModel(JolCraftItems.MUFFHORN_SPAWN_EGG.get(), SPAWN_EGG_MODEL);
    }
}