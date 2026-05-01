package net.sievert.jolcraft.world.item.material.trim;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.data.id.item.JolCraftTrimIds;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public final class JolCraftTrimPatterns {

    private JolCraftTrimPatterns() {}

    public static final ResourceKey<TrimPattern> FORGE =
            ResourceKey.create(Registries.TRIM_PATTERN, JolCraft.location(JolCraftTrimIds.FORGE));

    public record Entry(
            @NotNull ResourceKey<TrimPattern> key,
            @NotNull Supplier<Item> templateItem
    ) {
    }

    private static final List<Entry> ENTRIES = List.of(
            new Entry(FORGE, JolCraftItems.FORGE_ARMOR_TRIM_SMITHING_TEMPLATE)
    );

    public static @NotNull List<Entry> entries() {
        return ENTRIES;
    }

    public static void bootstrap(BootstrapContext<TrimPattern> context) {
        for (Entry entry : ENTRIES) {
            register(context, entry.templateItem().get(), entry.key());
        }
    }

    @SuppressWarnings("deprecation")
    private static void register(
            @NotNull BootstrapContext<TrimPattern> context,
            @NotNull Item item,
            @NotNull ResourceKey<TrimPattern> key
    ) {
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