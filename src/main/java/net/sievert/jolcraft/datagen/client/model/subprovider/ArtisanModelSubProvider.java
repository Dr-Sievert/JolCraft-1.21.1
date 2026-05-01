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
public record ArtisanModelSubProvider(@NotNull JolCraftModelProvider parent) implements JolCraftModelSubProvider {

    private static final String SUB_GEM_UNCUT = JolCraftStrings.slashed(
            JolCraftDictionary.MATERIAL,
            JolCraftDictionary.GEM,
            JolCraftDictionary.UNCUT
    );

    private static final String SUB_GEM_CUT = JolCraftStrings.slashed(
            JolCraftDictionary.MATERIAL,
            JolCraftDictionary.GEM,
            JolCraftDictionary.CUT
    );

    private static final String SUB_GEM_DUST = JolCraftStrings.slashed(
            JolCraftDictionary.MATERIAL,
            JolCraftDictionary.GEM,
            JolCraftDictionary.DUST
    );

    @Override
    public @NotNull String id() {
        return JolCraftDictionary.ARTISAN;
    }

    @Override
    public void registerModels(
            @NotNull JolCraftModelBuilder builder,
            @NotNull JolCraftDataTracking tracking
    ) {
        builder.cubeTopBottomWithItem(JolCraftBlocks.LAPIDARY_BENCH.get());

        builder.flatItem(JolCraftItems.AEGISCORE.get(), SUB_GEM_UNCUT);
        builder.flatItem(JolCraftItems.ASHFANG.get(), SUB_GEM_UNCUT);
        builder.flatItem(JolCraftItems.DEEPMARROW.get(), SUB_GEM_UNCUT);
        builder.flatItem(JolCraftItems.EARTHBLOOD.get(), SUB_GEM_UNCUT);
        builder.flatItem(JolCraftItems.EMBERGLASS.get(), SUB_GEM_UNCUT);
        builder.flatItem(JolCraftItems.FROSTVEIN.get(), SUB_GEM_UNCUT);
        builder.flatItem(JolCraftItems.GRIMSTONE.get(), SUB_GEM_UNCUT);
        builder.flatItem(JolCraftItems.IRONHEART.get(), SUB_GEM_UNCUT);
        builder.flatItem(JolCraftItems.LUMIERE.get(), SUB_GEM_UNCUT);
        builder.flatItem(JolCraftItems.MOONSHARD.get(), SUB_GEM_UNCUT);
        builder.flatItem(JolCraftItems.RUSTAGATE.get(), SUB_GEM_UNCUT);
        builder.flatItem(JolCraftItems.SKYBURROW.get(), SUB_GEM_UNCUT);
        builder.flatItem(JolCraftItems.SUNGLEAM.get(), SUB_GEM_UNCUT);
        builder.flatItem(JolCraftItems.VERDANITE.get(), SUB_GEM_UNCUT);
        builder.flatItem(JolCraftItems.WOECRYSTAL.get(), SUB_GEM_UNCUT);

        builder.flatItem(JolCraftItems.AEGISCORE_CUT.get(), SUB_GEM_CUT);
        builder.flatItem(JolCraftItems.ASHFANG_CUT.get(), SUB_GEM_CUT);
        builder.flatItem(JolCraftItems.DEEPMARROW_CUT.get(), SUB_GEM_CUT);
        builder.flatItem(JolCraftItems.EARTHBLOOD_CUT.get(), SUB_GEM_CUT);
        builder.flatItem(JolCraftItems.EMBERGLASS_CUT.get(), SUB_GEM_CUT);
        builder.flatItem(JolCraftItems.FROSTVEIN_CUT.get(), SUB_GEM_CUT);
        builder.flatItem(JolCraftItems.GRIMSTONE_CUT.get(), SUB_GEM_CUT);
        builder.flatItem(JolCraftItems.IRONHEART_CUT.get(), SUB_GEM_CUT);
        builder.flatItem(JolCraftItems.LUMIERE_CUT.get(), SUB_GEM_CUT);
        builder.flatItem(JolCraftItems.MOONSHARD_CUT.get(), SUB_GEM_CUT);
        builder.flatItem(JolCraftItems.RUSTAGATE_CUT.get(), SUB_GEM_CUT);
        builder.flatItem(JolCraftItems.SKYBURROW_CUT.get(), SUB_GEM_CUT);
        builder.flatItem(JolCraftItems.SUNGLEAM_CUT.get(), SUB_GEM_CUT);
        builder.flatItem(JolCraftItems.VERDANITE_CUT.get(), SUB_GEM_CUT);
        builder.flatItem(JolCraftItems.WOECRYSTAL_CUT.get(), SUB_GEM_CUT);

        builder.flatItem(JolCraftItems.AEGISCORE_DUST.get(), SUB_GEM_DUST);
        builder.flatItem(JolCraftItems.ASHFANG_DUST.get(), SUB_GEM_DUST);
        builder.flatItem(JolCraftItems.DEEPMARROW_DUST.get(), SUB_GEM_DUST);
        builder.flatItem(JolCraftItems.EARTHBLOOD_DUST.get(), SUB_GEM_DUST);
        builder.flatItem(JolCraftItems.EMBERGLASS_DUST.get(), SUB_GEM_DUST);
        builder.flatItem(JolCraftItems.FROSTVEIN_DUST.get(), SUB_GEM_DUST);
        builder.flatItem(JolCraftItems.GRIMSTONE_DUST.get(), SUB_GEM_DUST);
        builder.flatItem(JolCraftItems.IRONHEART_DUST.get(), SUB_GEM_DUST);
        builder.flatItem(JolCraftItems.LUMIERE_DUST.get(), SUB_GEM_DUST);
        builder.flatItem(JolCraftItems.MOONSHARD_DUST.get(), SUB_GEM_DUST);
        builder.flatItem(JolCraftItems.RUSTAGATE_DUST.get(), SUB_GEM_DUST);
        builder.flatItem(JolCraftItems.SKYBURROW_DUST.get(), SUB_GEM_DUST);
        builder.flatItem(JolCraftItems.SUNGLEAM_DUST.get(), SUB_GEM_DUST);
        builder.flatItem(JolCraftItems.VERDANITE_DUST.get(), SUB_GEM_DUST);
        builder.flatItem(JolCraftItems.WOECRYSTAL_DUST.get(), SUB_GEM_DUST);
    }
}