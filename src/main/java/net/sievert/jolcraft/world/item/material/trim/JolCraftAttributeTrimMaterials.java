package net.sievert.jolcraft.world.item.material.trim;

import net.minecraft.Util;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.sievert.jolcraft.world.item.JolCraftItems;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class JolCraftAttributeTrimMaterials {

    private record Entry(
            JolCraftTrimMaterials.Attribute attribute,
            Supplier<Item> ingredient,
            String hexColor
    ) {
        public ResourceKey<TrimMaterial> key() {
            return attribute.key();
        }
    }

    private static final List<Entry> ENTRIES = List.of(
            new Entry(JolCraftTrimMaterials.Attribute.AEGISCORE, JolCraftItems.AEGISCORE_CUT, "#8397bf"),
            new Entry(JolCraftTrimMaterials.Attribute.ASHFANG, JolCraftItems.ASHFANG_CUT, "#fe8301"),
            new Entry(JolCraftTrimMaterials.Attribute.DEEPMARROW, JolCraftItems.DEEPMARROW_CUT, "#bbb2ac"),
            new Entry(JolCraftTrimMaterials.Attribute.EARTHBLOOD, JolCraftItems.EARTHBLOOD_CUT, "#3e4206"),
            new Entry(JolCraftTrimMaterials.Attribute.EMBERGLASS, JolCraftItems.EMBERGLASS_CUT, "#9c584b"),
            new Entry(JolCraftTrimMaterials.Attribute.FROSTVEIN, JolCraftItems.FROSTVEIN_CUT, "#067da8"),
            new Entry(JolCraftTrimMaterials.Attribute.GRIMSTONE, JolCraftItems.GRIMSTONE_CUT, "#b50002"),
            new Entry(JolCraftTrimMaterials.Attribute.IRONHEART, JolCraftItems.IRONHEART_CUT, "#5c2320"),
            new Entry(JolCraftTrimMaterials.Attribute.LUMIERE, JolCraftItems.LUMIERE_CUT, "#f8f338"),
            new Entry(JolCraftTrimMaterials.Attribute.MOONSHARD, JolCraftItems.MOONSHARD_CUT, "#a5a6ff"),
            new Entry(JolCraftTrimMaterials.Attribute.RUSTAGATE, JolCraftItems.RUSTAGATE_CUT, "#c95d38"),
            new Entry(JolCraftTrimMaterials.Attribute.SKYBURROW, JolCraftItems.SKYBURROW_CUT, "#5bc9dc"),
            new Entry(JolCraftTrimMaterials.Attribute.SUNGLEAM, JolCraftItems.SUNGLEAM_CUT, "#efd03c"),
            new Entry(JolCraftTrimMaterials.Attribute.VERDANITE, JolCraftItems.VERDANITE_CUT, "#6de775"),
            new Entry(JolCraftTrimMaterials.Attribute.WOECRYSTAL, JolCraftItems.WOECRYSTAL_CUT, "#737296")
    );

    /**
     * Ingredient items used by these trim materials (for datagen tags/recipes).
     */
    public static List<Supplier<Item>> ingredients() {
        return ENTRIES.stream().map(Entry::ingredient).toList();
    }

    public static void bootstrap(BootstrapContext<TrimMaterial> context) {
        for (Entry entry : ENTRIES) {
            register(context, entry.key(), entry.ingredient().get(), style(entry.hexColor()));
        }
    }

    private static Style style(String hexColor) {
        return Style.EMPTY.withColor(TextColor.parseColor(hexColor).getOrThrow());
    }

    private static void register(
            BootstrapContext<TrimMaterial> context,
            ResourceKey<TrimMaterial> trimKey,
            Item item,
            Style style
    ) {
        TrimMaterial trimMaterial = TrimMaterial.create(
                trimKey.location().getPath(),
                item,
                Component.translatable(Util.makeDescriptionId("trim_material", trimKey.location())).withStyle(style),
                Map.of()
        );
        context.register(trimKey, trimMaterial);
    }
}