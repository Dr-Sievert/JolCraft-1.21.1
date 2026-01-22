package net.sievert.jolcraft.event.client.mod;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.block.entity.custom.FermentingCauldronBlockEntity;
import net.sievert.jolcraft.item.client.coin.CoinPouchTooltipRenderer;
import net.sievert.jolcraft.item.client.compass.DialColor;
import net.sievert.jolcraft.entity.client.model.dwarf.profession.*;
import net.sievert.jolcraft.entity.client.render.dwarf.profession.*;
import net.sievert.jolcraft.item.util.coin.CoinPouchTooltip;
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
import net.sievert.jolcraft.item.client.coin.CoinPouchAmountProperty;
import net.sievert.jolcraft.data.custom.lore.client.LoreKeyProperty;
import net.sievert.jolcraft.item.client.compass.DeepslateCompassAngle;


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
        EntityRenderers.register(JolCraftEntities.DWARF_ALCHEMIST.get(), DwarfAlchemistRenderer::new);
        EntityRenderers.register(JolCraftEntities.DWARF_ARCANIST.get(), DwarfArcanistRenderer::new);
        EntityRenderers.register(JolCraftEntities.DWARF_PRIEST.get(), DwarfPriestRenderer::new);

        //Animals
        EntityRenderers.register(JolCraftEntities.MUFFHORN.get(), MuffhornRenderer::new);

        //Objects
        EntityRenderers.register(JolCraftEntities.RADIANT.get(), RadiantRenderer::new);

        //Blocks

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
        ItemBlockRenderTypes.setRenderLayer(JolCraftBlocks.DEEPSLATE_MORTAR.get(), RenderType.cutout());

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
        event.registerLayerDefinition(DwarfAlchemistModel.LAYER_LOCATION, DwarfAlchemistModel::createBodyLayer);
        event.registerLayerDefinition(DwarfArcanistModel.LAYER_LOCATION, DwarfArcanistModel::createBodyLayer);
        event.registerLayerDefinition(DwarfPriestModel.LAYER_LOCATION, DwarfPriestModel::createBodyLayer);

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
                LoreKeyProperty.KEY,
                LoreKeyProperty.TYPE
        );
        event.register(
                CoinPouchAmountProperty.KEY,
                CoinPouchAmountProperty.TYPE
        );
    }

    @SubscribeEvent
    public static void onRegisterRangeSelectItemModelProperty(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(
                JolCraft.location("deepslate_compass_angle"),
                DeepslateCompassAngle.MAP_CODEC
        );
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void registerTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(
                JolCraft.location("dial_color"),
                DialColor.MAP_CODEC
        );
    }

    @SubscribeEvent
    public static void onFermentingCauldronBlend(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (level == null || pos == null) return 0xFFFFFFFF;

            var be = level.getBlockEntity(pos);
            if (be instanceof FermentingCauldronBlockEntity cauldron) {
                return cauldron.getRenderColor();
            }

            return 0xFFFFFFFF;
        }, JolCraftBlocks.FERMENTING_CAULDRON.get());
    }
}