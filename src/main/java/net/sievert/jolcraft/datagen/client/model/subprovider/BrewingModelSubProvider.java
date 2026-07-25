package net.sievert.jolcraft.datagen.client.model.subprovider;

import net.minecraft.data.models.blockstates.MultiVariantGenerator;
import net.minecraft.data.models.blockstates.PropertyDispatch;
import net.minecraft.data.models.blockstates.Variant;
import net.minecraft.data.models.blockstates.VariantProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.sievert.jolcraft.JolCraft;
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
public record BrewingModelSubProvider(@NotNull JolCraftModelProvider parent) implements JolCraftModelSubProvider {

    private static final String SUB_BREWING = JolCraftDictionary.BREWING;

    @Override
    public @NotNull String id() {
        return SUB_BREWING;
    }

    @Override
    public void registerModels(
            @NotNull JolCraftModelBuilder builder,
            @NotNull JolCraftDataTracking tracking
    ) {
        builder.handheldItem(JolCraftItems.BARLEY_MALT.get(), SUB_BREWING);
        builder.flatItem(JolCraftItems.YEAST.get(), SUB_BREWING);
        builder.flatItem(JolCraftItems.GLASS_MUG.get(), SUB_BREWING);

        fermentingCauldron(builder);
        fermentingBarrel(builder);

        builder.layeredItem(
                JolCraftItems.DWARVEN_BREW.get(),
                JolCraft.location("item/brewing/dwarven_brew_glass_mug"),
                JolCraft.location("item/brewing/dwarven_brew")
        );

        builder.layeredItem(
                JolCraftItems.DWARVEN_BREW_BUCKET.get(),
                ResourceLocation.withDefaultNamespace("item/bucket"),
                JolCraft.location("item/brewing/dwarven_brew_bucket")
        );
    }

    private static void fermentingCauldron(@NotNull JolCraftModelBuilder builder) {
        ResourceLocation cauldronModel = ResourceLocation.withDefaultNamespace(
                JolCraftStrings.slashed(
                        JolCraftDictionary.BLOCK,
                        JolCraftDictionary.CAULDRON
                )
        );

        builder.addBlockState(
                MultiVariantGenerator.multiVariant(JolCraftBlocks.FERMENTING_CAULDRON.get())
                        .with(
                                PropertyDispatch.property(LayeredCauldronBlock.LEVEL)
                                        .select(1, Variant.variant().with(VariantProperties.MODEL, cauldronModel))
                                        .select(2, Variant.variant().with(VariantProperties.MODEL, cauldronModel))
                                        .select(3, Variant.variant().with(VariantProperties.MODEL, cauldronModel))
                        )
        );
    }

    private static void fermentingBarrel(@NotNull JolCraftModelBuilder builder) {
        ResourceLocation barrelModel = ResourceLocation.withDefaultNamespace("block/barrel");

        builder.addBlockState(
                MultiVariantGenerator.multiVariant(
                        JolCraftBlocks.FERMENTING_BARREL.get(),
                        Variant.variant().with(
                                VariantProperties.MODEL,
                                barrelModel
                        )
                ).with(builder.createColumnWithFacing())
        );
    }
}