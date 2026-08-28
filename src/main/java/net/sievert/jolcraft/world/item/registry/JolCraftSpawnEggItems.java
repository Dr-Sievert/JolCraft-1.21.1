package net.sievert.jolcraft.world.item.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.util.client.JolCraftColors;
import net.sievert.jolcraft.world.entity.JolCraftEntities;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

import java.util.function.Supplier;

@SuppressWarnings("deprecation")
public final class JolCraftSpawnEggItems {

    private JolCraftSpawnEggItems() {}

    private static final int DWARF_PRIMARY = JolCraftColors.rgb("AA7D66");

    public static DeferredItem<Item> registerDwarfSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_SPAWN_EGG, JolCraftEntities.DWARF, JolCraftColors.rgb("4A342C"));
    }

    public static DeferredItem<Item> registerDwarfGuildmasterSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_GUILDMASTER_SPAWN_EGG, JolCraftEntities.DWARF_GUILDMASTER, JolCraftColors.rgb("4F2144"));
    }

    public static DeferredItem<Item> registerDwarfHistorianSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_HISTORIAN_SPAWN_EGG, JolCraftEntities.DWARF_HISTORIAN, JolCraftColors.rgb("49652D"));
    }

    public static DeferredItem<Item> registerDwarfMerchantSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_MERCHANT_SPAWN_EGG, JolCraftEntities.DWARF_MERCHANT, JolCraftColors.rgb("842610"));
    }

    public static DeferredItem<Item> registerDwarfScrapperSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_SCRAPPER_SPAWN_EGG, JolCraftEntities.DWARF_SCRAPPER, JolCraftColors.rgb("764721"));
    }

    public static DeferredItem<Item> registerDwarfBrewmasterSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_BREWMASTER_SPAWN_EGG, JolCraftEntities.DWARF_BREWMASTER, JolCraftColors.rgb("806723"));
    }

    public static DeferredItem<Item> registerDwarfGuardSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_GUARD_SPAWN_EGG, JolCraftEntities.DWARF_GUARD, JolCraftColors.rgb("333232"));
    }

    public static DeferredItem<Item> registerDwarfKeeperSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_KEEPER_SPAWN_EGG, JolCraftEntities.DWARF_KEEPER, JolCraftColors.rgb("166B11"));
    }

    public static DeferredItem<Item> registerDwarfArtisanSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_ARTISAN_SPAWN_EGG, JolCraftEntities.DWARF_ARTISAN, JolCraftColors.rgb("2F286C"));
    }

    public static DeferredItem<Item> registerDwarfExplorerSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_EXPLORER_SPAWN_EGG, JolCraftEntities.DWARF_EXPLORER, JolCraftColors.rgb("0089A0"));
    }

    public static DeferredItem<Item> registerDwarfMinerSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_MINER_SPAWN_EGG, JolCraftEntities.DWARF_MINER, JolCraftColors.rgb("28351C"));
    }

    public static DeferredItem<Item> registerDwarfAlchemistSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_ALCHEMIST_SPAWN_EGG, JolCraftEntities.DWARF_ALCHEMIST, JolCraftColors.rgb("89435E"));
    }

    public static DeferredItem<Item> registerDwarfArcanistSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_ARCANIST_SPAWN_EGG, JolCraftEntities.DWARF_ARCANIST, JolCraftColors.rgb("1E6C6A"));
    }

    public static DeferredItem<Item> registerDwarfPriestSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_PRIEST_SPAWN_EGG, JolCraftEntities.DWARF_PRIEST, JolCraftColors.rgb("FFF05A"));
    }

    public static DeferredItem<Item> registerDwarfBlacksmithSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_BLACKSMITH_SPAWN_EGG, JolCraftEntities.DWARF_BLACKSMITH, JolCraftColors.rgb("291E19"));
    }

    public static DeferredItem<Item> registerDwarfChampionSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_CHAMPION_SPAWN_EGG, JolCraftEntities.DWARF_CHAMPION, JolCraftColors.rgb("1D1021"));
    }

    public static DeferredItem<Item> registerDwarfSmelterSpawnEgg() {
        return registerDwarfEgg(JolCraftItemIds.DWARF_SMELTER_SPAWN_EGG, JolCraftEntities.DWARF_SMELTER, JolCraftColors.rgb("33280E"));
    }

    public static DeferredItem<Item> registerMuffhornSpawnEgg() {
        return registerEgg(
                JolCraftItemIds.MUFFHORN_SPAWN_EGG,
                JolCraftEntities.MUFFHORN,
                JolCraftColors.rgb("723119"),
                JolCraftColors.rgb("4B1F12")
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
