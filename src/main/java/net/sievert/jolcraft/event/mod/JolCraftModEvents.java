package net.sievert.jolcraft.event.mod;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.data.JolCraftAttributes;
import net.sievert.jolcraft.world.entity.custom.animal.MuffhornEntity;
import net.sievert.jolcraft.world.entity.custom.dwarf.base.AbstractDwarfEntity;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class JolCraftModEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        AttributeSupplier dwarf = AbstractDwarfEntity.createAttributes().build();

        event.put(JolCraftEntities.DWARF.get(), dwarf);
        event.put(JolCraftEntities.DWARF_GUILDMASTER.get(), dwarf);
        event.put(JolCraftEntities.DWARF_HISTORIAN.get(), dwarf);
        event.put(JolCraftEntities.DWARF_MERCHANT.get(), dwarf);
        event.put(JolCraftEntities.DWARF_SCRAPPER.get(), dwarf);
        event.put(JolCraftEntities.DWARF_BREWMASTER.get(), dwarf);
        event.put(JolCraftEntities.DWARF_GUARD.get(), dwarf);
        event.put(JolCraftEntities.DWARF_KEEPER.get(), dwarf);
        event.put(JolCraftEntities.DWARF_ARTISAN.get(), dwarf);
        event.put(JolCraftEntities.DWARF_EXPLORER.get(), dwarf);
        event.put(JolCraftEntities.DWARF_MINER.get(), dwarf);
        event.put(JolCraftEntities.DWARF_ALCHEMIST.get(), dwarf);
        event.put(JolCraftEntities.DWARF_ARCANIST.get(), dwarf);
        event.put(JolCraftEntities.DWARF_PRIEST.get(), dwarf);

        event.put(JolCraftEntities.MUFFHORN.get(), MuffhornEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, JolCraftAttributes.XP_BOOST);
        event.add(EntityType.PLAYER, JolCraftAttributes.SLOW_RESISTANCE);
        event.add(EntityType.PLAYER, JolCraftAttributes.EXTRA_CROP);
        event.add(EntityType.PLAYER, JolCraftAttributes.EXTRA_CHEST_LOOT);
        event.add(EntityType.PLAYER, JolCraftAttributes.RADIANT);
        event.add(EntityType.PLAYER, JolCraftAttributes.ARMOR_UNBREAKING);
        event.add(EntityType.PLAYER, JolCraftAttributes.MAGIC_RESISTANCE);
        event.add(EntityType.PLAYER, JolCraftAttributes.ARMOR_INCREASE);
        event.add(EntityType.PLAYER, JolCraftAttributes.ATTACK_DAMAGE_INCREASE);
        event.add(EntityType.PLAYER, JolCraftAttributes.MOVEMENT_SPEED_BOOST_DAY);
        event.add(EntityType.PLAYER, JolCraftAttributes.MOVEMENT_SPEED_BOOST_NIGHT);
    }
}
