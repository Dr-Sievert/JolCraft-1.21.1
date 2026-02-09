package net.sievert.jolcraft.event.mod;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.util.log.JolCraftLogTags;
import net.sievert.jolcraft.util.log.JolCraftLogs;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.data.JolCraftAttributes;
import net.sievert.jolcraft.world.entity.custom.animal.MuffhornEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;

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

        event.put(JolCraftEntities.MUFFHORN.get(), MuffhornEntity.createAttributes().build()); entityTypes++;

        JolCraftLogs.info(JolCraftLogTags.INIT, "Registered attributes for {} entity types", entityTypes);
    }

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        int added = 0;

        event.add(EntityType.PLAYER, JolCraftAttributes.XP_BOOST); added++;
        event.add(EntityType.PLAYER, JolCraftAttributes.SLOW_RESISTANCE); added++;
        event.add(EntityType.PLAYER, JolCraftAttributes.EXTRA_CROP); added++;
        event.add(EntityType.PLAYER, JolCraftAttributes.EXTRA_CHEST_LOOT); added++;
        event.add(EntityType.PLAYER, JolCraftAttributes.RADIANT); added++;
        event.add(EntityType.PLAYER, JolCraftAttributes.ARMOR_UNBREAKING); added++;
        event.add(EntityType.PLAYER, JolCraftAttributes.MAGIC_RESISTANCE); added++;
        event.add(EntityType.PLAYER, JolCraftAttributes.ARMOR_INCREASE); added++;
        event.add(EntityType.PLAYER, JolCraftAttributes.ATTACK_DAMAGE_INCREASE); added++;
        event.add(EntityType.PLAYER, JolCraftAttributes.MOVEMENT_SPEED_BOOST_DAY); added++;
        event.add(EntityType.PLAYER, JolCraftAttributes.MOVEMENT_SPEED_BOOST_NIGHT); added++;

        JolCraftLogs.info(JolCraftLogTags.INIT, "Registered {} new player attributes", added);
    }
}