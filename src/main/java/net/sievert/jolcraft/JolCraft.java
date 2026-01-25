package net.sievert.jolcraft;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.sievert.jolcraft.advancement.JolCraftCriteriaTriggers;
import net.sievert.jolcraft.block.JolCraftBlocks;
import net.sievert.jolcraft.block.entity.JolCraftBlockEntities;
import net.sievert.jolcraft.data.JolCraftAttachments;
import net.sievert.jolcraft.data.JolCraftDataComponents;
import net.sievert.jolcraft.data.JolCraftStats;
import net.sievert.jolcraft.effect.JolCraftEffects;
import net.sievert.jolcraft.entity.JolCraftEntities;
import net.sievert.jolcraft.data.JolCraftAttributes;
import net.sievert.jolcraft.item.creative.JolCraftCreativeModeTabs;
import net.sievert.jolcraft.item.JolCraftItems;
import net.sievert.jolcraft.item.armor.JolCraftEquipmentAssets;
import net.sievert.jolcraft.item.potion.JolCraftPotions;
import net.sievert.jolcraft.loot.JolCraftLootModifiers;
import net.sievert.jolcraft.network.JolCraftNetworking;
import net.sievert.jolcraft.recipe.JolCraftRecipes;
import net.sievert.jolcraft.gui.JolCraftMenuTypes;
import net.sievert.jolcraft.sound.JolCraftSounds;
import net.sievert.jolcraft.worldgen.predicate.JolCraftBlockPredicateTypes;
import net.sievert.jolcraft.worldgen.processor.JolCraftProcessors;
import net.sievert.jolcraft.worldgen.structure.JolCraftStructures;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

@Mod(JolCraft.MOD_ID)
public class JolCraft {

    public static final String MOD_ID = "jolcraft";

    public static final Logger LOGGER = LogUtils.getLogger();

    public JolCraft(IEventBus modEventBus, ModContainer modContainer) {

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
        JolCraftStructures.DEFERRED_REGISTRY_STRUCTURE.register(modEventBus);

        // --- Events ---
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(JolCraftNetworking::register);
        modEventBus.addListener(JolCraftCriteriaTriggers::register);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(JolCraftStats::initializeStats);
    }

    // --- Utility for ResourceLocation under this modid ---
    public static ResourceLocation location(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}
