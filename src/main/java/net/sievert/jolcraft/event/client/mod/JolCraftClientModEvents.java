package net.sievert.jolcraft.event.client.mod;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.block.custom.FermentingCauldronBlock;
import net.sievert.jolcraft.block.custom.FermentingStage;
import net.sievert.jolcraft.block.custom.crop.HopsType;
import net.sievert.jolcraft.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.client.item.coin.CoinPouchTooltipRenderer;
import net.sievert.jolcraft.client.item.compass.DialColor;
import net.sievert.jolcraft.client.item.coin.CoinPouchTooltip;
import net.sievert.jolcraft.entity.JolCraftEntities;
import net.sievert.jolcraft.entity.client.model.animal.MuffhornModel;
import net.sievert.jolcraft.entity.client.model.blockentity.StrongboxModel;
import net.sievert.jolcraft.entity.client.model.dwarf.*;
import net.sievert.jolcraft.entity.client.model.object.RadiantModel;
import net.sievert.jolcraft.entity.client.render.animal.MuffhornRenderer;
import net.sievert.jolcraft.entity.client.render.block.StrongboxRenderer;
import net.sievert.jolcraft.entity.client.render.dwarf.*;
import net.sievert.jolcraft.entity.client.render.object.RadiantRenderer;
import net.sievert.jolcraft.gui.JolCraftMenuTypes;
import net.sievert.jolcraft.gui.custom.dwarf.DwarfMerchantScreen;
import net.sievert.jolcraft.gui.custom.lapidary_bench.LapidaryBenchScreen;
import net.sievert.jolcraft.gui.custom.strongbox.LockScreen;
import net.sievert.jolcraft.gui.custom.strongbox.StrongboxScreen;
import net.sievert.jolcraft.data.custom.component.CoinPouchAmountProperty;
import net.sievert.jolcraft.data.custom.component.LoreLineIdProperty;
import net.sievert.jolcraft.client.item.compass.DeepslateCompassAngle;


