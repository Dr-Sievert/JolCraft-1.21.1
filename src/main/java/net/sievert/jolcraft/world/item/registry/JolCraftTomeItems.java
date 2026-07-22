package net.sievert.jolcraft.world.item.registry;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.JolCraftEnumExtensions;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.world.item.custom.book.*;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

public final class JolCraftTomeItems {

    private JolCraftTomeItems() {}

    public static DeferredItem<Item> registerDwarvenLexicon() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.DWARVEN_LEXICON,
                DwarvenLexiconItem::new,
                new Item.Properties().stacksTo(1).rarity(Rarity.RARE)
        );
    }

    public static DeferredItem<Item> registerAncientDwarvenLexicon() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.ANCIENT_DWARVEN_LEXICON,
                AncientDwarvenLexiconItem::new,
                new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)
        );
    }

    public static DeferredItem<Item> registerDwarvenTome() {
        return JolCraftItemRegistryHelper.registerSimpleItem(JolCraftItemIds.DWARVEN_TOME);
    }

    public static DeferredItem<Item> registerUnidentifiedDwarvenTome() {
        return registerUnidentified(
                JolCraftItemIds.UNIDENTIFIED_DWARVEN_TOME,
                UnidentifiedDwarvenTomeItem::new,
                Rarity.COMMON
        );
    }

    public static DeferredItem<Item> registerDwarvenTomeCommon() {
        return registerDwarvenTome(JolCraftItemIds.DWARVEN_TOME_COMMON, Rarity.COMMON);
    }

    public static DeferredItem<Item> registerDwarvenTomeUncommon() {
        return registerDwarvenTome(JolCraftItemIds.DWARVEN_TOME_UNCOMMON, Rarity.UNCOMMON);
    }

    public static DeferredItem<Item> registerDwarvenTomeRare() {
        return registerDwarvenTome(JolCraftItemIds.DWARVEN_TOME_RARE, Rarity.RARE);
    }

    public static DeferredItem<Item> registerDwarvenTomeEpic() {
        return registerDwarvenTome(JolCraftItemIds.DWARVEN_TOME_EPIC, Rarity.EPIC);
    }

    public static DeferredItem<Item> registerAncientDwarvenTome() {
        return JolCraftItemRegistryHelper.registerSimpleItem(JolCraftItemIds.ANCIENT_DWARVEN_TOME);
    }

    public static DeferredItem<Item> registerUnidentifiedAncientDwarvenTome() {
        return registerUnidentified(
                JolCraftItemIds.UNIDENTIFIED_ANCIENT_DWARVEN_TOME,
                UnidentifiedAncientTomeItem::new,
                Rarity.COMMON
        );
    }

    public static DeferredItem<Item> registerAncientDwarvenTomeCommon() {
        return registerAncientDwarvenTome(JolCraftItemIds.ANCIENT_DWARVEN_TOME_COMMON, Rarity.COMMON);
    }

    public static DeferredItem<Item> registerAncientDwarvenTomeUncommon() {
        return registerAncientDwarvenTome(JolCraftItemIds.ANCIENT_DWARVEN_TOME_UNCOMMON, Rarity.UNCOMMON);
    }

    public static DeferredItem<Item> registerAncientDwarvenTomeRare() {
        return registerAncientDwarvenTome(JolCraftItemIds.ANCIENT_DWARVEN_TOME_RARE, Rarity.RARE);
    }

    public static DeferredItem<Item> registerAncientDwarvenTomeEpic() {
        return registerAncientDwarvenTome(JolCraftItemIds.ANCIENT_DWARVEN_TOME_EPIC, Rarity.EPIC);
    }

    public static DeferredItem<Item> registerLegendaryPage() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.LEGENDARY_PAGE,
                new Item.Properties().rarity(JolCraftEnumExtensions.Rarity.LEGENDARY.getValue())
        );
    }

    public static DeferredItem<Item> registerUnidentifiedLegendaryAncientDwarvenTome() {
        return registerUnidentified(
                JolCraftItemIds.UNIDENTIFIED_LEGENDARY_ANCIENT_DWARVEN_TOME,
                UnidentifiedLegendaryAncientTomeItem::new,
                JolCraftEnumExtensions.Rarity.LEGENDARY.getValue()
        );
    }

    public static DeferredItem<Item> registerAncientDwarvenTomeLegendary() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.ANCIENT_DWARVEN_TOME_LEGENDARY,
                LegendaryAncientDwarvenTomeItem::new,
                new Item.Properties().stacksTo(1).rarity(JolCraftEnumExtensions.Rarity.LEGENDARY.getValue())
        );
    }

    private static DeferredItem<Item> registerDwarvenTome(String id, Rarity rarity) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                DwarvenTomeItem::new,
                new Item.Properties().stacksTo(1).rarity(rarity)
        );
    }

    private static DeferredItem<Item> registerAncientDwarvenTome(String id, Rarity rarity) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                AncientDwarvenTomeItem::new,
                new Item.Properties().stacksTo(1).rarity(rarity)
        );
    }

    private static DeferredItem<Item> registerUnidentified(
            String id,
            java.util.function.Function<Item.Properties, ? extends Item> factory,
            Rarity rarity
    ) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                factory,
                new Item.Properties().stacksTo(16).rarity(rarity)
        );
    }
}