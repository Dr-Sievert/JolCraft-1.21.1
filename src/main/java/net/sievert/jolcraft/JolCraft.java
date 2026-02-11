package net.sievert.jolcraft;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.sievert.jolcraft.data.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.util.JolCraftLogTags;
import net.sievert.jolcraft.util.JolCraftLogs;
import net.sievert.jolcraft.world.block.JolCraftBlocks;
import net.sievert.jolcraft.world.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.data.attachment.JolCraftAttachments;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.JolCraftStats;
import net.sievert.jolcraft.world.effect.JolCraftEffects;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.data.JolCraftAttributes;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.interaction.DwarfInteractions;
import net.sievert.jolcraft.world.entity.custom.dwarf.util.loadout.DwarfLoadouts;
import net.sievert.jolcraft.world.item.creative.JolCraftCreativeModeTabs;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.util.equipment.JolCraftEquipmentAssets;
import net.sievert.jolcraft.world.item.potion.JolCraftPotions;
import net.sievert.jolcraft.world.loot.JolCraftLootModifiers;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.data.recipe.JolCraftRecipes;
import net.sievert.jolcraft.world.gui.JolCraftMenuTypes;
import net.sievert.jolcraft.world.sound.JolCraftSounds;
import net.sievert.jolcraft.world.worldgen.predicate.JolCraftBlockPredicateTypes;
import net.sievert.jolcraft.world.worldgen.processor.JolCraftProcessors;
import net.sievert.jolcraft.world.worldgen.structure.JolCraftStructures;

@Mod(JolCraft.MOD_ID)
public class JolCraft {

    public static final String MOD_NAME = "JolCraft";
    public static final String MOD_ID = "jolcraft";

    public JolCraft(IEventBus modEventBus, ModContainer modContainer) {

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Initializing JolCraft {}",
                modContainer.getModInfo().getVersion()
        );

        // --- Registry & system setup ---
        JolCraftBlocks.register(modEventBus);
        JolCraftItems.register(modEventBus);
        JolCraftEntities.register(modEventBus);
        JolCraftBlockEntities.register(modEventBus);
        JolCraftMenuTypes.register(modEventBus);
        JolCraftCreativeModeTabs.register(modEventBus);
        JolCraftDataComponents.register(modEventBus);
        JolCraftLootModifiers.register(modEventBus);
        JolCraftSounds.register(modEventBus);
        JolCraftEffects.register(modEventBus);
        JolCraftPotions.register(modEventBus);
        JolCraftProcessors.register(modEventBus);
        JolCraftBlockPredicateTypes.register(modEventBus);
        JolCraftAttachments.register(modEventBus);
        JolCraftStats.register(modEventBus);
        JolCraftEquipmentAssets.register(modEventBus);
        JolCraftRecipes.register(modEventBus);
        JolCraftAttributes.register(modEventBus);
        JolCraftStructures.STRUCTURE_TYPES.register(modEventBus);

        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Queued all registrations"
        );

        // --- Events ---
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::loadComplete);
        modEventBus.addListener(JolCraftNetworking::register);
        modEventBus.addListener(JolCraftCriteriaTriggers::register);

        JolCraftLogs.debug(
                JolCraftLogTags.INIT,
                "Registered listeners"
        );
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            DwarfInteractions.registerAll();
            DwarfLoadouts.bootstrap();

            JolCraftLogs.info(
                    JolCraftLogTags.INIT,
                    "Common setup complete"
            );
        });
    }

    @OnlyIn(Dist.CLIENT)
    private void clientSetup(final FMLClientSetupEvent event) {
        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Client setup complete"
        );
    }

    private void loadComplete(final FMLLoadCompleteEvent event) {
        JolCraftLogs.info(
                JolCraftLogTags.INIT,
                "Load complete"
        );
    }

    // --- Utility for ResourceLocation under this modid ---
    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