@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class JolCraftClientModEvents {

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        //Dwarves
        EntityRenderers.register(JolCraftEntities.DWARF.get(), DwarfRenderer::new);
        EntityRenderers.register(JolCraftEntities.DWARF_GUILDMASTER.get(), DwarfGuildmasterRenderer::new);
        EntityRenderers.register(JolCraftEntities.DWARF_HISTORIAN.get(), DwarfHistorianRenderer::new);
        EntityRenderers.register(JolCraftEntities.DWARF_MERCHANT.get(), DwarfMerchantRenderer::new);
        EntityRenderers.register(JolCraftEntities.DWARF_SCRAPPER.get(), DwarfScrapperRenderer::new);
        EntityRenderers.register(JolCraftEntities.DWARF_BREWMASTER.get(), DwarfBrewmasterRenderer::new);
        EntityRenderers.register(JolCraftEntities.DWARF_GUARD.get(), DwarfGuardRenderer::new);
        EntityRenderers.register(JolCraftEntities.DWARF_KEEPER.get(), DwarfKeeperRenderer::new);
        EntityRenderers.register(JolCraftEntities.DWARF_ARTISAN.get(), DwarfArtisanRenderer::new);
        EntityRenderers.register(JolCraftEntities.DWARF_EXPLORER.get(), DwarfExplorerRenderer::new);
        EntityRenderers.register(JolCraftEntities.DWARF_MINER.get(), DwarfMinerRenderer::new);

        //Animals
        EntityRenderers.register(JolCraftEntities.MUFFHORN.get(), MuffhornRenderer::new);

        //Objects
        EntityRenderers.register(JolCraftEntities.RADIANT.get(), RadiantRenderer::new);

        //Blocks
        //ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.STRONGBOX.get(), RenderType.cutout());

        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.VERDANT_FARMLAND.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.BARLEY_CROP.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.DEEPSLATE_BULBS_CROP.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.ASGARNIAN_CROP_BOTTOM.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.ASGARNIAN_CROP_TOP.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.DUSKHOLD_CROP_BOTTOM.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.DUSKHOLD_CROP_TOP.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.KRANDONIAN_CROP_BOTTOM.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.KRANDONIAN_CROP_TOP.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.YANILLIAN_CROP_BOTTOM.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.YANILLIAN_CROP_TOP.get(), RenderType.cutout());

        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.FERMENTING_CAULDRON.get(), RenderType.cutout());

        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.DUSKCAP.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.POTTED_DUSKCAP.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.FESTERLING_CROP.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.FESTERLING.get(), RenderType.cutout());
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.POTTED_FESTERLING.get(), RenderType.cutout());

    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {

        //Dwarves
        event.registerLayerDefinition(DwarfModel.LAYER_LOCATION, DwarfModel::createBodyLayer);
        event.registerLayerDefinition(DwarfGuildmasterModel.LAYER_LOCATION, DwarfGuildmasterModel::createBodyLayer);
        event.registerLayerDefinition(DwarfHistorianModel.LAYER_LOCATION, DwarfHistorianModel::createBodyLayer);
        event.registerLayerDefinition(DwarfMerchantModel.LAYER_LOCATION, DwarfMerchantModel::createBodyLayer);
        event.registerLayerDefinition(DwarfScrapperModel.LAYER_LOCATION, DwarfScrapperModel::createBodyLayer);
        event.registerLayerDefinition(DwarfBrewmasterModel.LAYER_LOCATION, DwarfBrewmasterModel::createBodyLayer);
        event.registerLayerDefinition(DwarfGuardModel.LAYER_LOCATION, DwarfGuardModel::createBodyLayer);
        event.registerLayerDefinition(DwarfKeeperModel.LAYER_LOCATION, DwarfKeeperModel::createBodyLayer);
        event.registerLayerDefinition(DwarfArtisanModel.LAYER_LOCATION, DwarfArtisanModel::createBodyLayer);
        event.registerLayerDefinition(DwarfExplorerModel.LAYER_LOCATION, DwarfExplorerModel::createBodyLayer);
        event.registerLayerDefinition(DwarfMinerModel.LAYER_LOCATION, DwarfMinerModel::createBodyLayer);

        //Animals
        event.registerLayerDefinition(MuffhornModel.LAYER_LOCATION, MuffhornModel::createBodyLayer);
        event.registerLayerDefinition(
                MuffhornModel.BABY_LAYER_LOCATION,
                () -> MuffhornModel.createBodyLayer().apply(MuffhornModel.BABY_TRANSFORMER)
        );

        //Objects
        event.registerLayerDefinition(RadiantModel.LAYER_LOCATION, RadiantModel::createBodyLayer);

        //Blocks
        event.registerLayerDefinition(StrongboxModel.LAYER_LOCATION, StrongboxModel::createBodyLayer);

    }

    @SubscribeEvent
    public static void registerBER(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(JolCraftBlockEntities.STRONGBOX.get(), StrongboxRenderer::new);
    }

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(JolCraftMenuTypes.DWARF_MERCHANT_MENU.get(), DwarfMerchantScreen::new);
        event.register(JolCraftMenuTypes.STRONGBOX_MENU.get(), StrongboxScreen::new);
        event.register(JolCraftMenuTypes.LOCK_MENU.get(), LockScreen::new);
        event.register(JolCraftMenuTypes.LAPIDARY_BENCH_MENU.get(), LapidaryBenchScreen::new);
    }

    @SubscribeEvent
    public static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        event.register(
                CoinPouchTooltip.class,
                CoinPouchTooltipRenderer::new
        );
    }

    @SubscribeEvent
    public static void onRegisterSelectItemModelProperty(RegisterSelectItemModelPropertyEvent event) {
        event.register(
                ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "lore_line_id"),
                LoreLineIdProperty.TYPE
        );
        event.register(
                CoinPouchAmountProperty.KEY,
                CoinPouchAmountProperty.TYPE
        );
    }

    @SubscribeEvent
    public static void onRegisterRangeSelectItemModelProperty(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(
                ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "deepslate_compass_angle"),
                DeepslateCompassAngle.MAP_CODEC
        );
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void registerTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(
                ResourceLocation.fromNamespaceAndPath(JolCraft.MOD_ID, "dial_color"),
                DialColor.MAP_CODEC
        );
    }

    private static final int YEAST_FERMENTING_START = 0xFF8EE8AA; // Light green
    private static final int YEAST_FERMENTING_END = 0xFF40B14A; // Dark green
    private static final int MALT_COLOR = 0xFFB16A1D; // Brown/orange malt
    private static final int BREW_COLOR = 0xFF9A652B; // Final orange brew
    private static final int BLEND_COLOR = 0xFFA66824; // Malt and brew blend

    @SubscribeEvent
    public static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, world, pos, tintIndex) -> {
            if (tintIndex != 0) return 0xFFFFFFFF; // Default fallback color

            // Retrieve the current stage of fermentation
            FermentingStage stage = state.getValue(FermentingCauldronBlock.STAGE);

            // Retrieve the current hop type from the block state
            HopsType hopsType = state.getValue(FermentingCauldronBlock.HOPS_TYPE);

            // Color blending logic for fermentation stages
            float ratio = 0f;
            if (state.hasProperty(FermentingCauldronBlock.FERMENTATION_PROGRESS)) {
                int progress = state.getValue(FermentingCauldronBlock.FERMENTATION_PROGRESS);
                ratio = clamp01(progress / 9f);
            }


            // If the cauldron is in the 'MALTED' stage, return the malt color directly without blending
            if (stage == FermentingStage.MALTED) {
                return MALT_COLOR;  // Just the malt color when in MALTED stage
            }


            if (state.getValue(FermentingCauldronBlock.STAGE) != FermentingStage.HOPS){
                return switch (stage) {
                    case YEAST_FERMENTING ->
                            blendColors(YEAST_FERMENTING_START, YEAST_FERMENTING_END, ratio); // Blend yeast fermentation
                    case BREW_FERMENTING ->
                            blendColors(BLEND_COLOR, BREW_COLOR, ratio); // Blending between hop and final brew color
                    case YEAST_READY -> YEAST_FERMENTING_END; // Final yeast color (dark green)
                    case BREW_READY -> BREW_COLOR; // Final brew color when brewing is completed
                    default -> YEAST_FERMENTING_START; // Default color for fallback
                };
            }

            // Check for specific hop types and return corresponding color
            return switch (hopsType) {
                case ASGARNIAN -> 0xFF6B5352; // Color for Asgarnian hop
                case DUSKHOLD -> 0xFF5F5864; // Color for Duskhold hop
                case KRANDONIAN -> 0xFF526B69; // Color for Krandonian hop
                case YANILLIAN -> 0xFF2B4318; // Color for Yanillian hop
                default -> 0xFFB16A1D; // Default hop color
            };

        }, JolCraftBlocks.FERMENTING_CAULDRON.get());
    }

    // Helper method to clamp a float value between 0 and 1
    private static float clamp01(float value) {
        return Math.min(1.0f, Math.max(0.0f, value));
    }

    // Helper method to blend two colors based on the given ratio
    private static int blendColors(int color1, int color2, float ratio) {
        ratio = clamp01(ratio);
        int a1 = (color1 >> 24) & 0xFF;
        int r1 = (color1 >> 16) & 0xFF;
        int g1 = (color1 >> 8) & 0xFF;
        int b1 = color1 & 0xFF;

        int a2 = (color2 >> 24) & 0xFF;
        int r2 = (color2 >> 16) & 0xFF;
        int g2 = (color2 >> 8) & 0xFF;
        int b2 = color2 & 0xFF;

        int a = (int) (a1 + (a2 - a1) * ratio);
        int r = (int) (r1 + (r2 - r1) * ratio);
        int g = (int) (g1 + (g2 - g1) * ratio);
        int b = (int) (b1 + (b2 - b1) * ratio);

        return (a << 24) | (r << 16) | (g << 8) | b;
    }

}