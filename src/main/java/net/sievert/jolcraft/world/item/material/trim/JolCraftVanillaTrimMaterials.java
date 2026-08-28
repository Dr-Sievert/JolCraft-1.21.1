package net.sievert.jolcraft.world.item.material.trim;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.sievert.jolcraft.data.id.item.JolCraftTrimIds;
import net.sievert.jolcraft.util.client.JolCraftColors;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.material.JolCraftMaterials;
import net.sievert.jolcraft.world.item.material.armor.JolCraftArmorMaterials;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public final class JolCraftVanillaTrimMaterials {

    private JolCraftVanillaTrimMaterials() {}

    public record Entry(
            @NotNull JolCraftMaterials.Material material,
            @NotNull Supplier<Item> ingredient,
            @NotNull String hexColor
    ) {
        public @NotNull ResourceKey<TrimMaterial> key() {
            return this.material.trimKey();
        }

        public @NotNull String assetName() {
            return this.material.trimAssetName();
        }

        public float itemModelIndex() {
            return JolCraftTrimMaterials.vanillaItemModelIndex(this.material);
        }

        public @NotNull Map<Holder<ArmorMaterial>, String> overrideArmorMaterials() {
            return Map.of(
                    JolCraftArmorMaterials.armorMaterial(this.material),
                    this.material.darkerTrimName()
            );
        }
    }

    private static final List<Entry> ENTRIES = List.of(
            new Entry(
                    JolCraftMaterials.Material.DEEPSLATE,
                    JolCraftItems.DEEPSLATE_PLATE,
                    "595959"
            ),
            new Entry(
                    JolCraftMaterials.Material.MITHRIL,
                    JolCraftItems.MITHRIL_INGOT,
                    "A6CFE5"
            )
    );

    private static final Map<JolCraftMaterials.Material, Entry> BY_MATERIAL = buildAll();

    private static @NotNull Map<JolCraftMaterials.Material, Entry> buildAll() {
        Map<JolCraftMaterials.Material, Entry> out = new EnumMap<>(JolCraftMaterials.Material.class);

        for (Entry entry : ENTRIES) {
            Entry previous = out.put(entry.material(), entry);
            if (previous != null) {
                throw new IllegalStateException("Duplicate vanilla trim material entry for: " + entry.material());
            }
        }

        return Map.copyOf(out);
    }

    public static @NotNull Entry entry(@NotNull JolCraftMaterials.Material material) {
        Entry entry = BY_MATERIAL.get(material);
        if (entry == null) {
            throw new IllegalStateException("Missing vanilla trim material entry for: " + material);
        }
        return entry;
    }

    public static @NotNull List<Supplier<Item>> ingredients() {
        return ENTRIES.stream().map(Entry::ingredient).toList();
    }

    public static void bootstrap(@NotNull BootstrapContext<TrimMaterial> context) {
        for (Entry entry : BY_MATERIAL.values()) {
            register(
                    context,
                    entry.key(),
                    entry.assetName(),
                    entry.ingredient().get(),
                    entry.itemModelIndex(),
                    style(entry.hexColor()),
                    entry.overrideArmorMaterials()
            );
        }
    }

    private static @NotNull Style style(@NotNull String hexColor) {
        return Style.EMPTY.withColor(TextColor.fromRgb(JolCraftColors.rgb(hexColor)));
    }

    private static void register(
            @NotNull BootstrapContext<TrimMaterial> context,
            @NotNull ResourceKey<TrimMaterial> trimKey,
            @NotNull String assetName,
            @NotNull Item item,
            float itemModelIndex,
            @NotNull Style style,
            @NotNull Map<Holder<ArmorMaterial>, String> overrideArmorMaterials
    ) {
        TrimMaterial trimMaterial = TrimMaterial.create(
                assetName,
                item,
                itemModelIndex,
                Component.translatable(
                        Util.makeDescriptionId(JolCraftTrimIds.TRIM_MATERIAL, trimKey.location())
                ).withStyle(style),
                overrideArmorMaterials
        );

        context.register(trimKey, trimMaterial);
    }
}
