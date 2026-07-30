package net.sievert.jolcraft.event.client.mod;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.client.render.FermentingCauldronRenderer;
import net.sievert.jolcraft.world.block.fluid.util.brewing.BrewingColors;
import net.sievert.jolcraft.world.block.fluid.JolCraftFluids;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.component.JolCraftDataComponents;
import net.sievert.jolcraft.world.item.client.color.JolCraftItemColors;
import net.sievert.jolcraft.world.item.client.property.JolCraftItemProperties;
import net.sievert.jolcraft.world.item.client.tooltip.JolCraftTooltipRenderers;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.entity.client.model.creature.MuffhornModel;
import net.sievert.jolcraft.world.block.entity.custom.client.model.StrongboxModel;
import net.sievert.jolcraft.world.entity.client.model.dwarf.*;
import net.sievert.jolcraft.world.entity.client.model.object.RadiantModel;
import net.sievert.jolcraft.world.entity.client.render.creature.MuffhornRenderer;
import net.sievert.jolcraft.world.block.entity.custom.client.render.StrongboxRenderer;
import net.sievert.jolcraft.world.entity.client.render.dwarf.*;
import net.sievert.jolcraft.world.entity.client.render.object.RadiantRenderer;
import net.sievert.jolcraft.world.gui.JolCraftMenuTypes;
import net.sievert.jolcraft.world.gui.client.screen.DwarfMerchantScreen;
import net.sievert.jolcraft.world.gui.client.screen.LapidaryBenchScreen;
import net.sievert.jolcraft.world.gui.client.screen.LockScreen;
import net.sievert.jolcraft.world.gui.client.screen.StrongboxScreen;
import net.sievert.jolcraft.world.item.custom.container.strongbox.client.StrongboxItemRenderer;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class JolCraftClientModEvents {

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(JolCraftItemProperties::register);
    }

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {

        int entityRenderers = 0;
        int itemBlockRenderTypes = 0;

        // Dwarves
        EntityRenderers.register(JolCraftEntities.DWARF.get(), DwarfRenderer::new); entityRenderers++;
        EntityRenderers.register(JolCraftEntities.DWARF_GUILDMASTER.get(), ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_GUILDMASTER.get())); entityRenderers++;
        EntityRenderers.register(JolCraftEntities.DWARF_HISTORIAN.get(), ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_HISTORIAN.get())); entityRenderers++;
        EntityRenderers.register(JolCraftEntities.DWARF_MERCHANT.get(), ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_MERCHANT.get())); entityRenderers++;
        EntityRenderers.register(JolCraftEntities.DWARF_SCRAPPER.get(), ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_SCRAPPER.get())); entityRenderers++;
        EntityRenderers.register(JolCraftEntities.DWARF_BREWMASTER.get(), ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_BREWMASTER.get())); entityRenderers++;
        EntityRenderers.register(JolCraftEntities.DWARF_GUARD.get(), ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_GUARD.get(), 1.1f)); entityRenderers++;
        EntityRenderers.register(JolCraftEntities.DWARF_KEEPER.get(), ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_KEEPER.get())); entityRenderers++;
        EntityRenderers.register(JolCraftEntities.DWARF_ARTISAN.get(), ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_ARTISAN.get())); entityRenderers++;
        EntityRenderers.register(JolCraftEntities.DWARF_EXPLORER.get(), ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_EXPLORER.get())); entityRenderers++;
        EntityRenderers.register(JolCraftEntities.DWARF_MINER.get(), ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_MINER.get())); entityRenderers++;
        EntityRenderers.register(JolCraftEntities.DWARF_ALCHEMIST.get(), ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_ALCHEMIST.get())); entityRenderers++;
        EntityRenderers.register(JolCraftEntities.DWARF_ARCANIST.get(), ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_ARCANIST.get())); entityRenderers++;
        EntityRenderers.register(JolCraftEntities.DWARF_PRIEST.get(), ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_PRIEST.get())); entityRenderers++;
        EntityRenderers.register(JolCraftEntities.DWARF_BLACKSMITH.get(), ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_BLACKSMITH.get())); entityRenderers++;
        EntityRenderers.register(JolCraftEntities.DWARF_CHAMPION.get(), ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_CHAMPION.get(), 1.1f)); entityRenderers++;
        EntityRenderers.register(JolCraftEntities.DWARF_SMELTER.get(), ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_SMELTER.get())); entityRenderers++;

        // Animals
        EntityRenderers.register(JolCraftEntities.MUFFHORN.get(), MuffhornRenderer::new); entityRenderers++;

        // Objects
        EntityRenderers.register(JolCraftEntities.RADIANT.get(), RadiantRenderer::new); entityRenderers++;

        // Blocks
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.VERDANT_FARMLAND.get(), RenderType.cutout()); itemBlockRenderTypes++;
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.BARLEY_CROP.get(), RenderType.cutout()); itemBlockRenderTypes++;
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.DEEPSLATE_BULBS_CROP.get(), RenderType.cutout()); itemBlockRenderTypes++;
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.ASGARNIAN_CROP_BOTTOM.get(), RenderType.cutout()); itemBlockRenderTypes++;
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.ASGARNIAN_CROP_TOP.get(), RenderType.cutout()); itemBlockRenderTypes++;
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.DUSKHOLD_CROP_BOTTOM.get(), RenderType.cutout()); itemBlockRenderTypes++;
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.DUSKHOLD_CROP_TOP.get(), RenderType.cutout()); itemBlockRenderTypes++;
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.KRANDONIAN_CROP_BOTTOM.get(), RenderType.cutout()); itemBlockRenderTypes++;
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.KRANDONIAN_CROP_TOP.get(), RenderType.cutout()); itemBlockRenderTypes++;
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.YANILLIAN_CROP_BOTTOM.get(), RenderType.cutout()); itemBlockRenderTypes++;
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.YANILLIAN_CROP_TOP.get(), RenderType.cutout()); itemBlockRenderTypes++;

        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.FERMENTING_CAULDRON.get(), RenderType.cutout()); itemBlockRenderTypes++;
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.DEEPSLATE_MORTAR.get(), RenderType.cutout()); itemBlockRenderTypes++;

        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.DUSKCAP.get(), RenderType.cutout()); itemBlockRenderTypes++;
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.POTTED_DUSKCAP.get(), RenderType.cutout()); itemBlockRenderTypes++;
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.FESTERLING_CROP.get(), RenderType.cutout()); itemBlockRenderTypes++;
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.FESTERLING.get(), RenderType.cutout()); itemBlockRenderTypes++;
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.POTTED_FESTERLING.get(), RenderType.cutout()); itemBlockRenderTypes++;

        JolCraftLogs.info(JolCraftLogTags.INIT, "Registered {} entity renderers and {} item/block render types", entityRenderers, itemBlockRenderTypes);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {

        int layers = 0;

        // Entity

        event.registerLayerDefinition(DwarfModel.LAYER_LOCATION, DwarfModel::createBodyLayer); layers++;

        event.registerLayerDefinition(MuffhornModel.LAYER_LOCATION, MuffhornModel::createBodyLayer); layers++;

        event.registerLayerDefinition(RadiantModel.LAYER_LOCATION, RadiantModel::createBodyLayer); layers++;

        // BlockEntity

        event.registerLayerDefinition(StrongboxModel.LAYER_LOCATION, StrongboxModel::createBodyLayer); layers++;

        JolCraftLogs.info(JolCraftLogTags.INIT, "Registered {} layer definitions", layers);
    }

    @SubscribeEvent
    public static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        JolCraftTooltipRenderers.register(event);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        JolCraftItemColors.register(event);
    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        int ber = 0;

        event.registerBlockEntityRenderer(JolCraftBlockEntities.STRONGBOX.get(), StrongboxRenderer::new); ber++;
        event.registerBlockEntityRenderer(JolCraftBlockEntities.FERMENTING_CAULDRON.get(), FermentingCauldronRenderer::new); ber++;

        JolCraftLogs.info(JolCraftLogTags.INIT, "Registered {} block entity renderers", ber);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        int screens = 0;

        event.register(JolCraftMenuTypes.DWARF_MERCHANT_MENU.get(), DwarfMerchantScreen::new); screens++;
        event.register(JolCraftMenuTypes.STRONGBOX_MENU.get(), StrongboxScreen::new); screens++;
        event.register(JolCraftMenuTypes.LOCK_MENU.get(), LockScreen::new); screens++;
        event.register(JolCraftMenuTypes.LAPIDARY_BENCH_MENU.get(), LapidaryBenchScreen::new); screens++;

        JolCraftLogs.info(JolCraftLogTags.INIT, "Registered {} menu screens", screens);
    }

    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {

        event.registerItem(
                new IClientItemExtensions() {
                    private final StrongboxItemRenderer renderer =
                            new StrongboxItemRenderer();

                    @Override
                    public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
                        return this.renderer;
                    }
                },
                JolCraftItems.STRONGBOX_ITEM.get()
        );

        registerTintedFluid(
                event,
                JolCraftFluids.DWARVEN_BREW_TYPE.get(),
                BrewingColors.DWARVEN_BREW
        );

        registerTintedFluid(
                event,
                JolCraftFluids.UNFINISHED_DWARVEN_BREW_TYPE.get(),
                BrewingColors.UNFINISHED_DWARVEN_BREW
        );

        registerTintedFluid(
                event,
                JolCraftFluids.YEAST_TYPE.get(),
                BrewingColors.YEAST
        );

        registerTintedFluid(
                event,
                JolCraftFluids.UNFINISHED_YEAST_TYPE.get(),
                BrewingColors.UNFINISHED_YEAST
        );
    }

    private static void registerTintedFluid(
            RegisterClientExtensionsEvent event,
            FluidType fluid,
            int defaultColor
    ) {
        event.registerFluidType(
                new IClientFluidTypeExtensions() {

                    private static final ResourceLocation STILL_TEXTURE =
                            ResourceLocation.withDefaultNamespace("block/water_still");

                    private static final ResourceLocation FLOWING_TEXTURE =
                            ResourceLocation.withDefaultNamespace("block/water_flow");

                    @Override
                    public @NotNull ResourceLocation getStillTexture() {
                        return STILL_TEXTURE;
                    }

                    @Override
                    public @NotNull ResourceLocation getFlowingTexture() {
                        return FLOWING_TEXTURE;
                    }

                    @Override
                    public int getTintColor() {
                        return defaultColor;
                    }

                    @Override
                    public int getTintColor(@NotNull FluidStack stack) {
                        return stack.getOrDefault(
                                JolCraftDataComponents.BREW_COLOR.get(),
                                defaultColor
                        );
                    }
                },
                fluid
        );
    }
}