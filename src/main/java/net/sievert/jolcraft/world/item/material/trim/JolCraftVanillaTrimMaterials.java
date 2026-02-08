package net.sievert.jolcraft.world.item.material.trim;

import net.minecraft.Util;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class JolCraftVanillaTrimMaterials {

    private JolCraftVanillaTrimMaterials() {
    }

    private record Entry(
            JolCraftMaterials.Material material,
            Supplier<Item> ingredient,
            String hexColor
    ) {
        public ResourceKey<TrimMaterial> key() {
            return material.trimKey();
        }

        public Map<ResourceKey<EquipmentAsset>, String> overrideArmorAssets() {
            return Map.of(material.equipmentAssetKey(), material.darkerTrimName());
        }
    }

    private static final List<Entry> ENTRIES = List.of(
            new Entry(
                    JolCraftMaterials.Material.DEEPSLATE,
                    JolCraftItems.DEEPSLATE_PLATE,
                    "#595959"
            ),
            new Entry(
                    JolCraftMaterials.Material.MITHRIL,
                    JolCraftItems.MITHRIL_INGOT,
                    "#a6cfe5"
            )
    );

    private static final Map<JolCraftMaterials.Material, Entry> BY_MATERIAL = buildAll();

    private static Map<JolCraftMaterials.Material, Entry> buildAll() {
        Map<JolCraftMaterials.Material, Entry> out = new EnumMap<>(JolCraftMaterials.Material.class);
        for (Entry entry : ENTRIES) {
            Entry previous = out.put(entry.material(), entry);
            if (previous != null) {
                throw new IllegalStateException("Duplicate vanilla trim material entry for: " + entry.material());
            }
        }

        return Map.copyOf(out);
    }

    @SuppressWarnings("ClassEscapesDefinedScope")
    public static Entry entry(JolCraftMaterials.Material material) {
        Entry e = BY_MATERIAL.get(material);
        if (e == null) {
            throw new IllegalStateException("Missing vanilla trim material entry for: " + material);
        }
        return e;
    }

    /**
     * Ingredient items used by these trim materials (for datagen tags/recipes).
     */
    public static List<Supplier<Item>> ingredients() {
        return ENTRIES.stream().map(Entry::ingredient).toList();
    }

    public static void bootstrap(BootstrapContext<TrimMaterial> context) {
        for (Entry entry : BY_MATERIAL.values()) {
            register(
                    context,
                    entry.key(),
                    entry.ingredient().get(),
                    style(entry.hexColor()),
                    entry.overrideArmorAssets()
            );
        }
    }

    private static Style style(String hexColor) {
        return Style.EMPTY.withColor(TextColor.parseColor(hexColor).getOrThrow());
    }

    private static void register(
            BootstrapContext<TrimMaterial> context,
            ResourceKey<TrimMaterial> trimKey,
            Item item,
            Style style,
            Map<ResourceKey<EquipmentAsset>, String> overrideArmorAssets
    ) {
        TrimMaterial trimMaterial = TrimMaterial.create(
                trimKey.location().getPath(),
                item,
                Component.translatable(Util.makeDescriptionId("trim_material", trimKey.location())).withStyle(style),
                overrideArmorAssets
        );
        context.register(trimKey, trimMaterial);
    }
}