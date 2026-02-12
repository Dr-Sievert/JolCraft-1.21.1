package net.sievert.jolcraft.world.item.trim;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftTrimIds;
import net.sievert.jolcraft.world.item.JolCraftItems;

public final class JolCraftTrimPatterns {

    public static final ResourceKey<TrimPattern> FORGE = ResourceKey.create(Registries.TRIM_PATTERN, JolCraft.location(JolCraftTrimIds.FORGE));

    public static void bootstrap(BootstrapContext<TrimPattern> context) {
        register(context, JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get(), FORGE);
    }

    @SuppressWarnings("deprecation")
    private static void register(BootstrapContext<TrimPattern> context, Item item, ResourceKey<TrimPattern> key) {
        HolderGetter<Item> items = context.lookup(Registries.ITEM);
        Holder<Item> itemHolder = items.getOrThrow(item.builtInRegistryHolder().key());
        TrimPattern trimPattern = new TrimPattern(
                key.location(),
                itemHolder,
                Component.translatable(Util.makeDescriptionId(JolCraftTrimIds.TRIM_PATTERN, key.location())),
                false
        );
        context.register(key, trimPattern);
    }
}
