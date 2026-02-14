package net.sievert.jolcraft.world.entity.custom.dwarf.util.profession;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.entity.custom.dwarf.DwarfEntity;
import net.sievert.jolcraft.world.item.JolCraftItems;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class DwarfProfessionHelper {

    private DwarfProfessionHelper() {}

    private static final Map<DwarfProfession, Supplier<EntityType<DwarfEntity>>> TYPES =
            new EnumMap<>(DwarfProfession.class);

    private static final Map<DwarfProfession, DeferredItem<Item>> SPAWN_EGGS =
            new EnumMap<>(DwarfProfession.class);

    static {
        // Entity types
        TYPES.put(DwarfProfession.NONE, JolCraftEntities.DWARF);
        TYPES.put(DwarfProfession.ALCHEMIST, JolCraftEntities.DWARF_ALCHEMIST);
        TYPES.put(DwarfProfession.ARCANIST, JolCraftEntities.DWARF_ARCANIST);
        TYPES.put(DwarfProfession.ARTISAN, JolCraftEntities.DWARF_ARTISAN);
        TYPES.put(DwarfProfession.BREWMASTER, JolCraftEntities.DWARF_BREWMASTER);
        TYPES.put(DwarfProfession.EXPLORER, JolCraftEntities.DWARF_EXPLORER);
        TYPES.put(DwarfProfession.GUARD, JolCraftEntities.DWARF_GUARD);
        TYPES.put(DwarfProfession.GUILDMASTER, JolCraftEntities.DWARF_GUILDMASTER);
        TYPES.put(DwarfProfession.HISTORIAN, JolCraftEntities.DWARF_HISTORIAN);
        TYPES.put(DwarfProfession.KEEPER, JolCraftEntities.DWARF_KEEPER);
        TYPES.put(DwarfProfession.MERCHANT, JolCraftEntities.DWARF_MERCHANT);
        TYPES.put(DwarfProfession.MINER, JolCraftEntities.DWARF_MINER);
        TYPES.put(DwarfProfession.PRIEST, JolCraftEntities.DWARF_PRIEST);
        TYPES.put(DwarfProfession.SCRAPPER, JolCraftEntities.DWARF_SCRAPPER);

        // Spawn eggs
        SPAWN_EGGS.put(DwarfProfession.NONE, JolCraftItems.DWARF_SPAWN_EGG);
        SPAWN_EGGS.put(DwarfProfession.GUILDMASTER, JolCraftItems.DWARF_GUILDMASTER_SPAWN_EGG);
        SPAWN_EGGS.put(DwarfProfession.HISTORIAN, JolCraftItems.DWARF_HISTORIAN_SPAWN_EGG);
        SPAWN_EGGS.put(DwarfProfession.MERCHANT, JolCraftItems.DWARF_MERCHANT_SPAWN_EGG);
        SPAWN_EGGS.put(DwarfProfession.SCRAPPER, JolCraftItems.DWARF_SCRAPPER_SPAWN_EGG);
        SPAWN_EGGS.put(DwarfProfession.BREWMASTER, JolCraftItems.DWARF_BREWMASTER_SPAWN_EGG);
        SPAWN_EGGS.put(DwarfProfession.GUARD, JolCraftItems.DWARF_GUARD_SPAWN_EGG);
        SPAWN_EGGS.put(DwarfProfession.KEEPER, JolCraftItems.DWARF_KEEPER_SPAWN_EGG);
        SPAWN_EGGS.put(DwarfProfession.ARTISAN, JolCraftItems.DWARF_ARTISAN_SPAWN_EGG);
        SPAWN_EGGS.put(DwarfProfession.EXPLORER, JolCraftItems.DWARF_EXPLORER_SPAWN_EGG);
        SPAWN_EGGS.put(DwarfProfession.MINER, JolCraftItems.DWARF_MINER_SPAWN_EGG);
        SPAWN_EGGS.put(DwarfProfession.ALCHEMIST, JolCraftItems.DWARF_ALCHEMIST_SPAWN_EGG);
        SPAWN_EGGS.put(DwarfProfession.ARCANIST, JolCraftItems.DWARF_ARCANIST_SPAWN_EGG);
        SPAWN_EGGS.put(DwarfProfession.PRIEST, JolCraftItems.DWARF_PRIEST_SPAWN_EGG);
    }

    public static EntityType<DwarfEntity> getEntityType(DwarfProfession profession) {
        Supplier<EntityType<DwarfEntity>> sup = TYPES.get(profession);
        if (sup == null) sup = TYPES.get(DwarfProfession.NONE);
        return sup.get();
    }

    public static DeferredItem<Item> getSpawnEgg(DwarfProfession profession) {
        DeferredItem<Item> egg = SPAWN_EGGS.get(profession);
        if (egg == null) egg = SPAWN_EGGS.get(DwarfProfession.NONE);
        return egg;
    }
}