package net.sievert.jolcraft.world.item.registry;

import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.sievert.jolcraft.data.id.item.JolCraftItemIds;
import net.sievert.jolcraft.data.language.JolCraftLanguageKeys;
import net.sievert.jolcraft.world.item.custom.tooltip.SimpleTooltipItem;
import net.sievert.jolcraft.world.item.registry.util.JolCraftItemRegistryHelper;

public final class JolCraftMaterialItems {

    private JolCraftMaterialItems() {}

    public static DeferredItem<Item> registerImpureMithril() {
        return JolCraftItemRegistryHelper.registerMithrilItem(
                JolCraftItemIds.IMPURE_MITHRIL,
                Item::new
        );
    }

    public static DeferredItem<Item> registerPureMithril() {
        return JolCraftItemRegistryHelper.registerMithrilItem(
                JolCraftItemIds.PURE_MITHRIL,
                Item::new
        );
    }

    public static DeferredItem<Item> registerMithrilIngot() {
        return JolCraftItemRegistryHelper.registerMithrilItem(
                JolCraftItemIds.MITHRIL_INGOT,
                props -> new SimpleTooltipItem(props, JolCraftLanguageKeys.TOOLTIP_TRIM_MATERIALS)
        );
    }

    public static DeferredItem<Item> registerMithrilNugget() {
        return JolCraftItemRegistryHelper.registerMithrilItem(
                JolCraftItemIds.MITHRIL_NUGGET,
                Item::new
        );
    }

    public static DeferredItem<Item> registerMithrilChainweave() {
        return JolCraftItemRegistryHelper.registerMithrilItem(
                JolCraftItemIds.MITHRIL_CHAINWEAVE,
                Item::new
        );
    }

    public static DeferredItem<Item> registerDeepslatePlate() {
        return JolCraftItemRegistryHelper.registerItem(
                JolCraftItemIds.DEEPSLATE_PLATE,
                props -> new SimpleTooltipItem(props, JolCraftLanguageKeys.TOOLTIP_TRIM_MATERIALS)
        );
    }

    public static DeferredItem<Item> registerDeepslateRod() {
        return JolCraftItemRegistryHelper.registerSimpleItem(JolCraftItemIds.DEEPSLATE_ROD);
    }

    public static DeferredItem<Item> registerMuffhornFur() {
        return JolCraftItemRegistryHelper.registerSimpleItem(JolCraftItemIds.MUFFHORN_FUR);
    }

    public static DeferredItem<Item> registerGeodeSmall() {
        return registerGeode(JolCraftItemIds.GEODE_SMALL);
    }

    public static DeferredItem<Item> registerGeodeMedium() {
        return registerGeode(JolCraftItemIds.GEODE_MEDIUM);
    }

    public static DeferredItem<Item> registerGeodeLarge() {
        return registerGeode(JolCraftItemIds.GEODE_LARGE);
    }

    private static DeferredItem<Item> registerGeode(String id) {
        return JolCraftItemRegistryHelper.registerItem(
                id,
                props -> new SimpleTooltipItem(props, JolCraftLanguageKeys.TOOLTIP_GEODE)
        );
    }
}