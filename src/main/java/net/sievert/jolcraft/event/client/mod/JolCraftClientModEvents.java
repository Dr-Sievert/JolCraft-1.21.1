package net.sievert.jolcraft.event.client.mod;

import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.client.render.FermentingCauldronRenderer;
import net.sievert.jolcraft.world.item.client.BrewColor;
import net.sievert.jolcraft.world.item.client.coin.CoinPouchTooltipRenderer;
import net.sievert.jolcraft.world.item.client.compass.DialColor;
import net.sievert.jolcraft.world.item.util.coin.CoinPouchTooltip;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.entity.client.model.animal.MuffhornModel;
import net.sievert.jolcraft.world.block.entity.custom.client.model.StrongboxModel;
import net.sievert.jolcraft.world.entity.client.model.dwarf.*;
import net.sievert.jolcraft.world.entity.client.model.object.RadiantModel;
import net.sievert.jolcraft.world.entity.client.render.animal.MuffhornRenderer;
import net.sievert.jolcraft.world.block.entity.custom.client.render.StrongboxRenderer;
import net.sievert.jolcraft.world.entity.client.render.dwarf.*;
import net.sievert.jolcraft.world.entity.client.render.object.RadiantRenderer;
import net.sievert.jolcraft.world.gui.JolCraftMenuTypes;
import net.sievert.jolcraft.world.gui.custom.screen.DwarfMerchantScreen;
import net.sievert.jolcraft.world.gui.custom.screen.LapidaryBenchScreen;
import net.sievert.jolcraft.world.gui.custom.screen.LockScreen;
import net.sievert.jolcraft.world.gui.custom.screen.StrongboxScreen;
import net.sievert.jolcraft.world.item.client.coin.CoinPouchAmountProperty;
import net.sievert.jolcraft.data.custom.lore.client.LoreKeyProperty;
import net.sievert.jolcraft.world.item.client.compass.DeepslateCompassAngle;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class JolCraftClientModEvents {

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
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

        // Animals
        EntityRenderers.register(JolCraftEntities.MUFFHORN.get(), MuffhornRenderer::new); entityRenderers++;

        // Objects
        EntityRenderers.register(JolCraftEntities.RADIANT.get(), RadiantRenderer::new); entityRenderers++;

        // Blocks (cutout layers)
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

        JolCraftLogs.info(JolCraftLogTags.INIT,
                "Registered {} entity renderers and {} item/block render types",
                entityRenderers, itemBlockRenderTypes);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        int layers = 0;

        event.registerLayerDefinition(DwarfModel.LAYER_LOCATION, DwarfModel::createBodyLayer); layers++;

        event.registerLayerDefinition(MuffhornModel.LAYER_LOCATION, MuffhornModel::createBodyLayer); layers++;
        event.registerLayerDefinition(MuffhornModel.BABY_LAYER_LOCATION,
                () -> MuffhornModel.createBodyLayer().apply(MuffhornModel.BABY_TRANSFORMER)); layers++;

        event.registerLayerDefinition(RadiantModel.LAYER_LOCATION, RadiantModel::createBodyLayer); layers++;

        event.registerLayerDefinition(StrongboxModel.LAYER_LOCATION, StrongboxModel::createBodyLayer); layers++;

        JolCraftLogs.info(JolCraftLogTags.INIT, "Registered {} layer definitions", layers);
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
    public static void registerTooltipFactories(RegisterClientTooltipComponentFactoriesEvent event) {
        int tooltips = 0;
        event.register(CoinPouchTooltip.class, CoinPouchTooltipRenderer::new); tooltips++;
        JolCraftLogs.info(JolCraftLogTags.INIT, "Registered {} tooltip factories", tooltips);
    }

    @SubscribeEvent
    public static void onRegisterSelectItemModelProperty(RegisterSelectItemModelPropertyEvent event) {
        int props = 0;
        event.register(LoreKeyProperty.KEY, LoreKeyProperty.TYPE); props++;
        event.register(CoinPouchAmountProperty.KEY, CoinPouchAmountProperty.TYPE); props++;
        JolCraftLogs.info(JolCraftLogTags.INIT, "Registered {} select item properties", props);
    }

    @SubscribeEvent
    public static void onRegisterRangeSelectItemModelProperty(RegisterRangeSelectItemModelPropertyEvent event) {
        int props = 0;
        event.register(JolCraft.location("deepslate_compass_angle"), DeepslateCompassAngle.MAP_CODEC); props++;
        JolCraftLogs.info(JolCraftLogTags.INIT, "Registered {} range select item properties", props);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onRegisterTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        int tints = 0;
        event.register(JolCraft.location("dial_color"), DialColor.MAP_CODEC); tints++;
        event.register(JolCraft.location("brew_color"), BrewColor.MAP_CODEC); tints++;
        JolCraftLogs.info(JolCraftLogTags.INIT, "Registered {} item tint sources", tints);
    }
}