package net.sievert.jolcraft.world.item.trim;

import net.minecraft.Util;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import net.sievert.jolcraft.JolCraft;
import net.sievert.jolcraft.world.item.JolCraftItems;
import net.sievert.jolcraft.world.item.armor.JolCraftEquipmentAssets;

import java.util.Map;

public class JolCraftTrimMaterials {


    // Material-based
    public static final ResourceKey<TrimMaterial> DEEPSLATE  = ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location("deepslate"));
    public static final ResourceKey<TrimMaterial> MITHRIL    = ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location("mithril"));

    // Gems
    public static final ResourceKey<TrimMaterial> AEGISCORE   = ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location("aegiscore"));
    public static final ResourceKey<TrimMaterial> ASHFANG     = ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location("ashfang"));
    public static final ResourceKey<TrimMaterial> DEEPMARROW  = ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location("deepmarrow"));
    public static final ResourceKey<TrimMaterial> EARTHBLOOD  = ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location("earthblood"));
    public static final ResourceKey<TrimMaterial> EMBERGLASS  = ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location("emberglass"));
    public static final ResourceKey<TrimMaterial> FROSTVEIN   = ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location("frostvein"));
    public static final ResourceKey<TrimMaterial> GRIMSTONE   = ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location("grimstone"));
    public static final ResourceKey<TrimMaterial> IRONHEART   = ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location("ironheart"));
    public static final ResourceKey<TrimMaterial> LUMIERE     = ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location("lumiere"));
    public static final ResourceKey<TrimMaterial> MOONSHARD   = ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location("moonshard"));
    public static final ResourceKey<TrimMaterial> RUSTAGATE   = ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location("rustagate"));
    public static final ResourceKey<TrimMaterial> SKYBURROW   = ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location("skyburrow"));
    public static final ResourceKey<TrimMaterial> SUNGLEAM    = ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location("sungleam"));
    public static final ResourceKey<TrimMaterial> VERDANITE   = ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location("verdanite"));
    public static final ResourceKey<TrimMaterial> WOECRYSTAL  = ResourceKey.create(Registries.TRIM_MATERIAL, JolCraft.location("woecrystal"));

    public static void bootstrap(BootstrapContext<TrimMaterial> context) {
        register(
                context,
                DEEPSLATE,
                JolCraftItems.DEEPSLATE_PLATE.get(),
                Style.EMPTY.withColor(TextColor.parseColor("#595959").getOrThrow()),
                Map.of(JolCraftEquipmentAssets.DEEPSLATE.getKey(), "deepslate_darker")
        );
        register(
                context,
                MITHRIL,
                JolCraftItems.MITHRIL_INGOT.get(),
                Style.EMPTY.withColor(TextColor.parseColor("#a6cfe5").getOrThrow()),
                Map.of(JolCraftEquipmentAssets.MITHRIL.getKey(), "mithril_darker")
        );
        register(
                context,
                AEGISCORE,
                JolCraftItems.AEGISCORE_CUT.get(),
                Style.EMPTY.withColor(TextColor.parseColor("#8397bf").getOrThrow()),
                Map.of()
        );
        register(
                context,
                ASHFANG,
                JolCraftItems.ASHFANG_CUT.get(),
                Style.EMPTY.withColor(TextColor.parseColor("#fe8301").getOrThrow()),
                Map.of()
        );
        register(
                context,
                DEEPMARROW,
                JolCraftItems.DEEPMARROW_CUT.get(),
                Style.EMPTY.withColor(TextColor.parseColor("#bbb2ac").getOrThrow()),
                Map.of()
        );
        register(
                context,
                EARTHBLOOD,
                JolCraftItems.EARTHBLOOD_CUT.get(),
                Style.EMPTY.withColor(TextColor.parseColor("#3e4206").getOrThrow()),
                Map.of()
        );
        register(
                context,
                EMBERGLASS,
                JolCraftItems.EMBERGLASS_CUT.get(),
                Style.EMPTY.withColor(TextColor.parseColor("#9c584b").getOrThrow()),
                Map.of()
        );
        register(
                context,
                FROSTVEIN,
                JolCraftItems.FROSTVEIN_CUT.get(),
                Style.EMPTY.withColor(TextColor.parseColor("#067da8").getOrThrow()),
                Map.of()
        );
        register(
                context,
                GRIMSTONE,
                JolCraftItems.GRIMSTONE_CUT.get(),
                Style.EMPTY.withColor(TextColor.parseColor("#b50002").getOrThrow()),
                Map.of()
        );
        register(
                context,
                IRONHEART,
                JolCraftItems.IRONHEART_CUT.get(),
                Style.EMPTY.withColor(TextColor.parseColor("#5c2320").getOrThrow()),
                Map.of()
        );
        register(
                context,
                LUMIERE,
                JolCraftItems.LUMIERE_CUT.get(),
                Style.EMPTY.withColor(TextColor.parseColor("#f8f338").getOrThrow()),
                Map.of()
        );
        register(
                context,
                MOONSHARD,
                JolCraftItems.MOONSHARD_CUT.get(),
                Style.EMPTY.withColor(TextColor.parseColor("#a5a6ff").getOrThrow()),
                Map.of()
        );
        register(
                context,
                RUSTAGATE,
                JolCraftItems.RUSTAGATE_CUT.get(),
                Style.EMPTY.withColor(TextColor.parseColor("#c95d38").getOrThrow()),
                Map.of()
        );
        register(
                context,
                SKYBURROW,
                JolCraftItems.SKYBURROW_CUT.get(),
                Style.EMPTY.withColor(TextColor.parseColor("#5bc9dc").getOrThrow()),
                Map.of()
        );
        register(
                context,
                SUNGLEAM,
                JolCraftItems.SUNGLEAM_CUT.get(),
                Style.EMPTY.withColor(TextColor.parseColor("#efd03c").getOrThrow()),
                Map.of()
        );
        register(
                context,
                VERDANITE,
                JolCraftItems.VERDANITE_CUT.get(),
                Style.EMPTY.withColor(TextColor.parseColor("#6de775").getOrThrow()),
                Map.of()
        );
        register(
                context,
                WOECRYSTAL,
                JolCraftItems.WOECRYSTAL_CUT.get(),
                Style.EMPTY.withColor(TextColor.parseColor("#737296").getOrThrow()),
                Map.of()
        );
    }

    private static void register(
            BootstrapContext<TrimMaterial> context,
            ResourceKey<TrimMaterial> trimKey,
            Item item,
            Style style,
            Map<ResourceKey<EquipmentAsset>, String> overrideArmorAssets // NEW PARAM
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
