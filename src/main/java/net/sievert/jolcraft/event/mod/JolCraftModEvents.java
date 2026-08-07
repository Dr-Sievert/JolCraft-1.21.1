package net.sievert.jolcraft.event.mod;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.JolCraftRegistries;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.capability.JolCraftCapabilities;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.entity.JolCraftAttributes;
import net.sievert.jolcraft.world.entity.custom.creature.MuffhornEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class JolCraftModEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        AttributeSupplier dwarf = AbstractDwarfEntity.createAttributes().build();

        int entityTypes = 0;

        event.put(JolCraftEntities.DWARF.get(), dwarf); entityTypes++;
        event.put(JolCraftEntities.DWARF_GUILDMASTER.get(), dwarf); entityTypes++;
        event.put(JolCraftEntities.DWARF_HISTORIAN.get(), dwarf); entityTypes++;
        event.put(JolCraftEntities.DWARF_MERCHANT.get(), dwarf); entityTypes++;
        event.put(JolCraftEntities.DWARF_SCRAPPER.get(), dwarf); entityTypes++;
        event.put(JolCraftEntities.DWARF_BREWMASTER.get(), dwarf); entityTypes++;
        event.put(JolCraftEntities.DWARF_GUARD.get(), dwarf); entityTypes++;
        event.put(JolCraftEntities.DWARF_KEEPER.get(), dwarf); entityTypes++;
        event.put(JolCraftEntities.DWARF_ARTISAN.get(), dwarf); entityTypes++;
        event.put(JolCraftEntities.DWARF_EXPLORER.get(), dwarf); entityTypes++;
        event.put(JolCraftEntities.DWARF_MINER.get(), dwarf); entityTypes++;
        event.put(JolCraftEntities.DWARF_ALCHEMIST.get(), dwarf); entityTypes++;
        event.put(JolCraftEntities.DWARF_ARCANIST.get(), dwarf); entityTypes++;
        event.put(JolCraftEntities.DWARF_PRIEST.get(), dwarf); entityTypes++;
        event.put(JolCraftEntities.DWARF_BLACKSMITH.get(), dwarf); entityTypes++;
        event.put(JolCraftEntities.DWARF_CHAMPION.get(), dwarf); entityTypes++;
        event.put(JolCraftEntities.DWARF_SMELTER.get(), dwarf); entityTypes++;

        event.put(JolCraftEntities.MUFFHORN.get(), MuffhornEntity.createAttributes().build()); entityTypes++;

        JolCraftLogs.info(JolCraftLogTags.INIT, "Registered attributes for {} new entity types", entityTypes);
    }

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        int allEntities = addLivingEntityAttributes(event);
        int playerOnly = addPlayerOnlyAttributes(event);

        JolCraftLogs.info(JolCraftLogTags.INIT, "Registered {} attribute entries for all living entities", allEntities);
        JolCraftLogs.info(JolCraftLogTags.INIT, "Registered {} player-only attributes", playerOnly);
    }

    private static int addLivingEntityAttributes(EntityAttributeModificationEvent event) {
        int added = 0;

        added += addToAllLivingEntities(event, JolCraftAttributes.RADIANT);
        added += addToAllLivingEntities(event, JolCraftAttributes.ARMOR_PENETRATION);
        added += addToAllLivingEntities(event, JolCraftAttributes.MAGIC_RESISTANCE);
        added += addToAllLivingEntities(event, JolCraftAttributes.ATTACK_DAMAGE_INCREASE);
        added += addToAllLivingEntities(event, JolCraftAttributes.SLOW_RESISTANCE);
        added += addToAllLivingEntities(event, JolCraftAttributes.ARMOR_TOTAL);
        added += addToAllLivingEntities(event, JolCraftAttributes.MOON_SHIELD);
        added += addToAllLivingEntities(event, JolCraftAttributes.PROJECTILE_DAMAGE);

        return added;
    }

    private static int addToAllLivingEntities(
            EntityAttributeModificationEvent event,
            Holder<Attribute> attribute
    ) {
        int added = 0;

        for (EntityType<? extends LivingEntity> type : event.getTypes()) {
            event.add(type, attribute);
            added++;
        }

        return added;
    }

    private static int addPlayerOnlyAttributes(EntityAttributeModificationEvent event) {
        int added = 0;

        event.add(EntityType.PLAYER, JolCraftAttributes.EXPERIENCE_INCREASE); added++;
        event.add(EntityType.PLAYER, JolCraftAttributes.CROP_LOOT_INCREASE); added++;
        event.add(EntityType.PLAYER, JolCraftAttributes.CONTAINER_LOOT_INCREASE); added++;
        event.add(EntityType.PLAYER, JolCraftAttributes.ITEM_USE_SPEED); added++;

        return added;
    }

    @SubscribeEvent
    public static void registerRegistries(NewRegistryEvent event) {
        event.register(JolCraftRegistries.RECIPE_OUTPUT_TYPE);
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void registerCapabilities(
            RegisterCapabilitiesEvent event
    ) {
        JolCraftCapabilities.register(
                event
        );
    }
}