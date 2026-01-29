package net.sievert.jolcraft.datagen.model.subprovider;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.world.item.Item;
import net.sievert.jolcraft.datagen.model.util.AbstractModelProvider;
import net.sievert.jolcraft.world.item.JolCraftItems;
import org.jetbrains.annotations.NotNull;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public final class EggModelSubProvider implements AbstractModelProvider.ModelSubProvider {

    private static final String DWARF_EGG_PRIMARY = "aa7d66";

    @Override
    public void addModels(@NotNull BlockModelGenerators blocks, @NotNull ItemModelGenerators items) {

        generateSpawnEgg(items, JolCraftItems.DWARF_SPAWN_EGG.get(),             DWARF_EGG_PRIMARY, "4a342c");
        generateSpawnEgg(items, JolCraftItems.DWARF_GUILDMASTER_SPAWN_EGG.get(), DWARF_EGG_PRIMARY, "4f2144");
        generateSpawnEgg(items, JolCraftItems.DWARF_HISTORIAN_SPAWN_EGG.get(),   DWARF_EGG_PRIMARY, "49652d");
        generateSpawnEgg(items, JolCraftItems.DWARF_MERCHANT_SPAWN_EGG.get(),    DWARF_EGG_PRIMARY, "842610");
        generateSpawnEgg(items, JolCraftItems.DWARF_SCRAPPER_SPAWN_EGG.get(),    DWARF_EGG_PRIMARY, "764721");
        generateSpawnEgg(items, JolCraftItems.DWARF_BREWMASTER_SPAWN_EGG.get(),  DWARF_EGG_PRIMARY, "806723");
        generateSpawnEgg(items, JolCraftItems.DWARF_GUARD_SPAWN_EGG.get(),       DWARF_EGG_PRIMARY, "333232");
        generateSpawnEgg(items, JolCraftItems.DWARF_KEEPER_SPAWN_EGG.get(),      DWARF_EGG_PRIMARY, "166b11");
        generateSpawnEgg(items, JolCraftItems.DWARF_ARTISAN_SPAWN_EGG.get(),     DWARF_EGG_PRIMARY, "2f286c");
        generateSpawnEgg(items, JolCraftItems.DWARF_EXPLORER_SPAWN_EGG.get(),    DWARF_EGG_PRIMARY, "0089a0");
        generateSpawnEgg(items, JolCraftItems.DWARF_MINER_SPAWN_EGG.get(),       DWARF_EGG_PRIMARY, "28351c");
        generateSpawnEgg(items, JolCraftItems.DWARF_ALCHEMIST_SPAWN_EGG.get(),   DWARF_EGG_PRIMARY, "89435e");
        generateSpawnEgg(items, JolCraftItems.DWARF_ARCANIST_SPAWN_EGG.get(),    DWARF_EGG_PRIMARY, "1e6c6a");
        generateSpawnEgg(items, JolCraftItems.DWARF_PRIEST_SPAWN_EGG.get(),      DWARF_EGG_PRIMARY, "fff05a");

        generateSpawnEgg(items, JolCraftItems.MUFFHORN_SPAWN_EGG.get(), "723119", "4b1f12");
    }

    public static void generateSpawnEgg(
            ItemModelGenerators itemModels,
            Item eggItem,
            String primaryHex,
            String secondaryHex
    ) {
        int primaryColor = eggColorPrimary(primaryHex);
        int secondaryColor = eggColorSecondary(secondaryHex);
        itemModels.generateSpawnEgg(eggItem, primaryColor, secondaryColor);
    }

    private static int eggColor(String hex, int mask) {
        String s = hex.trim();
        if (s.startsWith("#")) s = s.substring(1);
        else if (s.startsWith("0x") || s.startsWith("0X")) s = s.substring(2);
        int rgb = Integer.parseInt(s, 16) & 0xFFFFFF;
        int r = Math.min(255, (((rgb >> 16) & 0xFF) * 255 + mask) / mask);
        int g = Math.min(255, (((rgb >> 8)  & 0xFF) * 255 + mask) / mask);
        int b = Math.min(255, (( rgb        & 0xFF) * 255 + mask) / mask);
        return (int)(0xFF000000L | (r << 16) | (g << 8) | b);
    }

    private static int eggColorPrimary(String hex) {
        return eggColor(hex, 232);
    }

    private static int eggColorSecondary(String hex) {
        return eggColor(hex, 222);
    }
}
