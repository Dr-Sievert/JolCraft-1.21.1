package net.sievert.jolcraft.event.mod;

import net.minecraft.world.entity.EntityType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.entity.JolCraftEntities;
import net.sievert.jolcraft.data.JolCraftAttributes;
import net.sievert.jolcraft.entity.custom.animal.MuffhornEntity;
import net.sievert.jolcraft.entity.custom.dwarf.*;
import net.sievert.jolcraft.entity.custom.dwarf.profession.*;

@EventBusSubscriber(modid = JolCraft.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class JolCraftModEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {

        event.put(JolCraftEntities.DWARF.get(), EntityEntity.createAttributes().build());
        event.put(JolCraftEntities.DWARF_GUILDMASTER.get(), EntityGuildmasterEntity.createAttributes().build());
        event.put(JolCraftEntities.DWARF_HISTORIAN.get(), EntityHistorianEntity.createAttributes().build());
        event.put(JolCraftEntities.DWARF_MERCHANT.get(), EntityMerchantEntity.createAttributes().build());
        event.put(JolCraftEntities.DWARF_SCRAPPER.get(), EntityScrapperEntity.createAttributes().build());
        event.put(JolCraftEntities.DWARF_BREWMASTER.get(), EntityBrewmasterEntity.createAttributes().build());
        event.put(JolCraftEntities.DWARF_GUARD.get(), EntityGuardEntity.createAttributes().build());
        event.put(JolCraftEntities.DWARF_KEEPER.get(), EntityKeeperEntity.createAttributes().build());
        event.put(JolCraftEntities.DWARF_ARTISAN.get(), EntityArtisanEntity.createAttributes().build());
        event.put(JolCraftEntities.DWARF_EXPLORER.get(), EntityExplorerEntity.createAttributes().build());
        event.put(JolCraftEntities.DWARF_MINER.get(), EntityMinerEntity.createAttributes().build());
        event.put(JolCraftEntities.DWARF_ALCHEMIST.get(), EntityAlchemistEntity.createAttributes().build());
        event.put(JolCraftEntities.DWARF_ARCANIST.get(), EntityArcanistEntity.createAttributes().build());
        event.put(JolCraftEntities.DWARF_PRIEST.get(), EntityPriestEntity.createAttributes().build());

        event.put(JolCraftEntities.MUFFHORN.get(), MuffhornEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onEntityAttributeModification(EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, JolCraftAttributes.XP_BOOST);
        event.add(EntityType.PLAYER, JolCraftAttributes.SLOW_RESIST);
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
