package net.sievert.jolcraft.world.item.material.trim;

import net.minecraft.Util;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.sievert.jolcraft.data.id.item.JolCraftTrimIds;
import net.sievert.jolcraft.util.client.JolCraftColors;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class JolCraftAttributeTrimMaterials {

    private JolCraftAttributeTrimMaterials() {}

    public record Entry(
            @NotNull JolCraftTrimMaterials.Attribute attribute,
            @NotNull Supplier<Item> ingredient,
            @NotNull String hexColor
    ) {
        public @NotNull ResourceKey<TrimMaterial> key() {
            return this.attribute.key();
        }

        public float itemModelIndex() {
            return this.attribute.itemModelIndex();
        }
    }

    private static final List<Entry> ENTRIES = List.of(
            new Entry(JolCraftTrimMaterials.Attribute.AEGISCORE, JolCraftItems.AEGISCORE_CUT, "8397BF"),
            new Entry(JolCraftTrimMaterials.Attribute.ASHFANG, JolCraftItems.ASHFANG_CUT, "FE8301"),
            new Entry(JolCraftTrimMaterials.Attribute.DEEPMARROW, JolCraftItems.DEEPMARROW_CUT, "BBB2AC"),
            new Entry(JolCraftTrimMaterials.Attribute.EARTHBLOOD, JolCraftItems.EARTHBLOOD_CUT, "3E4206"),
            new Entry(JolCraftTrimMaterials.Attribute.EMBERGLASS, JolCraftItems.EMBERGLASS_CUT, "9C584B"),
            new Entry(JolCraftTrimMaterials.Attribute.FROSTVEIN, JolCraftItems.FROSTVEIN_CUT, "067DA8"),
            new Entry(JolCraftTrimMaterials.Attribute.GRIMSTONE, JolCraftItems.GRIMSTONE_CUT, "B50002"),
            new Entry(JolCraftTrimMaterials.Attribute.IRONHEART, JolCraftItems.IRONHEART_CUT, "5C2320"),
            new Entry(JolCraftTrimMaterials.Attribute.LUMIERE, JolCraftItems.LUMIERE_CUT, "F8F338"),
            new Entry(JolCraftTrimMaterials.Attribute.MOONSHARD, JolCraftItems.MOONSHARD_CUT, "A5A6FF"),
            new Entry(JolCraftTrimMaterials.Attribute.RUSTAGATE, JolCraftItems.RUSTAGATE_CUT, "C95D38"),
            new Entry(JolCraftTrimMaterials.Attribute.SKYBURROW, JolCraftItems.SKYBURROW_CUT, "5BC9DC"),
            new Entry(JolCraftTrimMaterials.Attribute.SUNGLEAM, JolCraftItems.SUNGLEAM_CUT, "EFD03C"),
            new Entry(JolCraftTrimMaterials.Attribute.VERDANITE, JolCraftItems.VERDANITE_CUT, "6DE775"),
            new Entry(JolCraftTrimMaterials.Attribute.WOECRYSTAL, JolCraftItems.WOECRYSTAL_CUT, "737296")
    );

    public static @NotNull List<Supplier<Item>> ingredients() {
        return ENTRIES.stream().map(Entry::ingredient).toList();
    }

    public static void bootstrap(@NotNull BootstrapContext<TrimMaterial> context) {
        for (Entry entry : ENTRIES) {
            register(
                    context,
                    entry.key(),
                    entry.ingredient().get(),
                    entry.itemModelIndex(),
                    style(entry.hexColor())
            );
        }
    }

    private static @NotNull Style style(@NotNull String hexColor) {
        return Style.EMPTY.withColor(TextColor.fromRgb(JolCraftColors.rgb(hexColor)));
    }

    private static void register(
            @NotNull BootstrapContext<TrimMaterial> context,
            @NotNull ResourceKey<TrimMaterial> trimKey,
            @NotNull Item item,
            float itemModelIndex,
            @NotNull Style style
    ) {
        TrimMaterial trimMaterial = TrimMaterial.create(
                trimKey.location().getPath(),
                item,
                itemModelIndex,
                Component.translatable(
                        Util.makeDescriptionId(JolCraftTrimIds.TRIM_MATERIAL, trimKey.location())
                ).withStyle(style),
                Map.of()
        );

        context.register(trimKey, trimMaterial);
    }
}
