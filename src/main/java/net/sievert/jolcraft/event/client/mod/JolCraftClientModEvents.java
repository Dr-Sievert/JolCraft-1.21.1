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
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.world.block.entity.custom.FermentingCauldronBlockEntity;
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
public class JolCraftClientModEvents {

    @SuppressWarnings("deprecation")
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event)
    {
        //Dwarves
        EntityRenderers.register(JolCraftEntities.DWARF.get(), DwarfRenderer::new);

        EntityRenderers.register(JolCraftEntities.DWARF_GUILDMASTER.get(),
                ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_GUILDMASTER.get()));

        EntityRenderers.register(JolCraftEntities.DWARF_HISTORIAN.get(),
                ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_HISTORIAN.get()));

        EntityRenderers.register(JolCraftEntities.DWARF_MERCHANT.get(),
                ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_MERCHANT.get()));

        EntityRenderers.register(JolCraftEntities.DWARF_SCRAPPER.get(),
                ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_SCRAPPER.get()));

        EntityRenderers.register(JolCraftEntities.DWARF_BREWMASTER.get(),
                ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_BREWMASTER.get()));

        EntityRenderers.register(JolCraftEntities.DWARF_GUARD.get(),
                ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_GUARD.get(), 1.1f));

        EntityRenderers.register(JolCraftEntities.DWARF_KEEPER.get(),
                ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_KEEPER.get()));

        EntityRenderers.register(JolCraftEntities.DWARF_ARTISAN.get(),
                ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_ARTISAN.get()));

        EntityRenderers.register(JolCraftEntities.DWARF_EXPLORER.get(),
                ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_EXPLORER.get()));

        EntityRenderers.register(JolCraftEntities.DWARF_MINER.get(),
                ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_MINER.get()));

        EntityRenderers.register(JolCraftEntities.DWARF_ALCHEMIST.get(),
                ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_ALCHEMIST.get()));

        EntityRenderers.register(JolCraftEntities.DWARF_ARCANIST.get(),
                ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_ARCANIST.get()));

        EntityRenderers.register(JolCraftEntities.DWARF_PRIEST.get(),
                ctx -> DwarfRenderer.profession(ctx, JolCraftEntities.DWARF_PRIEST.get()));

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
        event.registerBlockEntityRenderer(JolCraftBlockEntities.FERMENTING_CAULDRON.get(), FermentingCauldronRenderer::new);
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
        event.register(CoinPouchTooltip.class, CoinPouchTooltipRenderer::new);
    }

    @SubscribeEvent
    public static void onRegisterSelectItemModelProperty(RegisterSelectItemModelPropertyEvent event) {
        event.register(LoreKeyProperty.KEY, LoreKeyProperty.TYPE);
        event.register(CoinPouchAmountProperty.KEY, CoinPouchAmountProperty.TYPE);
    }

    @SubscribeEvent
    public static void onRegisterRangeSelectItemModelProperty(RegisterRangeSelectItemModelPropertyEvent event) {
        event.register(JolCraft.location("deepslate_compass_angle"), DeepslateCompassAngle.MAP_CODEC);
    }

    @SubscribeEvent(priority = EventPriority.NORMAL)
    public static void onRegisterTintSources(RegisterColorHandlersEvent.ItemTintSources event) {
        event.register(JolCraft.location("dial_color"), DialColor.MAP_CODEC);
        event.register(JolCraft.location("brew_color"), BrewColor.MAP_CODEC);
    }
}