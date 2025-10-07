package net.sievert.jolcraft.item.trim;

import net.minecraft.Util;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.trim.TrimPattern;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.item.JolCraftItems;

public class JolCraftTrimPatterns {
    public static final ResourceKey<TrimPattern> FORGE = ResourceKey.create(Registries.TRIM_PATTERN, JolCraft.location("forge"));

    public static void bootstrap(BootstrapContext<TrimPattern> context) {
        register(context, JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE.get(), FORGE);
    }

    private static void register(BootstrapContext<TrimPattern> context, Item item, ResourceKey<TrimPattern> key) {
        TrimPattern trimPattern = new TrimPattern(
                key.location(),
                BuiltInRegistries.ITEM.wrapAsHolder(item),
                Component.translatable(Util.makeDescriptionId("trim_pattern", key.location())),
                false
        );
        context.register(key, trimPattern);
    }

}
