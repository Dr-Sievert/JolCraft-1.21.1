package net.sievert.jolcraft.world.item.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public final class JolCraftSpawnEggItems {

    private JolCraftSpawnEggItems() {}

    private static final int DWARF_PRIMARY = 0xAA7D66;

    public static DeferredItem<Item> registerDwarfSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_SPAWN_EGG, JolCraftEntities.DWARF, 0x4A342C);
    }

    public static DeferredItem<Item> registerDwarfGuildmasterSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_GUILDMASTER_SPAWN_EGG, JolCraftEntities.DWARF_GUILDMASTER, 0x4F2144);
    }

    public static DeferredItem<Item> registerDwarfHistorianSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_HISTORIAN_SPAWN_EGG, JolCraftEntities.DWARF_HISTORIAN, 0x49652D);
    }

    public static DeferredItem<Item> registerDwarfMerchantSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_MERCHANT_SPAWN_EGG, JolCraftEntities.DWARF_MERCHANT, 0x842610);
    }

    public static DeferredItem<Item> registerDwarfScrapperSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_SCRAPPER_SPAWN_EGG, JolCraftEntities.DWARF_SCRAPPER, 0x764721);
    }

    public static DeferredItem<Item> registerDwarfBrewmasterSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_BREWMASTER_SPAWN_EGG, JolCraftEntities.DWARF_BREWMASTER, 0x806723);
    }

    public static DeferredItem<Item> registerDwarfGuardSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_GUARD_SPAWN_EGG, JolCraftEntities.DWARF_GUARD, 0x333232);
    }

    public static DeferredItem<Item> registerDwarfKeeperSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_KEEPER_SPAWN_EGG, JolCraftEntities.DWARF_KEEPER, 0x166B11);
    }

    public static DeferredItem<Item> registerDwarfArtisanSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_ARTISAN_SPAWN_EGG, JolCraftEntities.DWARF_ARTISAN, 0x2F286C);
    }

    public static DeferredItem<Item> registerDwarfExplorerSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_EXPLORER_SPAWN_EGG, JolCraftEntities.DWARF_EXPLORER, 0x0089A0);
    }

    public static DeferredItem<Item> registerDwarfMinerSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_MINER_SPAWN_EGG, JolCraftEntities.DWARF_MINER, 0x28351C);
    }

    public static DeferredItem<Item> registerDwarfAlchemistSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_ALCHEMIST_SPAWN_EGG, JolCraftEntities.DWARF_ALCHEMIST, 0x89435E);
    }

    public static DeferredItem<Item> registerDwarfArcanistSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_ARCANIST_SPAWN_EGG, JolCraftEntities.DWARF_ARCANIST, 0x1E6C6A);
    }

    public static DeferredItem<Item> registerDwarfPriestSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_PRIEST_SPAWN_EGG, JolCraftEntities.DWARF_PRIEST, 0xFFF05A);
    }

    public static DeferredItem<Item> registerMuffhornSpawnEgg() {
        return registerEgg(
                JolCraftItemIds.MUFFHORN_SPAWN_EGG,
                JolCraftEntities.MUFFHORN,
                0x723119,
                0x4B1F12
        );
    }

    private static DeferredItem<Item> registerDwarfEgg(
            String name,
            Supplier<? extends EntityType<? extends Mob>> entity,
            int secondaryColor
    ) {
        return registerEgg(name, entity, DWARF_PRIMARY, secondaryColor);
    }

    private static DeferredItem<Item> registerEgg(
            String name,
            Supplier<? extends EntityType<? extends Mob>> entity,
            int primaryColor,
            int secondaryColor
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                name,
                props -> new SpawnEggItem(entity.get(), primaryColor, secondaryColor, props)
        );
    }
}